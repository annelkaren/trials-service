package mx.gob.pjpuebla.trials.workflow.bandejas.criteria;

import jakarta.persistence.criteria.*;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.oficialias.Oficialia;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.entrada.BandejaEntradaFilter;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.movimientos.Movimiento;

import mx.gob.pjpuebla.trials.workflow.bandejas.BandejaCriteriaHelper;

/**
 * Utilidades de Criteria para las bandejas:
 * - Crea JOINs con LEFT (para no perder filas).
 * - Predicados de scope (estricto/relajado).
 * - Predicados de búsqueda por 'key'.
 * - Helpers simples (notBlank, normalizeLike, orAll).
 *
 * Ojo: los JOINs dependen del CriteriaQuery, por eso se crean por query.
 */
public final class BandejaCriteriaSupport {

    private BandejaCriteriaSupport() {
    }

    /** Contenedor de joins/paths para reusar en cada query. */
    public record Joins(
            Root<Movimiento> m,
            Join<Movimiento, Documento> doc,
            Join<Movimiento, Carpeta> cMov,
            Join<Documento, Carpeta> cDoc,
            Join<Carpeta, Juzgado> jcMov,
            Join<Carpeta, Juzgado> jcDoc,
            Join<Juzgado, Materia> matMov,
            Join<Juzgado, Materia> matDoc,
            Join<Movimiento, Juzgado> jMov,
            Join<Movimiento, Oficialia> oMov) {
    }

    public static Joins buildJoins(Root<Movimiento> m) {
        Join<Movimiento, Documento> doc = m.join("documento", JoinType.LEFT);
        Join<Movimiento, Carpeta> cMov = m.join("carpeta", JoinType.LEFT);
        Join<Documento, Carpeta> cDoc = doc.join("carpeta", JoinType.LEFT);

        Join<Carpeta, Juzgado> jcMov = cMov.join("juzgado", JoinType.LEFT);
        Join<Carpeta, Juzgado> jcDoc = cDoc.join("juzgado", JoinType.LEFT);

        Join<Juzgado, Materia> matMov = jcMov.join("materia", JoinType.LEFT);
        Join<Juzgado, Materia> matDoc = jcDoc.join("materia", JoinType.LEFT);

        Join<Movimiento, Juzgado> jMov = m.join("juzgado", JoinType.LEFT);
        Join<Movimiento, Oficialia> oMov = m.join("oficialia", JoinType.LEFT);

        return new Joins(m, doc, cMov, cDoc, jcMov, jcDoc, matMov, matDoc, jMov, oMov);
    }

    /**
     * Scope estricto: si ambos son null ⇒ disjunction() (cero filas).
     * Entrada/Salida.
     */
    public static Predicate scopeStrict(CriteriaBuilder cb, Joins j, @Nullable Integer juzgadoId,
            @Nullable Integer oficialiaId) {
        if (juzgadoId == null && oficialiaId == null) {
            return cb.disjunction();
        }
        if (juzgadoId != null && oficialiaId != null) {
            return cb.or(cb.equal(j.jMov().get("id"), juzgadoId),
                    cb.equal(j.oMov().get("id"), oficialiaId));
        }
        return (juzgadoId != null)
                ? cb.equal(j.jMov().get("id"), juzgadoId)
                : cb.equal(j.oMov().get("id"), oficialiaId);
    }

    /**
     * Scope relajado: si ambos son null ⇒ conjunction() (todas las filas).
     * Historial.
     */
    public static Predicate scopeRelaxed(CriteriaBuilder cb, Joins j, @Nullable Integer juzgadoId,
            @Nullable Integer oficialiaId) {
        if (juzgadoId == null && oficialiaId == null) {
            return cb.conjunction();
        }
        if (juzgadoId != null && oficialiaId != null) {
            return cb.or(cb.equal(j.jMov().get("id"), juzgadoId),
                    cb.equal(j.oMov().get("id"), oficialiaId));
        }
        return (juzgadoId != null)
                ? cb.equal(j.jMov().get("id"), juzgadoId)
                : cb.equal(j.oMov().get("id"), oficialiaId);
    }

