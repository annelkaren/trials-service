package mx.gob.pjpuebla.trials.workflow.bandejas;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import jakarta.persistence.criteria.AbstractQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.criteria.CriteriaBuilder.Trimspec;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.oficialias.Oficialia;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.movimientos.Movimiento;

/**
 * Utilidades estáticas para construir expresiones y predicados de JPA Criteria
 * utilizados por la "bandeja de entrada".
 *
 * <h3>Mini-guía de Criteria API</h3>
 * <ul>
 *   <li><b>CriteriaBuilder</b>: fábrica de expresiones y predicados (cb.equal, cb.and, cb.or, cb.coalesce, cb.selectCase...).</li>
 *   <li><b>CriteriaQuery&lt;T&gt;</b>: query tipada; define select/from/where/orderBy.</li>
 *   <li><b>Root&lt;X&gt;</b>: raíz (FROM) de la entidad principal; aquí es {@link Movimiento}.</li>
 *   <li><b>Join&lt;X,Y&gt;</b>: join tipado; usa LEFT para no perder filas cuando la relación es opcional.</li>
 *   <li><b>Expression&lt;T&gt;</b>: una expresión SQL tipada (columna, función, CASE, COALESCE, subquery...).</li>
 *   <li><b>Predicate</b>: condición booleana; combínalas con {@code cb.and()} / {@code cb.or()}.</li>
 *   <li><b>Subquery&lt;S&gt;</b>: subconsulta que puede proyectar un valor (MAX, EXISTS, etc.).</li>
 * </ul>
 *
 */
public final class BandejaCriteriaHelper {

    private BandejaCriteriaHelper() {}

    // =========================
    //  Strings / enums helpers
    // =========================

