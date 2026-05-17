package mtaas.generated.order_total;

public final class MetamorphicFunction_order_total {
  public boolean run(demo.order.OrderGenModel model) {
    demo.order.Order input = demo.order.OrderDataGenerator.make(model);
    MetamorphicRelation_order_total relation = new MetamorphicRelation_order_total();
    return relation.validate(input);
  }
}
