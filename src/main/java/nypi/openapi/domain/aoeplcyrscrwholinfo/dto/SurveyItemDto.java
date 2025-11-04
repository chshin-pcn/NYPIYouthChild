package nypi.openapi.domain.aoeplcyrscrwholinfo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class SurveyItemDto {

    @JsonProperty("opn_data_cd")
    private String surveyCode;

    @JsonProperty("com_cd_nm")
    private String surveyName;

    @JsonProperty("ornu_nm")
    private String wave;

    @JsonProperty("srvy_yr")
    private String year;

    @JsonProperty("rspns_mnbd_nm")
    private String respondent;
}
