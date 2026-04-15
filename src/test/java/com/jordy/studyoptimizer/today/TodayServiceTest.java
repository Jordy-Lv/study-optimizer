package com.jordy.studyoptimizer.today;

import com.jordy.studyoptimizer.errorlog.ErrorLogRepository;
import com.jordy.studyoptimizer.exercise.Exercise;
import com.jordy.studyoptimizer.exercise.ExerciseRepository;
import com.jordy.studyoptimizer.review.ReviewService;
import com.jordy.studyoptimizer.today.dto.TodayPlanResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TodayServiceTest {

    @Test
    void planUsesStoredExerciseEstimateWhenAvailable() {
        ReviewService reviewService = mock(ReviewService.class);
        ExerciseRepository exerciseRepository = mock(ExerciseRepository.class);
        ErrorLogRepository errorLogRepository = mock(ErrorLogRepository.class);

        Exercise exercise = new Exercise();
        exercise.setDayNumber(6);
        exercise.setTitle("Cadenas de texto");
        exercise.setPhase(2);
        exercise.setEstimatedMinutes(20);

        when(reviewService.today()).thenReturn(List.of());
        when(exerciseRepository.findByDoneOrderByDayNumber(false)).thenReturn(List.of(exercise));
        when(errorLogRepository.search("", null, false)).thenReturn(List.of());

        TodayService service = new TodayService(reviewService, exerciseRepository, errorLogRepository);

        TodayPlanResponse plan = service.plan(25, Energy.HIGH);

        assertThat(plan.plannedMinutes()).isEqualTo(20);
        assertThat(plan.items()).singleElement().satisfies(item -> {
            assertThat(item.type()).isEqualTo(StudyItemType.EXERCISE);
            assertThat(item.estimatedMinutes()).isEqualTo(20);
            assertThat(item.detail()).contains("estimacion guardada");
        });
    }
}
