package nypi.openapi.domain.mcltaoepnlinfo.controller;

import nypi.openapi.domain.common.searchfilter.dto.ProcessedDataDto;
import nypi.openapi.domain.common.searchfilter.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class McltAoePnlInfoController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/mcltAoePnlInfo")
    public String mcltAoePnlInfo(Model model) throws IOException {
        ProcessedDataDto processedData = searchService.getProcessedData();
        model.addAttribute("data", processedData);
        return "McltAoePnlInfo";
    }
}
