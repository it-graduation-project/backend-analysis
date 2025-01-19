package com.muse.RhyFeel.repository;

import com.muse.RhyFeel.model.FFTData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FFTDataRepository extends JpaRepository<FFTData, Long> {
    Optional<FFTData> findByMusicId(Long musicId);
}

