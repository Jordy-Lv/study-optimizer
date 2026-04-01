package com.jordy.studyoptimizer.session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SessionRepository extends JpaRepository<StudySession, Long> {

    List<StudySession> findByExercise_IdOrderByStudiedAtDesc(Long exerciseId);

    List<StudySession> findByConcepts_IdOrderByStudiedAtDesc(Long conceptId);

    List<StudySession> findAllByOrderByStudiedAtDesc();

    /** Cuantas sesiones cayeron en un rango de fechas (para metas semanales). */
    long countByStudiedAtBetween(LocalDateTime start, LocalDateTime end);

    /** Suma de minutos estudiados en un rango. coalesce → 0 si no hubo sesiones. */
    @Query("select coalesce(sum(s.minutes), 0) from StudySession s "
            + "where s.studiedAt between :start and :end")
    long sumMinutesBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /** Minutos reales acumulados en las sesiones de un reto (milestone 8: estimado vs real). */
    @Query("select coalesce(sum(s.minutes), 0) from StudySession s where s.exercise.id = :exerciseId")
    long sumMinutesByExerciseId(@Param("exerciseId") Long exerciseId);

    /**
     * Minutos reales por reto, agrupados, para comparar varios retos sin caer en N+1.
     * Devuelve proyecciones de interfaz (ExerciseMinutesView); el service las pasa a un Map.
     */
    @Query("select s.exercise.id as exerciseId, coalesce(sum(s.minutes), 0) as minutes "
            + "from StudySession s where s.exercise.id is not null group by s.exercise.id")
    List<ExerciseMinutesView> sumMinutesGroupedByExercise();
}
