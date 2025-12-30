package demo.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import mtaas.annotations.DataGenerator;

@DataGenerator(relationName = "order-total")
public class OrderDataGenerator {
    public static Order make(OrderGenModel model) {
        List<BigDecimal> items = new ArrayList<>();
        for (int i = 0; i < model.itemCount; i++) {
            items.add(BigDecimal.valueOf(10 + i)); // 10, 11, 12, ...
        }
        return new Order(items);
    }
}
