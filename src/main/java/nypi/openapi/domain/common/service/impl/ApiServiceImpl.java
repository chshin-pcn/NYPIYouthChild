package nypi.openapi.domain.common.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nypi.openapi.domain.common.dto.PagedResultDto;
import nypi.openapi.domain.common.service.ApiService;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiServiceImpl extends EgovAbstractServiceImpl implements ApiService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public <T> PagedResultDto<T> getSurveyResult(URI uri, Class<T> itemType) {
        try {
            JsonNode rootNode = restTemplate.getForObject(uri, JsonNode.class);

            if (rootNode == null || rootNode.isMissingNode()) {
                throw new IOException("외부 API 응답에 rootNode가 없거나 비어있습니다.");
            }

            JsonNode bodyNode = rootNode.at("/response/body");
            JsonNode itemsNode = bodyNode.path("items").path("item");

            if (itemsNode.isMissingNode() || !itemsNode.isArray()) {
                throw new IOException("외부 API 응답의 items 노드가 없거나 배열이 아닙니다.");
            }

            List<T> items = objectMapper.convertValue(itemsNode, objectMapper.getTypeFactory().constructCollectionType(List.class, itemType));
            int totalCount = bodyNode.path("totalCount").asInt();
            int numOfRows = bodyNode.path("numOfRows").asInt();
            int pageNo = bodyNode.path("pageNo").asInt();

            return PagedResultDto.<T>builder()
                    .items(items)
                    .totalCount(totalCount)
                    .numOfRows(numOfRows)
                    .pageNo(pageNo)
                    .build();

        } catch (RestClientException e) {
            log.error("외부 API 호출 실패. URI: {}, 에러: {}", uri, e.getMessage());
            return PagedResultDto.<T>builder()
                    .items(Collections.emptyList())
                    .totalCount(0)
                    .numOfRows(0)
                    .pageNo(0)
                    .build();

        } catch (IOException e) {
            log.error("API 응답 데이터 처리 실패. URI: {}, 에러: {}", uri, e.getMessage());
            return PagedResultDto.<T>builder()
                    .items(Collections.emptyList())
                    .totalCount(0)
                    .numOfRows(0)
                    .pageNo(0)
                    .build();
        }
    }
}
