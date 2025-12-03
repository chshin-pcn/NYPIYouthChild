package kr.re.nypi.data.domain.upload.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UploadController {

    @GetMapping("/upload")
    public String uploadPage() {
        return "Upload";
    }
}
