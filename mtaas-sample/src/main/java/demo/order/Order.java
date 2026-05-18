package demo.order;

import java.math.BigDecimal;
import java.util.List;

public class Order {
    public final List<BigDecimal> items;

    public Order(List<BigDecimal> items) {
        this.items = items;
    }
}
