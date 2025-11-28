package kr.re.nypi.data.domain.openapi.stdsitrpaeoepnlinfo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import kr.re.nypi.data.domain.openapi.common.dto.PagedResultDto;
import kr.re.nypi.data.domain.openapi.common.service.ApiService;
import kr.re.nypi.data.domain.openapi.korkidaoepnlinfo.dto.SurveyItemDto;
import kr.re.nypi.data.util.UriBuilderUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StdsItrpAoePnlInfoApiController {
    private final ApiService apiService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping("/stdsItrpAoePnlInfo")
    public ResponseEntity<PagedResultDto<SurveyItemDto>> stdsItrpAoePnlInfo(
            @RequestParam(defaultValue = "1") String pageNo,
            @RequestParam(defaultValue = "10") String numOfRows,
            @RequestParam String srvyYr,
            @RequestParam String srvyQitemId,
            @RequestParam(required = false) String svbnClsfCd01,
            @RequestParam(required = false) String svbnClsfCd02,
            @RequestParam(required = false) String svbnClsfCd03,
            @RequestParam(required = false, defaultValue = "N") String aiCrtYn
    ) {
        URI uri = uriBuilderUtil.buildStdsItrpAoePnlInfoUri(
                pageNo, numOfRows, srvyYr, srvyQitemId, svbnClsfCd01, svbnClsfCd02, svbnClsfCd03, aiCrtYn
        );

        PagedResultDto<SurveyItemDto> data = apiService.getSurveyResult(uri, SurveyItemDto.class);
        return ResponseEntity.ok(data);
    }
}
