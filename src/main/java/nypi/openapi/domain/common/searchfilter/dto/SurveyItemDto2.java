package nypi.openapi.domain.common.searchfilter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class SurveyItemDto2 {

    @JsonProperty("opnDataCd")
    private String surveyCode;

    @JsonProperty("cohortNm")
    private String cohortName;

    @JsonProperty("ornuNm")
    private String wave;

    @JsonProperty("srvyYr")
    private String year;

    @JsonProperty("srvyCycl")
    private String cycle;

    @JsonProperty("rspnsMnbdNm")
    private String respondent;

    @JsonProperty("ctgryId")
    private String categoryId;

    @JsonProperty("large")
    private String majorCategory;

    @JsonProperty("mid")
    private String mediumCategory;

    @JsonProperty("small")
    private String minorCategory;

    @JsonProperty("detail")
    private String detailedCategory;

    @JsonProperty("otptCtgryNm")
    private String fullCategory;

    @JsonProperty("srvyQitemId")
    private String questionId;

    @JsonProperty("cbookQitemCn")
    private String questionContent;

    // ???
    @JsonProperty("srvySn")
    private String srvySn;
}