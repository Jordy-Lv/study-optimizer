package com.jordy.studyoptimizer.review;

import com.jordy.studyoptimizer.concept.Concept;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Ficha de repaso espaciado de un concepto (estado del algoritmo SM-2).
 * Relacion 1:1 con Concept: como mucho una ficha por concepto (columna
 * concept_id UNIQUE en V4).
 */
@Entity
@Table(name = "concept_review")
@Getter
@Setter
@NoArgsConstructor
public class ConceptReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concept_id", nullable = false, unique = true)
    private Concept concept;

    @Column(nullable = false)
    private double easiness = 2.5;

    @Column(nullable = false)
    private int repetitions = 0;

    @Column(name = "interval_days", nullable = false)
    private int intervalDays = 0;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "last_reviewed_at")
    private LocalDateTime lastReviewedAt;

    @Column(name = "total_reviews", nullable = false)
    private int totalReviews = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ConceptReview(Concept concept, LocalDate dueDate) {
        this.concept = concept;
        this.dueDate = dueDate;
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
