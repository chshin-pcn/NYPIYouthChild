package nypi.openapi.domain.common.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nypi.openapi.domain.common.dto.FilterItemDto;
import nypi.openapi.domain.common.dto.FilterOptionsDto;
import nypi.openapi.domain.common.service.SearchFilterService;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchFilterServiceImpl extends EgovAbstractServiceImpl implements SearchFilterService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public FilterOptionsDto getProcessedFilterData(URI uri, String opnDataCd) {
        FilterOptionsDto emptyFilterOptions = FilterOptionsDto.builder()
                .yearData(Collections.emptyList())
                .respondentData(Collections.emptyList())
                .categoryMajorData(Collections.emptyList())
                .categoryMediumData(Collections.emptyList())
                .categoryMinorData(Collections.emptyList())
                .categoryDetailedData(Collections.emptyList())
                .questionData(Collections.emptyList())
                .build();
        try {
            JsonNode rootNode = restTemplate.getForObject(uri, JsonNode.class);

            if (rootNode == null || rootNode.isMissingNode()) {
                throw new IOException("외부 API 응답에 rootNode가 없거나 비어있습니다.");
            }

            JsonNode surveyItemsNode = rootNode.at("/response/body/items/item");
            if (surveyItemsNode.isMissingNode() || !surveyItemsNode.isArray()) {
                throw new IOException("외부 API 응답의 surveyItems 노드가 없거나 배열이 아닙니다.");
            }

            List<FilterItemDto> surveyItems = objectMapper.convertValue(surveyItemsNode, new TypeReference<List<FilterItemDto>>() {
            });
            return processSurveyItems(surveyItems, opnDataCd);

        } catch (RestClientException e) {
            log.error("외부 API 호출 실패. URI: {}, 에러: {}", uri, e.getMessage());
            return emptyFilterOptions;
        } catch (IOException e) {
            log.error("API 응답 데이터 처리 실패. URI: {}, 에러: {}", uri, e.getMessage());
            return emptyFilterOptions;
        }
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

            // id라는 스트링빌더 생성(이후 문자열을 지속적으로 추가해서 사용할 예정)
            StringBuilder id = new StringBuilder();

            // yearData 기수/연도 처리(별도 함수 분리)
            if (isWave) {
                processWaveAndYear(yearData, wave, year, aiCrtYn, id);
            } else {
                processYear(yearData, year, aiCrtYn, id);
            }

            // respondentData 응답주체 처리(별도 함수 분리)
            if (isRespondent) {
                processRespondent(respondentData, respondent, id);
            }

            // category 카테고리(대/중/소/세) 처리 (별도 함수 분리)
            processMajorCategory(categoryMajorData, majorCategory, categoryId, id);
            processMediumCategory(categoryMediumData, mediumCategory, categoryId, id);
            processMinorCategory(categoryMinorData, minorCategory, categoryId, id);
            processDetailedCategory(categoryDetailedData, detailedCategory, categoryId, id);

            // questionData 문항 처리 (별도 함수 분리)
            processQuestion(questionData, questionId, questionContent, id);
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

    private void processWaveAndYear(Set<Map<String, String>> yearData, String wave, String year, String aiCrtYn, StringBuilder id) {
        String waveYear = wave + " / " + year;
        String tmpWaveYear = waveYear + "-" + aiCrtYn;

        Map<String, String> tmpMap = new HashMap<>();
        tmpMap.put("parentId", null);
        id.append(tmpWaveYear);
        tmpMap.put("id", tmpWaveYear);
        if (aiCrtYn.equals("Y")) {
            tmpMap.put("name", waveYear + " (AI 생성)");
        } else {
            tmpMap.put("name", waveYear);
        }
        tmpMap.put("value", tmpWaveYear);
        yearData.add(tmpMap);
    }

    private void processYear(Set<Map<String, String>> yearData, String year, String aiCrtYn, StringBuilder id) {
        Map<String, String> tmpMap = new HashMap<>();
        String tmpYear = year + "-" + aiCrtYn;

        tmpMap.put("parentId", null);
        id.append(tmpYear);
        tmpMap.put("id", tmpYear);
        if (aiCrtYn.equals("Y")) {
            tmpMap.put("name", year + " (AI 생성)");
        } else {
            tmpMap.put("name", year);
        }
        tmpMap.put("value", tmpYear);
        yearData.add(tmpMap);
    }

    private void processRespondent(Set<Map<String, String>> respondentData, String respondent, StringBuilder id) {
        Map<String, String> tmpMap = new HashMap<>();
        tmpMap.put("parentId", id.toString());
        id.append("-").append(respondent);
        tmpMap.put("id", id.toString());
        tmpMap.put("name", respondent);
        tmpMap.put("value", respondent);
        respondentData.add(tmpMap);
    }

    private void processMajorCategory(Set<Map<String, String>> categoryMajorData, String majorCategory, String categoryId, StringBuilder id) {
        Map<String, String> tmpMap = new HashMap<>();
        tmpMap.put("parentId", id.toString());

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

        id.append("-").append(tmpCategoryId);
        tmpMap.put("id", id.toString());
        tmpMap.put("name", tmpMajorCategory);
        categoryMajorData.add(tmpMap);
    }

    private void processMediumCategory(Set<Map<String, String>> categoryMediumData, String mediumCategory, String categoryId, StringBuilder id) {
        Map<String, String> tmpMap = new HashMap<>();
        tmpMap.put("parentId", id.toString());

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

        id.append("-").append(tmpCategoryId);
        tmpMap.put("id", id.toString());
        tmpMap.put("name", tmpMediumCategory);
        categoryMediumData.add(tmpMap);
    }

    private void processMinorCategory(Set<Map<String, String>> categoryMinorData, String minorCategory, String categoryId, StringBuilder id) {
        Map<String, String> tmpMap = new HashMap<>();
        tmpMap.put("parentId", id.toString());

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

        id.append("-").append(tmpCategoryId);
        tmpMap.put("id", id.toString());
        tmpMap.put("name", tmpMinorCategory);
        categoryMinorData.add(tmpMap);
    }

    private void processDetailedCategory(Set<Map<String, String>> categoryDetailedData, String detailedCategory, String categoryId, StringBuilder id) {
        Map<String, String> tmpMap = new HashMap<>();
        tmpMap.put("parentId", id.toString());

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

        id.append("-").append(tmpCategoryId);
        tmpMap.put("id", id.toString());
        tmpMap.put("name", tmpDetailedCategory);
        categoryDetailedData.add(tmpMap);
    }

    private void processQuestion(Set<Map<String, String>> questionData, String questionId, String questionContent, StringBuilder id) {
        Map<String, String> tmpMap = new HashMap<>();
        tmpMap.put("parentId", id.toString());
        id.append("-").append(questionId);
        tmpMap.put("id", id.toString());
        tmpMap.put("name", questionContent);
        tmpMap.put("value", questionId);
        questionData.add(tmpMap);
    }
}
