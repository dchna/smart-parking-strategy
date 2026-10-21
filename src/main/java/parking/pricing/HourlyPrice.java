package parking.pricing;
public class HourlyPrice implements PriceStrategy {
    @Override public int calculate(int hours) { return hours * 200; }
    @Override public String name() { return "Hourly"; }
}
