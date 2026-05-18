package demo.order;

import java.math.BigDecimal;
import java.util.Comparator;
import mtaas.annotations.OutputModelComparer;

@OutputModelComparer(relationName = "order-total")
public class OrderOutputModelComparer implements Comparator<BigDecimal> {
    @Override
    public int compare(BigDecimal a, BigDecimal b) {
        return a.compareTo(b);
    }
}
