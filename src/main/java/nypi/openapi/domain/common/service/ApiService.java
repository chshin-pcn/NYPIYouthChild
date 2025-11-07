package nypi.openapi.domain.common.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import nypi.openapi.domain.common.dto.PagedResultDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public <T> PagedResultDto<T> getSurveyResult(URI uri) throws IOException {
        JsonNode rootNode = restTemplate.getForObject(uri, JsonNode.class);

        if (rootNode == null || rootNode.isMissingNode()) {
            return PagedResultDto.<T>builder()
                    .items(Collections.emptyList())
                    .totalCount(0)
                    .numOfRows(0)
                    .pageNo(0)
                    .build();
        }

        JsonNode bodyNode = rootNode.at("/response/body");

        int totalCount = bodyNode.path("totalCount").asInt();
        int numOfRows = bodyNode.path("numOfRows").asInt();
        int pageNo = bodyNode.path("pageNo").asInt();

        JsonNode itemsNode = bodyNode.path("items").path("item");
        List<T> items;
        if (itemsNode.isMissingNode() || !itemsNode.isArray()) {
            items = Collections.emptyList();
        } else {
            items = objectMapper.convertValue(itemsNode, new TypeReference<List<T>>() {
            });
        }

        return PagedResultDto.<T>builder()
                .items(items)
                .totalCount(totalCount)
                .numOfRows(numOfRows)
                .pageNo(pageNo)
                .build();
    }
}