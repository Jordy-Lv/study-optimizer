package com.jordy.studyoptimizer.goal;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<WeeklyGoal, Long> {

    boolean existsByMetric(GoalMetric metric);
}
