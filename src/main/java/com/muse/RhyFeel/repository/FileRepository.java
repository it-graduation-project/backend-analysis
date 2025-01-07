package com.muse.RhyFeel.repository;
import com.muse.RhyFeel.model.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
}
