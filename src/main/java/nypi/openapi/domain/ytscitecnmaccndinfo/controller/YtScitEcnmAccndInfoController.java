package nypi.openapi.domain.ytscitecnmaccndinfo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class YtScitEcnmAccndInfoController {

    @GetMapping("/ytScitEcnmAccndInfo")
    public String ytScitEcnmAccndInfo() {
        return "YtScitEcnmAccndInfo";
    }
}
