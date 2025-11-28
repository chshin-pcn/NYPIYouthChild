package kr.re.nypi.data.domain.upload.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UploadService {
    void uploadCsvFiles(MultipartFile srvyBscFile, MultipartFile srvyCbookFile, MultipartFile srvyCtgryFile, MultipartFile srvyRspnsFile) throws IOException;
}
