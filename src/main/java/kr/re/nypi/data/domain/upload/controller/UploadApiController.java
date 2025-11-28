package kr.re.nypi.data.domain.upload.controller;

import lombok.RequiredArgsConstructor;
import kr.re.nypi.data.domain.upload.service.UploadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class UploadApiController {

    private final UploadService uploadService;

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(
            @RequestParam("srvyBscFile") MultipartFile srvyBscFile,
            @RequestParam("srvyCbookFile") MultipartFile srvyCbookFile,
            @RequestParam("srvyCtgryFile") MultipartFile srvyCtgryFile,
            @RequestParam("srvyRspnsFile") MultipartFile srvyRspnsFile
    ) {
        try {
            uploadService.uploadCsvFiles(srvyBscFile, srvyCbookFile, srvyCtgryFile, srvyRspnsFile);
            return ResponseEntity.ok("파일이 성공적으로 업로드되었습니다.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패");
        }
    }
}
