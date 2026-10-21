package parking.gate;
public class GateService {
    private final GateRepository repo;
    private final PlateValidator validator;
    public GateService(GateRepository r,PlateValidator v) {
        repo=r;
        validator=v;
    }
    public GateEntry enter(String id,String plate) {
        if(repo.get(id)!=null)throw new IllegalArgumentException("ID already exists");
        GateEntry e=new GateEntry(id,validator.validate(plate),GateEntry.Status.NEW);
        e.allow();
        repo.save(e);
        return e;
    }
    public void cancel(String id) {
        GateEntry e=repo.get(id);
        if(e==null)throw new IllegalArgumentException("Missing gate entry");
        if(e.status()==GateEntry.Status.CANCELLED)return;
        e.cancel();
        repo.save(e);
    }
    public GateEntry get(String id) {
        return repo.get(id);
    }
}
