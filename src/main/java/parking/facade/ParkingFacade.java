package parking.facade;
import parking.gate.*;
import parking.space.*;
import parking.payment.*;
public class ParkingFacade {
    private final GateService gate;
    private final SpaceService space;
    private final PaymentService payment;
    private final ChainRepository chains;
    public ParkingFacade(GateService g,SpaceService s,PaymentService p,ChainRepository c) {
        gate=g;
        space=s;
        payment=p;
        chains=c;
    }
    public String park(String id,String plate,int hours,boolean failB,boolean failC) {
        if(chains.get(id)!=null)throw new IllegalArgumentException("Use a new ID");
        if(hours<1||hours>24)throw new IllegalArgumentException("Hours must be 1-24");
        gate.enter(id,plate);
        chains.save(id,ChainRepository.Stage.PENDING_B);
        return continueChain(id,hours,failB,failC);
    }
    public String continueChain(String id,int hours,boolean failB,boolean failC) {
        ChainRepository.Stage stage=chains.get(id);
        if(stage==null)throw new IllegalArgumentException("Unknown chain");
        if(stage==ChainRepository.Stage.COMPLETED)return "Already completed";
        if(stage==ChainRepository.Stage.COMPENSATED)return "Already compensated";
        try {
            if(stage==ChainRepository.Stage.PENDING_B) {
                if(failB)throw new IllegalStateException("Simulated space sensor failure");
                space.reserve(id);
                chains.save(id,ChainRepository.Stage.PENDING_C);
            }
            if(failC)throw new IllegalStateException("Simulated payment terminal failure");
            payment.charge(id,hours,false);
            chains.save(id,ChainRepository.Stage.COMPLETED);
            return "COMPLETED: spot "+space.get(id).spot()+", paid "+payment.get(id).amount()+" KZT";
        }
        catch(RuntimeException ex) {
            compensate(id);
            return "FAILED: "+ex.getMessage()+"; chain COMPENSATED";
        }
    }
    public void compensate(String id) {
        ChainRepository.Stage stage=chains.get(id);
        if(stage==null)throw new IllegalArgumentException("Unknown chain");
        if(stage==ChainRepository.Stage.COMPENSATED)return;
        if(stage==ChainRepository.Stage.COMPLETED)throw new IllegalStateException("Cannot compensate completed payment");
        if(stage==ChainRepository.Stage.PENDING_C) {
            ParkingReservation r=space.get(id);
            if(r!=null&&r.status()==ParkingReservation.Status.RESERVED)space.release(id);
        }
        GateEntry e=gate.get(id);
        if(e!=null&&e.status()==GateEntry.Status.ALLOWED)gate.cancel(id);
        chains.save(id,ChainRepository.Stage.COMPENSATED);
    }
}
