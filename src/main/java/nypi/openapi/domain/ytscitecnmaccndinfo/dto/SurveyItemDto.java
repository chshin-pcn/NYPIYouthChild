package nypi.openapi.domain.ytscitecnmaccndinfo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class SurveyItemDto {

    @JsonProperty("opnSn")
    private String id;

    @JsonProperty("srvyYr")
    private String year;

    @JsonProperty("srvyExmnCycl")
    private String cycle;

    @JsonProperty("otptCtgryNm")
    private String fullCategory;

    @JsonProperty("svbnClsfCd")
    private String bannerVariableCode;

    @JsonProperty("svbnVrblCn")
    private String bannerVariableContent;

    @JsonProperty("srvyQitemId")
    private String questionId;

    @JsonProperty("cbookQitemCn")
    private String questionContent;

    @JsonProperty("rspvl")
    private String responseValue;

    @JsonProperty("rspnsNm")
    private String response;

    @JsonProperty("aiCrtYn")
    private String isAiCreated;

    @JsonProperty("caseCnt")
    private String count;

    @JsonProperty("freqRt")
    private String frequency;
}
