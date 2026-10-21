package parking;
import org.junit.jupiter.api.Test;
import parking.pricing.*;
import static org.junit.jupiter.api.Assertions.*;
class PricingRegressionTest {
    private final ParkingPricing pricing = new ParkingPricing(new FixedOccupancySource(90));
    @Test void hourly() { assertEquals(800, pricing.calculate(PriceMode.HOURLY, 4)); }
    @Test void dayPass() { assertEquals(1200, pricing.calculate(PriceMode.DAY_PASS, 12)); }
    @Test void busyHours() { assertEquals(1100, pricing.calculate(PriceMode.BUSY_HOURS, 4)); }
    @Test void invalidHours() { assertThrows(IllegalArgumentException.class,
            () -> pricing.calculate(PriceMode.HOURLY, 0)); }
}
