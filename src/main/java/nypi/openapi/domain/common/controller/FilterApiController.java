package nypi.openapi.domain.common.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nypi.openapi.domain.aoeplcyrscrwholinfo.dto.FilterItemDto;
import nypi.openapi.domain.aoeplcyrscrwholinfo.service.AoePlcyRscrWholInfoService;
import nypi.openapi.domain.common.code.SurveyType;
import nypi.openapi.domain.common.dto.FilterOptionsDto;
import nypi.openapi.domain.common.service.SearchFilterService;
import nypi.openapi.util.UriBuilderUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FilterApiController {
    private final UriBuilderUtil uriBuilderUtil;
    private final SearchFilterService searchFilterService;
    private final AoePlcyRscrWholInfoService aoePlcyRscrWholInfoService;

    @GetMapping("/filter")
    public ResponseEntity<?> getFilterOptions(
            @RequestParam(required = false) String surveyName
    ) {
        if (surveyName == null) {
            URI uri = uriBuilderUtil.buildWholeSearchUri();
            List<FilterItemDto> filterData = aoePlcyRscrWholInfoService.getFilterData(uri);
            return ResponseEntity.ok(filterData);
        } else {
            String opnDataCd = SurveyType.fromSurveyName(surveyName).getOpnDataCd();
            URI uri = uriBuilderUtil.buildSearchUri(opnDataCd);
            FilterOptionsDto processedFilterData = searchFilterService.getProcessedFilterData(uri, opnDataCd);
            return ResponseEntity.ok(processedFilterData);
        }
    }
}
