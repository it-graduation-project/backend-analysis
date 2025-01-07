package com.muse.RhyFeel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "beat_data")
@Getter
@Setter
@NoArgsConstructor
public class BeatData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long beatId;

    @Column(nullable = false)
    private Long musicId;

    @Column(name = "beat_time", nullable = false)
    private float beatTime;

    @Column(name = "beat_strength", nullable = false)
    private float beatStrength;
}

