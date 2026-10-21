package parking.pricing;
import java.util.Objects;
public class PriceContext {
    private PriceStrategy strategy;
    public PriceContext(PriceStrategy strategy) { setStrategy(strategy); }
    public void setStrategy(PriceStrategy strategy) { this.strategy = Objects.requireNonNull(strategy); }
    public int calculate(int hours) {
        if (hours < 1 || hours > 24) throw new IllegalArgumentException("Hours must be 1..24");
        return strategy.calculate(hours);
    }
    public String strategyName() { return strategy.name(); }
}
