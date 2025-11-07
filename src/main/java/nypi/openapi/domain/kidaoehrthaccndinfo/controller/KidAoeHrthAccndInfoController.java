package nypi.openapi.domain.kidaoehrthaccndinfo.controller;

import lombok.RequiredArgsConstructor;
import nypi.openapi.domain.common.dto.FilterOptionsDto;
import nypi.openapi.domain.common.service.SearchFilterService;
import nypi.openapi.util.UriBuilderUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.net.URI;

@Controller
@RequiredArgsConstructor
public class KidAoeHrthAccndInfoController {

    private final SearchFilterService searchService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping("/kidAoeHrthAccndInfo")
    public String kidAoeHrthAccndInfo(Model model) throws IOException {
        String opnDataCd = "SRVY010301";
        URI uri = uriBuilderUtil.buildSearchUri(opnDataCd);

        FilterOptionsDto processedData = searchService.getProcessedFilterData(uri, opnDataCd);
        model.addAttribute("data", processedData);
        return "KidAoeHrthAccndInfo";
    }
}
