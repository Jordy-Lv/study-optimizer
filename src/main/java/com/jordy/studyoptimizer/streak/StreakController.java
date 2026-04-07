package com.jordy.studyoptimizer.streak;

import com.jordy.studyoptimizer.streak.dto.StreakResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/streak")
public class StreakController {

    private final StreakService service;

    public StreakController(StreakService service) {
        this.service = service;
    }

    /** Racha actual y racha mas larga de dias seguidos estudiando. */
    @GetMapping
    public StreakResponse streak() {
        return service.compute();
    }
}
