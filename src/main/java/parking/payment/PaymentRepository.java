package parking.payment;
import parking.store.DiskStore;
import java.util.*;
public class PaymentRepository {
    private final DiskStore store;
    public PaymentRepository(DiskStore s) {
        store=s;
    }
    public void save(Payment e) {
        Properties p=new Properties();
        p.setProperty("amount",String.valueOf(e.amount()));
        p.setProperty("status",e.status().name());
        store.write("payment",e.id(),p);
    }
    public Payment get(String id) {
        Properties p=store.read("payment",id);
        return p.isEmpty()?null:new Payment(id,Integer.parseInt(p.getProperty("amount")),Payment.Status.valueOf(p.getProperty("status")));
    }
}
