package demo.order;

import mtaas.annotations.OutputMetamorphosis;

import java.math.BigDecimal;
import java.math.RoundingMode;

@OutputMetamorphosis(relationName = "order-total")
public class OrderOutputMetamorphosis {

    public static BigDecimal addTax(BigDecimal total) {
        return total.multiply(new BigDecimal("1.20")).setScale(2, RoundingMode.HALF_UP);
    }
}
