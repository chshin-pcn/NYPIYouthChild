package nypi.openapi.domain.aoeplcyrscrwholinfo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import nypi.openapi.domain.aoeplcyrscrwholinfo.dto.FilterItemDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AoePlcyRscrWholInfoService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public List<FilterItemDto> getFilterData(URI uri) throws IOException {
        JsonNode rootNode = restTemplate.getForObject(uri, JsonNode.class);
        if (rootNode == null || rootNode.isMissingNode()) {
            return Collections.emptyList();
        }

        JsonNode itemsNode = rootNode.at("/response/body/items/item");
        if (itemsNode.isMissingNode() || !itemsNode.isArray()) {
            return Collections.emptyList();
        }

        return objectMapper.convertValue(itemsNode, new TypeReference<List<FilterItemDto>>() {
        });
    }
}
