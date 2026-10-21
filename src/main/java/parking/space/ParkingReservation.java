package parking.space;
public class ParkingReservation {
    public enum Status {
        NEW,RESERVED,RELEASED
    }
    private final String id,spot;
    private Status status;
    public ParkingReservation(String id,String spot,Status s) {
        this.id=id;
        this.spot=spot;
        status=s;
    }
    public String id() {
        return id;
    }
    public String spot() {
        return spot;
    }
    public Status status() {
        return status;
    }
    public void reserve() {
        if(status!=Status.NEW)throw new IllegalStateException("Reservation not NEW");
        status=Status.RESERVED;
    }
    public void release() {
        if(status!=Status.RESERVED)throw new IllegalStateException("Reservation not RESERVED");
        status=Status.RELEASED;
    }
}
