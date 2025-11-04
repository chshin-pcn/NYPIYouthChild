package nypi.openapi.domain.aoeplcyrscrwholinfo.controller;

import nypi.openapi.domain.aoeplcyrscrwholinfo.dto.SurveyItemDto;
import nypi.openapi.domain.aoeplcyrscrwholinfo.service.GlobalSearchFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.List;

@Controller
public class AoePlcyRscrWholInfoController {

    @Autowired
    private GlobalSearchFilterService searchService;

    @GetMapping("/aoePlcyRscrWholInfo")
    public String aoePlcyRscrWholInfo(Model model) throws IOException {
        List<SurveyItemDto> data = searchService.getFilterData();
        model.addAttribute("data", data);
        return "AoePlcyRscrWholInfo";
    }
}
