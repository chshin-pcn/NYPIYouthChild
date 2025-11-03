package nypi.openapi.domain.common.searchfilter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class SurveyItemDto {

    @JsonProperty("연도/차수")
    private String year;

    @JsonProperty("코호트명")
    private String cohortName;

    @JsonProperty("기수명")
    private String waveName;

    @JsonProperty("응답주체")
    private String respondent;

    @JsonProperty("카테고리단계")
    private int categoryStep;

    @JsonProperty("카테고리 id")
    private String categoryId;

    @JsonProperty("상위 카테고리 id")
    private String parentCategoryId;

    @JsonProperty("대분류")
    private String majorCategory;

    @JsonProperty("중분류")
    private String mediumCategory;

    @JsonProperty("소분류")
    private String minorCategory;

    @JsonProperty("세분류")
    private String detailedCategory;

    @JsonProperty("문항 ID")
    private String questionId;

    @JsonProperty("문항 내용")
    private String questionContent;
}