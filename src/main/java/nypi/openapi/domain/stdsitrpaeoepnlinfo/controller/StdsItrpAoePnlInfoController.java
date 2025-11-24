package nypi.openapi.domain.stdsitrpaeoepnlinfo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StdsItrpAoePnlInfoController {

    @GetMapping("/stdsItrpAoePnlInfo")
    public String stdsItrpAoePnlInfo() {
        return "StdsItrpAoePnlInfo";
    }
}
