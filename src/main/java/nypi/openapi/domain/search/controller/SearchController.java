package nypi.openapi.domain.search.controller;

import nypi.openapi.domain.search.dto.ProcessedDataDto;
import nypi.openapi.domain.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/search")
    public String searchFilter(Model model) throws IOException {
        ProcessedDataDto processedData = searchService.getProcessedData();
        model.addAttribute("data", processedData);
        return "SearchFilter";
    }
}
