package nypi.openapi.domain.aoeplcyrscrwholinfo.controller;

import lombok.RequiredArgsConstructor;
import nypi.openapi.domain.aoeplcyrscrwholinfo.dto.SurveyItemDto;
import nypi.openapi.domain.common.dto.PagedResultDto;
import nypi.openapi.domain.common.service.ApiService;
import nypi.openapi.util.UriBuilderUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.URI;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class AoePlcyRscrWholInfoApiController {

    private final ApiService apiService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping("/aoePlcyRscrWholInfo")
    public ResponseEntity<PagedResultDto<SurveyItemDto>> aoePlcyRscrWholInfo(
            @RequestParam(required = false, defaultValue = "") String searchKeyword,
            @RequestParam(required = false) String opnDataCd,
            @RequestParam(required = false) String ornuNm,
            @RequestParam(required = false) String srvyYr01,
            @RequestParam(required = false) String srvyYr02,
            @RequestParam(required = false) String rspnsMnbdNm
    ) throws IOException {
        URI uri = uriBuilderUtil.buildAoePlcyRscrWholInfoUri(
                ornuNm, opnDataCd, rspnsMnbdNm, searchKeyword, srvyYr01, srvyYr02
        );

        PagedResultDto<SurveyItemDto> data = apiService.getSurveyResult(uri);
        return ResponseEntity.ok(data);
    }
}
