package kr.re.nypi.data.domain.upload.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UploadService {
    void uploadCsvFiles(List<MultipartFile> srvyBscFile, List<MultipartFile> srvyCbookFile, List<MultipartFile> srvyCtgryFile, List<MultipartFile> srvyRspnsFile) throws IOException;
}
