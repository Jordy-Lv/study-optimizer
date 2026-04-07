package com.jordy.studyoptimizer.analytics;

import com.jordy.studyoptimizer.analytics.projection.ExerciseStatsProjection;
import com.jordy.studyoptimizer.analytics.projection.SummaryProjection;
import com.jordy.studyoptimizer.analytics.projection.WeeklyStatsProjection;
import com.jordy.studyoptimizer.session.StudySession;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

/**
 * Repositorio dedicado a las consultas de analitica sobre study_session.
 * Spring permite varios repositorios para una misma entidad; asi el dominio
 * "analytics" tiene sus propias queries sin ensuciar el de "session".
 *
 * Usamos queries NATIVAS (SQL de PostgreSQL) porque necesitamos funciones de
 * fecha como date_trunc('week', ...), que no existen en JPQL. nativeQuery=true.
 * Los resultados se mapean a proyecciones por el nombre de cada columna (alias).
 */
public interface AnalyticsRepository extends Repository<StudySession, Long> {

    @Query(value = """
            SELECT COALESCE(SUM(minutes), 0)                  AS totalMinutes,
                   COUNT(*)                                   AS totalSessions,
                   COUNT(DISTINCT studied_at::date)           AS distinctDays,
                   CAST(AVG(difficulty) AS double precision)  AS avgDifficulty
            FROM study_session
            """, nativeQuery = true)
    SummaryProjection summary();

    @Query(value = """
            SELECT CAST(date_trunc('week', studied_at) AS date) AS weekStart,
                   COALESCE(SUM(minutes), 0)                     AS totalMinutes,
                   COUNT(*)                                      AS sessions,
                   CAST(AVG(difficulty) AS double precision)     AS avgDifficulty
            FROM study_session
            GROUP BY date_trunc('week', studied_at)
            ORDER BY weekStart DESC
            """, nativeQuery = true)
    List<WeeklyStatsProjection> weekly();

    @Query(value = """
            SELECT e.id                       AS exerciseId,
                   e.day_number               AS dayNumber,
                   e.title                    AS title,
                   COALESCE(SUM(s.minutes),0) AS totalMinutes,
                   COUNT(*)                   AS sessions
            FROM study_session s
            JOIN exercise e ON e.id = s.exercise_id
            GROUP BY e.id, e.day_number, e.title
            ORDER BY totalMinutes DESC, e.day_number
            """, nativeQuery = true)
    List<ExerciseStatsProjection> byExercise();
}
