package demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import mtaas.generated.registry.MetamorphicServiceRegistry;
import mtaas.integration.api.MetamorphicServiceAdapter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mtaas")
public class SpringMetamorphicController {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, MetamorphicServiceAdapter> adapters =
            MetamorphicServiceRegistry.adapters();

    @PostMapping("/{relationName}")
    public ResponseEntity<Boolean> run(
            @PathVariable("relationName") String relationName,
            @RequestBody String json
    ) throws Exception {

        MetamorphicServiceAdapter adapter = adapters.get(relationName);
        if (adapter == null) {
            return ResponseEntity.notFound().build();
        }

        Object model = objectMapper.readValue(json, adapter.inputType());
        boolean result = adapter.execute(model);

        return ResponseEntity.ok(result);
    }
}
