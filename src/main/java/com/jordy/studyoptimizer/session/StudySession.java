package com.jordy.studyoptimizer.session;

import com.jordy.studyoptimizer.concept.Concept;
import com.jordy.studyoptimizer.exercise.Exercise;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Una sesion de estudio: cuanto tiempo, que tan dificil se sintio y notas.
 * Puede estar ligada a un ejercicio concreto (o no, si fue estudio libre).
 */
@Entity
@Table(name = "study_session")
@Getter
@Setter
@NoArgsConstructor
public class StudySession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Muchas sesiones pueden apuntar a un mismo ejercicio (N:1). LAZY para no
     * traer el ejercicio entero salvo que lo pidamos.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    @Column(nullable = false)
    private Integer minutes;

    @Column(nullable = false)
    private Integer difficulty;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "studied_at", nullable = false)
    private LocalDateTime studiedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Conceptos repasados en esta sesion (N:M, tabla puente session_concept). */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "session_concept",
            joinColumns = @JoinColumn(name = "session_id"),
            inverseJoinColumns = @JoinColumn(name = "concept_id")
    )
    private Set<Concept> concepts = new LinkedHashSet<>();

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (studiedAt == null) {
            studiedAt = now;
        }
    }
}
