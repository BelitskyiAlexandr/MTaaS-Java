package demo;

import java.util.Map;
import demo.textnorm.TextGenModel;
import mtaas.generated.registry.MetamorphicServiceRegistry;
import mtaas.integration.api.MetamorphicServiceAdapter;

public class TextnormIntegrationRunner {

    public static void main(String[] args) {
        Map<String, MetamorphicServiceAdapter> adapters = MetamorphicServiceRegistry.adapters();

        MetamorphicServiceAdapter adapter = adapters.get("textnorm-case");
        if (adapter == null) {
            throw new IllegalStateException("Adapter for relation textnorm-case not found");
        }

        TextGenModel model = new TextGenModel();
        model.setSentences(2);
        model.setMinTokensPerSentence(3);
        model.setMaxTokensPerSentence(5);
        model.setSeed(42L);

        boolean result = adapter.execute(model);

        System.out.println("Relation: " + adapter.relationName());
        System.out.println("Input type: " + adapter.inputType().getName());
        System.out.println("Result: " + result);
    }
}