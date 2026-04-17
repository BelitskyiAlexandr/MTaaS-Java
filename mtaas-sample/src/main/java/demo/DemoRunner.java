package demo;

import demo.convexhull.generator.HullGenModel;
import demo.order.OrderGenModel;
import mtaas.generated.hull_permute.MetamorphicFunction_hull_permute;
import mtaas.generated.hull_rotate.MetamorphicFunction_hull_rotate;
import mtaas.generated.hull_scale.MetamorphicFunction_hull_scale;
import mtaas.generated.hull_translate.MetamorphicFunction_hull_translate;
import mtaas.generated.order_total.MetamorphicFunction_order_total;

public class DemoRunner {
    public static void main(String[] args) {
        OrderGenModel model = new OrderGenModel(3);
        MetamorphicFunction_order_total fn = new MetamorphicFunction_order_total();
        boolean ok = fn.run(model);

        System.out.println("Relation order-total result: " + ok);

        MetamorphicFunction_hull_permute hp = new MetamorphicFunction_hull_permute();
        MetamorphicFunction_hull_rotate hr = new MetamorphicFunction_hull_rotate();
        MetamorphicFunction_hull_scale hs = new MetamorphicFunction_hull_scale();
        MetamorphicFunction_hull_translate ht = new MetamorphicFunction_hull_translate();
        boolean hp_ok = hp.run(new HullGenModel(45L));
        System.out.println("hull-permute: " + hp_ok);
        boolean hr_ok = hr.run(new HullGenModel(44L));
        System.out.println("hull-rotate: " + hr_ok);
        boolean hs_ok = hs.run(new HullGenModel(43L));
        System.out.println("hull-scale: " + hs_ok);
        boolean ht_ok = ht.run(new HullGenModel(42L));
        System.out.println("hull-translate: " + ht_ok);
    }
}
