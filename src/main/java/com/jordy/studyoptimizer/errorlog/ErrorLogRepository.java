package com.jordy.studyoptimizer.errorlog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long> {

    /**
     * Busqueda con filtros opcionales en una sola query JPQL:
     *  - q: texto buscado (case-insensitive) en titulo, descripcion o solucion.
     *       Llega como cadena vacia cuando no se busca: el LIKE '%%' hace match
     *       con todo. (Pasar null romperia en Postgres: no puede inferir el tipo
     *       del parametro y lo trata como bytea → "function lower(bytea)...".)
     *  - exerciseId: solo errores de ese reto (null = todos).
     *  - resolved: true = resueltos (resolved_at no nulo), false = abiertos.
     *
     * Es un LIKE simple, sin subquery que devuelva la entidad, asi que no cae
     * en el bug de reescritura de JPQL detectado en la milestone 2.
     */
    @Query("""
            select e from ErrorLog e
            where (lower(e.title) like lower(concat('%', :q, '%'))
                   or lower(e.description) like lower(concat('%', :q, '%'))
                   or lower(coalesce(e.solution, '')) like lower(concat('%', :q, '%')))
              and (:exerciseId is null or e.exercise.id = :exerciseId)
              and (:resolved is null
                   or (:resolved = true and e.resolvedAt is not null)
                   or (:resolved = false and e.resolvedAt is null))
            order by e.createdAt desc
            """)
    List<ErrorLog> search(@Param("q") String q,
                          @Param("exerciseId") Long exerciseId,
                          @Param("resolved") Boolean resolved);
}
