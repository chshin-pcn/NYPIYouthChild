package kr.re.nypi.data.domain.openapi.korkidaoepnlinfo.controller;

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
public class KorKidAoePnlInfoApiController {
    private final ApiService apiService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping("/korKidAoePnlInfo")
    public ResponseEntity<PagedResultDto<SurveyItemDto>> korKidAoePnlInfo(
            @RequestParam(defaultValue = "1") String pageNo,
            @RequestParam(defaultValue = "10") String numOfRows,
            @RequestParam String srvyYr,
            @RequestParam(required = false) String rspnsMnbdNm,
            @RequestParam(required = false) String large,
            @RequestParam(required = false) String mid,
            @RequestParam(required = false) String small,
            @RequestParam(required = false) String detail,
            @RequestParam(required = false) String srvyQitemId,
            @RequestParam(required = false) String svbnClsfCd01,
            @RequestParam(required = false) String svbnClsfCd02,
            @RequestParam(required = false) String svbnClsfCd03,
            @RequestParam(required = false, defaultValue = "N") String aiCrtYn
    ) {
        URI uri = uriBuilderUtil.buildKorKidAoePnlInfoUri(
                pageNo, numOfRows, srvyYr, rspnsMnbdNm, large, mid, small, detail, srvyQitemId, svbnClsfCd01, svbnClsfCd02, svbnClsfCd03, aiCrtYn
        );

        PagedResultDto<SurveyItemDto> data = apiService.getSurveyResult(uri, SurveyItemDto.class);
        return ResponseEntity.ok(data);
    }
}
