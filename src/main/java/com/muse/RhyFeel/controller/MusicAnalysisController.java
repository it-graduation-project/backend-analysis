package com.muse.RhyFeel.controller;
import com.muse.RhyFeel.model.BeatData;
import com.muse.RhyFeel.model.FFTData;
import com.muse.RhyFeel.repository.BeatDataRepository;
import com.muse.RhyFeel.repository.FFTDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/analysis")
public class MusicAnalysisController {

    @Autowired
    private FFTDataRepository fftDataRepository;

    @Autowired
    private BeatDataRepository beatDataRepository;



    @GetMapping("/{musicId}")
    public ResponseEntity<Map<String, Object>> getAnalysisData(
            @PathVariable Long musicId,
            @RequestParam(value = "target", required = false) String target) {

        // FFT 데이터 가져오기
        Optional<FFTData> fftDataOptional = fftDataRepository.findByMusicId(musicId);
        if (fftDataOptional.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "No FFT data found for the given musicId."));
        }

        // 비트 데이터 가져오기
        List<BeatData> beatDataList = beatDataRepository.findByMusicId(musicId);

        if ("arduino".equalsIgnoreCase(target)) {
            return ResponseEntity.ok(Map.of(
                    "beats", beatDataList.stream()
                            .map(beat -> Map.of(
                                    "time", beat.getBeatTime(),
                                    "intensity", beat.getBeatStrength()
                            ))
                            .toList()
            ));
        }

        return ResponseEntity.ok(Map.of(
                "fftData", fftDataOptional.get().getFrequencyData(),
                "beatData", beatDataList
        ));
    }
}

