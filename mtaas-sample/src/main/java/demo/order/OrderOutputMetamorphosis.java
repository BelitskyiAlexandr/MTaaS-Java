package demo.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import mtaas.annotations.OutputMetamorphosis;

@OutputMetamorphosis(relationName = "order-total")
public class OrderOutputMetamorphosis {

    public static BigDecimal addTax(BigDecimal total) {
        return total.multiply(new BigDecimal("1.20")).setScale(2, RoundingMode.HALF_UP);
    }
}
