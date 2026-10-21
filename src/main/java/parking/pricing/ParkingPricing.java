package parking.pricing;
// AFTER refactoring: old public method preserved so original tests still pass.
public class ParkingPricing {
    private final OccupancySource occupancy;
    public ParkingPricing(OccupancySource occupancy) { this.occupancy = occupancy; }
    public int calculate(PriceMode mode, int hours) {
        PriceStrategy strategy = switch (mode) {
            case HOURLY -> new HourlyPrice();
            case DAY_PASS -> new DayPassPrice();
            case BUSY_HOURS -> new BusyHoursPrice(occupancy);
        };
        return new PriceContext(strategy).calculate(hours);
    }
}
