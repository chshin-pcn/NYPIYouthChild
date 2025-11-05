package nypi.openapi.domain.aoeplcyrscrwholinfo.controller;

import nypi.openapi.domain.aoeplcyrscrwholinfo.dto.SurveyItemDto;
import nypi.openapi.domain.aoeplcyrscrwholinfo.service.WholeSearchFilterService;
import nypi.openapi.util.UriBuilderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@Controller
public class AoePlcyRscrWholInfoController {

    @Autowired
    private WholeSearchFilterService searchService;

    @Autowired
    private UriBuilderUtil uriBuilderUtil;

    @GetMapping("/aoePlcyRscrWholInfo")
    public String aoePlcyRscrWholInfo(Model model) throws IOException {
        URI uri = uriBuilderUtil.buildWholeSearchUri();

        List<SurveyItemDto> data = searchService.getFilterData(uri);
        model.addAttribute("data", data);
        return "AoePlcyRscrWholInfo";
    }
}
