package nypi.openapi.domain.common.searchfilter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class DummyDataDto {

    // TODO: 전달받는 데이터 형식에 따라 수정
    @JsonProperty("다문화청소년패널조사")
    private List<SurveyItemDto> surveyItems;

}
