//@RestController
//@RequestMapping("/analysis")
//public class MusicAnalysisController {
//
//    @Autowired
//    private FFTDataRepository fftDataRepository;
//
//    @Autowired
//    private BeatDataRepository beatDataRepository;
//
//    @GetMapping("/{musicId}")
//    public ResponseEntity<Map<String, Object>> getAnalysisData(
//            @PathVariable Long musicId,
//            @RequestParam(value = "target", required = false) String target) {
//
//        // FFT 데이터 가져오기
//        Optional<FFTData> fftDataOptional = fftDataRepository.findByMusicId(musicId);
//        if (fftDataOptional.isEmpty()) {
//            return ResponseEntity.status(404).body(Map.of("error", "No FFT data found for the given musicId."));
//        }
//
//        // 비트 데이터 가져오기
//        List<BeatData> beatDataList = beatDataRepository.findByMusicId(musicId);
//
//        // 데이터 경량화 (아두이노 요청인 경우)
//        if ("arduino".equalsIgnoreCase(target)) {
//            Map<String, Object> arduinoResponse = Map.of(
//                    "fftSummary", fftDataOptional.get().getSummary(),  // 요약 데이터 제공
//                    "beats", beatDataList.stream()
//                            .map(beat -> Map.of(
//                                    "time", beat.getBeatTime(),         // 타임스탬프
//                                    "intensity", beat.getBeatStrength() // 강도
//                            ))
//                            .toList()
//            );
//            return ResponseEntity.ok(arduinoResponse);
//        }
//
//        // 프론트엔드용 상세 데이터 생성
//        Map<String, Object> response = new HashMap<>();
//        response.put("fftData", fftDataOptional.get());
//        response.put("beatData", beatDataList);
//
//        return ResponseEntity.ok(response);
//    }
//}
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







