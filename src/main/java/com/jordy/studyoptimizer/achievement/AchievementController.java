package com.jordy.studyoptimizer.achievement;

import com.jordy.studyoptimizer.achievement.dto.AchievementResponse;
import com.jordy.studyoptimizer.achievement.dto.CheckResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
public class AchievementController {

    private final AchievementService service;

    public AchievementController(AchievementService service) {
        this.service = service;
    }

    /** Estado de todos los logros (no modifica nada). */
    @GetMapping
    public List<AchievementResponse> list() {
        return service.list();
    }

    /** Evalua las reglas y desbloquea los logros recien cumplidos. */
    @PostMapping("/check")
    public CheckResponse check() {
        return service.check();
    }
}
