package kr.re.nypi.data.domain.openapi.common.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.re.nypi.data.domain.openapi.common.dto.FilterItemDto;
import kr.re.nypi.data.domain.openapi.common.dto.FilterOptionsDto;
import kr.re.nypi.data.domain.openapi.common.service.SearchFilterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchFilterServiceImpl extends EgovAbstractServiceImpl implements SearchFilterService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate;

    private static final String REDIS_KEY_PREFIX = "filter:";

    @Override
    public FilterOptionsDto getProcessedFilterData(URI uri, String opnDataCd) {
        String redisKey = REDIS_KEY_PREFIX + opnDataCd;
        JsonNode surveyItemsNode;

        // 1) Redis에서 캐시 조회
        try {
            String jsonString = redisTemplate.opsForValue().get(redisKey);
            if (jsonString != null) {
                surveyItemsNode = objectMapper.readTree(jsonString);
                return convertToDto(surveyItemsNode, opnDataCd);
            }
        } catch (DataAccessException e) {
            log.error("Redis 조회 실패. key={}", redisKey);
        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 실패. key={}", redisKey);
        }

        surveyItemsNode = refreshFilterCache(uri, opnDataCd);
        if (surveyItemsNode == null) {
            return emptyFilterOptions();
        }

        // 5) JSON → FilterOptionsDto 변환
        return convertToDto(surveyItemsNode, opnDataCd);
    }

    @Override
    public JsonNode refreshFilterCache(URI uri, String opnDataCd) {
        String redisKey = REDIS_KEY_PREFIX + opnDataCd;
        JsonNode rootNode;
        JsonNode surveyItemsNode;

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
        surveyItemsNode = rootNode.at("/response/body/items/item");
        if (surveyItemsNode.isMissingNode() || !surveyItemsNode.isArray()) {
            log.error("외부 API 응답의 item 노드가 없거나 배열이 아닙니다. URI: {}", uri);
            return null;
        }

        // 4) Redis에 JSON 문자열 저장
        try {
            String jsonString = objectMapper.writeValueAsString(surveyItemsNode);
            redisTemplate.opsForValue().set(redisKey, jsonString);
        } catch (DataAccessException e) {
            log.error("Redis 저장 실패. key={}", redisKey);
        } catch (JsonProcessingException e) {
            log.error("JSON 직렬화 실패. key={}", redisKey);
        }

        return surveyItemsNode;
    }

    private FilterOptionsDto emptyFilterOptions() {
        return FilterOptionsDto.builder()
                .yearData(Collections.emptyList())
                .respondentData(Collections.emptyList())
                .categoryMajorData(Collections.emptyList())
                .categoryMediumData(Collections.emptyList())
                .categoryMinorData(Collections.emptyList())
                .categoryDetailedData(Collections.emptyList())
                .questionData(Collections.emptyList())
                .build();
    }

    private FilterOptionsDto convertToDto(JsonNode surveyItemsNode, String opnDataCd) {
        List<FilterItemDto> surveyItems =
                objectMapper.convertValue(surveyItemsNode, new TypeReference<List<FilterItemDto>>() {
                });
        return processSurveyItems(surveyItems, opnDataCd);
    }

    private FilterOptionsDto processSurveyItems(List<FilterItemDto> surveyItems, String opnDataCd) {
        boolean isWave = opnDataCd.equals("SRVY010102");
        boolean isRespondent = !opnDataCd.equals("SRVY010104") && !opnDataCd.equals("SRVY010302");

        // 각 데이터 중복 제거 및 조회 속도를 위해 해시셋으로 생성(나중에 리스트로 변환)
        Set<Map<String, String>> yearData = new HashSet<>();
        Set<Map<String, String>> respondentData = new HashSet<>();
        Set<Map<String, String>> categoryMajorData = new HashSet<>();
        Set<Map<String, String>> categoryMediumData = new HashSet<>();
        Set<Map<String, String>> categoryMinorData = new HashSet<>();
        Set<Map<String, String>> categoryDetailedData = new HashSet<>();
        Set<Map<String, String>> questionData = new HashSet<>();


        // for문 시작 surveyItems를 돌려 surveyItem 하나씩
        for (FilterItemDto surveyItem : surveyItems) {
            // surveyItem에서 기수/연도, 응답주체, 카테고리 id, 출력 카테고리명, 문항 ID, 문항 내용을 각각 변수로 저장(for문이 돌때마다 재할당)
            String wave = surveyItem.getWave();
            String year = surveyItem.getYear();
            String respondent = surveyItem.getRespondent();
            String categoryId = surveyItem.getCategoryId();
            String majorCategory = surveyItem.getMajorCategory();
            String mediumCategory = surveyItem.getMediumCategory();
            String minorCategory = surveyItem.getMinorCategory();
            String detailedCategory = surveyItem.getDetailedCategory();
            String questionId = surveyItem.getQuestionId();
            String questionContent = surveyItem.getQuestionContent();
            String aiCrtYn = surveyItem.getAiCrtYn();

            // sb라는 스트링빌더 생성(이후 문자열을 지속적으로 추가해서 사용할 예정)
            StringBuilder sb = new StringBuilder();

            // yearData 기수/연도 처리(별도 함수 분리)
            if (isWave) {
                processWaveAndYear(yearData, wave, year, aiCrtYn, sb);
            } else {
                processYear(yearData, year, aiCrtYn, sb);
            }

            // respondentData 응답주체 처리(별도 함수 분리)
            if (isRespondent) {
                processRespondent(respondentData, respondent, sb);
            }

            // category 카테고리(대/중/소/세) 처리 (별도 함수 분리)
            processMajorCategory(categoryMajorData, majorCategory, categoryId, sb);
            processMediumCategory(categoryMediumData, mediumCategory, categoryId, sb);
            processMinorCategory(categoryMinorData, minorCategory, categoryId, sb);
            processDetailedCategory(categoryDetailedData, detailedCategory, categoryId, sb);

            // questionData 문항 처리 (별도 함수 분리)
            processQuestion(questionData, questionId, questionContent, sb);
        }

        FilterOptionsDto.FilterOptionsDtoBuilder builder = FilterOptionsDto.builder()
                .yearData(getSortedList(yearData))
                .categoryMajorData(getSortedList(categoryMajorData))
                .categoryMediumData(getSortedList(categoryMediumData))
                .categoryMinorData(getSortedList(categoryMinorData))
                .categoryDetailedData(getSortedList(categoryDetailedData))
                .questionData(getSortedList(questionData));

        if (isRespondent) {
            builder.respondentData(getSortedList(respondentData));
        }

        return builder.build();
    }

    private List<Map<String, String>> getSortedList(Set<Map<String, String>> dataSet) {
        List<Map<String, String>> list = new ArrayList<>(dataSet);
        list.sort(Comparator.comparing(m -> m.get("id")));
        return list;
    }

    private void processWaveAndYear(Set<Map<String, String>> yearData, String wave, String year, String aiCrtYn, StringBuilder sb) {
        String waveYear = wave + " / " + year;
        String tmpWaveYear = waveYear + "-" + aiCrtYn;

        Map<String, String> tmpMap = new HashMap<>();
        tmpMap.put("parentId", null);
        sb.append(tmpWaveYear);
        tmpMap.put("id", tmpWaveYear);
        if (aiCrtYn.equals("Y")) {
            tmpMap.put("text", waveYear + " (AI 생성)");
        } else {
            tmpMap.put("text", waveYear);
        }
        tmpMap.put("value", tmpWaveYear);
        yearData.add(tmpMap);
    }

    private void processYear(Set<Map<String, String>> yearData, String year, String aiCrtYn, StringBuilder sb) {
        Map<String, String> tmpMap = new HashMap<>();
        String tmpYear = year + "-" + aiCrtYn;

        tmpMap.put("parentId", null);
        sb.append(tmpYear);
        tmpMap.put("id", tmpYear);
        if (aiCrtYn.equals("Y")) {
            tmpMap.put("text", year + " (AI 생성)");
        } else {
            tmpMap.put("text", year);
        }
        tmpMap.put("value", tmpYear);
        yearData.add(tmpMap);
    }

    private void processRespondent(Set<Map<String, String>> respondentData, String respondent, StringBuilder sb) {
        Map<String, String> tmpMap = new HashMap<>();
        tmpMap.put("parentId", sb.toString());
        sb.append("-").append(respondent);
        tmpMap.put("id", sb.toString());
        tmpMap.put("text", respondent);
        respondentData.add(tmpMap);
    }

    private void processMajorCategory(Set<Map<String, String>> categoryMajorData, String majorCategory, String categoryId, StringBuilder sb) {
        Map<String, String> tmpMap = new HashMap<>();
        tmpMap.put("parentId", sb.toString());

        String tmpCategoryId;
        String tmpMajorCategory;
        if (majorCategory == null || majorCategory.isEmpty() || majorCategory.equals("없음")) {
            tmpMajorCategory = "없음";
            tmpCategoryId = "없음";
        } else {
            tmpMajorCategory = majorCategory;
            int startIndex = categoryId.indexOf('A');
            tmpCategoryId = categoryId.substring(startIndex, startIndex + 5);
        }

        sb.append("-").append(tmpCategoryId);
        tmpMap.put("id", sb.toString());
        tmpMap.put("text", tmpMajorCategory);
        categoryMajorData.add(tmpMap);
    }

    private void processMediumCategory(Set<Map<String, String>> categoryMediumData, String mediumCategory, String categoryId, StringBuilder sb) {
        Map<String, String> tmpMap = new HashMap<>();
        tmpMap.put("parentId", sb.toString());

        String tmpCategoryId;
        String tmpMediumCategory;
        if (mediumCategory == null || mediumCategory.isEmpty() || mediumCategory.equals("없음")) {
            tmpMediumCategory = "없음";
            tmpCategoryId = "없음";
        } else {
            tmpMediumCategory = mediumCategory;
            int startIndex = categoryId.indexOf('B');
            tmpCategoryId = categoryId.substring(startIndex, startIndex + 5);
        }

        sb.append("-").append(tmpCategoryId);
        tmpMap.put("id", sb.toString());
        tmpMap.put("text", tmpMediumCategory);
        categoryMediumData.add(tmpMap);
    }

    private void processMinorCategory(Set<Map<String, String>> categoryMinorData, String minorCategory, String categoryId, StringBuilder sb) {
        Map<String, String> tmpMap = new HashMap<>();
        tmpMap.put("parentId", sb.toString());

        String tmpCategoryId;
        String tmpMinorCategory;
        if (minorCategory == null || minorCategory.isEmpty() || minorCategory.equals("없음")) {
            tmpMinorCategory = "없음";
            tmpCategoryId = "없음";
        } else {
            tmpMinorCategory = minorCategory;
            int startIndex = categoryId.indexOf('C');
            tmpCategoryId = categoryId.substring(startIndex, startIndex + 6);
        }

        sb.append("-").append(tmpCategoryId);
        tmpMap.put("id", sb.toString());
        tmpMap.put("text", tmpMinorCategory);
        categoryMinorData.add(tmpMap);
    }

    private void processDetailedCategory(Set<Map<String, String>> categoryDetailedData, String detailedCategory, String categoryId, StringBuilder sb) {
        Map<String, String> tmpMap = new HashMap<>();
        tmpMap.put("parentId", sb.toString());

        String tmpCategoryId;
        String tmpDetailedCategory;
        if (detailedCategory == null || detailedCategory.isEmpty() || detailedCategory.equals("없음")) {
            tmpDetailedCategory = "없음";
            tmpCategoryId = "없음";
        } else {
            tmpDetailedCategory = detailedCategory;
            int startIndex = categoryId.indexOf('D');
            tmpCategoryId = categoryId.substring(startIndex, startIndex + 6);
        }

        sb.append("-").append(tmpCategoryId);
        tmpMap.put("id", sb.toString());
        tmpMap.put("text", tmpDetailedCategory);
        categoryDetailedData.add(tmpMap);
    }

    private void processQuestion(Set<Map<String, String>> questionData, String questionId, String questionContent, StringBuilder sb) {
        Map<String, String> tmpMap = new HashMap<>();
        tmpMap.put("parentId", sb.toString());
        sb.append("-").append(questionId);
        tmpMap.put("id", sb.toString());
        tmpMap.put("text", questionContent);
        tmpMap.put("value", questionId);
        questionData.add(tmpMap);
    }
}
