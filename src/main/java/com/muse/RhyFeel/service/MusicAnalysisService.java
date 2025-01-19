//package com.muse.RhyFeel.service;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.muse.RhyFeel.model.BeatData;
//import com.muse.RhyFeel.model.FFTData;
//import com.muse.RhyFeel.repository.BeatDataRepository;
//import com.muse.RhyFeel.repository.FFTDataRepository;
//import org.jtransforms.fft.DoubleFFT_1D;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class MusicAnalysisService {
//
//    @Autowired
//    private FFTDataRepository fftDataRepository;
//
//    @Autowired
//    private BeatDataRepository beatDataRepository;
//
//    public void analyzeMusic(Long musicId, byte[] musicBytes) throws Exception {
//        // 바이트 배열 -> double 배열로 변환
//        double[] signal = convertToDoubleArray(musicBytes);
//
//        // FFT 수행 (시간 도메인 데이터를 주파수 도메인 데이터로 변환
//        DoubleFFT_1D fft = new DoubleFFT_1D(signal.length);
//        fft.realForward(signal);
//
//        // 정규화 및 주파수 데이터 저장
//        Map<String, Double> frequencyData = new HashMap<>();
//        double maxSignalValue = getMaxValue(signal);
//        for (int i = 0; i < signal.length / 2; i++) {
//            frequencyData.put(String.valueOf(i), Math.abs(signal[i]) / maxSignalValue);
//        }
//        String frequencyDataJson = new ObjectMapper().writeValueAsString(frequencyData);
//
//        FFTData fftData = new FFTData();
//        fftData.setMusicId(musicId);
//        fftData.setFrequencyData(frequencyDataJson);
//        fftDataRepository.save(fftData);
//
//        // 비트 데이터 저장
//        List<BeatData> beats = analyzeBeats(musicId, signal, maxSignalValue);
//        beatDataRepository.saveAll(beats);
//    }
//
//    private List<BeatData> analyzeBeats(Long musicId, double[] signal, double maxSignalValue) {
//        List<BeatData> beats = new ArrayList<>();
//        for (int i = 0; i < signal.length; i += 44100) { // 초당 44100 샘플 기준
//            BeatData beat = new BeatData();
//            beat.setMusicId(musicId);
//            beat.setBeatTime(i / 44100.0f);
//            beat.setBeatStrength((float) (Math.abs(signal[i]) / maxSignalValue)); // 정규화
//            beats.add(beat);
//        }
//        return beats;
//    }
//
//    private double[] convertToDoubleArray(byte[] musicBytes) {
//        double[] result = new double[musicBytes.length];
//        for (int i = 0; i < musicBytes.length; i++) {
//            result[i] = musicBytes[i];
//        }
//        return result;
//    }
//
//    private double getMaxValue(double[] signal) {
//        double max = Double.MIN_VALUE;
//        for (double value : signal) {
//            max = Math.max(max, Math.abs(value));
//        }
//        return max;
//    }
//}
//

package com.muse.RhyFeel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muse.RhyFeel.model.BeatData;
import com.muse.RhyFeel.model.FFTData;
import com.muse.RhyFeel.repository.BeatDataRepository;
import com.muse.RhyFeel.repository.FFTDataRepository;
import org.jtransforms.fft.DoubleFFT_1D;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MusicAnalysisService {

    @Autowired
    private FFTDataRepository fftDataRepository;

    @Autowired
    private BeatDataRepository beatDataRepository;

    public void analyzeMusic(Long musicId, byte[] musicBytes) throws Exception {
        // 오디오 데이터를 double 배열로 변환
        double[] signal = convertToDoubleArray(musicBytes);

        // FFT 수행
        DoubleFFT_1D fft = new DoubleFFT_1D(signal.length);
        fft.realForward(signal);

        // 모든 주파수 데이터를 JSON 형식으로 저장
        Map<String, Double> frequencyData = new HashMap<>();
        for (int i = 0; i < signal.length / 2; i++) {
            frequencyData.put(i + "Hz", Math.abs(signal[i]));
        }
        String frequencyDataJson = new ObjectMapper().writeValueAsString(frequencyData);

        // FFTData 객체 생성 및 저장
        FFTData fftData = new FFTData();
        fftData.setMusicId(musicId);
        fftData.setFrequencyData(frequencyDataJson);
        fftDataRepository.save(fftData);

        // 비트 데이터 계산 및 저장
        List<BeatData> beats = analyzeBeats(musicId, signal);
        beatDataRepository.saveAll(beats);
    }

    private List<BeatData> analyzeBeats(Long musicId, double[] signal) {
        List<BeatData> beats = new ArrayList<>();
        for (int i = 0; i < signal.length; i += 44100) { // 초당 44100 샘플 기준
            BeatData beat = new BeatData();
            beat.setMusicId(musicId);
            beat.setBeatTime(i / 44100.0f);
            beat.setBeatStrength((float) Math.abs(signal[i]));
            beats.add(beat);
        }
        return beats;
    }

    private double[] convertToDoubleArray(byte[] musicBytes) {
        double[] result = new double[musicBytes.length];
        for (int i = 0; i < musicBytes.length; i++) {
            result[i] = musicBytes[i];
        }
        return result;
    }
}
