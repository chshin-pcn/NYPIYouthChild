package nypi.openapi.domain.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum SurveyType {
    SRVY010101("한국아동·청소년패널조사"),
    SRVY010102("다문화청소년패널조사"),
    SRVY010103("한국청소년패널조사"),
    SRVY010104("학업중단패널조사"),
    SRVY010301("아동·청소년인권실태조사"),
    SRVY010302("청년사회경제실태조사");

    private final String surveyName;

    public static SurveyType fromSurveyName(String surveyName) {
        return Arrays.stream(values())
                .filter(type -> type.surveyName.equals(surveyName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown survey name: " + surveyName));
    }

    public String getOpnDataCd() {
        return this.name();
    }
}
