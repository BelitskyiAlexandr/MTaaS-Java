package mtaas.generated.textnorm_case;

import mtaas.integration.api.MetamorphicServiceAdapter;
import demo.textnorm.TextGenModel;

public final class MetamorphicServiceAdapter_textnorm_case implements MetamorphicServiceAdapter {

    @Override
    public String relationName() {
        return "textnorm-case";
    }

    @Override
    public Class<?> inputType() {
        return TextGenModel.class;
    }

    @Override
    public boolean execute(Object model) {
        return new MetamorphicFunction_textnorm_case().run((TextGenModel) model);
    }
}
