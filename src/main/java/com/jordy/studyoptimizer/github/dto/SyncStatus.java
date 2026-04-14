package com.jordy.studyoptimizer.github.dto;

/**
 * Resultado de procesar un reto detectado en los commits.
 * En modo dryRun (previsualizacion) NEWLY_MARKED significa "se marcaria", no que
 * se haya tocado la BD.
 */
public enum SyncStatus {
    /** El reto existe y estaba sin hacer: se marco como hecho (o se marcaria en dryRun). */
    NEWLY_MARKED,
    /** El reto existe pero ya estaba hecho: no se hace nada. */
    ALREADY_DONE,
    /** El numero detectado no corresponde a ningun reto de la BD (p.ej. reto 45). */
    NO_EXERCISE
}
