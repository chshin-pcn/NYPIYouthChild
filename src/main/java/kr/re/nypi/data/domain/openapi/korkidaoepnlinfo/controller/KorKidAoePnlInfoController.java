package kr.re.nypi.data.domain.openapi.korkidaoepnlinfo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class KorKidAoePnlInfoController {

    @GetMapping("/korKidAoePnlInfo")
    public String korKidAoePnlInfo() {
        return "openapi/KorKidAoePnlInfo";
    }
}
