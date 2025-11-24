package nypi.openapi.domain.mcltaoepnlinfo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class McltAoePnlInfoController {

    @GetMapping("/mcltAoePnlInfo")
    public String mcltAoePnlInfo() {
        return "McltAoePnlInfo";
    }
}
