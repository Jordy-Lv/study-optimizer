package com.jordy.studyoptimizer.concept;

import com.jordy.studyoptimizer.common.exception.DuplicateResourceException;
import com.jordy.studyoptimizer.common.exception.ResourceNotFoundException;
import com.jordy.studyoptimizer.concept.dto.ConceptResponse;
import com.jordy.studyoptimizer.concept.dto.CreateConceptRequest;
import com.jordy.studyoptimizer.concept.dto.UpdateConceptRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Logica de negocio del catalogo de conceptos. El controller no toca el
 * repositorio directamente: pasa siempre por aqui.
 */
@Service
@Transactional
public class ConceptService {

    private final ConceptRepository repository;

    public ConceptService(ConceptRepository repository) {
        this.repository = repository;
    }

    public ConceptResponse create(CreateConceptRequest req) {
        String name = req.name().trim();
        if (repository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException("Ya existe un concepto llamado '" + name + "'");
        }
        Concept c = new Concept();
        c.setName(name);
        c.setDescription(req.description());
        c.setCategory(req.category());
        return ConceptResponse.from(repository.save(c));
    }

    @Transactional(readOnly = true)
    public List<ConceptResponse> list() {
        return repository.findAllByOrderByNameAsc().stream()
                .map(ConceptResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ConceptResponse get(Long id) {
        return ConceptResponse.from(findOrThrow(id));
    }

    public ConceptResponse update(Long id, UpdateConceptRequest req) {
        Concept c = findOrThrow(id);
        String name = req.name().trim();
        // Solo chequeamos duplicado si el nombre cambio.
        if (!c.getName().equalsIgnoreCase(name) && repository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException("Ya existe un concepto llamado '" + name + "'");
        }
        c.setName(name);
        c.setDescription(req.description());
        c.setCategory(req.category());
        return ConceptResponse.from(c); // dentro de @Transactional, el cambio se persiste solo (dirty checking)
    }

    public void delete(Long id) {
        Concept c = findOrThrow(id);
        repository.delete(c);
    }

    /** Reutilizado por otros servicios para resolver ids -> entidades. */
    @Transactional(readOnly = true)
    public Concept findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Concepto", id));
    }
}
