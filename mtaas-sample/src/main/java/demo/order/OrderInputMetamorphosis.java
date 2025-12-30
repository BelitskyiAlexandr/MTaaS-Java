package demo.order;

import mtaas.annotations.InputMetamorphosis;

@InputMetamorphosis(relationName = "order-total")
public class OrderInputMetamorphosis {
    public static Order normalize(Order o) {
        return o;
    }
}
