package nypi.openapi.domain.aoeplcyrscrwholinfo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AoePlcyRscrWholInfoController {

    @GetMapping("/aoePlcyRscrWholInfo")
    public String aoePlcyRscrWholInfo() {
        return "AoePlcyRscrWholInfo";
    }
}
