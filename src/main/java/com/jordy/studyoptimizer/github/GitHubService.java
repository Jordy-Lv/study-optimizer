package com.jordy.studyoptimizer.github;

import com.jordy.studyoptimizer.exercise.Exercise;
import com.jordy.studyoptimizer.exercise.ExerciseRepository;
import com.jordy.studyoptimizer.github.dto.CommitView;
import com.jordy.studyoptimizer.github.dto.GitHubCommit;
import com.jordy.studyoptimizer.github.dto.SyncResult;
import com.jordy.studyoptimizer.github.dto.SyncStatus;
import com.jordy.studyoptimizer.github.dto.SyncedReto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * Logica de la integracion con GitHub (milestone 10): lee commits de un repo y
 * auto-marca los retos que se mencionan en sus mensajes.
 *
 * No persiste nada propio: la "verdad" sigue siendo el flag done de cada reto.
 * El informe (SyncResult) se calcula al vuelo y volver a sincronizar es
 * idempotente (un reto ya hecho no se vuelve a marcar).
 */
@Service
public class GitHubService {

    /** Tope de commits por peticion: GitHub no devuelve mas de 100 por pagina. */
    private static final int MAX_LIMIT = 100;
    private static final int DEFAULT_LIMIT = 30;
    /** Cuantos mensajes "sin reto detectado" mostramos como muestra para depurar. */
    private static final int UNMATCHED_SAMPLE = 5;

    private final GitHubClient client;
    private final GitHubProperties props;
    private final ExerciseRepository exerciseRepository;

    public GitHubService(GitHubClient client, GitHubProperties props,
                         ExerciseRepository exerciseRepository) {
        this.client = client;
        this.props = props;
        this.exerciseRepository = exerciseRepository;
    }

    /**
     * Exploracion: lista los ultimos commits del repo con el reto detectado en
     * cada uno (null si el mensaje no menciona ninguno). Solo lectura, no marca nada.
     */
    @Transactional(readOnly = true)
    public List<CommitView> listCommits(String owner, String repo, Integer limit) {
        String o = resolveOwner(owner);
        String r = resolveRepo(repo);
        return client.fetchCommits(o, r, clampLimit(limit)).stream()
                .map(GitHubService::toView)
                .toList();
    }

    /**
     * Sincroniza: lee commits, detecta a que reto se refiere cada uno y marca
     * como hechos los retos correspondientes que aun no lo estuvieran.
     *
     * @param dryRun si es true, solo previsualiza: calcula el informe pero NO
     *               toca la base de datos (util para revisar antes de aplicar).
     */
    @Transactional
    public SyncResult sync(String owner, String repo, Integer limit, boolean dryRun) {
        String o = resolveOwner(owner);
        String r = resolveRepo(repo);
        List<GitHubCommit> commits = client.fetchCommits(o, r, clampLimit(limit));

        // Reto detectado -> sha del commit (mas reciente) que lo menciono. Un
        // LinkedHashMap conserva el orden de insercion (GitHub devuelve del mas
        // nuevo al mas viejo) y putIfAbsent se queda con el primero = mas reciente.
        Map<Integer, String> detected = new LinkedHashMap<>();
        List<String> unmatched = new ArrayList<>();

        for (GitHubCommit commit : commits) {
            OptionalInt day = CommitMatcher.detectDayNumber(commit.message());
            if (day.isPresent()) {
                detected.putIfAbsent(day.getAsInt(), commit.shortSha());
            } else if (unmatched.size() < UNMATCHED_SAMPLE) {
                unmatched.add(firstLine(commit.message()));
            }
        }

        List<SyncedReto> retos = new ArrayList<>();
        int retosDetected = 0;
        int newlyMarked = 0;
        int alreadyDone = 0;

        for (Map.Entry<Integer, String> entry : detected.entrySet()) {
            int dayNumber = entry.getKey();
            String sha = entry.getValue();
            Optional<Exercise> match = exerciseRepository.findByDayNumber(dayNumber);

            if (match.isEmpty()) {
                // El mensaje cito un numero que no es uno de nuestros 30 retos.
                retos.add(new SyncedReto(dayNumber, null, SyncStatus.NO_EXERCISE, sha));
                continue;
            }

            Exercise exercise = match.get();
            retosDetected++;

            if (exercise.isDone()) {
                alreadyDone++;
                retos.add(new SyncedReto(dayNumber, exercise.getTitle(), SyncStatus.ALREADY_DONE, sha));
            } else {
                newlyMarked++;
                if (!dryRun) {
                    // Mismo criterio que ExerciseService.markDone: solo sella la
                    // fecha en la transicion. Dentro de @Transactional, el dirty
                    // checking de JPA persiste el cambio al hacer commit.
                    exercise.setDone(true);
                    exercise.setCompletedAt(LocalDateTime.now());
                }
                retos.add(new SyncedReto(dayNumber, exercise.getTitle(), SyncStatus.NEWLY_MARKED, sha));
            }
        }

        return new SyncResult(o, r, dryRun, commits.size(), retosDetected,
                newlyMarked, alreadyDone, retos, unmatched);
    }

    private static CommitView toView(GitHubCommit c) {
        OptionalInt day = CommitMatcher.detectDayNumber(c.message());
        return new CommitView(
                c.shortSha(),
                firstLine(c.message()),
                c.authorName(),
                c.date(),
                day.isPresent() ? day.getAsInt() : null);
    }

    /** Primera linea del mensaje (los commits suelen traer cuerpo multilinea). */
    private static String firstLine(String message) {
        if (message == null) {
            return null;
        }
        int nl = message.indexOf('\n');
        return (nl >= 0 ? message.substring(0, nl) : message).strip();
    }

    private String resolveOwner(String owner) {
        return (owner != null && !owner.isBlank()) ? owner.trim() : props.owner();
    }

    private String resolveRepo(String repo) {
        return (repo != null && !repo.isBlank()) ? repo.trim() : props.repo();
    }

    /** Acota el limite a [1, 100]; null -> 30 por defecto. */
    private static int clampLimit(Integer limit) {
        if (limit == null) {
            return DEFAULT_LIMIT;
        }
        return Math.max(1, Math.min(limit, MAX_LIMIT));
    }
}
