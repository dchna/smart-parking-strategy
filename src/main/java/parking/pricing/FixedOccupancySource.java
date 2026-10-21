package parking.pricing;
public class FixedOccupancySource implements OccupancySource {
    private final int percent;
    public FixedOccupancySource(int percent) {
        if (percent < 0 || percent > 100) throw new IllegalArgumentException("Occupancy must be 0..100");
        this.percent = percent;
    }
    @Override public int occupiedPercent() { return percent; }
}
