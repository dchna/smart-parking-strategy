package parking.spaces;
import java.util.List;
public interface SpotStrategy {
    ParkingSpot select(List<ParkingSpot> spots);
    String name();
}
