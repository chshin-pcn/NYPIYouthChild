package nypi.openapi.domain.koraoepnlinfo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nypi.openapi.domain.common.dto.PagedResultDto;
import nypi.openapi.domain.common.service.ApiService;
import nypi.openapi.domain.koraoepnlinfo.dto.SurveyItemDto;
import nypi.openapi.util.UriBuilderUtil;
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
public class KorAoePnlInfoApiController {
    private final ApiService apiService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping("/korAoePnlInfo")
    public ResponseEntity<PagedResultDto<SurveyItemDto>> korAoePnlInfo(
            @RequestParam(defaultValue = "1") String pageNo,
            @RequestParam(defaultValue = "10") String numOfRows,
            @RequestParam String srvyYr,
            @RequestParam String rspnsMnbdNm,
            @RequestParam String srvyQitemId,
            @RequestParam(required = false) String svbnClsfCd01,
            @RequestParam(required = false) String svbnClsfCd02,
            @RequestParam(required = false) String svbnClsfCd03,
            @RequestParam(required = false, defaultValue = "N") String aiCrtYn
    ) {
        URI uri = uriBuilderUtil.buildKorAoePnlInfoUri(
                pageNo, numOfRows, srvyYr, rspnsMnbdNm, srvyQitemId, svbnClsfCd01, svbnClsfCd02, svbnClsfCd03, aiCrtYn
        );

        PagedResultDto<SurveyItemDto> data = apiService.getSurveyResult(uri, SurveyItemDto.class);
        return ResponseEntity.ok(data);
    }
}
