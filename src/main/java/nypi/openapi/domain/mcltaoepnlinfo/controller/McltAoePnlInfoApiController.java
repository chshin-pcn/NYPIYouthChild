package nypi.openapi.domain.mcltaoepnlinfo.controller;

import lombok.RequiredArgsConstructor;
import nypi.openapi.domain.common.dto.PagedResultDto;
import nypi.openapi.domain.common.service.ApiService;
import nypi.openapi.domain.mcltaoepnlinfo.dto.SurveyItemDto;
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
public class McltAoePnlInfoApiController {

    private final ApiService apiService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping("/mcltAoePnlInfo")
    public ResponseEntity<PagedResultDto<SurveyItemDto>> mcltAoePnlInfo(
            @RequestParam String ornuNm,
            @RequestParam String srvyYr,
            @RequestParam String rspnsMnbdNm,
            @RequestParam String srvyQitemId,
            @RequestParam(required = false) String svbnClsfCd01,
            @RequestParam(required = false) String svbnClsfCd02,
            @RequestParam(required = false) String svbnClsfCd03,
            @RequestParam(required = false, defaultValue = "N") String aiCrtYn
    ) throws IOException {
        URI uri = uriBuilderUtil.buildMcltAoePnlInfoUri(
                ornuNm, srvyYr, rspnsMnbdNm, srvyQitemId, svbnClsfCd01, svbnClsfCd02, svbnClsfCd03, aiCrtYn
        );

        PagedResultDto<SurveyItemDto> data = apiService.getSurveyResult(uri);
        return ResponseEntity.ok(data);
    }
}
