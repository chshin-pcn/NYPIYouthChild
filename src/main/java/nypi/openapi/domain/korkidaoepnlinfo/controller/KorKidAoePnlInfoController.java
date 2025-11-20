package nypi.openapi.domain.korkidaoepnlinfo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nypi.openapi.domain.common.dto.FilterOptionsDto;
import nypi.openapi.domain.common.service.SearchFilterService;
import nypi.openapi.util.UriBuilderUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.net.URI;

@Slf4j
@Controller
@RequiredArgsConstructor
public class KorKidAoePnlInfoController {

    private final SearchFilterService searchService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping("/korKidAoePnlInfo")
    public String korKidAoePnlInfo(Model model) {
        String opnDataCd = "SRVY010101";
        URI uri = uriBuilderUtil.buildSearchUri(opnDataCd);

        FilterOptionsDto processedData = searchService.getProcessedFilterData(uri, opnDataCd);
        model.addAttribute("data", processedData);
        return "KorKidAoePnlInfo";
    }
}
