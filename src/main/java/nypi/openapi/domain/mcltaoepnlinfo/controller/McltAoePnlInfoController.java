package nypi.openapi.domain.mcltaoepnlinfo.controller;

import nypi.openapi.domain.common.searchfilter.dto.FilterDataDto;
import nypi.openapi.domain.common.searchfilter.service.SearchFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class McltAoePnlInfoController {

    @Autowired
    private SearchFilterService searchService;

    @GetMapping("/mcltAoePnlInfo")
    public String mcltAoePnlInfo(Model model) throws IOException {
        FilterDataDto processedData = searchService.getProcessedFilterData();
        model.addAttribute("data", processedData);
        return "McltAoePnlInfo";
    }
}
