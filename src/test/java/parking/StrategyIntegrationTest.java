package parking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import parking.store.*;
import parking.gate.*;
import parking.space.*;
import parking.payment.*;
import parking.facade.*;
import parking.pricing.*;
import parking.spaces.*;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class StrategyIntegrationTest {
    @TempDir Path dir;

    @Test void switchingStrategiesChangesPersistedPayment() {
        DiskStore disk = new DiskStore(dir);
        GateService gate = new GateService(new GateRepository(disk), new PlateValidator());
        SpaceService space = new SpaceService(new SpaceRepository(disk), new SpacePolicy(), 3);
        PaymentService payment = new PaymentService(new PaymentRepository(disk), new PaymentPolicy());
        ParkingFacade facade = new ParkingFacade(gate, space, payment, new ChainRepository(disk));
        payment.setPriceStrategy(new DayPassPrice());
        assertTrue(facade.park("long", "ABC123", 10, false, false).contains("COMPLETED"));
        assertEquals(1200, new PaymentRepository(new DiskStore(dir)).get("long").amount());
        payment.setPriceStrategy(new HourlyPrice());
        assertTrue(facade.park("short", "ABC124", 2, false, false).contains("COMPLETED"));
        assertEquals(400, payment.get("short").amount());
    }

    @Test void switchingSpotStrategyChangesPersistedSpot() {
        DiskStore disk = new DiskStore(dir);
        SpaceService space = new SpaceService(new SpaceRepository(disk), new SpacePolicy(), 3);
        space.setSpotStrategy(new ClosestSpot());
        assertEquals("P3", space.reserve("car1").spot());
        space.setSpotStrategy(new CoveredSpot());
        assertEquals("P2", space.reserve("car2").spot());
        assertEquals("P2", new SpaceRepository(new DiskStore(dir)).get("car2").spot());
    }
}
