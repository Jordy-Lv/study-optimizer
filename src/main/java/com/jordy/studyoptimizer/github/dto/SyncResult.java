package com.jordy.studyoptimizer.github.dto;

import java.util.List;

/**
 * Informe de una sincronizacion con GitHub (POST /api/github/sync).
 * Es un resultado calculado al vuelo, NO se persiste: la fuente de verdad sigue
 * siendo el estado "done" de cada reto. Volver a sincronizar es idempotente.
 *
 * @param owner          dueno del repo consultado.
 * @param repo           nombre del repo consultado.
 * @param dryRun         si true, fue solo previsualizacion: no se toco la BD.
 * @param commitsScanned cuantos commits se leyeron.
 * @param retosDetected  retos distintos detectados que existen en la BD.
 * @param newlyMarked    cuantos retos se marcaron como hechos en esta corrida.
 * @param alreadyDone    cuantos retos detectados ya estaban hechos.
 * @param retos          detalle por reto detectado (incluye los NO_EXERCISE).
 * @param unmatched      muestra de mensajes de commit donde no se detecto reto
 *                       (ayuda a depurar el formato de los mensajes).
 */
public record SyncResult(
        String owner,
        String repo,
        boolean dryRun,
        int commitsScanned,
        int retosDetected,
        int newlyMarked,
        int alreadyDone,
        List<SyncedReto> retos,
        List<String> unmatched) {
}
