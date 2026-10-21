package parking.space;
import parking.store.DiskStore;
import java.util.*;
public class SpaceRepository {
    private final DiskStore store;
    public SpaceRepository(DiskStore s) {
        store=s;
    }
    public void save(ParkingReservation e) {
        Properties p=new Properties();
        p.setProperty("spot",e.spot());
        p.setProperty("status",e.status().name());
        store.write("space",e.id(),p);
    }
    public ParkingReservation get(String id) {
        Properties p=store.read("space",id);
        return p.isEmpty()?null:new ParkingReservation(id,p.getProperty("spot"),ParkingReservation.Status.valueOf(p.getProperty("status")));
    }
    public int occupied() {
        int count=0;
        for(String id:store.ids("space")) {
            if(get(id).status()==ParkingReservation.Status.RESERVED)count++;
        }
        return count;
    }
    public boolean isOccupied(String spot) {
        for(String id:store.ids("space")) {
            ParkingReservation r=get(id);
            if(r.status()==ParkingReservation.Status.RESERVED&&r.spot().equals(spot))return true;
        }
        return false;
    }
}
