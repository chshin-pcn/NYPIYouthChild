package nypi.openapi.domain.mcltaoepnlinfo.controller;

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
public class McltAoePnlInfoController {

    @Autowired
    private SearchFilterService searchService;

    @Autowired
    private UriBuilderUtil uriBuilderUtil;

    @GetMapping("/mcltAoePnlInfo")
    public String mcltAoePnlInfo(Model model) throws IOException {
        String opnDataCd = "SRVY010102";
        URI uri = uriBuilderUtil.buildSearchUri(opnDataCd);

        FilterDataDto processedData = searchService.getProcessedFilterData(uri, opnDataCd);
        model.addAttribute("data", processedData);
        return "McltAoePnlInfo";
    }
}
