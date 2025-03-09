package com.muse.RhyFeel.controller;

import com.muse.RhyFeel.dto.FileRequestDTO;
import com.muse.RhyFeel.dto.FileResponseDTO;
import com.muse.RhyFeel.model.FileEntity;
import com.muse.RhyFeel.model.FFTData;
import com.muse.RhyFeel.model.BeatData;
import com.muse.RhyFeel.repository.FileRepository;
import com.muse.RhyFeel.repository.FFTDataRepository;
import com.muse.RhyFeel.repository.BeatDataRepository;
import com.muse.RhyFeel.service.MusicAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
//
//@CrossOrigin(origins = {"http://localhost:3000", "${NGROK_URL}"})
//@RestController
//@RequestMapping("/files")
//public class FileController {
//
//    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
//
//    @Value("${FILE_UPLOAD_DIR}")
//    private String uploadDir;
//
//    @Autowired
//    private FileRepository fileRepository;
//
//    @Autowired
//    private MusicAnalysisService musicAnalysisService;
//
//    @PostMapping
//    public ResponseEntity<FileResponseDTO> uploadFile(@ModelAttribute FileRequestDTO fileRequestDTO) {
//        MultipartFile file = fileRequestDTO.getFile();
//        String filePath = null;
//
//        try {
//            logger.info("Received file upload request: {}", file.getOriginalFilename());
//
//            // 업로드 폴더 생성 확인
//            Path uploadPath = Paths.get(uploadDir);
//            if (!Files.exists(uploadPath)) {
//                Files.createDirectories(uploadPath);
//                logger.info("Upload directory created at {}", uploadDir);
//            }
//
//            // 파일 저장
//            filePath = Paths.get(uploadDir, file.getOriginalFilename()).toString();
//            file.transferTo(new File(filePath));
//            logger.info("File saved at {}", filePath);
//
//            // DB 저장
//            FileEntity fileEntity = new FileEntity();
//            fileEntity.setFileName(file.getOriginalFilename());
//            fileEntity.setFilePath(filePath);
//            fileEntity.setFileSize(file.getSize());
//            fileRepository.save(fileEntity);
//            logger.info("File metadata saved in the database");
//
//            // 음악 분석 수행
//            musicAnalysisService.analyzeMusic(fileEntity.getId(), Files.readAllBytes(Paths.get(filePath)));
//
//            // 성공 응답 반환
//            return ResponseEntity.ok(new FileResponseDTO(
//                    "success",
//                    "File uploaded and analyzed successfully",
//                    file.getOriginalFilename(),
//                    file.getSize(),
//                    fileEntity.getId()
//            ));
//        } catch (IOException e) {
//            logger.error("File upload failed due to IOException: {}", e.getMessage(), e);
//            return ResponseEntity.status(500).body(new FileResponseDTO(
//                    "error",
//                    "File upload failed due to an I/O error. Please try again.",
//                    null,
//                    0,
//                    null
//            ));
//        } catch (IllegalArgumentException e) {
//            logger.error("File upload failed due to invalid input: {}", e.getMessage(), e);
//            return ResponseEntity.badRequest().body(new FileResponseDTO(
//                    "error",
//                    "Invalid file upload request. Please check the file and try again.",
//                    null,
//                    0,
//                    null
//            ));
//        } catch (Exception e) {
//            logger.error("Unexpected error during file upload: {}", e.getMessage(), e);
//            return ResponseEntity.status(500).body(new FileResponseDTO(
//                    "error",
//                    "An unexpected error occurred. Please contact support if the issue persists.",
//                    null,
//                    0,
//                    null
//            ));
//        }
//    }
//}

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:3000", "${NGROK_URL}"})
@RestController
@RequestMapping("/files")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Value("${FILE_UPLOAD_DIR}")
    private String uploadDir;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private MusicAnalysisService musicAnalysisService;

    @Autowired
    private FFTDataRepository fftDataRepository;

    @Autowired
    private BeatDataRepository beatDataRepository;

    @PostMapping
    public ResponseEntity<Map<String, Object>> uploadFile(@ModelAttribute FileRequestDTO fileRequestDTO) {
        MultipartFile file = fileRequestDTO.getFile();
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

            // 음악 분석 수행
            musicAnalysisService.analyzeMusic(fileEntity.getId(), Files.readAllBytes(Paths.get(filePath)));

            // 분석된 데이터 가져오기
            FFTData fftData = fftDataRepository.findByMusicId(fileEntity.getId())
                    .orElseThrow(() -> new IllegalStateException("FFT data not found for musicId: " + fileEntity.getId()));
            List<BeatData> beats = beatDataRepository.findByMusicId(fileEntity.getId());

            // 성공 응답 반환 (Gzip은 자동 적용)
            return ResponseEntity.ok(Map.of(
                    "status", "success",  // 요청 상태
                    "message", "File uploaded and analyzed successfully",  // 성공 메시지
                    "fileName", file.getOriginalFilename(),  // 파일 이름
                    "fileSize", file.getSize(),  // 파일 크기
                    "musicId", fileEntity.getId(),  // 음악 ID
                    "analysis", Map.of(  // 분석 데이터
                            "fftData", fftData.getFrequencyData(),  // 주파수 데이터
                            "beatData", beats  // 비트 데이터
                    )
            ));
        } catch (IOException e) {
            logger.error("File upload failed due to IOException: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                    "status", "error",
                    "message", "File upload failed due to an I/O error. Please try again."
            ));
        } catch (Exception e) {
            logger.error("Unexpected error during file upload: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                    "status", "error",
                    "message", "An unexpected error occurred. Please contact support if the issue persists."
            ));
        }
    }
}