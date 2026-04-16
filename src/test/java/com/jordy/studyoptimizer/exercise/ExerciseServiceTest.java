package com.jordy.studyoptimizer.exercise;

import com.jordy.studyoptimizer.common.exception.DuplicateResourceException;
import com.jordy.studyoptimizer.concept.ConceptService;
import com.jordy.studyoptimizer.exercise.dto.CreateExerciseRequest;
import com.jordy.studyoptimizer.exercise.dto.ExerciseResponse;
import com.jordy.studyoptimizer.exercise.dto.UpdateExerciseRequest;
import com.jordy.studyoptimizer.session.SessionRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExerciseServiceTest {

    private final ExerciseRepository repository = mock(ExerciseRepository.class);
    private final ConceptService conceptService = mock(ConceptService.class);
    private final SessionRepository sessionRepository = mock(SessionRepository.class);
    private final ExerciseService service = new ExerciseService(repository, conceptService, sessionRepository);

    @Test
    void createAssignsNextDayNumberWhenMissing() {
        Exercise last = new Exercise();
        last.setDayNumber(30);

        when(repository.findTopByOrderByDayNumberDesc()).thenReturn(Optional.of(last));
        when(repository.findByDayNumber(31)).thenReturn(Optional.empty());
        when(repository.save(any(Exercise.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateExerciseRequest request = new CreateExerciseRequest(
                null,
                "Mi reto personal",
                "Practicar APIs REST",
                null,
                45,
                null);

        ExerciseResponse created = service.create(request);

        assertThat(created.dayNumber()).isEqualTo(31);
        assertThat(created.phase()).isEqualTo(1);
        assertThat(created.estimatedMinutes()).isEqualTo(45);
        assertThat(created.title()).isEqualTo("Mi reto personal");
    }

    @Test
    void createRejectsDuplicateDayNumber() {
        Exercise existing = new Exercise();
        existing.setId(99L);
        existing.setDayNumber(7);

        when(repository.findByDayNumber(7)).thenReturn(Optional.of(existing));

        CreateExerciseRequest request = new CreateExerciseRequest(
                7,
                "Otro reto",
                null,
                2,
                null,
                null);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("numero 7");
    }

    @Test
    void updateKeepsCurrentDayNumberAndPhaseWhenMissing() {
        Exercise existing = new Exercise();
        existing.setId(10L);
        existing.setDayNumber(12);
        existing.setTitle("Viejo titulo");
        existing.setPhase(3);

        when(repository.findById(10L)).thenReturn(Optional.of(existing));
        when(repository.findByDayNumber(12)).thenReturn(Optional.of(existing));

        UpdateExerciseRequest request = new UpdateExerciseRequest(
                null,
                "Titulo actualizado",
                "Nueva descripcion",
                null);

        ExerciseResponse updated = service.update(10L, request);

        assertThat(updated.dayNumber()).isEqualTo(12);
        assertThat(updated.phase()).isEqualTo(3);
        assertThat(updated.title()).isEqualTo("Titulo actualizado");
        assertThat(updated.description()).isEqualTo("Nueva descripcion");
    }
}
