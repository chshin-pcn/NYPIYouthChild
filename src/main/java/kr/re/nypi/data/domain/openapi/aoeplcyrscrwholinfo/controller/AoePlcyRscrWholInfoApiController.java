package kr.re.nypi.data.domain.openapi.aoeplcyrscrwholinfo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import kr.re.nypi.data.domain.openapi.aoeplcyrscrwholinfo.dto.SurveyItemDto;
import kr.re.nypi.data.domain.openapi.common.dto.PagedResultDto;
import kr.re.nypi.data.domain.openapi.common.service.ApiService;
import kr.re.nypi.data.util.UriBuilderUtil;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AoePlcyRscrWholInfoApiController {

    private static final Path RAW_DATA_PATH = Paths.get("/ydo_data/RAW_DATA").toAbsolutePath().normalize();
    private static final Path CODEBOOK_DIR = RAW_DATA_PATH.resolve("SRVY_CBOOK_DIR/success");
    private static final Path RESPONSE_DIR = RAW_DATA_PATH.resolve("SRVY_RSPNS_DIR/success");
    private final ApiService apiService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping("/aoePlcyRscrWholInfo")
    public ResponseEntity<PagedResultDto<SurveyItemDto>> aoePlcyRscrWholInfo(
            @RequestParam(defaultValue = "1") String pageNo,
            @RequestParam(defaultValue = "10") String numOfRows,
            @RequestParam(required = false) String searchKeyword,
            @RequestParam(required = false) String opnDataCd,
            @RequestParam(required = false) String ornuNm,
            @RequestParam(required = false) String srvyYr01,
            @RequestParam(required = false) String srvyYr02,
            @RequestParam(required = false) String rspnsMnbdNm
    ) {
        URI uri = uriBuilderUtil.buildAoePlcyRscrWholInfoUri(
                pageNo, numOfRows, ornuNm, opnDataCd, rspnsMnbdNm, searchKeyword, srvyYr01, srvyYr02
        );

        PagedResultDto<SurveyItemDto> data = apiService.getSurveyResult(uri, SurveyItemDto.class);
        transformFileUrls(data);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/files/download/codebook/{filename}")
    public ResponseEntity<Resource> downloadCodebookFile(
            @PathVariable("filename") String filename
    ) throws IOException {
        return serveFile(CODEBOOK_DIR, filename);
    }

    @GetMapping("/files/download/response/{filename}")
    public ResponseEntity<Resource> downloadResponseFile(
            @PathVariable("filename") String filename
    ) throws IOException {
        return serveFile(RESPONSE_DIR, filename);
    }

    private void transformFileUrls(PagedResultDto<SurveyItemDto> pagedResultDto) {
        if (pagedResultDto == null || pagedResultDto.getItems() == null) {
            return;
        }
        for (SurveyItemDto item : pagedResultDto.getItems()) {
            // Transform codebook file URL
            if (item.getCodebookFileUrl() != null && !item.getCodebookFileUrl().isEmpty()) {
                String filename = Paths.get(item.getCodebookFileUrl()).getFileName().toString();
                item.setCodebookFileUrl("/api/files/download/codebook/" + filename);
            }
            // Transform response value file URL
            if (item.getResponseValueFileUrl() != null && !item.getResponseValueFileUrl().isEmpty()) {
                String filename = Paths.get(item.getResponseValueFileUrl()).getFileName().toString();
                item.setResponseValueFileUrl("/api/files/download/response/" + filename);
            }
        }
    }

    private ResponseEntity<Resource> serveFile(Path baseDir, String filename) throws IOException {
        Path targetPath = baseDir.resolve(filename).normalize();

        // 경로 조작 공격 방지를 위한 보안 검사
        // targetPath가 baseDir 내에 있는지 확인
        if (!targetPath.startsWith(baseDir)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        File file = targetPath.toFile();
        if (!file.exists() || !file.isFile() || !file.canRead()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Resource resource = new FileSystemResource(file);
        String fileName = file.getName();

        // Content-Type 결정
        String contentType = Files.probeContentType(targetPath);
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // 알 수 없는 경우 기본값
        }

        // 파일명 인코딩 (Spring UriUtils 사용)
        String encodedName = UriUtils.encode(fileName, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + fileName + "\"; filename*=UTF-8''" + encodedName;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .cacheControl(CacheControl.noCache()) // 캐시 제어 추가
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }
}
