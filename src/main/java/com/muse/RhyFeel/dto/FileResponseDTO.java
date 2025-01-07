package com.muse.RhyFeel.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileResponseDTO {
    private String status;
    private String message;
    private String fileName;
    private long fileSize;
    private Long musicId;

    public FileResponseDTO(String status, String message, String fileName, long fileSize) {
        this.status = status;
        this.message = message;
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    // Music ID 포함 생성자
    public FileResponseDTO(String status, String message, String fileName, long fileSize, Long musicId) {
        this.status = status;
        this.message = message;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.musicId = musicId;
    }
}
