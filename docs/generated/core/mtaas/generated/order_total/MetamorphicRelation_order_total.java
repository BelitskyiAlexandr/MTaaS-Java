package mtaas.generated.order_total;

public final class MetamorphicRelation_order_total {
  private final java.util.Comparator<java.math.BigDecimal> comparer = new demo.order.OrderOutputModelComparer();
  public boolean validate(demo.order.Order input) {
    java.math.BigDecimal output1 = demo.order.OrderOutputMetamorphosis.addTax(demo.order.OrderArtifactEntry.total(input));
    java.math.BigDecimal output2 = demo.order.OrderArtifactEntry.total(demo.order.OrderInputMetamorphosis.normalize(input));
    return comparer.compare(output1, output2) == 0;
  }
}
