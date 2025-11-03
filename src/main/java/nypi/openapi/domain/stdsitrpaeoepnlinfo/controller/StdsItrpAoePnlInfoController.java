package nypi.openapi.domain.stdsitrpaeoepnlinfo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StdsItrpAoePnlInfoController {

    @GetMapping("/stdsItrpAoePnlInfo")
    public String stdsItrpAoePnlInfo() {
        return "StdsItrpAoePnlInfo";
    }
}
