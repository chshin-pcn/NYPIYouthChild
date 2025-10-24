package nypi.openapi.domain.search.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class ProcessedDataDto {
    private List<Map<String, String>> yearOrderData;
    private List<Map<String, String>> respondentData;
    private List<Map<String, String>> categoryMajorData;
    private List<Map<String, String>> categoryMediumData;
    private List<Map<String, String>> categoryMinorData;
    private List<Map<String, String>> categoryDetailedData;
    private List<Map<String, String>> questionData;
}
