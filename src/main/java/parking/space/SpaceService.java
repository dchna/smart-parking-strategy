package parking.space;
import parking.spaces.*;
import java.util.*;

public class SpaceService {
    private final SpaceRepository repo;
    private final SpacePolicy policy;
    private final int capacity;
    private SpotContext spotContext = new SpotContext(new FirstFreeSpot());

    public SpaceService(SpaceRepository repo, SpacePolicy policy, int capacity) {
        this.repo = repo;
        this.policy = policy;
        this.capacity = capacity;
    }

    public void setSpotStrategy(SpotStrategy strategy) { spotContext.setStrategy(strategy); }
    public String spotStrategyName() { return spotContext.strategyName(); }

    public List<ParkingSpot> availableSpots() {
        List<ParkingSpot> spots = new ArrayList<>();
        for (int i = 1; i <= capacity; i++) {
            // Sample metadata for the strategies; reservation occupancy is read from disk.
            int distance = (capacity - i + 1) * 10;
            boolean covered = i % 2 == 0;
            spots.add(new ParkingSpot("P" + i, distance, covered, !repo.isOccupied("P" + i)));
        }
        return spots;
    }

    public ParkingReservation reserve(String id) {
        if (repo.get(id) != null) throw new IllegalArgumentException("Reservation already exists");
        ParkingSpot selected = spotContext.select(availableSpots());
        ParkingReservation reservation = new ParkingReservation(id, selected.id(), ParkingReservation.Status.NEW);
        reservation.reserve();
        repo.save(reservation);
        return reservation;
    }

    public void release(String id) {
        ParkingReservation reservation = repo.get(id);
        if (reservation == null) throw new IllegalArgumentException("Missing reservation");
        if (reservation.status() == ParkingReservation.Status.RELEASED) return;
        reservation.release();
        repo.save(reservation);
    }

    public ParkingReservation get(String id) { return repo.get(id); }
}
