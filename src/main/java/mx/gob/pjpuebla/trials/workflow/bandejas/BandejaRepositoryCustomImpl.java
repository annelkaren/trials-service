package mx.gob.pjpuebla.trials.workflow.bandejas;

import static mx.gob.pjpuebla.trials.workflow.bandejas.BandejaCriteriaHelper.*;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.oficialias.Oficialia;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.entrada.BandejaEntradaFilter;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.entrada.BandejaEntradaResponse;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
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
                Join<Movimiento, Documento> doc = m.join("documento", JoinType.LEFT);
                Join<Movimiento, Carpeta> cMov = m.join("carpeta", JoinType.LEFT);
                Join<Documento, Carpeta> cDoc = doc.join("carpeta", JoinType.LEFT);

                Join<Carpeta, Juzgado> jcMov = cMov.join("juzgado", JoinType.LEFT);
                Join<Carpeta, Juzgado> jcDoc = cDoc.join("juzgado", JoinType.LEFT);

                Join<Juzgado, Materia> matMov = jcMov.join("materia", JoinType.LEFT);
                Join<Juzgado, Materia> matDoc = jcDoc.join("materia", JoinType.LEFT);

                Join<Movimiento, Juzgado> jMov = m.join("juzgado", JoinType.LEFT);
                Join<Movimiento, Oficialia> oMov = m.join("oficialia", JoinType.LEFT);

                // ---- Predicados base ----
                Predicate baseEstados = m.get("estado").in(estados);
                Predicate ultimoMovimiento = buildUltimoMovimientoPredicate(cb, cq, m, doc, cMov, estados);

                // Estatus OK (idéntico a JPQL original: carpeta→cMov.estatus,
                // documento→doc.estatus)
                List<EstadoCarpeta> ESTATUS_OK = byOrdinals(EstadoCarpeta.class, 0, 14);
                Predicate estatusOk = cb.or(
                                cb.and(cb.isNotNull(cMov.get("id")), cMov.get("estatus").in(ESTATUS_OK)),
                                cb.and(cb.isNotNull(doc.get("id")), doc.get("estatus").in(ESTATUS_OK)));

                // Scope por usuario (o.id = :oficialiaId OR j.id = :juzgadoId)
                Predicate scope = buildScopePredicate(cb, jMov, oMov, juzgadoId, oficialiaId);

                // ---- Filtros adicionales del record ----
                Predicate filtrosExtras = buildFiltersPredicate(cb, cq, m, doc, cMov, cDoc, jcMov, jcDoc, matMov,
                                matDoc, filtro);

                // ---- Campos calculados (expresiones) usados en el SELECT ----
                Expression<Integer> idDocumentoFinal = buildIdDocumentoExpr(cb, cq, doc, cMov);
                Expression<Boolean> hasFile = buildHasFileExpr(cb, cq, doc, cMov);

                // Folio mostrado: por presencia (doc si hay, si no carpeta)
                Expression<String> folioExpr = buildFolioPorPresenciaExpr(cb, doc, cMov);

                // Expediente y materia con COALESCE (doc primero, luego carpeta) +
                // capitalización
                Expression<String> expediente = cb.<String>coalesce()
                                .value(cDoc.get("expediente"))
                                .value(cMov.get("expediente"));
                Expression<String> materiaRaw = cb.<String>coalesce()
                                .value(matDoc.get("nombre"))
                                .value(matMov.get("nombre"));
                Expression<String> materiaNombre = capitalizeExpr(cb, materiaRaw);

                // órgano jurisdiccional con COALESCE + capitalización
                Expression<String> organoRaw = cb.<String>coalesce()
                                .value(jcDoc.get("nombre"))
                                .value(jcMov.get("nombre"));
                Expression<String> organoJ = capitalizeExpr(cb, organoRaw);

                // sello estatus / estatus
                Expression<SelloEstatus> selloEstatus = cb.<SelloEstatus>coalesce()
                                .value(cDoc.get("selloEstatus"))
                                .value(cMov.get("selloEstatus"));
                Expression<EstadoCarpeta> estatus = cb.<EstadoCarpeta>coalesce()
                                .value(cDoc.get("estatus"))
                                .value(cMov.get("estatus"));

                // "En Juzgado" por CASE de estado
                Expression<String> estaEnJuzgado = cb.<String>selectCase()
                                .when(cb.equal(m.get("estado"), "CAPTURA"), "En Juzgado")
                                .when(cb.equal(m.get("estado"), "SALIDA"), "En Juzgado")
                                .when(cb.equal(m.get("estado"), "DEVUELTO_A_OFICIALIA"), "En Juzgado")
                                .otherwise("");

                // ---- WHERE y SELECT final (DTO por constructor) ----
                cq.where(cb.and(baseEstados, estatusOk, ultimoMovimiento, scope, filtrosExtras));

                cq.select(cb.construct(
                                BandejaEntradaResponse.class,
                                m.get("id").as(Integer.class), // movimientoId
                                idDocumentoFinal, // idDocumento (con fallback)
                                cb.<Integer>coalesce() // idCarpeta: coalesce entre cDoc.id y cMov.id
                                                .value(cDoc.get("id").as(Integer.class))
                                                .value(cMov.get("id").as(Integer.class)),
                                folioExpr, // folio (por presencia)
                                expediente,
                                materiaNombre,
                                doc.get("tipoDocumento"), // NO tocar: alimenta tipoEntrada del DTO
                                cMov.get("tipoCarpeta"), // NO tocar
                                organoJ,
                                m.get("fechaAsignacion"),
                                selloEstatus,
                                estatus,
                                hasFile, // hasFile (con fallback)
                                estaEnJuzgado,
                                m.get("motivo")));

                // ---- ORDER BY global (antes de paginar) ----
                List<Order> orders = buildOrderBy(cb, cq, m, doc, cMov, cDoc, jcMov, jcDoc, matMov, matDoc,
                                pageable.getSort());
                if (!orders.isEmpty()) {
                        cq.orderBy(orders);
                } else {
                        // Default: fechaAsignacion DESC
                        cq.orderBy(cb.desc(m.get("fechaAsignacion")));
                }

                TypedQuery<BandejaEntradaResponse> typed = em.createQuery(cq);
                typed.setFirstResult((int) pageable.getOffset());
                typed.setMaxResults(pageable.getPageSize());
                List<BandejaEntradaResponse> content = typed.getResultList();

                // ======================
                // COUNT (mismo WHERE)
                // ======================
                CriteriaQuery<Long> countQ = cb.createQuery(Long.class);
                Root<Movimiento> mC = countQ.from(Movimiento.class);

                Join<Movimiento, Documento> docC = mC.join("documento", JoinType.LEFT);
                Join<Movimiento, Carpeta> cMovC = mC.join("carpeta", JoinType.LEFT);
                Join<Documento, Carpeta> cDocC = docC.join("carpeta", JoinType.LEFT);

                Join<Carpeta, Juzgado> jcMovC = cMovC.join("juzgado", JoinType.LEFT);
                Join<Carpeta, Juzgado> jcDocC = cDocC.join("juzgado", JoinType.LEFT);

                Join<Juzgado, Materia> matMovC = jcMovC.join("materia", JoinType.LEFT);
                Join<Juzgado, Materia> matDocC = jcDocC.join("materia", JoinType.LEFT);

                Join<Movimiento, Juzgado> jMovC = mC.join("juzgado", JoinType.LEFT);
                Join<Movimiento, Oficialia> oMovC = mC.join("oficialia", JoinType.LEFT);

                Predicate baseEstadosC = mC.get("estado").in(estados);
                Predicate ultimoMovimientoC = buildUltimoMovimientoPredicate(cb, countQ, mC, docC, cMovC, estados);
                Predicate filtrosExtrasC = buildFiltersPredicate(cb, countQ, mC, docC, cMovC, cDocC, jcMovC, jcDocC,
                                matMovC, matDocC, filtro);

                List<EstadoCarpeta> ESTATUS_OK_C = byOrdinals(EstadoCarpeta.class, 0, 14);
                Predicate estatusOkC = cb.or(
                                cb.and(cb.isNotNull(cMovC.get("id")), cMovC.get("estatus").in(ESTATUS_OK_C)),
                                cb.and(cb.isNotNull(docC.get("id")), docC.get("estatus").in(ESTATUS_OK_C)));

                Predicate scopeC = buildScopePredicate(cb, jMovC, oMovC, juzgadoId, oficialiaId);

                countQ.select(cb.countDistinct(mC));
                countQ.where(cb.and(baseEstadosC, estatusOkC, ultimoMovimientoC, scopeC, filtrosExtrasC));

                long total = em.createQuery(countQ).getSingleResult();

                return new PageImpl<>(content, pageable, total);
        }

        // ============================================================
        // Helpers locales (solo orquestan; delegan en el helper estático)
        // ============================================================

        /**
         * Construye los filtros del record {@link BandejaEntradaFilter}:
         * folio, expediente, materia, órgano jurisdiccional, fechaRegistro, tipoEntrada
         * y key.
         * <p>
         * Nota: el filtro especial “X.folio” (p.ej. D.424B) se agrega como OR adicional
         * equivalente a la JPQL original.
         */
        private Predicate buildFiltersPredicate(
                        CriteriaBuilder cb,
                        CriteriaQuery<?> cq,
                        Root<Movimiento> m,
                        Join<Movimiento, Documento> doc,
                        Join<Movimiento, Carpeta> cMov,
                        Join<Documento, Carpeta> cDoc,
                        Join<Carpeta, Juzgado> jcMov,
                        Join<Carpeta, Juzgado> jcDoc,
                        Join<Juzgado, Materia> matMov,
                        Join<Juzgado, Materia> matDoc,
                        @Nullable BandejaEntradaFilter f) {
                if (f == null)
                        return cb.conjunction();

                List<Predicate> ps = new ArrayList<>();

                // Folio mostrado (por presencia)
                Expression<String> folioExpr = buildFolioPorPresenciaExpr(cb, doc, cMov);

                // Expediente
                Expression<String> expediente = cb.<String>coalesce()
                                .value(cDoc.get("expediente"))
                                .value(cMov.get("expediente"));

                // Materia / Órgano (capitalizados)
                Expression<String> materiaNombre = capitalizeExpr(cb,
                                cb.<String>coalesce().value(matDoc.get("nombre")).value(matMov.get("nombre")));
                Expression<String> organoJ = capitalizeExpr(cb,
                                cb.<String>coalesce().value(jcDoc.get("nombre")).value(jcMov.get("nombre")));

                // --------- filtros simples ---------
                if (notBlank(f.folio())) {
                        ps.add(cb.like(cb.lower(folioExpr), normalizeLike(f.folio())));
                }
                if (notBlank(f.expediente())) {
                        ps.add(cb.like(cb.lower(expediente), normalizeLike(f.expediente())));
                }
                if (notBlank(f.materia())) {
                        ps.add(cb.like(cb.lower(materiaNombre), normalizeLike(f.materia())));
                }
                if (notBlank(f.organoJurisdiccional())) {
                        ps.add(cb.like(cb.lower(organoJ), normalizeLike(f.organoJurisdiccional())));
                }
                if (f.fechaRegistro() != null) {
                        // = fecha (exacta) — si necesitas por día indiferente a hora, usa BETWEEN
                        // [00:00, 23:59]
                        ps.add(cb.equal(m.get("fechaAsignacion").as(LocalDate.class), f.fechaRegistro()));
                }

                // --------- tipoEntrada (documento vs carpeta) ----------
                if (notBlank(f.tipoEntrada())) {
                        // lado documento
                        parseEnumSafe(TipoDocumento.class, f.tipoEntrada())
                                        .ifPresent(tipo -> ps.add(cb.and(cb.isNotNull(doc.get("id")),
                                                        cb.equal(doc.get("tipoDocumento"), tipo))));
                        // lado carpeta
                        parseEnumSafe(TipoCarpeta.class, f.tipoEntrada())
                                        .ifPresent(tipo -> ps.add(cb.and(cb.isNotNull(cMov.get("id")),
                                                        cb.equal(cMov.get("tipoCarpeta"), tipo))));
                }

                // --------- key (buscador general + especial X.folio) ----------
                if (notBlank(f.key())) {
                        String k = normalizeLike(f.key());
                        List<Predicate> ors = new ArrayList<>();
                        ors.add(cb.like(cb.lower(folioExpr), k));
                        ors.add(cb.like(cb.lower(expediente), k));
                        ors.add(cb.like(cb.lower(materiaNombre), k));
                        ors.add(cb.like(cb.lower(organoJ), k));
                        ors.add(cb.like(cb.lower(m.get("motivo")), k));

                        // OR especial "X.folio" (D/A/P/E/O)
                        addCompositeKeyOrIfMatches(cb, cq, ors, doc, cMov, f.key());

                        ps.add(cb.or(ors.toArray(new Predicate[0])));
                }

                return cb.and(ps.toArray(new Predicate[0]));
        }

        /**
         * Traductor de {@link Sort} a lista de {@link Order} del Criteria,
         * soportando columnas calculadas (folio por presencia, tipoEntrada).
         */
        private List<Order> buildOrderBy(
                        CriteriaBuilder cb,
                        CriteriaQuery<?> cq,
                        Root<Movimiento> m,
                        Join<Movimiento, Documento> doc,
                        Join<Movimiento, Carpeta> cMov,
                        Join<Documento, Carpeta> cDoc,
                        Join<Carpeta, Juzgado> jcMov,
                        Join<Carpeta, Juzgado> jcDoc,
                        Join<Juzgado, Materia> matMov,
                        Join<Juzgado, Materia> matDoc,
                        Sort sort) {
                List<Order> orders = new ArrayList<>();
                if (sort == null || sort.isUnsorted())
                        return orders;

                // Columnas calculadas
                Expression<String> folioExpr = buildFolioPorPresenciaExpr(cb, doc, cMov);
                Expression<String> tipoEntradaStr = cb.<String>selectCase()
                                .when(cb.isNotNull(doc.get("id")), doc.get("tipoDocumento").as(String.class))
                                .otherwise(cMov.get("tipoCarpeta").as(String.class));

                for (Sort.Order s : sort) {
                        String prop = s.getProperty();
                        boolean asc = s.isAscending();

                        switch (prop) {
                                case "folio" ->
                                        orders.add(asc ? cb.asc(cb.lower(folioExpr)) : cb.desc(cb.lower(folioExpr)));
                                case "expediente" -> {
                                        Expression<String> expediente = cb.<String>coalesce()
                                                        .value(cDoc.get("expediente"))
                                                        .value(cMov.get("expediente"));
                                        orders.add(asc ? cb.asc(cb.lower(expediente)) : cb.desc(cb.lower(expediente)));
                                }
                                case "materia" -> {
                                        Expression<String> materia = capitalizeExpr(cb,
                                                        cb.<String>coalesce().value(matDoc.get("nombre"))
                                                                        .value(matMov.get("nombre")));
                                        orders.add(asc ? cb.asc(cb.lower(materia)) : cb.desc(cb.lower(materia)));
                                }
                                case "organoJurisdiccional" -> {
                                        Expression<String> org = capitalizeExpr(cb,
                                                        cb.<String>coalesce().value(jcDoc.get("nombre"))
                                                                        .value(jcMov.get("nombre")));
                                        orders.add(asc ? cb.asc(cb.lower(org)) : cb.desc(cb.lower(org)));
                                }
                                case "fechaRegistro", "fechaAsignacion" -> orders.add(
                                                asc ? cb.asc(m.get("fechaAsignacion"))
                                                                : cb.desc(m.get("fechaAsignacion")));
                                case "tipoEntrada" -> orders.add(
                                                asc ? cb.asc(cb.lower(tipoEntradaStr))
                                                                : cb.desc(cb.lower(tipoEntradaStr)));
                                default -> {
                                        // ignora propiedades desconocidas
                                }
                        }
                }
                return orders;
        }
}
