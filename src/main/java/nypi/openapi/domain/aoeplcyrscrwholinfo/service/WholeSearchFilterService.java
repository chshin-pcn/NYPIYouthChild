package nypi.openapi.domain.aoeplcyrscrwholinfo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nypi.openapi.domain.aoeplcyrscrwholinfo.dto.SurveyItemDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Objects;

@Service
public class WholeSearchFilterService {

    public List<SurveyItemDto> getFilterData(URI uri) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        RestTemplate restTemplate = new RestTemplate();

        JsonNode rootNode = restTemplate.getForObject(uri, JsonNode.class);
        JsonNode surveyItemsNode = Objects.requireNonNull(rootNode)
                .path("response")
                .path("body")
                .path("items")
                .path("item");

        return objectMapper.convertValue(surveyItemsNode, new TypeReference<List<SurveyItemDto>>() {
        });
    }
}
