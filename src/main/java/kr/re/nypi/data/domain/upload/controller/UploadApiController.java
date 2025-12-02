package kr.re.nypi.data.domain.upload.controller;

import kr.re.nypi.data.domain.upload.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class UploadApiController {

    private final UploadService uploadService;
    private final RestTemplate restTemplate;

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(
            @RequestParam("srvyBscFile") List<MultipartFile> srvyBscFile,
            @RequestParam("srvyCbookFile") List<MultipartFile> srvyCbookFile,
            @RequestParam("srvyCtgryFile") List<MultipartFile> srvyCtgryFile,
            @RequestParam("srvyRspnsFile") List<MultipartFile> srvyRspnsFile
    ) {
        try {
            uploadService.uploadCsvFiles(srvyBscFile, srvyCbookFile, srvyCtgryFile, srvyRspnsFile);
            return ResponseEntity.ok("파일이 성공적으로 업로드되었습니다.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패");
        }
    }

    @GetMapping("/trigger-hop")
    public ResponseEntity<String> triggerHop(@RequestParam("USER_MAIL") String userMail) {
        String url = "http://211.205.54.19:8092/hop/asyncRun/?service=trigger&USER_MAIL=" + userMail;

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("pcn", "Pcn2025!@");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("HOP API 호출 실패");
        }
    }
}
