package com.jordy.studyoptimizer.session;

import com.jordy.studyoptimizer.concept.dto.AttachConceptsRequest;
import com.jordy.studyoptimizer.session.dto.CreateSessionRequest;
import com.jordy.studyoptimizer.session.dto.SessionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService service;

    public SessionController(SessionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SessionResponse> create(@Valid @RequestBody CreateSessionRequest req,
                                                  UriComponentsBuilder uri) {
        SessionResponse created = service.create(req);
        URI location = uri.path("/api/sessions/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    /** Lista sesiones; opcionalmente filtra por reto con ?exerciseId=3 */
    @GetMapping
    public List<SessionResponse> list(@RequestParam(required = false) Long exerciseId) {
        return service.list(exerciseId);
    }

    @GetMapping("/{id}")
    public SessionResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping("/{id}/concepts")
    public SessionResponse attachConcepts(@PathVariable Long id,
                                          @Valid @RequestBody AttachConceptsRequest req) {
        return service.attachConcepts(id, req.conceptIds());
    }

    @DeleteMapping("/{id}/concepts/{conceptId}")
    public SessionResponse detachConcept(@PathVariable Long id, @PathVariable Long conceptId) {
        return service.detachConcept(id, conceptId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
