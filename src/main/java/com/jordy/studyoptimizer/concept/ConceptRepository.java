package com.jordy.studyoptimizer.concept;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConceptRepository extends JpaRepository<Concept, Long> {

    boolean existsByNameIgnoreCase(String name);

    Optional<Concept> findByNameIgnoreCase(String name);

    List<Concept> findAllByOrderByNameAsc();
}
