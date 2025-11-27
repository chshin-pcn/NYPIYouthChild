package kr.re.nypi.data.domain.openapi.aoeplcyrscrwholinfo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AoePlcyRscrWholInfoController {

    @GetMapping("/")
    public String redirectToAoePlcyRscrWholInfo() {
        return "redirect:/aoePlcyRscrWholInfo";
    }

    @GetMapping("/aoePlcyRscrWholInfo")
    public String aoePlcyRscrWholInfo() {
        return "openapi/AoePlcyRscrWholInfo";
    }
}
