package egovframework.openapi.post;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OpenApiController {

    @GetMapping("/tbDomRegiPrclRecevSendList")
    public String tbDomRegiPrclRecevSendList() {
        return "tbDomRegiPrclRecevSendList";
    }
}