    /** Predicados OR de búsqueda por 'key' como en tu JPQL base. */
    public static <T> List<Predicate> keyPredicates(
            CriteriaBuilder cb, AbstractQuery<T> cq, Joins j, @Nullable String key) {
        var ors = new ArrayList<Predicate>();
        if (!notBlank(key))
            return ors;

        String k = normalizeLike(key);

        // LIKEs principales (idénticos al JPQL que compartiste)
        ors.add(cb.like(cb.lower(j.cMov().get("folio")), k));
        ors.add(cb.like(cb.lower(j.cMov().get("expediente")), k));
        ors.add(cb.like(cb.lower(j.doc().get("folio")), k));
        ors.add(cb.like(cb.lower(j.cDoc().get("folio")), k));
        ors.add(cb.like(cb.lower(j.cDoc().get("expediente")), k));
        ors.add(cb.like(cb.lower(j.matMov().get("nombre")), k));
        ors.add(cb.like(cb.lower(j.matDoc().get("nombre")), k));

        // Exact match sobre folio coalesce (coherente con otras bandejas)
        var folioCoa = cb.<String>coalesce().value(j.cMov().get("folio")).value(j.doc().get("folio"));
        ors.add(cb.equal(cb.lower(folioCoa), key.toLowerCase(Locale.ROOT)));

        // (Opcional) Soporte de clave compuesta "D.424B" / "P.123" como en
        // entrada/salida:
        addCompositeKeyOrIfMatches(cb, cq, ors, j, key);

        return ors;
    }

    /** cb.or de una lista; si está vacía, devuelve cb.conjunction(). */
    public static Predicate orAll(CriteriaBuilder cb, List<Predicate> ors) {
        return ors.isEmpty() ? cb.conjunction() : cb.or(ors.toArray(Predicate[]::new));
    }

    // ----------------- Helpers menores -----------------

    public static boolean notBlank(@Nullable String s) {
        return s != null && !s.isBlank();
    }

    public static String normalizeLike(String s) {
        return "%" + s.toLowerCase(Locale.ROOT).trim() + "%";
    }

    public static <T> void addCompositeKeyOrIfMatches(
            CriteriaBuilder cb, AbstractQuery<T> cq, List<Predicate> ors, Joins j, String key) {

        if (key == null)
            return;
        String s = key.trim();
        if (!s.matches("(?i)^[a-z]{1,2}\\.[a-z0-9]+$"))
            return;

        int dot = s.indexOf('.');
        String prefix = s.substring(0, dot).trim().toUpperCase(Locale.ROOT);
        String folioPart = s.substring(dot + 1).trim().toLowerCase(Locale.ROOT);

        // Folios normalizados
        Expression<String> folioDocLower = cb.lower(j.doc().get("folio"));
        Expression<String> folioCarpLower = cb.lower(
                cb.<String>coalesce()
                        .value(j.cDoc().get("folio")) // folio de la carpeta del documento (si m.documento != null)
                        .value(j.cMov().get("folio")) // o folio de la carpeta del movimiento (si m.carpeta != null)
        );

        // Tipos para el lado carpeta (coalesce entre la carpeta del doc y la del
        // movimiento)
        Expression<TipoCarpeta> tipoCarp = cb.<TipoCarpeta>coalesce()
                .value(j.cDoc().get("tipoCarpeta"))
                .value(j.cMov().get("tipoCarpeta"));

        switch (prefix) {
            // ---- Prefijos de CARPETA ----
            case "D": // Demanda
                ors.add(cb.and(
                        cb.equal(folioCarpLower, folioPart),
                        cb.equal(tipoCarp, TipoCarpeta.DEMANDA)));
                break;

            case "E": // Exhorto (⚠ era el fallo: aquí va CARPETA, no documento)
                ors.add(cb.and(
                        cb.equal(folioCarpLower, folioPart),
                        cb.equal(tipoCarp, TipoCarpeta.EXHORTO)));
                break;

            case "PZ": // Pieza (si lo usan)
                ors.add(cb.and(
                        cb.equal(folioCarpLower, folioPart),
                        cb.equal(tipoCarp, TipoCarpeta.PIEZA)));
                break;

             case "A": // Apelación
                ors.add(cb.and(
                        cb.equal(folioDocLower, folioPart),
                        cb.equal(tipoCarp, TipoDocumento.APELACION)));
                break;

            // ---- Prefijos de DOCUMENTO ----

            case "P": // Promoción
                ors.add(cb.and(
                        cb.equal(folioDocLower, folioPart),
                        cb.equal(j.doc().get("tipoDocumento"), TipoDocumento.PROMOCION)));
                break;

            case "O": // Oficio
                ors.add(cb.and(
                        cb.equal(folioDocLower, folioPart),
                        cb.equal(j.doc().get("tipoDocumento"), TipoDocumento.OFICIO)));
                break;

            default:
                // prefijo no reconocido → nada
        }
    }

