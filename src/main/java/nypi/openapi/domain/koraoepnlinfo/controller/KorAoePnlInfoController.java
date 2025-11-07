package nypi.openapi.domain.koraoepnlinfo.controller;

import lombok.RequiredArgsConstructor;
import nypi.openapi.domain.common.dto.FilterOptionsDto;
import nypi.openapi.domain.common.service.SearchFilterService;
import nypi.openapi.util.UriBuilderUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.net.URI;

@Controller
@RequiredArgsConstructor
public class KorAoePnlInfoController {

    private final SearchFilterService searchService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping("/korAoePnlInfo")
    public String korAoePnlInfo(Model model) throws IOException {
        String opnDataCd = "SRVY010103";
        URI uri = uriBuilderUtil.buildSearchUri(opnDataCd);

        FilterOptionsDto processedData = searchService.getProcessedFilterData(uri, opnDataCd);
        model.addAttribute("data", processedData);
        return "KorAoePnlInfo";
    }
}
