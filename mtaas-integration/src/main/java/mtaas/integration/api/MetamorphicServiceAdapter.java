package mtaas.integration.api;

public interface MetamorphicServiceAdapter {
    String relationName();
    Class<?> inputType();
    boolean execute(Object model);
}
