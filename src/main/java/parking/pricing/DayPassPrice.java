package parking.pricing;
public class DayPassPrice implements PriceStrategy {
    @Override public int calculate(int hours) { return Math.min(hours * 200, 1200); }
    @Override public String name() { return "Day pass with cap"; }
}
