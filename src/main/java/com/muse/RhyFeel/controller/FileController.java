package com.muse.RhyFeel.controller;

import com.muse.RhyFeel.dto.FileRequestDTO;
import com.muse.RhyFeel.dto.FileResponseDTO;
import com.muse.RhyFeel.model.FileEntity;
import com.muse.RhyFeel.repository.FileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@CrossOrigin(origins = {"http://localhost:3000", "${ngrok.url}"}) // 명확한 출처 시 허용
//@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/upload")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Value("${file.upload-dir}")
    private String uploadDir; // application.properties 참조 (환경변수)

    @Autowired
    private FileRepository fileRepository;

    @PostMapping
    public ResponseEntity<FileResponseDTO> uploadFile(@ModelAttribute FileRequestDTO fileRequestDTO) {
        MultipartFile file = fileRequestDTO.getFile(); // DTO에서 파일 추출
        String filePath = null;

        try {
            logger.info("Received file upload request: {}", file.getOriginalFilename());

            // 업로드 폴더 생성 확인
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                logger.info("Upload directory created at {}", uploadDir);
            }

            // 파일 저장
            filePath = Paths.get(uploadDir, file.getOriginalFilename()).toString();
            file.transferTo(new File(filePath));
            logger.info("File saved at {}", filePath);

            // DB 저장
            FileEntity fileEntity = new FileEntity();
            fileEntity.setFileName(file.getOriginalFilename());
            fileEntity.setFilePath(filePath);
            fileEntity.setFileSize(file.getSize());
            fileRepository.save(fileEntity);
            logger.info("File metadata saved in the database");

            // 성공 응답 반환
            return ResponseEntity.ok(new FileResponseDTO(
                    "success",
                    "File uploaded successfully",
                    file.getOriginalFilename(),
                    file.getSize()
            ));
            // 파일 저장 실패 시
        } catch (IOException e) {
            logger.error("File upload failed due to IOException: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(new FileResponseDTO(
                    "error",
                    "File upload failed due to an I/O error. Please try again.",
                    null,
                    0
            ));
            // 잘못된 입력 처리
        } catch (IllegalArgumentException e) {
            logger.error("File upload failed due to invalid input: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new FileResponseDTO(
                    "error",
                    "Invalid file upload request. Please check the file and try again.",
                    null,
                    0
            ));
            // 예상하지 못한 에러 처리
        } catch (Exception e) {
            logger.error("Unexpected error during file upload: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(new FileResponseDTO(
                    "error",
                    "An unexpected error occurred. Please contact support if the issue persists.",
                    null,
                    0
            ));
        }
    }
}
