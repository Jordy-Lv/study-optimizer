package com.jordy.studyoptimizer.today;

/**
 * Nivel de energia con el que el usuario encara la sesion de hoy. No se
 * persiste: llega como parametro de la peticion y solo sirve para priorizar
 * que actividades sugerir (con poca energia pesan mas los repasos; con mucha,
 * avanzar un reto nuevo).
 */
public enum Energy {
    LOW,
    MEDIUM,
    HIGH
}
