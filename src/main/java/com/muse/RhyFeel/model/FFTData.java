package com.muse.RhyFeel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Entity
@Table(name = "fft_data")
@Getter
@Setter
public class FFTData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fftId;

    @Column(nullable = false)
    private Long musicId;

    @Column(nullable = false)
    private double lowFreq;  // float -> double

    @Column(nullable = false)
    private double midFreq;  // float -> double

    @Column(nullable = false)
    private double highFreq;  // float -> double

    // 요약 데이터를 반환하는 메서드
    public Map<String, Double> getSummary() {
        return Map.of(
                "lowFreq", lowFreq,
                "midFreq", midFreq,
                "highFreq", highFreq
        );
    }
}
