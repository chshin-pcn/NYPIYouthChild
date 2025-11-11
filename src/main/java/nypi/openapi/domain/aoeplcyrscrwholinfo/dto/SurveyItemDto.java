package nypi.openapi.domain.aoeplcyrscrwholinfo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
public class SurveyItemDto {

    @JsonProperty("opnSn")
    private String id;

    @JsonProperty("opnDataCd")
    private String surveyCode;

    @JsonProperty("otptDataSeNm")
    private String surveyName;

    @JsonProperty("cohortNm")
    private String cohort;

    @JsonProperty("ornuNm")
    private String wave;

    @JsonProperty("srvyYr")
    private String year;

    @JsonProperty("srvyExmnCycl")
    private String cycle;

    @JsonProperty("rspnsMnbdNm")
    private String respondent;

    @JsonProperty("otptCtgryNm")
    private String fullCategory;

    @JsonProperty("srvyQitemId")
    private String questionId;

    @JsonProperty("cbookQitemCn")
    private String questionContent;

    @JsonProperty("cbookAtchFileNm")
    @Setter
    private String codebookFileUrl;

    @JsonProperty("rspvlAtchFileNm")
    @Setter
    private String responseValueFileUrl;
}
