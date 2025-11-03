package nypi.openapi.domain.koraoepnlinfo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KorAoePnlInfoController {

    @GetMapping("/korAoePnlInfo")
    public String korAoePnlInfo() {
        return "KorAoePnlInfo";
    }
}
