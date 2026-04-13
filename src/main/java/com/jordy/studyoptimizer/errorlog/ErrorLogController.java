package com.jordy.studyoptimizer.errorlog;

import com.jordy.studyoptimizer.errorlog.dto.CreateErrorLogRequest;
import com.jordy.studyoptimizer.errorlog.dto.ErrorLogResponse;
import com.jordy.studyoptimizer.errorlog.dto.SolveErrorRequest;
import com.jordy.studyoptimizer.errorlog.dto.UpdateErrorLogRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/error-logs")
public class ErrorLogController {

    private final ErrorLogService service;

    public ErrorLogController(ErrorLogService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ErrorLogResponse> create(@Valid @RequestBody CreateErrorLogRequest req,
                                                    UriComponentsBuilder uri) {
        ErrorLogResponse created = service.create(req);
        URI location = uri.path("/api/error-logs/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Lista/busca errores. Todos los filtros son opcionales y se combinan:
     *   ?q=texto         busca en titulo, descripcion y solucion
     *   ?exerciseId=3    solo errores de ese reto
     *   ?resolved=false  solo abiertos (true = solo resueltos)
     */
    @GetMapping
    public List<ErrorLogResponse> search(@RequestParam(required = false) String q,
                                         @RequestParam(required = false) Long exerciseId,
                                         @RequestParam(required = false) Boolean resolved) {
        return service.search(q, exerciseId, resolved);
    }

    @GetMapping("/{id}")
    public ErrorLogResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public ErrorLogResponse update(@PathVariable Long id, @Valid @RequestBody UpdateErrorLogRequest req) {
        return service.update(id, req);
    }

    /** Registra la solucion y marca el error como resuelto. */
    @PostMapping("/{id}/solution")
    public ErrorLogResponse solve(@PathVariable Long id, @Valid @RequestBody SolveErrorRequest req) {
        return service.solve(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
