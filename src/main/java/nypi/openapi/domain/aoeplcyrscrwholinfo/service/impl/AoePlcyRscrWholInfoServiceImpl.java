package nypi.openapi.domain.aoeplcyrscrwholinfo.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nypi.openapi.domain.aoeplcyrscrwholinfo.dto.FilterItemDto;
import nypi.openapi.domain.aoeplcyrscrwholinfo.service.AoePlcyRscrWholInfoService;
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
public class AoePlcyRscrWholInfoServiceImpl extends EgovAbstractServiceImpl implements AoePlcyRscrWholInfoService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public List<FilterItemDto> getFilterData(URI uri) {
        try {
            JsonNode rootNode = restTemplate.getForObject(uri, JsonNode.class);
            if (rootNode == null || rootNode.isMissingNode()) {
                throw new IOException("외부 API 응답에 rootNode가 없거나 비어있습니다.");
            }

            JsonNode itemsNode = rootNode.at("/response/body/items/item");
            if (itemsNode.isMissingNode() || !itemsNode.isArray()) {
                throw new IOException("외부 API 응답의 items 노드가 없거나 배열이 아닙니다.");
            }

            return objectMapper.convertValue(itemsNode, new TypeReference<List<FilterItemDto>>() {
            });

        } catch (RestClientException e) {
            log.error("외부 API 호출 실패. URI: {}, 에러: {}", uri, e.getMessage());
            return Collections.emptyList();
        } catch (IOException e) {
            log.error("API 응답 데이터 처리 실패. URI: {}, 에러: {}", uri, e.getMessage());
            return Collections.emptyList();
        }
    }
}
