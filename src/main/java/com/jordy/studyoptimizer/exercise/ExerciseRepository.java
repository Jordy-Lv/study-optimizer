package com.jordy.studyoptimizer.exercise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data genera la implementacion en tiempo de ejecucion a partir
 * de los nombres de los metodos (derived queries).
 */
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    List<Exercise> findByPhaseOrderByDayNumber(Integer phase);

    // Busca un reto por su numero de dia (1..30). Lo usa la integracion con
    // GitHub para mapear "Reto 9" del mensaje del commit al reto concreto.
    Optional<Exercise> findByDayNumber(Integer dayNumber);

    List<Exercise> findByDoneOrderByDayNumber(boolean done);

    // Recorre la relacion N:M: retos que tienen un concepto con ese id.
    List<Exercise> findByConcepts_IdOrderByDayNumber(Long conceptId);

    long countByDone(boolean done);

    /** Retos completados (done=true) dentro de un rango de fechas (metas semanales). */
    long countByDoneTrueAndCompletedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Fases en las que TODOS sus retos estan hechos. Agrupa por fase y se queda
     * con las que cumplen "retos hechos == retos totales".
     */
    @Query("""
            select e.phase from Exercise e
            group by e.phase
            having sum(case when e.done = true then 1L else 0L end) = count(e)
            """)
    List<Integer> findCompletedPhases();
}
