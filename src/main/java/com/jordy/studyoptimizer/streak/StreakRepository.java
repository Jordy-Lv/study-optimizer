package com.jordy.studyoptimizer.streak;

import com.jordy.studyoptimizer.session.StudySession;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio del dominio "streak" (rachas) sobre study_session.
 *
 * Traemos solo el instante de cada sesion y reducimos a fecha (dia) en Java.
 * Es deliberadamente simple: a esta escala es de sobra, y evita depender de
 * funciones de fecha del motor o de conversiones de tipos en proyecciones.
 */
public interface StreakRepository extends Repository<StudySession, Long> {

    @Query("select s.studiedAt from StudySession s")
    List<LocalDateTime> findAllStudiedAt();
}
