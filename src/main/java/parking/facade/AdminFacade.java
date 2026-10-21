package parking.facade;
import parking.gate.*;
import parking.space.*;
import parking.payment.*;
public class AdminFacade {
    private final GateService gate;
    private final SpaceService space;
    private final PaymentService payment;
    private final ChainRepository chains;
    private final ParkingFacade workflow;
    public AdminFacade(GateService g,SpaceService s,PaymentService p,ChainRepository c,ParkingFacade w) {
        gate=g;
        space=s;
        payment=p;
        chains=c;
        workflow=w;
    }
    public String inspect(String id) {
        var stage=chains.get(id);
        if(stage==null)return "No record";
        var g=gate.get(id);
        var s=space.get(id);
        var p=payment.get(id);
        return id+" | chain="+stage+" | gate="+(g==null?"NONE":g.status())+" | space="+(s==null?"NONE":s.status())+" | payment="+(p==null?"NONE":p.status());
    }
    public String all() {
        StringBuilder b=new StringBuilder();
        for(String id:chains.ids())b.append(inspect(id)).append(System.lineSeparator());
        return b.isEmpty()?"No records":b.toString();
    }
    public void manualCompensate(String id) {
        workflow.compensate(id);
    }
}
