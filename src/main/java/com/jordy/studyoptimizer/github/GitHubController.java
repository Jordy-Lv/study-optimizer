package com.jordy.studyoptimizer.github;

import com.jordy.studyoptimizer.github.dto.CommitView;
import com.jordy.studyoptimizer.github.dto.SyncResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API de la integracion con GitHub (milestone 10).
 *
 * Todos los parametros owner/repo son opcionales: si no se pasan, se usan los
 * del bloque "github" de application.yml (variables GITHUB_OWNER/GITHUB_REPO).
 * Asi la demo funciona sin configurar nada y el uso real apunta al repo propio.
 */
@RestController
@RequestMapping("/api/github")
public class GitHubController {

    private final GitHubService service;

    public GitHubController(GitHubService service) {
        this.service = service;
    }

    /**
     * Explora los ultimos commits del repo mostrando, en cada uno, el reto que
     * se detecta en su mensaje (campo detectedDay, null si no hay). Solo lectura:
     * sirve para revisar antes de sincronizar.
     *   ?owner=  ?repo=  override del repo configurado
     *   ?limit=  cuantos commits (1..100, por defecto 30)
     */
    @GetMapping("/commits")
    public List<CommitView> commits(@RequestParam(required = false) String owner,
                                    @RequestParam(required = false) String repo,
                                    @RequestParam(required = false) Integer limit) {
        return service.listCommits(owner, repo, limit);
    }

    /**
     * Sincroniza: marca como hechos los retos detectados en los commits.
     * Idempotente (un reto ya hecho no se vuelve a tocar).
     *   ?dryRun=true  previsualiza sin escribir en la BD (por defecto false = aplica)
     */
    @PostMapping("/sync")
    public SyncResult sync(@RequestParam(required = false) String owner,
                           @RequestParam(required = false) String repo,
                           @RequestParam(required = false) Integer limit,
                           @RequestParam(defaultValue = "false") boolean dryRun) {
        return service.sync(owner, repo, limit, dryRun);
    }
}
