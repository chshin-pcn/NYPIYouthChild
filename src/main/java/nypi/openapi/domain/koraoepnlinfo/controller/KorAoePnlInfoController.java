package nypi.openapi.domain.koraoepnlinfo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nypi.openapi.domain.common.dto.FilterOptionsDto;
import nypi.openapi.domain.common.service.impl.SearchFilterServiceImpl;
import nypi.openapi.util.UriBuilderUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.net.URI;

@Slf4j
@Controller
@RequiredArgsConstructor
public class KorAoePnlInfoController {

    private final SearchFilterServiceImpl searchService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping("/korAoePnlInfo")
    public String korAoePnlInfo(Model model) {
        String opnDataCd = "SRVY010103";
        URI uri = uriBuilderUtil.buildSearchUri(opnDataCd);

        FilterOptionsDto processedData = searchService.getProcessedFilterData(uri, opnDataCd);
        model.addAttribute("data", processedData);
        return "KorAoePnlInfo";
    }
}
