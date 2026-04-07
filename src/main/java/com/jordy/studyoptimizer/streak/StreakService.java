package com.jordy.studyoptimizer.streak;

import com.jordy.studyoptimizer.streak.dto.StreakResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StreakService {

    private final StreakRepository repository;

    public StreakService(StreakRepository repository) {
        this.repository = repository;
    }

    public StreakResponse compute() {
        // 1) Reducir todas las sesiones a un conjunto ORDENADO de dias distintos.
        //    TreeSet -> ordenado ascendente y sin duplicados automaticamente.
        NavigableSet<LocalDate> days = repository.findAllStudiedAt().stream()
                .map(LocalDateTime::toLocalDate)
                .collect(Collectors.toCollection(TreeSet::new));

        if (days.isEmpty()) {
            return new StreakResponse(0, 0, 0, null, false);
        }

        LocalDate today = LocalDate.now();
        boolean studiedToday = days.contains(today);

        // 2) Racha actual: contar hacia atras desde hoy (o ayer, gracia de 1 dia).
        int current = 0;
        LocalDate cursor = studiedToday
                ? today
                : (days.contains(today.minusDays(1)) ? today.minusDays(1) : null);
        while (cursor != null && days.contains(cursor)) {
            current++;
            cursor = cursor.minusDays(1);
        }

        // 3) Racha mas larga: recorrer los dias en orden y medir tramos seguidos.
        int longest = 0;
        int run = 0;
        LocalDate prev = null;
        for (LocalDate d : days) {
            run = (prev != null && prev.plusDays(1).equals(d)) ? run + 1 : 1;
            longest = Math.max(longest, run);
            prev = d;
        }

        return new StreakResponse(current, longest, days.size(), days.last(), studiedToday);
    }
}