    /**
     * Último movimiento GLOBAL: no depende de estados.
     * Mantiene la misma semántica que tu JPQL:
     * - Si hay documento => agrupa por documento
     * - Si NO hay documento => agrupa por carpeta (con documento NULL)
     * - Desempate: fechaAsignacion DESC, id DESC
     */
    public static Predicate ultimoMovimientoGlobal(
            CriteriaBuilder cb,
            AbstractQuery<?> query,
            Joins j) {
        Subquery<Integer> sq = query.subquery(Integer.class);
        Root<?> m2 = sq.from(j.m().getJavaType()); // Movimiento.class
        Path<?> m2Doc = m2.get("documento");
        Path<?> m2Carp = m2.get("carpeta");

        // "m2 es más reciente" (fecha >) o "empate de fecha y id mayor"
        Predicate m2Newer = cb.greaterThan(m2.get("fechaAsignacion"), j.m().get("fechaAsignacion"));
        Predicate m2TieBiggerId = cb.and(
                cb.equal(m2.get("fechaAsignacion"), j.m().get("fechaAsignacion")),
                cb.greaterThan(m2.get("id"), j.m().get("id")));
        Predicate m2Wins = cb.or(m2Newer, m2TieBiggerId);

        // bucket por documento
        Predicate sameDocBucket = cb.and(
                cb.isNotNull(j.doc().get("id")),
                cb.equal(m2Doc.get("id"), j.doc().get("id")));

        // bucket por carpeta cuando doc es NULL
        Predicate sameCarpBucket = cb.and(
                cb.isNull(j.doc().get("id")),
                cb.isNotNull(j.cMov().get("id")),
                cb.equal(m2Carp.get("id"), j.cMov().get("id")),
                cb.isNull(m2Doc.get("id")));

        sq.select(cb.literal(1))
                .where(cb.and(cb.or(sameDocBucket, sameCarpBucket), m2Wins));

        return cb.not(cb.exists(sq));
    }

    /**
     * Último movimiento DENTRO de un conjunto de estados.
     * Igual que el global pero agregando "m2.estado IN :estados".
     */
    public static Predicate ultimoMovimientoPorEstados(
            CriteriaBuilder cb,
            AbstractQuery<?> query,
            Joins j,
            List<String> estados) {
        Subquery<Integer> sq = query.subquery(Integer.class);
        Root<?> m2 = sq.from(j.m().getJavaType()); // Movimiento.class
        Path<?> m2Doc = m2.get("documento");
        Path<?> m2Carp = m2.get("carpeta");

        Predicate m2Newer = cb.greaterThan(m2.get("fechaAsignacion"), j.m().get("fechaAsignacion"));
        Predicate m2TieBiggerId = cb.and(
                cb.equal(m2.get("fechaAsignacion"), j.m().get("fechaAsignacion")),
                cb.greaterThan(m2.get("id"), j.m().get("id")));
        Predicate m2Wins = cb.or(m2Newer, m2TieBiggerId);

        Predicate sameDocBucket = cb.and(
                cb.isNotNull(j.doc().get("id")),
                cb.equal(m2Doc.get("id"), j.doc().get("id")));
        Predicate sameCarpBucket = cb.and(
                cb.isNull(j.doc().get("id")),
                cb.isNotNull(j.cMov().get("id")),
                cb.equal(m2Carp.get("id"), j.cMov().get("id")),
                cb.isNull(m2Doc.get("id")));

        Predicate estadoOk = m2.get("estado").in(estados);

        sq.select(cb.literal(1))
                .where(cb.and(cb.or(sameDocBucket, sameCarpBucket), estadoOk, m2Wins));

        return cb.not(cb.exists(sq));
    }

