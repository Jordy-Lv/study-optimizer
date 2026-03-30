package com.jordy.studyoptimizer.exercise;

import com.jordy.studyoptimizer.concept.Concept;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Un reto de los 30 de mouredev. Su esquema lo crea Flyway (V1).
 *
 * Nota sobre Lombok: @Getter/@Setter generan los metodos de acceso en
 * tiempo de compilacion para no llenar la clase de boilerplate. No usamos
 * @Data en entidades JPA porque su equals/hashCode sobre todos los campos
 * da problemas con colecciones perezosas.
 */
@Entity
@Table(name = "exercise")
@Getter
@Setter
@NoArgsConstructor
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "day_number", nullable = false, unique = true)
    private Integer dayNumber;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer phase;

    @Column(nullable = false)
    private boolean done = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * Tiempo estimado para el reto, en minutos (milestone 8). Es opcional:
     * null mientras no se haya estimado. El tiempo REAL no se guarda aqui,
     * se calcula sumando los minutos de las sesiones del reto.
     */
    @Column(name = "estimated_minutes")
    private Integer estimatedMinutes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Conceptos que practica este reto. Relacion N:M materializada en la
     * tabla puente exercise_concept (migracion V3). Es el lado "dueno" de
     * la relacion: este es quien escribe en la tabla puente.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "exercise_concept",
            joinColumns = @JoinColumn(name = "exercise_id"),
            inverseJoinColumns = @JoinColumn(name = "concept_id")
    )
    private Set<Concept> concepts = new LinkedHashSet<>();

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
