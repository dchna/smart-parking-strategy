
package parking.pricing;

public class ParkingPricing {

    private final OccupancySource occupancy;

    public ParkingPricing(OccupancySource occupancy) {
        this.occupancy = occupancy;
    }

    public int calculate(PriceMode mode, int hours) {

        if (hours < 1 || hours > 24) {
            throw new IllegalArgumentException("Hours must be 1..24");
        }

        if (mode == PriceMode.HOURLY) {
            return hours * 200;

        } else if (mode == PriceMode.DAY_PASS) {
            return Math.min(hours * 200, 1200);

        } else if (mode == PriceMode.BUSY_HOURS) {
            int price = hours * 200;

            if (occupancy.occupiedPercent() >= 80) {
                price += 300;
            }

            return price;

        } else {
            throw new IllegalArgumentException("Unknown price mode");
        }
    }
}