    /** Devuelve true si el string no es nulo ni está en blanco. */
    public static boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }

    /** Prepara un patrón "%value%" en minúsculas y sin acentos para LIKE/ILIKE. */
    public static String normalizeLike(String value) {
        String t = value == null ? "" : value.toLowerCase(Locale.ROOT);
        t = Normalizer.normalize(t, Normalizer.Form.NFD).replaceAll("\\p{M}", ""); // quita acentos
        return "%" + t + "%";
    }

    /** Intenta parsear de forma tolerante un enum, devolviendo Optional vacío si falla. */
    public static <E extends Enum<E>> Optional<E> parseEnumSafe(Class<E> type, String raw) {
        if (!notBlank(raw)) return Optional.empty();
        try {
            return Optional.of(Enum.valueOf(type, normalizeEnumToken(raw)));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    /** Normaliza un token textual a formato NAME_DE_ENUM (mayúsculas, sin acentos, guiones/espacios a "_"). */
    public static String normalizeEnumToken(String s) {
        String t = s.trim().toUpperCase(Locale.ROOT)
                .replace(' ', '_')
                .replace('-', '_');
        t = Normalizer.normalize(t, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return t;
    }

    /** Obtiene elementos del enum por ordinal (¡cuidado si cambia el orden del enum!). */
    public static <E extends Enum<E>> List<E> byOrdinals(Class<E> type, int... ords) {
        E[] all = type.getEnumConstants();
        List<E> out = new ArrayList<>(ords.length);
        for (int ord : ords) out.add(all[ord]);
        return out;
    }

    // =========================
    //  Presentación / Strings
    // =========================

    /**
     * Capitaliza la primera letra y pone el resto en minúsculas (null-safe),
     * usando {@link CriteriaBuilder#trim(Trimspec, Expression)} para evitar dependencias del dialecto.
     */
    public static Expression<String> capitalizeExpr(CriteriaBuilder cb, Expression<String> expr) {
        Expression<String> safe = cb.<String>coalesce().value(expr).value(cb.literal(""));
        Expression<String> trimmed = cb.trim(Trimspec.BOTH, safe);
        Expression<Integer> len = cb.length(trimmed);
        Expression<String> first = cb.upper(cb.substring(trimmed, 1, 1));
        Expression<String> rest = cb.lower(
                cb.<String>selectCase()
                        .when(cb.gt(len, 1), cb.substring(trimmed, 2))
                        .otherwise(cb.literal("")));
        return cb.<String>selectCase()
                .when(cb.gt(len, 0), cb.concat(first, rest))
                .otherwise(trimmed);
    }

    // =========================
    //  Expresiones de dominio
    // =========================

    /**
     * Folio "por presencia":
     * <ul>
     *   <li>Si el movimiento trae documento → {@code doc.folio}</li>
     *   <li>En caso contrario → {@code cMov.folio}</li>
     * </ul>
     */
    public static Expression<String> buildFolioPorPresenciaExpr(
            CriteriaBuilder cb,
            Join<Movimiento, Documento> doc,
            Join<Movimiento, Carpeta> cMov
    ) {
        return cb.<String>selectCase()
                .when(cb.isNotNull(doc.get("id")), doc.get("folio"))
                .otherwise(cMov.get("folio"));
    }

    /**
     * Id de documento "efectivo": si el movimiento trae documento, usa su id.
     * En caso contrario, busca el documento canónico de la <b>misma carpeta</b> según:
     * DEMANDA→NULL, EXHORTO→EXHORTO, APELACION→APELACION, PIEZA→PROMOCION.
     * Si hay varios, toma el de id mayor.
     */
    public static <T> Expression<Integer> buildIdDocumentoExpr(
            CriteriaBuilder cb,
            CriteriaQuery<T> cq,
            Join<Movimiento, Documento> doc,
            Join<Movimiento, Carpeta> cMov
    ) {
        Subquery<Integer> sqMaxId = cq.subquery(Integer.class);
        Root<Documento> d2 = sqMaxId.from(Documento.class);
        Join<Documento, Carpeta> d2Carp = d2.join("carpeta", JoinType.INNER);
        Predicate mismaCarpeta2 = cb.equal(d2Carp.get("id").as(Integer.class), cMov.get("id").as(Integer.class));
        Predicate mapeo2 = cb.or(
                cb.and(cb.equal(cMov.get("tipoCarpeta"), TipoCarpeta.DEMANDA),   cb.isNull(d2.get("tipoDocumento"))),
                cb.and(cb.equal(cMov.get("tipoCarpeta"), TipoCarpeta.EXHORTO),   cb.equal(d2.get("tipoDocumento"), TipoDocumento.EXHORTO)),
                cb.and(cb.equal(cMov.get("tipoCarpeta"), TipoCarpeta.APELACION), cb.equal(d2.get("tipoDocumento"), TipoDocumento.APELACION)),
                cb.and(cb.equal(cMov.get("tipoCarpeta"), TipoCarpeta.PIEZA),     cb.equal(d2.get("tipoDocumento"), TipoDocumento.PROMOCION))
        );
        sqMaxId.select(cb.greatest(d2.get("id").as(Integer.class)));
        sqMaxId.where(mismaCarpeta2, mapeo2);

        return cb.<Integer>coalesce()
                .value(doc.get("id").as(Integer.class))
                .value(sqMaxId);
    }

    /**
     * hasFile "efectivo":
     * <ul>
     *   <li>Si hay documento en el movimiento → {@code doc.ruta IS NOT NULL}</li>
     *   <li>Si no hay documento → {@code EXISTS} de documento canónico con {@code ruta IS NOT NULL}</li>
     * </ul>
     */
    public static <T> Expression<Boolean> buildHasFileExpr(
            CriteriaBuilder cb,
            CriteriaQuery<T> cq,
            Join<Movimiento, Documento> doc,
            Join<Movimiento, Carpeta> cMov
    ) {
        Subquery<Integer> sq = cq.subquery(Integer.class);
        Root<Documento> d = sq.from(Documento.class);
        Join<Documento, Carpeta> dCarp = d.join("carpeta", JoinType.INNER);
        Predicate mismaCarpeta = cb.equal(dCarp.get("id"), cMov.get("id"));
        Predicate mapeo = cb.or(
                cb.and(cb.equal(cMov.get("tipoCarpeta"), TipoCarpeta.DEMANDA),   cb.isNull(d.get("tipoDocumento"))),
                cb.and(cb.equal(cMov.get("tipoCarpeta"), TipoCarpeta.EXHORTO),   cb.equal(d.get("tipoDocumento"), TipoDocumento.EXHORTO)),
                cb.and(cb.equal(cMov.get("tipoCarpeta"), TipoCarpeta.APELACION), cb.equal(d.get("tipoDocumento"), TipoDocumento.APELACION)),
                cb.and(cb.equal(cMov.get("tipoCarpeta"), TipoCarpeta.PIEZA),     cb.equal(d.get("tipoDocumento"), TipoDocumento.PROMOCION))
        );
        sq.select(cb.literal(1));
        sq.where(mismaCarpeta, mapeo, cb.isNotNull(d.get("ruta")));

        return cb.<Boolean>selectCase()
                .when(cb.isNotNull(doc.get("id")), cb.isNotNull(doc.get("ruta")))
                .otherwise(cb.exists(sq));
    }

    // =========================
    //  Reglas de negocio (predicados)
    // =========================

    /**
     * "Último movimiento" con el mismo enfoque de tu JPQL original (MAX fecha + tie-break por id),
     * dividido en dos ramas:
     * <ul>
     *   <li>Rama A: filas sin documento → último por <b>carpeta</b></li>
     *   <li>Rama B: filas con documento → último por <b>documento</b></li>
     * </ul>
     */
    public static <T> Predicate buildUltimoMovimientoPredicate(
            CriteriaBuilder cb,
            CriteriaQuery<T> cq,
            Root<Movimiento> m,
            Join<Movimiento, Documento> doc,
            Join<Movimiento, Carpeta> cMov,
            List<String> estados
    ) {
        //Guardamos la fecha y el id del movimiento.
        Expression<LocalDateTime> mFecha = m.get("fechaAsignacion").as(LocalDateTime.class);
        Expression<Integer> mId = m.get("id").as(Integer.class);

        // === Caso A: por carpeta (doc IS NULL)
        //Pasos: 
        /*
         * Se define con subquery el tipo de dato que vamos a ocupar en la subconsulta
         * Root<Movimiento> aqui configuramos de donde vamos a obener la información.
         * Guardamos el elemento fechaAsignación en una varibale de tipo expresión LocalDateTime
         * Ejecutamos el select pero aplicando un MAX con gratest sobre la columna m2Fecha.
         * Aplicamos los wheres correspondeintes:
         */
        Subquery<LocalDateTime> sqAFecha = cq.subquery(LocalDateTime.class);
        Root<Movimiento> m2 = sqAFecha.from(Movimiento.class);
        Expression<LocalDateTime> m2Fecha = m2.get("fechaAsignacion").as(LocalDateTime.class);
        sqAFecha.select(cb.greatest(m2Fecha));
        sqAFecha.where(
                cb.equal(m2.get("carpeta"), cMov),
                m2.get("estado").in(estados));


        Subquery<Integer> sqAId = cq.subquery(Integer.class);
        Root<Movimiento> m2b = sqAId.from(Movimiento.class);
        Expression<Integer> m2bId = m2b.get("id").as(Integer.class);
        Expression<LocalDateTime> m2bFecha = m2b.get("fechaAsignacion").as(LocalDateTime.class);
        sqAId.select(cb.greatest(m2bId));
        sqAId.where(
                cb.equal(m2b.get("carpeta"), cMov),
                m2b.get("estado").in(estados),
                cb.equal(m2bFecha, mFecha));

        Predicate casoA = cb.and(
                cb.isNull(doc.get("id")),
                cb.equal(mFecha, sqAFecha),
                cb.equal(mId, sqAId));

        // === Caso B: por documento (doc IS NOT NULL)
        Subquery<LocalDateTime> sqBFecha = cq.subquery(LocalDateTime.class);
        Root<Movimiento> m3 = sqBFecha.from(Movimiento.class);
        Expression<LocalDateTime> m3Fecha = m3.get("fechaAsignacion").as(LocalDateTime.class);
        sqBFecha.select(cb.greatest(m3Fecha));
        sqBFecha.where(
                cb.equal(m3.get("documento"), doc),
                m3.get("estado").in(estados));

        Subquery<Integer> sqBId = cq.subquery(Integer.class);
        Root<Movimiento> m3b = sqBId.from(Movimiento.class);
        Expression<Integer> m3bId = m3b.get("id").as(Integer.class);
        Expression<LocalDateTime> m3bFecha = m3b.get("fechaAsignacion").as(LocalDateTime.class);
        sqBId.select(cb.greatest(m3bId));
        sqBId.where(
                cb.equal(m3b.get("documento"), doc),
                m3b.get("estado").in(estados),
                cb.equal(m3bFecha, mFecha));

        Predicate casoB = cb.and(
                cb.isNotNull(doc.get("id")),
                cb.equal(mFecha, sqBFecha),
                cb.equal(mId, sqBId));

        return cb.or(casoA, casoB);
    }


    public static <T> Predicate ultimoMovimientoGlobal(
        CriteriaBuilder cb,
        AbstractQuery<T> cq,
        Root<Movimiento> m,       // root de Movimiento en tu query actual
        From<?,?> doc,            // m.join("documento", LEFT)
        From<?,?> cMov            // m.join("carpeta", LEFT)
) {
    // Campos del movimiento actual
    Expression<java.time.LocalDateTime> mFecha = m.get("fechaAsignacion").as(java.time.LocalDateTime.class);
    Expression<Integer>                mId    = m.get("id").as(Integer.class);

    // ---------- Caso A: agrupar por CARPETA (cuando doc IS NULL) ----------
    // Max(fecha) para esa carpeta
    Subquery<java.time.LocalDateTime> sqAFecha = cq.subquery(java.time.LocalDateTime.class);
    Root<Movimiento> m2A = sqAFecha.from(Movimiento.class);
    sqAFecha.select(cb.greatest(m2A.get("fechaAsignacion").as(java.time.LocalDateTime.class)));
    sqAFecha.where(cb.equal(m2A.get("carpeta").get("id"), cMov.get("id")));

    // Tie-break por id: max(id) en esa fecha y carpeta
    Subquery<Integer> sqAId = cq.subquery(Integer.class);
    Root<Movimiento> m2B = sqAId.from(Movimiento.class);
    sqAId.select(cb.greatest(m2B.get("id").as(Integer.class)));
    sqAId.where(
            cb.equal(m2B.get("carpeta").get("id"), cMov.get("id")),
            cb.equal(m2B.get("fechaAsignacion"), mFecha)
    );

    Predicate casoA = cb.and(
            cb.isNull(doc.get("id")),          // este movimiento está “por carpeta”
            cb.equal(mFecha, sqAFecha),        // tiene la fecha máxima de su carpeta
            cb.equal(mId, sqAId)               // y el id máximo en esa fecha (tie-break)
    );

    // ---------- Caso B: agrupar por DOCUMENTO (cuando doc IS NOT NULL) ----------
    // Max(fecha) para ese documento
    Subquery<java.time.LocalDateTime> sqBFecha = cq.subquery(java.time.LocalDateTime.class);
    Root<Movimiento> m3A = sqBFecha.from(Movimiento.class);
    sqBFecha.select(cb.greatest(m3A.get("fechaAsignacion").as(java.time.LocalDateTime.class)));
    sqBFecha.where(cb.equal(m3A.get("documento").get("id"), doc.get("id")));

    // Tie-break por id: max(id) en esa fecha y documento
    Subquery<Integer> sqBId = cq.subquery(Integer.class);
    Root<Movimiento> m3B = sqBId.from(Movimiento.class);
    sqBId.select(cb.greatest(m3B.get("id").as(Integer.class)));
    sqBId.where(
            cb.equal(m3B.get("documento").get("id"), doc.get("id")),
            cb.equal(m3B.get("fechaAsignacion"), mFecha)
    );

    Predicate casoB = cb.and(
            cb.isNotNull(doc.get("id")),       // este movimiento está “por documento”
            cb.equal(mFecha, sqBFecha),        // tiene la fecha máxima de su documento
            cb.equal(mId, sqBId)               // y el id máximo en esa fecha (tie-break)
    );

    return cb.or(casoA, casoB); // último global = último por carpeta OR último por documento
}

    /**
     * Aplica el "scope" del usuario:
     * <ul>
     *   <li>Si hay juzgadoId y oficialiaId: {@code o.id = :oficialiaId OR j.id = :juzgadoId}.</li>
     *   <li>Si solo uno viene distinto de null: usa solo ese.</li>
     *   <li>Si ambos null: retorna {@code FALSE} (equivalente a no tener ámbito).</li>
     * </ul>
     */
    public static Predicate buildScopePredicate(
            CriteriaBuilder cb,
            Join<Movimiento, Juzgado> jMov,
            Join<Movimiento, Oficialia> oMov,
            Integer juzgadoId,
            Integer oficialiaId
    ) {
        if (juzgadoId != null && oficialiaId != null) {
            return cb.or(
                    cb.equal(oMov.get("id"), oficialiaId),
                    cb.equal(jMov.get("id"), juzgadoId));
        } else if (juzgadoId != null) {
            return cb.equal(jMov.get("id"), juzgadoId);
        } else if (oficialiaId != null) {
            return cb.equal(oMov.get("id"), oficialiaId);
        } else {
            return cb.disjunction(); // siempre FALSE
        }
    }
}
