package parking.payment;
public class Payment {
    public enum Status {
        NEW,PAID,DECLINED
    }
    private final String id;
    private final int amount;
    private Status status;
    public Payment(String id,int amount,Status s) {
        this.id=id;
        this.amount=amount;
        status=s;
    }
    public String id() {
        return id;
    }
    public int amount() {
        return amount;
    }
    public Status status() {
        return status;
    }
    public void pay() {
        if(status!=Status.NEW)throw new IllegalStateException("Payment not NEW");
        status=Status.PAID;
    }
    public void decline() {
        if(status!=Status.NEW)throw new IllegalStateException("Payment not NEW");
        status=Status.DECLINED;
    }
}
