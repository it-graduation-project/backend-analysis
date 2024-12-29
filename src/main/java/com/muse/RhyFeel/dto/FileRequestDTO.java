package com.muse.RhyFeel.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class FileRequestDTO {
    private MultipartFile file; // 업로드할 파일
}
