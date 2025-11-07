package nypi.openapi.domain.aoeplcyrscrwholinfo.controller;

import lombok.RequiredArgsConstructor;
import nypi.openapi.domain.aoeplcyrscrwholinfo.dto.FilterItemDto;
import nypi.openapi.domain.aoeplcyrscrwholinfo.service.AoePlcyRscrWholInfoService;
import nypi.openapi.util.UriBuilderUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AoePlcyRscrWholInfoController {

    private final AoePlcyRscrWholInfoService aoePlcyRscrWholInfoService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping("/")
    public String redirectToAoePlcyRscrWholInfo() {
        return "redirect:/aoePlcyRscrWholInfo";
    }

    @GetMapping("/aoePlcyRscrWholInfo")
    public String aoePlcyRscrWholInfo(Model model) throws IOException {
        URI uri = uriBuilderUtil.buildWholeSearchUri();

        List<FilterItemDto> data = aoePlcyRscrWholInfoService.getFilterData(uri);
        model.addAttribute("data", data);
        return "AoePlcyRscrWholInfo";
    }
}
