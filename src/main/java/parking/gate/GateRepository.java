package parking.gate;
import parking.store.DiskStore;
import java.util.*;
public class GateRepository {
    private final DiskStore store;
    public GateRepository(DiskStore s) {
        store=s;
    }
    public void save(GateEntry e) {
        Properties p=new Properties();
        p.setProperty("plate",e.plate());
        p.setProperty("status",e.status().name());
        store.write("gate",e.id(),p);
    }
    public GateEntry get(String id) {
        Properties p=store.read("gate",id);
        return p.isEmpty()?null:new GateEntry(id,p.getProperty("plate"),GateEntry.Status.valueOf(p.getProperty("status")));
    }
}
