package com.muse.RhyFeel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Column(name = "frequency_data", columnDefinition = "TEXT", nullable = false)
    private String frequencyData; // JSON 형식으로 저장
}

