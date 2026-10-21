package parking.gate;
public class GateEntry {
    public enum Status {
        NEW,ALLOWED,CANCELLED
    }
    private final String id,plate;
    private Status status;
    public GateEntry(String id,String plate,Status status) {
        this.id=id;
        this.plate=plate;
        this.status=status;
    }
    public String id() {
        return id;
    }
    public String plate() {
        return plate;
    }
    public Status status() {
        return status;
    }
    public void allow() {
        if(status!=Status.NEW)throw new IllegalStateException("Gate entry is not NEW");
        status=Status.ALLOWED;
    }
    public void cancel() {
        if(status!=Status.ALLOWED)throw new IllegalStateException("Gate entry is not ALLOWED");
        status=Status.CANCELLED;
    }
}
