package com.jordy.studyoptimizer.today;

/**
 * Que clase de actividad propone el plan de hoy. No se persiste: es solo una
 * etiqueta en la respuesta para que el cliente sepa de que dominio viene cada
 * sugerencia.
 */
public enum StudyItemType {
    REVIEW,     // repasar un concepto (repaso espaciado SM-2)
    EXERCISE,   // avanzar el siguiente reto pendiente
    ERROR_FIX   // volver sobre un error aun sin resolver
}
