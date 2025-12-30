package demo.order;

import java.math.BigDecimal;
import mtaas.annotations.ArtifactEntry;

@ArtifactEntry(relationName = "order-total")
public class OrderArtifactEntry {
    public static BigDecimal total(Order order) {
        return order.items.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