    public static Predicate filters(
            CriteriaBuilder cb,
            AbstractQuery<?> cq,
            Joins j,
            @Nullable BandejaEntradaFilter f) {
        if (f == null)
            return cb.conjunction();

        List<Predicate> ps = new ArrayList<>();

        // Folio por presencia (doc si hay, si no carpeta)
        Expression<String> folioExpr = cb.<String>coalesce()
                .value(j.doc().get("folio"))
                .value(j.cMov().get("folio"));

        // Expediente (doc.carpeta o mov.carpeta)
        Expression<String> expediente = cb.<String>coalesce()
                .value(j.cDoc().get("expediente"))
                .value(j.cMov().get("expediente"));

        // Materia / Órgano (sin capitalizar para filtro; comparamos en lower)
        Expression<String> materiaRaw = cb.<String>coalesce()
                .value(j.matDoc().get("nombre"))
                .value(j.matMov().get("nombre"));

        Expression<String> organoRaw = cb.<String>coalesce()
                .value(j.jcDoc().get("nombre"))
                .value(j.jcMov().get("nombre"));

        // --------- filtros simples ---------
        if (notBlank(f.folio())) {
            ps.add(cb.like(cb.lower(folioExpr), normalizeLike(f.folio())));
        }
        if (notBlank(f.expediente())) {
            ps.add(cb.like(cb.lower(expediente), normalizeLike(f.expediente())));
        }
        if (notBlank(f.materia())) {
            ps.add(cb.like(cb.lower(materiaRaw), normalizeLike(f.materia())));
        }
        if (notBlank(f.organoJurisdiccional())) {
            ps.add(cb.like(cb.lower(organoRaw), normalizeLike(f.organoJurisdiccional())));
        }
        if (f.fechaRegistro() != null) {
            // Igualdad por fecha (día exacto)
            ps.add(cb.equal(j.m().get("fechaAsignacion").as(LocalDate.class), f.fechaRegistro()));
        }

        // --------- tipoEntrada (documento vs carpeta) ----------
        if (notBlank(f.tipoEntrada())) {

            BandejaCriteriaHelper.parseEnumSafe(TipoDocumento.class, f.tipoEntrada())
                    .ifPresent(tipo -> ps.add(cb.and(cb.isNotNull(j.doc().get("id")),
                            cb.equal(j.doc().get("tipoDocumento"), tipo))));

            BandejaCriteriaHelper.parseEnumSafe(TipoCarpeta.class, f.tipoEntrada())
                    .ifPresent(tipo -> ps.add(cb.and(cb.isNotNull(j.cMov().get("id")),
                            cb.equal(j.cMov().get("tipoCarpeta"), tipo))));
        }

        // --------- key (buscador general + especial X.folio) ----------
        if (notBlank(f.key())) {
            String k = normalizeLike(f.key());
            List<Predicate> ors = new ArrayList<>();
            ors.add(cb.like(cb.lower(folioExpr), k));
            ors.add(cb.like(cb.lower(expediente), k));
            ors.add(cb.like(cb.lower(materiaRaw), k));
            ors.add(cb.like(cb.lower(organoRaw), k));
            ors.add(cb.like(cb.lower(j.m().get("motivo")), k));

            // OR exacto por folio coalesce
            ors.add(cb.equal(cb.lower(folioExpr), f.key().toLowerCase(Locale.ROOT)));

            // OR especial "X.folio" (D/A/P/E/O)
            addCompositeKeyOrIfMatches(cb, cq, ors, j, f.key());

            ps.add(orAll(cb, ors));
        }

        return ps.isEmpty() ? cb.conjunction() : cb.and(ps.toArray(Predicate[]::new));
    }
}
