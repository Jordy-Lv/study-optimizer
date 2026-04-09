package com.jordy.studyoptimizer.goal;

import com.jordy.studyoptimizer.goal.dto.CreateGoalRequest;
import com.jordy.studyoptimizer.goal.dto.GoalResponse;
import com.jordy.studyoptimizer.goal.dto.UpdateGoalRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService service;

    public GoalController(GoalService service) {
        this.service = service;
    }

    @GetMapping
    public List<GoalResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public GoalResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public GoalResponse create(@Valid @RequestBody CreateGoalRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public GoalResponse update(@PathVariable Long id, @Valid @RequestBody UpdateGoalRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
