package kr.re.nypi.data.domain.upload.service.impl;

import lombok.extern.slf4j.Slf4j;
import kr.re.nypi.data.domain.upload.service.UploadService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class UploadServiceImpl implements UploadService {

    private static final Path RAW_DATA_PATH = Paths.get("/ydo_data/RAW_DATA").toAbsolutePath().normalize();
    private static final Path SRVY_BSC_DIR = RAW_DATA_PATH.resolve("SRVY_BSC_DIR");
    private static final Path SRVY_CBOOK_DIR = RAW_DATA_PATH.resolve("SRVY_CBOOK_DIR");
    private static final Path SRVY_CTGRY_DIR = RAW_DATA_PATH.resolve("SRVY_CTGRY_DIR");
    private static final Path SRVY_RSPNS_DIR = RAW_DATA_PATH.resolve("SRVY_RSPNS_DIR");

    @Override
    public void uploadCsvFiles(List<MultipartFile> srvyBscFile, List<MultipartFile> srvyCbookFile, List<MultipartFile> srvyCtgryFile, List<MultipartFile> srvyRspnsFile) throws IOException {
        validateFiles(srvyBscFile, SRVY_BSC_DIR);
        validateFiles(srvyCbookFile, SRVY_CBOOK_DIR);
        validateFiles(srvyCtgryFile, SRVY_CTGRY_DIR);
        validateFiles(srvyRspnsFile, SRVY_RSPNS_DIR);

        storeFiles(srvyBscFile, SRVY_BSC_DIR);
        storeFiles(srvyCbookFile, SRVY_CBOOK_DIR);
        storeFiles(srvyCtgryFile, SRVY_CTGRY_DIR);
        storeFiles(srvyRspnsFile, SRVY_RSPNS_DIR);
    }

    private void validateFiles(List<MultipartFile> files, Path dirName) throws IOException {
        if (files == null || files.isEmpty()) {
            throw new IOException("파일이 없거나 비어 있습니다: " + dirName);
        }

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                throw new IOException("파일 목록에 유효하지 않은 파일이 포함되어 있습니다: " + dirName);
            }

            String originalFilename = StringUtils.cleanPath(
                    Objects.requireNonNull(file.getOriginalFilename())
            );
            if (originalFilename.contains("..")) {
                throw new IOException("파일 이름에 유효하지 않은 경로 시퀀스가 포함되어 있습니다: " + originalFilename);
            }
            if (!originalFilename.toLowerCase().endsWith(".csv")) {
                throw new IOException("CSV 파일만 가능합니다: " + originalFilename);
            }

            Path destinationFile = dirName.resolve(originalFilename).normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(dirName.toAbsolutePath())) {
                throw new IOException("대상 디렉토리 외부에 파일을 저장할 수 없습니다.");
            }
        }
    }

    private void storeFiles(List<MultipartFile> files, Path dirName) throws IOException {
        for (MultipartFile file : files) {
            String originalFilename = StringUtils.cleanPath(
                    Objects.requireNonNull(file.getOriginalFilename())
            );
            Path destination = dirName.resolve(originalFilename).toAbsolutePath().normalize();

            file.transferTo(destination);
            log.info("파일 저장됨: {}", destination);
        }
    }
}
