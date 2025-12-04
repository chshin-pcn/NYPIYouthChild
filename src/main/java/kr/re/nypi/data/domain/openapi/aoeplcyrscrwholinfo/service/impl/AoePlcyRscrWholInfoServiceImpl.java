package kr.re.nypi.data.domain.openapi.aoeplcyrscrwholinfo.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.re.nypi.data.domain.openapi.aoeplcyrscrwholinfo.dto.FilterItemDto;
import kr.re.nypi.data.domain.openapi.aoeplcyrscrwholinfo.service.AoePlcyRscrWholInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AoePlcyRscrWholInfoServiceImpl extends EgovAbstractServiceImpl implements AoePlcyRscrWholInfoService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate;

    private static final String REDIS_KEY = "filter:whole";

    @Override
    public List<FilterItemDto> getFilterData(URI uri) {
        JsonNode itemsNode;

        // 1) Redis에서 캐시 조회
        try {
            String jsonString = redisTemplate.opsForValue().get(REDIS_KEY);
            if (jsonString != null) {
                itemsNode = objectMapper.readTree(jsonString);
                return objectMapper.convertValue(itemsNode, new TypeReference<List<FilterItemDto>>() {
                });
            }
        } catch (DataAccessException e) {
            log.error("Redis 조회 실패. key={}", REDIS_KEY);
        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 실패. key={}", REDIS_KEY);
        }

        itemsNode = refreshFilterCache(uri);
        if (itemsNode == null) {
            return Collections.emptyList();
        }
        return objectMapper.convertValue(itemsNode, new TypeReference<List<FilterItemDto>>() {
        });
    }

    @Override
    public JsonNode refreshFilterCache(URI uri) {
        JsonNode rootNode;
        JsonNode itemsNode;

        // 2) 캐시가 없으면 외부 API 호출
        try {
            rootNode = restTemplate.getForObject(uri, JsonNode.class);
        } catch (RestClientException e) {
            log.error("외부 API 호출 실패. URI: {}", uri);
            return null;
        }

        // 3) 응답 내 item 노드 검증
        if (rootNode == null || rootNode.isMissingNode()) {
            log.error("외부 API 응답에 rootNode가 없거나 비어있습니다. URI: {}", uri);
            return null;
        }
        itemsNode = rootNode.at("/response/body/items/item");
        if (itemsNode.isMissingNode() || !itemsNode.isArray()) {
            log.error("외부 API 응답의 item 노드가 없거나 배열이 아닙니다. URI: {}", uri);
            return null;
        }

        // 4) Redis에 JSON 문자열 저장
        try {
            String jsonString = objectMapper.writeValueAsString(itemsNode);
            redisTemplate.opsForValue().set(REDIS_KEY, jsonString);
        } catch (DataAccessException e) {
            log.error("Redis 저장 실패. key={}", REDIS_KEY);
        } catch (JsonProcessingException e) {
            log.error("JSON 직렬화 실패. key={}", REDIS_KEY);
        }

        return itemsNode;
    }
}
