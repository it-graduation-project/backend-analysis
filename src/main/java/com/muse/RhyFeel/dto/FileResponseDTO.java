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

    public FileResponseDTO(String status, String message, String fileName, long fileSize) {
        this.status = status;
        this.message = message;
        this.fileName = fileName;
        this.fileSize = fileSize;
    }
}
