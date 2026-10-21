package parking.facade;

import parking.gate.GateService;
import parking.space.SpaceService;

public class EntryFacade {
    private final GateService gate;
    private final SpaceService space;
    private final ChainRepository chains;
    private final ParkingFacade parkingFacade;

    public EntryFacade(GateService gate, SpaceService space, ChainRepository chains, ParkingFacade parkingFacade) {
        this.gate = gate;
        this.space = space;
        this.chains = chains;
        this.parkingFacade = parkingFacade;
    }

    public String enterAndReserve(String id, String plate) {
        if (chains.get(id) != null || gate.get(id) != null) throw new IllegalArgumentException("ID already exists");
        gate.enter(id, plate);
        chains.save(id, ChainRepository.Stage.PENDING_B);
        try {
            space.reserve(id);
            chains.save(id, ChainRepository.Stage.PENDING_C);
            return "SUCCESS: A and B completed, C not started";
        } catch (RuntimeException e) {
            parkingFacade.compensate(id);
            return "FAILED: " + e.getMessage();
        }
    }
}
