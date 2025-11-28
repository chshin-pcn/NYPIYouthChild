package kr.re.nypi.data.domain.openapi.aoeplcyrscrwholinfo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class FilterItemDto {

    @JsonProperty("opnDataCd")
    private String surveyCode;

    @JsonProperty("otptDataSeNm")
    private String surveyName;

    @JsonProperty("ornuNm")
    private String wave;

    @JsonProperty("srvyYr")
    private String year;

    @JsonProperty("rspnsMnbdNm")
    private String respondent;
}
