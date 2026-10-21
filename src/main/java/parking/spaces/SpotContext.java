package parking.spaces;
import java.util.List;
import java.util.Objects;
public class SpotContext {
    private SpotStrategy strategy;
    public SpotContext(SpotStrategy strategy) { setStrategy(strategy); }
    public void setStrategy(SpotStrategy strategy) { this.strategy = Objects.requireNonNull(strategy); }
    public ParkingSpot select(List<ParkingSpot> spots) { return strategy.select(spots); }
    public String strategyName() { return strategy.name(); }
}
