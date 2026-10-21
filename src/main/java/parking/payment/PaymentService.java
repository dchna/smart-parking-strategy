package parking.payment;
import parking.pricing.*;

public class PaymentService {
    private final PaymentRepository repo;
    private final PaymentPolicy policy;
    private PriceContext priceContext;

    public PaymentService(PaymentRepository repo, PaymentPolicy policy) {
        this.repo = repo;
        this.policy = policy;
        this.priceContext = new PriceContext(new HourlyPrice());
    }

    public void setPriceStrategy(PriceStrategy strategy) {
        priceContext.setStrategy(strategy);
    }

    public String priceStrategyName() { return priceContext.strategyName(); }
    public int previewPrice(int hours) { return priceContext.calculate(hours); }

    public Payment charge(String id, int hours, boolean decline) {
        if (repo.get(id) != null) throw new IllegalArgumentException("Payment already exists");
        int amount = priceContext.calculate(hours);
        Payment payment = new Payment(id, amount, Payment.Status.NEW);
        if (decline) payment.decline(); else payment.pay();
        repo.save(payment);
        if (decline) throw new IllegalStateException("Payment declined");
        return payment;
    }

    public Payment get(String id) { return repo.get(id); }
}
