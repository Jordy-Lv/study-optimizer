package com.jordy.studyoptimizer.review;

/**
 * Algoritmo SM-2 (SuperMemo 2), el mismo que usa Anki en su base.
 *
 * Idea: tras repasar un concepto le das una "calidad" de 0 a 5 segun que tan
 * bien lo recordaste. Con eso se ajusta:
 *   - easiness (EF): que tan facil es para ti; sube si aciertas, baja si fallas.
 *   - repetitions: rachas de aciertos seguidos.
 *   - intervalDays: cuantos dias esperar hasta el proximo repaso.
 *
 * Es una clase pura (sin estado ni dependencias): recibe numeros y devuelve
 * numeros. Asi es trivial de razonar y de testear.
 */
public final class Sm2 {

    private Sm2() {
    }

    /** Resultado del calculo: los tres valores actualizados. */
    public record Result(double easiness, int repetitions, int intervalDays) {
    }

    /**
     * @param easiness     EF actual (>= 1.3)
     * @param repetitions  aciertos seguidos previos
     * @param intervalDays intervalo previo en dias
     * @param quality      calidad del recuerdo, 0 (nada) a 5 (perfecto)
     */
    public static Result next(double easiness, int repetitions, int intervalDays, int quality) {
        // 1) Actualizar el factor de facilidad. Formula clasica del SM-2.
        double ef = easiness + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02));
        if (ef < 1.3) {
            ef = 1.3; // suelo: nunca dejamos que un concepto sea "imposible".
        }

        int reps;
        int interval;
        if (quality < 3) {
            // Fallaste: vuelve a empezar, repasalo manana.
            reps = 0;
            interval = 1;
        } else {
            reps = repetitions + 1;
            interval = switch (reps) {
                case 1 -> 1;   // primer acierto: 1 dia
                case 2 -> 6;   // segundo acierto: 6 dias
                default -> (int) Math.round(intervalDays * ef); // luego crece x EF
            };
        }
        return new Result(ef, reps, interval);
    }
}
