package com.jordy.studyoptimizer.session;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionRepository extends JpaRepository<StudySession, Long> {

    List<StudySession> findByExercise_IdOrderByStudiedAtDesc(Long exerciseId);

    List<StudySession> findByConcepts_IdOrderByStudiedAtDesc(Long conceptId);

    List<StudySession> findAllByOrderByStudiedAtDesc();
}
