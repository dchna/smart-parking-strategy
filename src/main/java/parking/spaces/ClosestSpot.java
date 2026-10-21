package parking.spaces;
import java.util.List;
import java.util.Comparator;
public class ClosestSpot implements SpotStrategy {
    @Override public ParkingSpot select(List<ParkingSpot> spots) {
        return spots.stream().filter(ParkingSpot::free)
            .min(Comparator.comparingInt(ParkingSpot::metersToEntrance))
            .orElseThrow(() -> new IllegalStateException("Parking full"));
    }
    @Override public String name() { return "Closest to entrance"; }
}
