package parking.spaces;
import java.util.List;
public class CoveredSpot implements SpotStrategy {
    @Override public ParkingSpot select(List<ParkingSpot> spots) {
        for (ParkingSpot spot : spots) if (spot.free() && spot.covered()) return spot;
        return new FirstFreeSpot().select(spots);
    }
    @Override public String name() { return "Covered preferred"; }
}
