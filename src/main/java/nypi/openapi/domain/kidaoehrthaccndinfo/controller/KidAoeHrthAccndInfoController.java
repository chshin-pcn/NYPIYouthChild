package nypi.openapi.domain.kidaoehrthaccndinfo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KidAoeHrthAccndInfoController {

    @GetMapping("/kidAoeHrthAccndInfo")
    public String kidAoeHrthAccndInfo() {
        return "KidAoeHrthAccndInfo";
    }
}
