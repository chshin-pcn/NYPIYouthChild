package nypi.openapi.domain.common.searchfilter.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nypi.openapi.domain.common.searchfilter.dto.FilterDataDto;
import nypi.openapi.domain.common.searchfilter.dto.SurveyItemDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.*;

@Service
public class SearchFilterService {

    public FilterDataDto getProcessedFilterData(URI uri, String opnDataCd) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        RestTemplate restTemplate = new RestTemplate();

        JsonNode rootNode = restTemplate.getForObject(uri, JsonNode.class);
        JsonNode surveyItemsNode = Objects.requireNonNull(rootNode)
                .path("response")
                .path("body")
                .path("items")
                .path("item");

        List<SurveyItemDto> surveyItems = objectMapper.convertValue(surveyItemsNode, new TypeReference<List<SurveyItemDto>>() {
        });

        return processSurveyItems(surveyItems, opnDataCd);
    }

    private FilterDataDto processSurveyItems(List<SurveyItemDto> surveyItems, String opnDataCd) {
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
        for (SurveyItemDto surveyItem : surveyItems) {
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

            // id라는 스트링빌더 생성(이후 문자열을 지속적으로 추가해서 사용할 예정)
            StringBuilder id = new StringBuilder();

            // yearData 기수/연도 처리(별도 함수 분리)
            if (isWave) {
                processWaveAndYear(yearData, wave, year, id);
            } else {
                processYear(yearData, year, id);
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

        FilterDataDto.FilterDataDtoBuilder builder = FilterDataDto.builder()
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

    private void processWaveAndYear(Set<Map<String, String>> yearData, String wave, String year, StringBuilder id) {
        String waveYear = wave + " / " + year;

        Map<String, String> tmpMap = new HashMap<>();
        tmpMap.put("parentId", null);
        id.append(waveYear);
        tmpMap.put("id", waveYear);
        tmpMap.put("name", waveYear);
        tmpMap.put("value", waveYear);
        yearData.add(tmpMap);
    }

    private void processYear(Set<Map<String, String>> yearData, String year, StringBuilder id) {
        Map<String, String> tmpMap = new HashMap<>();
        tmpMap.put("parentId", null);
        id.append(year);
        tmpMap.put("id", year);
        tmpMap.put("name", year);
        tmpMap.put("value", year);
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
