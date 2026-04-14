package com.jordy.studyoptimizer.github;

import java.util.OptionalInt;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Detecta a que reto se refiere el mensaje de un commit. La idea de la milestone:
 * el usuario resuelve un reto y lo commitea con un mensaje tipo "Reto 9 ...";
 * aqui extraemos ese "9" para luego marcar el reto correspondiente.
 *
 * Concepto nuevo: expresiones regulares. Compilamos los patrones UNA vez (son
 * caros de construir) en constantes estaticas y los reutilizamos en cada llamada.
 *
 * Es una clase de utilidad (solo metodos estaticos), por eso el constructor es
 * privado: no tiene sentido instanciarla.
 */
public final class CommitMatcher {

    private static final int MIN_DAY_NUMBER = 1;

    // (?iu) = case-insensitive + unicode (para que "Día"/"DIA"/"dia" valgan igual).
    // Tras una palabra clave admitimos separadores opcionales (#, :, -, espacio,
    // "nº"...) y capturamos 1 o 2 digitos. El (?!\d) final evita comerse un numero
    // mas largo: en "dia 2024" no queremos detectar el reto 20.
    private static final Pattern KEYWORD = Pattern.compile(
            "(?iu)(?:reto|d[ií]a|day|ejercicio|ej|challenge|desaf[ií]o)\\s*(?:n[º°.]?|#|:|-)?\\s*(\\d{1,2})(?!\\d)");

    // Forma corta "#9" sin palabra clave (estilo de los PRs de mouredev).
    private static final Pattern HASH = Pattern.compile("#(\\d{1,2})(?!\\d)");

    private CommitMatcher() {
    }

    /**
     * Devuelve el numero de reto detectado en el mensaje, o vacio si no hay.
     * Primero intenta con palabra clave ("reto 9", "dia 9"...) y, si no, con la
     * forma corta "#9". Usamos OptionalInt para expresar "puede que no haya" sin
     * recurrir a null ni a un sentinela como -1.
     */
    public static OptionalInt detectDayNumber(String message) {
        if (message == null || message.isBlank()) {
            return OptionalInt.empty();
        }
        Matcher keyword = KEYWORD.matcher(message);
        if (keyword.find()) {
            return validDayNumber(keyword.group(1));
        }
        Matcher hash = HASH.matcher(message);
        if (hash.find()) {
            return validDayNumber(hash.group(1));
        }
        return OptionalInt.empty();
    }

    private static OptionalInt validDayNumber(String raw) {
        int dayNumber = Integer.parseInt(raw);
        return dayNumber >= MIN_DAY_NUMBER ? OptionalInt.of(dayNumber) : OptionalInt.empty();
    }
}
