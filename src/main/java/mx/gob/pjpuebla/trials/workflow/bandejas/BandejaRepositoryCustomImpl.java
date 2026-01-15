package mx.gob.pjpuebla.trials.workflow.bandejas;

import static mx.gob.pjpuebla.trials.workflow.bandejas.BandejaCriteriaHelper.*;

import java.util.List;

import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.workflow.bandejas.criteria.BandejaCriteriaSupport;
import mx.gob.pjpuebla.trials.workflow.bandejas.criteria.OrderMapper;
import mx.gob.pjpuebla.trials.workflow.bandejas.criteria.ProjectionMapper;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.entrada.BandejaEntradaFilter;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.entrada.BandejaEntradaResponse;
import mx.gob.pjpuebla.trials.workflow.movimientos.Movimiento;

/**
 * Implementación custom de consultas para la Bandeja de Entrada usando JPA
 * Criteria.
 *
 * <h3>Guía rápida de Criteria API</h3>
 * <ul>
 * <li><b>CriteriaBuilder</b> (cb): fábrica de expresiones/predicados (equal,
 * and, or, coalesce, selectCase, greatest...).</li>
 * <li><b>CriteriaQuery&lt;T&gt;</b> (cq): query tipada; define SELECT, FROM,
 * WHERE, ORDER BY.</li>
 * <li><b>Root&lt;X&gt;</b> (root): raíz (FROM) de la entidad que consultamos;
 * aquí es {@link Movimiento}.</li>
 * <li><b>Join&lt;A,B&gt;</b>: join tipado (LEFT/INNER). LEFT evita “perder
 * filas” cuando la relación es opcional.</li>
 * <li><b>Expression&lt;T&gt;</b>: trozo de SQL tipado (columna, función, CASE,
 * COALESCE, subquery...).</li>
 * <li><b>Predicate</b>: condición booleana; se combinan con {@code cb.and()} /
 * {@code cb.or()} / {@code cb.not()}.</li>
 * <li><b>Subquery&lt;S&gt;</b>: subconsulta que proyecta un valor (MAX,
 * EXISTS...).</li>
 * </ul>
 *
 * <p>
 * Patrones de la bandeja implementados aquí:
 * </p>
 * <ul>
 * <li>Último movimiento por carpeta/documento (idéntico a JPQL original, con
 * tie-break por id).</li>
 * <li>Scope por usuario (juzgado/oficialía).</li>
 * <li>Filtros por record {@link BandejaEntradaFilter}.</li>
 * <li>Soporte para “key compuesto” tipo <code>X.folio</code> (D/A/P/E/O).</li>
 * <li>Campos calculados (folio efectivo, idDocumento efectivo, hasFile
 * efectivo).</li>
 * </ul>
 */
