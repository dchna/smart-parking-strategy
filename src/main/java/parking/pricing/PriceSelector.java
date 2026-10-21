package parking.pricing;
public class PriceSelector {
    private final OccupancySource source;
    public PriceSelector(OccupancySource source) { this.source = source; }
    public PriceStrategy choose(int hours) {
        if (hours < 1 || hours > 24) throw new IllegalArgumentException("Hours must be 1..24");
        if (source.occupiedPercent() >= 80) return new BusyHoursPrice(source);
        if (hours >= 7) return new DayPassPrice();
        return new HourlyPrice();
    }
}
