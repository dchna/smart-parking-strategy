package parking.spaces;
import java.util.List;
public class FirstFreeSpot implements SpotStrategy {
    @Override public ParkingSpot select(List<ParkingSpot> spots) {
        for (ParkingSpot spot : spots) if (spot.free()) return spot;
        throw new IllegalStateException("Parking full");
    }
    @Override public String name() { return "First free"; }
}