@Repository
public class BandejaRepositoryCustomImpl implements BandejaRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    /**
     * Consulta principal que devuelve la bandeja paginada.
     *
     * @param pageable    paginación y orden
     * @param estados     estados de movimiento permitidos (p.ej.
     *                    CAPTURA/EDICION/DEVUELTO_A_OFICIALIA)
     * @param filtro      filtros opcionales de la bandeja
     * @param juzgadoId   ámbito del usuario (juzgado) o {@code null}
     * @param oficialiaId ámbito del usuario (oficialía) o {@code null}
     */
    @Override
    public Page<BandejaEntradaResponse> findBandejaEntradas(
            Pageable pageable,
            List<String> estados,
            @Nullable BandejaEntradaFilter filtro,
            Integer juzgadoId,
            Integer oficialiaId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // ======================
        // SELECT (contenido)
        // ======================
        CriteriaQuery<BandejaEntradaResponse> cq = cb.createQuery(BandejaEntradaResponse.class);
        Root<Movimiento> m = cq.from(Movimiento.class);

        // ---- LEFT JOINs (no perder filas cuando alguna relación es opcional) ----
        var j = BandejaCriteriaSupport.buildJoins(m);

        // ---- Predicados base ----
        Predicate baseEstados = m.get("estado").in(estados);
        Predicate ultimoMovimiento = BandejaCriteriaSupport.ultimoMovimientoPorEstados(cb, cq, j, estados);

        // Estatus OK (idéntico a JPQL original: carpeta→cMov.estatus,
        // documento→doc.estatus)
        List<EstadoCarpeta> ESTATUS_OK = byOrdinals(EstadoCarpeta.class, 0, 14);
        Predicate estatusOk = cb.or(
                cb.and(cb.isNotNull(j.cMov().get("id")), j.cMov().get("estatus").in(ESTATUS_OK)),
                cb.and(cb.isNotNull(j.doc().get("id")), j.doc().get("estatus").in(ESTATUS_OK)));

        // Scope por usuario (o.id = :oficialiaId OR j.id = :juzgadoId)
        Predicate scope = buildScopePredicate(cb, j.jMov(), j.oMov(), juzgadoId, oficialiaId);

        // ---- Filtros adicionales del record ----
        Predicate filtrosExtras = BandejaCriteriaSupport.filters(cb, cq, j, filtro);

        // ---- Campos calculados (expresiones) usados en el SELECT ----
        Expression<Integer> idDocumentoFinal = buildIdDocumentoExpr(cb, cq, j.doc(), j.cMov());
        Expression<Boolean> hasFile = buildHasFileExpr(cb, cq, j.doc(), j.cMov());

        // ---- SELECT y where final (DTO por constructor) ----
        cq.select(ProjectionMapper.bandejaResponse(cb, cq, j, idDocumentoFinal, hasFile, null));
        cq.where(cb.and(baseEstados, estatusOk, ultimoMovimiento, scope, filtrosExtras));

        // ---- ORDER BY global (antes de paginar) ----
        var orders = OrderMapper.mapSort(pageable.getSort(), cb, j);
        cq.orderBy(orders.isEmpty() ? List.of(cb.desc(j.m().get("fechaAsignacion")), cb.desc(j.m().get("id")))
                : orders);

        TypedQuery<BandejaEntradaResponse> typed = em.createQuery(cq);
        typed.setFirstResult((int) pageable.getOffset());
        typed.setMaxResults(pageable.getPageSize());
        List<BandejaEntradaResponse> content = typed.getResultList();

        // ======================
        // COUNT (mismo WHERE)
        // ======================
        CriteriaQuery<Long> countQ = cb.createQuery(Long.class);
        Root<Movimiento> mC = countQ.from(Movimiento.class);

        var jc = BandejaCriteriaSupport.buildJoins(mC);

        Predicate baseEstadosC = mC.get("estado").in(estados);
        Predicate ultimoMovimientoC = BandejaCriteriaSupport.ultimoMovimientoPorEstados(cb, countQ, jc,
                estados);
        Predicate filtrosExtrasC = BandejaCriteriaSupport.filters(cb, countQ, jc, filtro);

        List<EstadoCarpeta> ESTATUS_OK_C = byOrdinals(EstadoCarpeta.class, 0, 14);
        Predicate estatusOkC = cb.or(
                cb.and(cb.isNotNull(jc.cMov().get("id")), jc.cMov().get("estatus").in(ESTATUS_OK_C)),
                cb.and(cb.isNotNull(jc.doc().get("id")), jc.doc().get("estatus").in(ESTATUS_OK_C)));

        Predicate scopeC = buildScopePredicate(cb, jc.jMov(), jc.oMov(), juzgadoId, oficialiaId);

        countQ.select(cb.countDistinct(mC));
        countQ.where(cb.and(baseEstadosC, estatusOkC, ultimoMovimientoC, scopeC, filtrosExtrasC));

        long total = em.createQuery(countQ).getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    /**
     * Consulta principal que devuelve la bandeja paginada.
     *
     * @param pageable    paginación y orden
     * @param estados     estados de movimiento permitidos (p.ej.
     *                    CAPTURA/EDICION/DEVUELTO_A_OFICIALIA)
     * @param filtro      filtros opcionales de la bandeja
     * @param juzgadoId   ámbito del usuario (juzgado) o {@code null}
     * @param oficialiaId ámbito del usuario (oficialía) o {@code null}
     */
    @Override
    public Page<BandejaEntradaResponse> findBandejaSalida(
            Pageable pageable,
            List<String> estados,
            @Nullable BandejaEntradaFilter filtro,
            Integer juzgadoId,
            Integer oficialiaId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // ======================
        // SELECT (contenido)
        // ======================
        CriteriaQuery<BandejaEntradaResponse> cq = cb.createQuery(BandejaEntradaResponse.class);
        Root<Movimiento> m = cq.from(Movimiento.class);

        // ---- LEFT JOINs (no perder filas cuando alguna relación es opcional) ----
        var j = BandejaCriteriaSupport.buildJoins(m);

        // ---- Predicados base ----
        Predicate ultimoMovimiento = BandejaCriteriaSupport.ultimoMovimientoGlobal(cb, cq, j);

        // Scope por usuario (o.id = :oficialiaId OR j.id = :juzgadoId)
        Predicate scope = buildScopePredicate(cb, j.jMov(), j.oMov(), juzgadoId, oficialiaId);

        // ---- Filtros adicionales del record ----
        Predicate filtrosExtras = BandejaCriteriaSupport.filters(cb, cq, j, filtro);

        Predicate esSalida = cb.equal(m.get("estado"), "SALIDA");

        // ---- Campos calculados (expresiones) usados en el SELECT ----
        Expression<Integer> idDocumentoFinal = buildIdDocumentoExpr(cb, cq, j.doc(), j.cMov());
        Expression<Boolean> hasFile = buildHasFileExpr(cb, cq, j.doc(), j.cMov());

        // ---- SELECT y WHERE final (DTO por constructor) ----
        cq.select(ProjectionMapper.bandejaResponse(cb, cq, j, idDocumentoFinal, hasFile, null));
        cq.where(cb.and(esSalida, ultimoMovimiento, scope, filtrosExtras));
        // ---- ORDER BY global (antes de paginar) ----
        var orders = OrderMapper.mapSort(pageable.getSort(), cb, j);
        cq.orderBy(orders.isEmpty() ? List.of(cb.desc(j.m().get("fechaAsignacion")), cb.desc(j.m().get("id")))
                : orders);

        TypedQuery<BandejaEntradaResponse> typed = em.createQuery(cq);
        typed.setFirstResult((int) pageable.getOffset());
        typed.setMaxResults(pageable.getPageSize());
        List<BandejaEntradaResponse> content = typed.getResultList();

        // --- COUNT (mismo WHERE que el SELECT) ---
        CriteriaQuery<Long> countQ = cb.createQuery(Long.class);
        Root<Movimiento> mC = countQ.from(Movimiento.class);

        var jc = BandejaCriteriaSupport.buildJoins(mC);

        // Igual que en SELECT:
        Predicate ultimoMovimientoC = BandejaCriteriaSupport.ultimoMovimientoGlobal(cb, countQ, jc);
        // estados)
        Predicate esSalidaC = cb.equal(mC.get("estado"), "SALIDA");
        Predicate scopeC = buildScopePredicate(cb, jc.jMov(), jc.oMov(), juzgadoId, oficialiaId);
        Predicate filtrosExtrasC = BandejaCriteriaSupport.filters(cb, countQ, jc, filtro);

        countQ.select(cb.count(mC)); // o cb.countDistinct(mC) si lo prefieres
        countQ.where(cb.and(esSalidaC, ultimoMovimientoC, scopeC, filtrosExtrasC));

        long total = em.createQuery(countQ).getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<BandejaEntradaResponse> findBandejaHistorial(
            Pageable pageable,
            List<String> estados,
            @Nullable BandejaEntradaFilter filtro,
            Integer juzgadoId,
            Integer oficialiaId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // SELECT (contenido)
        CriteriaQuery<BandejaEntradaResponse> cq = cb.createQuery(BandejaEntradaResponse.class);
        Root<Movimiento> m = cq.from(Movimiento.class);

        // ✅ JOINs centralizados
        var j = BandejaCriteriaSupport.buildJoins(m);

        // Scope por usuario (o.id = :oficialiaId OR j.id = :juzgadoId)
        Predicate scope = BandejaCriteriaSupport.scopeRelaxed(cb, j, juzgadoId, oficialiaId);

        // ---- Filtros adicionales del record ----
        Predicate filtrosExtras = BandejaCriteriaSupport.filters(cb, cq, j, filtro);

        // ---- Campos calculados (expresiones) usados en el SELECT ----
        Expression<Integer> idDocumentoFinal = buildIdDocumentoExpr(cb, cq, j.doc(), j.cMov());
        Expression<Boolean> hasFile = buildHasFileExpr(cb, cq, j.doc(), j.cMov());

        cq.select(ProjectionMapper.bandejaResponse(cb, cq, j, idDocumentoFinal, hasFile, null));
        cq.where(cb.and(scope, filtrosExtras));

        // ---- ORDER BY global (antes de paginar) ----
        var orders = OrderMapper.mapSort(pageable.getSort(), cb, j);
        cq.orderBy(orders.isEmpty() ? List.of(cb.desc(j.m().get("fechaAsignacion")), cb.desc(j.m().get("id")))
                : orders);

        TypedQuery<BandejaEntradaResponse> typed = em.createQuery(cq);
        typed.setFirstResult((int) pageable.getOffset());
        typed.setMaxResults(pageable.getPageSize());
        List<BandejaEntradaResponse> content = typed.getResultList();

        // --- COUNT (mismo WHERE que el SELECT) ---
        CriteriaQuery<Long> countQ = cb.createQuery(Long.class);
        Root<Movimiento> mC = countQ.from(Movimiento.class);

        var jc = BandejaCriteriaSupport.buildJoins(mC);

        Predicate scopeC = BandejaCriteriaSupport.scopeRelaxed(cb, jc, juzgadoId, oficialiaId);
        Predicate filtrosExtrasC = BandejaCriteriaSupport.filters(cb, countQ, jc, filtro);

        countQ.select(cb.count(mC));
        countQ.where(cb.and(scopeC, filtrosExtrasC));

        long total = em.createQuery(countQ).getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<BandejaEntradaResponse> findArchivoJudicialHistorial(
            Pageable pageable,
            List<String> estados,
            @Nullable BandejaEntradaFilter filtro) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // SELECT (contenido)
        CriteriaQuery<BandejaEntradaResponse> cq = cb.createQuery(BandejaEntradaResponse.class);
        Root<Movimiento> m = cq.from(Movimiento.class);
        Predicate estadoPredicate = cb.conjunction();

        if (estados != null && !estados.isEmpty()) {
            estadoPredicate = m.get("estado").in(estados);
        }

        // ✅ JOINs centralizados
        var j = BandejaCriteriaSupport.buildJoins(m);

        // ---- Filtros adicionales del record ----
        Predicate filtrosExtras = BandejaCriteriaSupport.filters(cb, cq, j, filtro);

        // ---- Campos calculados (expresiones) usados en el SELECT ----
        Expression<Integer> idDocumentoFinal = buildIdDocumentoExpr(cb, cq, j.doc(), j.cMov());
        Expression<Boolean> hasFile = buildHasFileExpr(cb, cq, j.doc(), j.cMov());

        cq.select(ProjectionMapper.bandejaResponse(cb, cq, j, idDocumentoFinal, hasFile, null));
        cq.where(cb.and(filtrosExtras, estadoPredicate));

        // ---- ORDER BY global (antes de paginar) ----
        var orders = OrderMapper.mapSort(pageable.getSort(), cb, j);
        cq.orderBy(orders.isEmpty() ? List.of(cb.desc(j.m().get("fechaAsignacion")), cb.desc(j.m().get("id")))
                : orders);

        TypedQuery<BandejaEntradaResponse> typed = em.createQuery(cq);
        typed.setFirstResult((int) pageable.getOffset());
        typed.setMaxResults(pageable.getPageSize());
        List<BandejaEntradaResponse> content = typed.getResultList();

        // --- COUNT (mismo WHERE que el SELECT) ---
        CriteriaQuery<Long> countQ = cb.createQuery(Long.class);
        Root<Movimiento> mC = countQ.from(Movimiento.class);


        var jc = BandejaCriteriaSupport.buildJoins(mC);

        Predicate filtrosExtrasC = BandejaCriteriaSupport.filters(cb, countQ, jc, filtro);

        Predicate estadoPredicateC = cb.conjunction();

        if (estados != null && !estados.isEmpty()) {
            estadoPredicateC = mC.get("estado").in(estados);
        }

        countQ.select(cb.count(mC));
        countQ.where(cb.and(filtrosExtrasC, estadoPredicateC));

        long total = em.createQuery(countQ).getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }
}
