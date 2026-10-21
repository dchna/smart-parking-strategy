package parking.facade;
import parking.store.DiskStore;
import java.util.*;
public class ChainRepository {
    public enum Stage {
        PENDING_B,PENDING_C,COMPLETED,COMPENSATED
    }
    private final DiskStore store;
    public ChainRepository(DiskStore s) {
        store=s;
    }
    public void save(String id,Stage stage) {
        Properties p=new Properties();
        p.setProperty("stage",stage.name());
        store.write("chain",id,p);
    }
    public Stage get(String id) {
        Properties p=store.read("chain",id);
        return p.isEmpty()?null:Stage.valueOf(p.getProperty("stage"));
    }
    public java.util.List<String> ids() {
        return store.ids("chain");
    }
}
