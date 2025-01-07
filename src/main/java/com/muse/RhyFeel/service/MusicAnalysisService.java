package com.muse.RhyFeel.service;

import com.muse.RhyFeel.model.BeatData;
import com.muse.RhyFeel.model.FFTData;
import com.muse.RhyFeel.repository.BeatDataRepository;
import com.muse.RhyFeel.repository.FFTDataRepository;
import org.jtransforms.fft.DoubleFFT_1D;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MusicAnalysisService {

    @Autowired
    private FFTDataRepository fftDataRepository;

    @Autowired
    private BeatDataRepository beatDataRepository;

    public void analyzeMusic(Long musicId, byte[] musicBytes) {
        double[] signal = convertToDoubleArray(musicBytes); // 오디오 데이터를 double 배열로 변환

        // FFT 수행
        DoubleFFT_1D fft = new DoubleFFT_1D(signal.length);
        fft.realForward(signal);

        // 주파수 데이터 계산
        FFTData fftData = new FFTData();
        fftData.setMusicId(musicId);
        fftData.setLowFreq(calculateFrequency(signal, 0, signal.length / 3));
        fftData.setMidFreq(calculateFrequency(signal, signal.length / 3, 2 * signal.length / 3));
        fftData.setHighFreq(calculateFrequency(signal, 2 * signal.length / 3, signal.length));
        fftDataRepository.save(fftData);

        // 비트 데이터 계산
        List<BeatData> beats = analyzeBeats(musicId, signal);
        beatDataRepository.saveAll(beats);
    }

    private double calculateFrequency(double[] signal, int start, int end) {
        double sum = 0;
        for (int i = start; i < end; i++) {
            sum += Math.abs(signal[i]);
        }
        return sum / (end - start);
    }

    private List<BeatData> analyzeBeats(Long musicId, double[] signal) {
        List<BeatData> beats = new ArrayList<>();
        for (int i = 0; i < signal.length; i += 44100) {
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
