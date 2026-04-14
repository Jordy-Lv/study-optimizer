package com.jordy.studyoptimizer.github;

import com.jordy.studyoptimizer.common.exception.ExternalServiceException;
import com.jordy.studyoptimizer.common.exception.ResourceNotFoundException;
import com.jordy.studyoptimizer.github.dto.GitHubCommit;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

/**
 * Adaptador a la API REST de GitHub. Aisla TODO lo que tiene que ver con HTTP
 * externo (URL, cabeceras, token, traduccion de errores) para que el service de
 * arriba trabaje solo con objetos del dominio y no sepa de RestClient.
 *
 * Concepto nuevo: RestClient es el cliente HTTP sincrono moderno de Spring 6
 * (sustituye a RestTemplate). Lo usamos para CONSUMIR una API ajena, el papel
 * inverso al de nuestros @RestController, que la EXPONEN.
 */
@Component
public class GitHubClient {

    private final RestClient restClient;
    private final GitHubProperties props;

    public GitHubClient(GitHubProperties props) {
        this.props = props;

        // Timeouts explicitos: una llamada de red SIEMPRE puede colgarse. Sin
        // esto, un GitHub lento dejaria nuestro hilo bloqueado indefinidamente.
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5_000);
        factory.setReadTimeout(8_000);

        this.restClient = RestClient.builder()
                .baseUrl(props.apiUrl())
                .requestFactory(factory)
                .build();
    }

    /**
     * Trae los ultimos {@code limit} commits del repo {owner}/{repo}.
     * Traduce los fallos de GitHub a excepciones del dominio:
     *   - 404 -> ResourceNotFoundException (el repo no existe) -> HTTP 404 para el cliente.
     *   - resto (403 rate limit, 401 token, 5xx, timeout) -> ExternalServiceException -> HTTP 502.
     */
    public List<GitHubCommit> fetchCommits(String owner, String repo, int limit) {
        try {
            GitHubCommit[] commits = restClient.get()
                    .uri("/repos/{owner}/{repo}/commits?per_page={limit}", owner, repo, limit)
                    .header("Accept", "application/vnd.github+json")
                    .header("X-GitHub-Api-Version", "2022-11-28")
                    .headers(headers -> {
                        if (props.hasToken()) {
                            headers.setBearerAuth(props.token());
                        }
                    })
                    .retrieve()
                    .body(GitHubCommit[].class);

            return commits == null ? List.of() : List.of(commits);

        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException(
                    "No existe el repositorio '" + owner + "/" + repo + "' en GitHub (404).");

        } catch (HttpClientErrorException ex) {
            // 4xx que no es 404: tipicamente 403 (rate limit) o 401 (token malo).
            String hint = (ex.getStatusCode().value() == 403 && !props.hasToken())
                    ? " Sin token GitHub limita a 60 peticiones/hora; define GITHUB_TOKEN para subirlo."
                    : "";
            throw new ExternalServiceException(
                    "GitHub respondio " + ex.getStatusCode() + " al pedir los commits de '"
                            + owner + "/" + repo + "'." + hint, ex);

        } catch (RestClientException ex) {
            // 5xx, timeout o problema de red: no es culpa del cliente -> 502.
            throw new ExternalServiceException(
                    "No se pudo contactar con GitHub para '" + owner + "/" + repo
                            + "': " + ex.getMessage(), ex);
        }
    }
}
