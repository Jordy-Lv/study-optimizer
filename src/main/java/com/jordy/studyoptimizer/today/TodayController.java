package com.jordy.studyoptimizer.today;

import com.jordy.studyoptimizer.today.dto.TodayPlanResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * "Que estudiar hoy". A diferencia de un CRUD, no recibe cuerpo: la entrada son
 * dos query params. @Validated en la clase activa la validacion de @Min/@Max
 * sobre los parametros (una violacion la traduce GlobalExceptionHandler a 400).
 */
@RestController
@RequestMapping("/api/today")
@Validated
public class TodayController {

    private final TodayService service;

    public TodayController(TodayService service) {
        this.service = service;
    }

    /**
     * Sugiere un plan de estudio para hoy segun el tiempo disponible y la energia.
     * Ejemplos:
     *   GET /api/today
     *   GET /api/today?minutes=45&energy=LOW
     */
    @GetMapping
    public TodayPlanResponse plan(
            @RequestParam(defaultValue = "60") @Min(5) @Max(600) int minutes,
            @RequestParam(defaultValue = "MEDIUM") Energy energy) {
        return service.plan(minutes, energy);
    }
}
