package nypi.openapi.domain.korkidaoepnlinfo.controller;

import nypi.openapi.domain.common.searchfilter.dto.FilterDataDto;
import nypi.openapi.domain.common.searchfilter.service.SearchFilterService;
import nypi.openapi.util.UriBuilderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.net.URI;

@Controller
public class KorKidAoePnlInfoController {

    @Autowired
    private SearchFilterService searchService;

    @Autowired
    private UriBuilderUtil uriBuilderUtil;

    @GetMapping("/korKidAoePnlInfo")
    public String korKidAoePnlInfo(Model model) throws IOException {
        String opnDataCd = "SRVY010101";
        URI uri = uriBuilderUtil.buildSearchUri(opnDataCd);

        FilterDataDto processedData = searchService.getProcessedFilterData(uri, opnDataCd);
        model.addAttribute("data", processedData);
        return "KorKidAoePnlInfo";
    }
}
