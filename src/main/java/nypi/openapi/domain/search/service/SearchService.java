package nypi.openapi.domain.search.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import nypi.openapi.domain.search.dto.DummyDataDto;
import nypi.openapi.domain.search.dto.ProcessedDataDto;
import nypi.openapi.domain.search.dto.SurveyItemDto;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class SearchService {

    public ProcessedDataDto getProcessedData() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File jsonFile = new ClassPathResource("static/dummy.json").getFile();

        DummyDataDto dummyData = objectMapper.readValue(jsonFile, DummyDataDto.class);
        List<SurveyItemDto> surveyItems = dummyData.getSurveyItems();

        return processSurveyItems(surveyItems);
    }

    private ProcessedDataDto processSurveyItems(List<SurveyItemDto> surveyItems) {
        // yearOrderData,respondentData,categoryMajorData,categoryMediumData,categoryMinorData,categoryDetailedData,questionData 중복 제거 및 조회 속도를 위해 해시셋으로 생성(나중에 리스트로 변환)
        Set<Map<String, String>> yearOrderData = new HashSet<>();
        Set<Map<String, String>> respondentData = new HashSet<>();
        Set<Map<String, String>> categoryMajorData = new HashSet<>();
        Set<Map<String, String>> categoryMediumData = new HashSet<>();
        Set<Map<String, String>> categoryMinorData = new HashSet<>();
        Set<Map<String, String>> categoryDetailedData = new HashSet<>();
        Set<Map<String, String>> questionData = new HashSet<>();

        // for문 시작 surveyItems를 돌려 surveyItem 하나씩
        for (SurveyItemDto surveyItem : surveyItems) {
            // surveyItem에서 연도/차수, 응답주체, 카테고리 id, 출력 카테고리명, 문항 ID, 문항 내용을 각각 변수로 저장(for문이 돌때마다 재할당)
            String yearOrder = surveyItem.getYear();
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

            // yearOrderData 연도/차수 처리(별도 함수 분리)
            processYearOrder(yearOrderData, yearOrder, id);

            // respondentData 응답주체 처리(별도 함수 분리)
            processRespondent(respondentData, respondent, id);

            // category 카테고리(대/중/소/세) 처리 (별도 함수 분리)
            processMajorCategory(categoryMajorData, majorCategory, categoryId, id);
            processMediumCategory(categoryMediumData, mediumCategory, categoryId, id);
            processMinorCategory(categoryMinorData, minorCategory, categoryId, id);
            processDetailedCategory(categoryDetailedData, detailedCategory, categoryId, id);

            // questionData 문항 처리 (별도 함수 분리)
            processQuestion(questionData, questionId, questionContent, id);
        }

        return ProcessedDataDto.builder()
                .yearOrderData(getSortedList(yearOrderData))
                .respondentData(getSortedList(respondentData))
                .categoryMajorData(getSortedList(categoryMajorData))
                .categoryMediumData(getSortedList(categoryMediumData))
                .categoryMinorData(getSortedList(categoryMinorData))
                .categoryDetailedData(getSortedList(categoryDetailedData))
                .questionData(getSortedList(questionData))
                .build();
    }

    private List<Map<String, String>> getSortedList(Set<Map<String, String>> dataSet) {
        List<Map<String, String>> list = new ArrayList<>(dataSet);
        list.sort(Comparator.comparing(m -> m.get("id")));
        return list;
    }

    private void processYearOrder(Set<Map<String, String>> yearOrderData, String yearOrder, StringBuilder id) {
        Map<String, String> tmpMap = new HashMap<>();
        tmpMap.put("parentId", null);
        id.append(yearOrder);
        tmpMap.put("id", yearOrder);
        tmpMap.put("name", yearOrder);
        yearOrderData.add(tmpMap);
    }

    private void processRespondent(Set<Map<String, String>> respondentData, String respondent, StringBuilder id) {
        Map<String, String> tmpMap = new HashMap<>();
        tmpMap.put("parentId", id.toString());
        id.append("-").append(respondent);
        tmpMap.put("id", id.toString());
        tmpMap.put("name", respondent);
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
        questionData.add(tmpMap);
    }
}
