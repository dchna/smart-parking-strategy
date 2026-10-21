package parking.payment;
public class PaymentPolicy {
    public int fee(int hours) {
        if(hours<1||hours>24)throw new IllegalArgumentException("Hours must be 1-24");
        return hours*200;
    }
}
