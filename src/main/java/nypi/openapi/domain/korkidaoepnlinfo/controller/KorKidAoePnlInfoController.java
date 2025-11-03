package nypi.openapi.domain.korkidaoepnlinfo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KorKidAoePnlInfoController {

    @GetMapping("/korKidAoePnlInfo")
    public String korKidAoePnlInfo() {
        return "KorKidAoePnlInfo";
    }
}
