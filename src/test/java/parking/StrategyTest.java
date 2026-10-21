package parking;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import parking.pricing.*;
import parking.spaces.*;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
class StrategyTest {
    private final List<ParkingSpot> spots = List.of(
            new ParkingSpot("P1", 60, false, true),
            new ParkingSpot("P2", 25, true, true),
            new ParkingSpot("P3", 10, false, true));
    @Test void hourly() { assertEquals(400, new HourlyPrice().calculate(2)); }
    @Test void dayPass() { assertEquals(1200, new DayPassPrice().calculate(12)); }
    @Test void busyWithFakeSensor() { assertEquals(700, new BusyHoursPrice(() -> 95).calculate(2)); }
    @Test void busyWithoutSurcharge() { assertEquals(400, new BusyHoursPrice(() -> 20).calculate(2)); }
    @Test void firstFree() { assertEquals("P1", new FirstFreeSpot().select(spots).id()); }
    @Test void closest() { assertEquals("P3", new ClosestSpot().select(spots).id()); }
    @Test void covered() { assertEquals("P2", new CoveredSpot().select(spots).id()); }
    @Test void coveredFallback() { assertEquals("P1", new CoveredSpot().select(List.of(new ParkingSpot("P1", 40, false, true))).id()); }
    @Test void parkingFull() { assertThrows(IllegalStateException.class, () -> new FirstFreeSpot().select(List.of())); }
    @Test void contextCanSwitchPrice() {
        PriceContext context = new PriceContext(new HourlyPrice());
        assertEquals(2400, context.calculate(12));
        context.setStrategy(new DayPassPrice());
        assertEquals(1200, context.calculate(12));
    }
    @Test void contextCanSwitchSpace() {
        SpotContext context = new SpotContext(new FirstFreeSpot());
        assertEquals("P1", context.select(spots).id());
        context.setStrategy(new ClosestSpot());
        assertEquals("P3", context.select(spots).id());
    }
    @Test void selectorBusy() { assertInstanceOf(BusyHoursPrice.class, new PriceSelector(() -> 90).choose(2)); }
    @Test void selectorLongStay() { assertInstanceOf(DayPassPrice.class, new PriceSelector(() -> 10).choose(8)); }
    @Test void selectorShortStay() { assertInstanceOf(HourlyPrice.class, new PriceSelector(() -> 10).choose(2)); }
    @Test void selectorCovered() { assertInstanceOf(CoveredSpot.class, new SpotSelector().choose(true, true)); }
    @Test void selectorMobility() { assertInstanceOf(ClosestSpot.class, new SpotSelector().choose(false, true)); }
    @Test void selectorDefault() { assertInstanceOf(FirstFreeSpot.class, new SpotSelector().choose(false, false)); }
    @Test void invalidHours() { assertThrows(IllegalArgumentException.class, () -> new PriceContext(new HourlyPrice()).calculate(0)); }
    static Stream<PriceStrategy> allPriceStrategies() {
        return Stream.of(new HourlyPrice(), new DayPassPrice(), new BusyHoursPrice(() -> 100));
    }
    @ParameterizedTest @MethodSource("allPriceStrategies")
    void priceNeverNegative(PriceStrategy strategy) {
        assertTrue(new PriceContext(strategy).calculate(12) >= 0);
    }
}
