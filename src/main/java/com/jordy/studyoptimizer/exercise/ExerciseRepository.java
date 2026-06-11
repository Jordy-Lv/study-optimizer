package com.jordy.studyoptimizer.exercise;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data genera la implementacion en tiempo de ejecucion a partir
 * de los nombres de los metodos (derived queries).
 */
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    List<Exercise> findByPhaseOrderByDayNumber(Integer phase);

    // Busca un ejercicio por su numero/orden. Sirve para validar que el numero
    // sea unico al crear o editar un ejercicio.
    Optional<Exercise> findByDayNumber(Integer dayNumber);

    Optional<Exercise> findTopByOrderByDayNumberDesc();

    // Recorre la relacion N:M: ejercicios que tienen un concepto con ese id.
    List<Exercise> findByConcepts_IdOrderByDayNumber(Long conceptId);

    long countByDone(boolean done);
}
