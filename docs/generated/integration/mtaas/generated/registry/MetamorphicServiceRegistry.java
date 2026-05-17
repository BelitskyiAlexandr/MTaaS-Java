package mtaas.generated.registry;

import java.util.LinkedHashMap;
import java.util.Map;
import mtaas.integration.api.MetamorphicServiceAdapter;
import mtaas.generated.textnorm_whitespace.MetamorphicServiceAdapter_textnorm_whitespace;
import mtaas.generated.textnorm_duplicate.MetamorphicServiceAdapter_textnorm_duplicate;
import mtaas.generated.textnorm_case.MetamorphicServiceAdapter_textnorm_case;

/**
 * Registry of generated MTaaS service adapters
 */
public final class MetamorphicServiceRegistry {

    private MetamorphicServiceRegistry() {
    }

    public static Map<String, MetamorphicServiceAdapter> adapters() {
        Map<String, MetamorphicServiceAdapter> map = new LinkedHashMap<>();
        map.put("textnorm-whitespace", new MetamorphicServiceAdapter_textnorm_whitespace());
        map.put("textnorm-duplicate", new MetamorphicServiceAdapter_textnorm_duplicate());
        map.put("textnorm-case", new MetamorphicServiceAdapter_textnorm_case());
        return map;
    }
}
