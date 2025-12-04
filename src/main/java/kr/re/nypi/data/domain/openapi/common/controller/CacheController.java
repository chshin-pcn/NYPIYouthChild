package kr.re.nypi.data.domain.openapi.common.controller;

import com.fasterxml.jackson.databind.JsonNode;
import kr.re.nypi.data.domain.openapi.aoeplcyrscrwholinfo.service.AoePlcyRscrWholInfoService;
import kr.re.nypi.data.domain.openapi.common.code.SurveyType;
import kr.re.nypi.data.domain.openapi.common.service.SearchFilterService;
import kr.re.nypi.data.util.UriBuilderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CacheController {

    private final UriBuilderUtil uriBuilderUtil;
    private final SearchFilterService searchFilterService;
    private final AoePlcyRscrWholInfoService aoePlcyRscrWholInfoService;

    @GetMapping("/cache/refresh")
    public ResponseEntity<?> refreshAllCaches() {
        URI uri;
        JsonNode jsonNode;
        String opnDataCd;
        String surveyName;

        for (SurveyType surveyType : SurveyType.values()) {
            opnDataCd = surveyType.getOpnDataCd();
            surveyName = surveyType.getSurveyName();

            uri = uriBuilderUtil.buildSearchUri(opnDataCd);
            jsonNode = searchFilterService.refreshFilterCache(uri, opnDataCd);
            if (jsonNode == null) {
                log.error("캐시 갱신 중 예외 발생. surveyName={}", surveyName);
                return ResponseEntity.internalServerError().build();
            }
        }

        uri = uriBuilderUtil.buildWholeSearchUri();
        jsonNode = aoePlcyRscrWholInfoService.refreshFilterCache(uri);
        if (jsonNode == null) {
            log.error("캐시 갱신 중 예외 발생. surveyName=아동·청소년·청년 데이터 통합검색");
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok().build();
    }
}
