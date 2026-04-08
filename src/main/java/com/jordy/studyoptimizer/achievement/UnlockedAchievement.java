package com.jordy.studyoptimizer.achievement;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Un logro YA desbloqueado. Solo persistimos esto; las reglas (que hace falta
 * para desbloquearlo) viven en el enum AchievementCode.
 */
@Entity
@Table(name = "unlocked_achievement")
@Getter
@Setter
@NoArgsConstructor
public class UnlockedAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 50)
    private AchievementCode code;

    @Column(name = "unlocked_at", nullable = false)
    private LocalDateTime unlockedAt;

    public UnlockedAchievement(AchievementCode code) {
        this.code = code;
    }

    @PrePersist
    void onCreate() {
        if (unlockedAt == null) {
            unlockedAt = LocalDateTime.now();
        }
    }
}
