package com.jordy.studyoptimizer.errorlog;

import com.jordy.studyoptimizer.exercise.Exercise;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Un error/bug encontrado mientras se resolvia un reto, con su descripcion y
 * (cuando se resuelve) la solucion. Su esquema lo crea Flyway (V7).
 *
 * El estado "resuelto" NO se guarda como booleano aparte: se deriva de
 * resolvedAt (null = abierto). Asi evitamos un campo que se pueda
 * desincronizar con la solucion.
 */
@Entity
@Table(name = "error_log")
@Getter
@Setter
@NoArgsConstructor
public class ErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String solution;

    /**
     * Reto en el que aparecio el error (opcional). ManyToOne perezoso: el lado
     * "muchos" guarda la FK exercise_id. Si el reto se borra, esta FK queda en
     * null (ON DELETE SET NULL en la migracion).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
