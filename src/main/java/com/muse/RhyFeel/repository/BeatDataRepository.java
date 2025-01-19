package com.muse.RhyFeel.repository;

import com.muse.RhyFeel.model.BeatData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BeatDataRepository extends JpaRepository<BeatData, Long> {
    List<BeatData> findByMusicId(Long musicId);
}

