package nypi.openapi.domain.aoeplcyrscrwholinfo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nypi.openapi.domain.aoeplcyrscrwholinfo.dto.SurveyItemDto;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class GlobalSearchFilterService {

    public List<SurveyItemDto> getFilterData() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File jsonFile = new ClassPathResource("static/dummy2.json").getFile();

        JsonNode rootNode = objectMapper.readTree(jsonFile);
        JsonNode surveyItemsNode = rootNode.path("response").path("body").path("items").path("item");

        return objectMapper.convertValue(surveyItemsNode, new TypeReference<List<SurveyItemDto>>() {});
    }
}
