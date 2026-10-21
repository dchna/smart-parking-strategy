package parking.pricing;
public interface PriceStrategy {
    int calculate(int hours);
    String name();
}
