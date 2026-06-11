package com.jordy.studyoptimizer.concept;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Un tema o concepto que se estudia (recursion, HashMap, streams...).
 * Es el cimiento del repaso espaciado (milestone 2): cada concepto tendra
 * su propia fecha de proximo repaso.
 *
 * No declara la relacion N:M hacia exercise/session: las tablas puente las
 * "poseen" Exercise y StudySession. Para consultar "que ejercicios usan este
 * concepto" usamos una query en ExerciseRepository, evitando relaciones
 * bidireccionales que complican la serializacion.
 */
@Entity
@Table(name = "concept")
@Getter
@Setter
@NoArgsConstructor
public class Concept {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 80)
    private String category;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
