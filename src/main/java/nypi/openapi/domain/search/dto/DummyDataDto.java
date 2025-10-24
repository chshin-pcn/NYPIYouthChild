package nypi.openapi.domain.search.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class DummyDataDto {

    @JsonProperty("다문화청소년패널조사")
    private List<SurveyItemDto> surveyItems;

}
