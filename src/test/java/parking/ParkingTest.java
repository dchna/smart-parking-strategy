package parking;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;
import parking.store.*;
import parking.gate.*;
import parking.space.*;
import parking.payment.*;
import parking.facade.*;
class ParkingTest {
    @TempDir Path dir;
    DiskStore disk;
    GateService gate;
    SpaceService space;
    PaymentService payment;
    ChainRepository chain;
    ParkingFacade facade;
    AdminFacade admin;
    @BeforeEach void setup() {
        disk=new DiskStore(dir);
        gate=new GateService(new GateRepository(disk),new PlateValidator());
        space=new SpaceService(new SpaceRepository(disk),new SpacePolicy(),2);
        payment=new PaymentService(new PaymentRepository(disk),new PaymentPolicy());
        chain=new ChainRepository(disk);
        facade=new ParkingFacade(gate,space,payment,chain);
        admin=new AdminFacade(gate,space,payment,chain,facade);
    }
    void parkingShouldBe(String id,ChainRepository.Stage stage,GateEntry.Status g,ParkingReservation.Status s) {
        assertEquals(stage,chain.get(id));
        assertEquals(g,gate.get(id).status());
        if(s==null)assertNull(space.get(id));
        else assertEquals(s,space.get(id).status());
    }
    @Test void happyPath() {
        assertTrue(facade.park("ok1","ABC123",2,false,false).contains("COMPLETED"));
        parkingShouldBe("ok1",ChainRepository.Stage.COMPLETED,GateEntry.Status.ALLOWED,ParkingReservation.Status.RESERVED);
        assertEquals(400,payment.get("ok1").amount());
        assertEquals(Payment.Status.PAID,payment.get("ok1").status());
        assertTrue(admin.inspect("ok1").contains("COMPLETED"));
        assertTrue(admin.all().contains("ok1"));
        assertEquals("Already completed",facade.continueChain("ok1",2,false,false));
    }
    @Test void failureAtB() {
        facade.park("b1","CAR123",1,true,false);
        parkingShouldBe("b1",ChainRepository.Stage.COMPENSATED,GateEntry.Status.CANCELLED,null);
        assertNull(payment.get("b1"));
        assertEquals("Already compensated",facade.continueChain("b1",1,false,false));
        admin.manualCompensate("b1");
    }
    @Test void failureAtC() {
        facade.park("c1","CAR123",1,false,true);
        parkingShouldBe("c1",ChainRepository.Stage.COMPENSATED,GateEntry.Status.CANCELLED,ParkingReservation.Status.RELEASED);
        assertNull(payment.get("c1"));
    }
    @Test void restartMidChain() {
        gate.enter("restart","ABC123");
        chain.save("restart",ChainRepository.Stage.PENDING_B);
        DiskStore reopened=new DiskStore(dir);
        GateService g2=new GateService(new GateRepository(reopened),new PlateValidator());
        SpaceService s2=new SpaceService(new SpaceRepository(reopened),new SpacePolicy(),2);
        PaymentService p2=new PaymentService(new PaymentRepository(reopened),new PaymentPolicy());
        ChainRepository c2=new ChainRepository(reopened);
        ParkingFacade f2=new ParkingFacade(g2,s2,p2,c2);
        assertTrue(f2.continueChain("restart",3,false,false).contains("COMPLETED"));
        assertEquals(ChainRepository.Stage.COMPLETED,c2.get("restart"));
    }
    @Test void restartAfterB() {
        gate.enter("restart2","ABC123");
        space.reserve("restart2");
        chain.save("restart2",ChainRepository.Stage.PENDING_C);
        ParkingFacade f2=new ParkingFacade(new GateService(new GateRepository(new DiskStore(dir)),new PlateValidator()),new SpaceService(new SpaceRepository(new DiskStore(dir)),new SpacePolicy(),2),new PaymentService(new PaymentRepository(new DiskStore(dir)),new PaymentPolicy()),new ChainRepository(new DiskStore(dir)));
        assertTrue(f2.continueChain("restart2",2,false,false).contains("COMPLETED"));
    }
    @Test void invalidInput() {
        assertThrows(IllegalArgumentException.class,()->facade.park("bad","?",1,false,false));
        assertNull(chain.get("bad"));
        assertThrows(IllegalArgumentException.class,()->facade.park("bad2","ABC123",0,false,false));
        assertNull(gate.get("bad2"));
        assertThrows(IllegalArgumentException.class,()->facade.continueChain("unknown",1,false,false));
        assertEquals("No record",admin.inspect("unknown"));
    }
    @Test void noFreeSpaces() {
        facade.park("one","ABC123",1,false,false);
        facade.park("two","DEF123",1,false,false);
        facade.park("three","GHI123",1,false,false);
        parkingShouldBe("three",ChainRepository.Stage.COMPENSATED,GateEntry.Status.CANCELLED,null);
    }
    @Test void directSubsystems() {
        gate.enter("direct","AAA111");
        assertEquals(GateEntry.Status.ALLOWED,gate.get("direct").status());
        gate.cancel("direct");
        space.reserve("spaceonly");
        assertEquals(ParkingReservation.Status.RESERVED,space.get("spaceonly").status());
        space.release("spaceonly");
        assertEquals(ParkingReservation.Status.RELEASED,space.get("spaceonly").status());
        assertEquals(600,payment.charge("payonly",3,false).amount());
        assertThrows(IllegalStateException.class,()->payment.charge("declined",1,true));
        assertEquals(Payment.Status.DECLINED,payment.get("declined").status());
    }
    @Test void duplicateAndTransitions() {
        facade.park("same","ABC123",1,false,false);
        assertThrows(IllegalArgumentException.class,()->facade.park("same","ABC123",1,false,false));
        assertThrows(IllegalStateException.class,()->admin.manualCompensate("same"));
        assertThrows(IllegalStateException.class,()->gate.get("same").allow());
        assertThrows(IllegalStateException.class,()->space.get("same").reserve());
        assertThrows(IllegalStateException.class,()->payment.get("same").pay());
    }
    @Test void manualRollbackAndPolicies() {
        gate.enter("manual","AAA111");
        chain.save("manual",ChainRepository.Stage.PENDING_B);
        admin.manualCompensate("manual");
        parkingShouldBe("manual",ChainRepository.Stage.COMPENSATED,GateEntry.Status.CANCELLED,null);
        assertThrows(IllegalArgumentException.class,()->admin.manualCompensate("missing"));
        assertThrows(IllegalArgumentException.class,()->new PaymentPolicy().fee(25));
        assertThrows(IllegalArgumentException.class,()->new PlateValidator().validate("a"));
    }
}
