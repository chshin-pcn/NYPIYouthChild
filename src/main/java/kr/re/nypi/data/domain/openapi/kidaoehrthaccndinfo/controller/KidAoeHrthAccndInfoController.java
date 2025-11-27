package kr.re.nypi.data.domain.openapi.kidaoehrthaccndinfo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class KidAoeHrthAccndInfoController {

    @GetMapping("/kidAoeHrthAccndInfo")
    public String kidAoeHrthAccndInfo() {
        return "openapi/KidAoeHrthAccndInfo";
    }
}
