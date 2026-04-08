package com.jordy.studyoptimizer.achievement;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AchievementRepository extends JpaRepository<UnlockedAchievement, Long> {

    List<UnlockedAchievement> findByCodeIn(List<AchievementCode> codes);

    boolean existsByCode(AchievementCode code);
}
