// 경로: src/main/java/com/muse/RhyFeel/controller/FileController.java
package com.muse.RhyFeel.controller;

import com.muse.RhyFeel.dto.FileRequestDTO;
import com.muse.RhyFeel.dto.FileResponseDTO;
import com.muse.RhyFeel.model.FileEntity;
import com.muse.RhyFeel.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/upload")
public class FileController {

    // 업로드 경로
    private static final String UPLOAD_DIR = "/Users/dankim/Documents/RhyFeel/uploads/";
    //"uploads/" 추후 상대경로나 환경변수 사용하기

    @Autowired
    private FileRepository fileRepository;

    @PostMapping
    public ResponseEntity<FileResponseDTO> uploadFile(@ModelAttribute FileRequestDTO fileRequestDTO) {
        MultipartFile file = fileRequestDTO.getFile(); // DTO에서 파일 추출
        try {
            // 업로드 폴더 생성 확인
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 파일 저장
            String filePath = UPLOAD_DIR + file.getOriginalFilename();
            File savedFile = new File(filePath);
            file.transferTo(savedFile);

            // DB 저장
            FileEntity fileEntity = new FileEntity();
            fileEntity.setFileName(file.getOriginalFilename());
            fileEntity.setFilePath(filePath);
            fileEntity.setFileSize(file.getSize());
            fileRepository.save(fileEntity);

            // 성공 응답 반환
            FileResponseDTO response = new FileResponseDTO(
                    "success",
                    "File uploaded successfully",
                    file.getOriginalFilename(),
                    file.getSize()
            );
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            // 실패 응답 반환
            FileResponseDTO response = new FileResponseDTO(
                    "error",
                    "File upload failed: " + e.getMessage(),
                    null,
                    0
            );
            return ResponseEntity.status(500).body(response);
        }
    }
}
