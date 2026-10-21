package parking.pricing;
public class BusyHoursPrice implements PriceStrategy {
    private final OccupancySource source;
    public BusyHoursPrice(OccupancySource source) { this.source = source; }
    @Override public int calculate(int hours) {
        return hours * 200 + (source.occupiedPercent() >= 80 ? 300 : 0);
    }
    @Override public String name() { return "Busy hours surcharge"; }
}
