package mx.gob.pjpuebla.trials.workflow.bandejas;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Locale;

import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.criteria.CriteriaBuilder.Trimspec;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.entrada.BandejaEntradaFilter;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.entrada.BandejaEntradaResponse;

import mx.gob.pjpuebla.trials.workflow.movimientos.Movimiento;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;

@Repository
public class BandejaRepositoryCustomImpl implements BandejaRepositoryCustom {

        private static final Pattern KEY_COMPOSITE = Pattern.compile("^\\s*([A-Za-z])\\.(.+)\\s*$");

        private static class KeyMap {
                final TipoDocumento docType; // puede ser null (Demanda)
                final TipoCarpeta carpType; // puede ser null (para demanda ponemos DEMANDA si te sirve en otros lados)

                KeyMap(TipoDocumento d, TipoCarpeta c) {
                        this.docType = d;
                        this.carpType = c;
                }
        }

        // Mapeo letra -> tipo de documento/carpeta
        private static KeyMap mapKeyPrefix(char ch) {
                switch (Character.toUpperCase(ch)) {
                        case 'D':
                                return new KeyMap(null, TipoCarpeta.DEMANDA);
                        case 'A':
                                return new KeyMap(TipoDocumento.APELACION, null);
                        case 'P':
                                return new KeyMap(TipoDocumento.PROMOCION, null);
                        case 'E':
                                return new KeyMap(TipoDocumento.EXHORTO, null);
                        case 'O':
                                return new KeyMap(TipoDocumento.OFICIO, null);
                        // Ejemplos extra por si luego los piden:
                        // case 'S': return new KeyMap(TipoDocumento.SENTENCIA, null);
                        // case 'M': return new KeyMap(TipoDocumento.AMPARO, null);
                        default:
                                return null;
                }
        }

        /**
         * Si key es del tipo "X.folio", agrega a 'ps' un predicado que filtre
         * por tipo (según la X) y folio del DOCUMENTO (considerando fallback).
         * Devuelve true si aplicó la regla especial (para que no se agregue el OR
         * genérico).
         */
        private <T> boolean tryAddCompositeKeyPredicate(
                        CriteriaBuilder cb,
                        CriteriaQuery<T> cq,
                        List<Predicate> ps,
                        Join<Movimiento, Documento> doc,
                        Join<Movimiento, Carpeta> cMov,
                        String rawKey) {
                if (!notBlank(rawKey))
                        return false;
                Matcher m = KEY_COMPOSITE.matcher(rawKey);
                if (!m.matches())
                        return false;

                char prefix = m.group(1).toUpperCase().charAt(0);
                String folioToken = m.group(2).trim();
                if (folioToken.isEmpty())
                        return false;

                KeyMap km = mapKeyPrefix(prefix);
                if (km == null)
                        return false;

                String like = normalizeLike(folioToken); // %token%

                // Rama 1: el movimiento SI trae doc -> comparamos contra ese doc
                Predicate p1;
                if (km.docType == null) {
                        // DEMANDA: tipoDocumento IS NULL
                        p1 = cb.and(
                                        cb.isNotNull(doc),
                                        cb.isNull(doc.get("tipoDocumento")),
                                        cb.like(cb.lower(doc.get("folio")), like));
                } else {
                        p1 = cb.and(
                                        cb.isNotNull(doc),
                                        cb.equal(doc.get("tipoDocumento"), km.docType),
                                        cb.like(cb.lower(doc.get("folio")), like));
                }

                // Rama 2: el movimiento NO trae doc -> existe documento "canónico" en la
                // carpeta con ese tipo y folio
                Subquery<Integer> sq = cq.subquery(Integer.class);
                Root<Documento> d = sq.from(Documento.class);
                Join<Documento, Carpeta> dCarp = d.join("carpeta", JoinType.INNER);

                List<Predicate> whereSq = new ArrayList<>();
                whereSq.add(cb.equal(dCarp.get("id"), cMov.get("id")));
                if (km.docType == null) {
                        whereSq.add(cb.isNull(d.get("tipoDocumento"))); // DEMANDA
                } else {
                        whereSq.add(cb.equal(d.get("tipoDocumento"), km.docType));
                }
                whereSq.add(cb.like(cb.lower(d.get("folio")), like));

                sq.select(cb.literal(1));
                sq.where(whereSq.toArray(new Predicate[0]));

                Predicate p2 = cb.and(cb.isNull(doc), cb.exists(sq));

                ps.add(cb.or(p1, p2));
                return true;
        }

        @PersistenceContext
        private EntityManager em;

        @Override
        public Page<BandejaEntradaResponse> findBandejaEntradas(
                        Pageable pageable,
                        List<String> estados,
                        @Nullable BandejaEntradaFilter filtro) {
                CriteriaBuilder cb = em.getCriteriaBuilder();

                // ====== Query de datos ======
                CriteriaQuery<BandejaEntradaResponse> cq = cb.createQuery(BandejaEntradaResponse.class);
                Root<Movimiento> m = cq.from(Movimiento.class);

                // Joins equivalentes JPQL
                Join<Movimiento, Documento> doc = m.join("documento", JoinType.LEFT);
                Join<Movimiento, Carpeta> cMov = m.join("carpeta", JoinType.LEFT);
                Join<Documento, Carpeta> cDoc = doc.join("carpeta", JoinType.LEFT);
                Join<Carpeta, Juzgado> jcMov = cMov.join("juzgado", JoinType.LEFT);
                Join<Carpeta, Juzgado> jcDoc = cDoc.join("juzgado", JoinType.LEFT);
                Join<Juzgado, Materia> matMov = jcMov.join("materia", JoinType.LEFT);
                Join<Juzgado, Materia> matDoc = jcDoc.join("materia", JoinType.LEFT);

                // ====== Expresiones COALESCE ======
                // idCarpeta (asegúrate que el tipo coincide con tu mapeo real)
                Expression<Integer> idCarpeta = cb.<Integer>coalesce()
                                .value(cDoc.get("id").as(Integer.class))
                                .value(cMov.get("id").as(Integer.class));

                Expression<String> folio = cb.<String>coalesce()
                                .value(cDoc.get("folio"))
                                .value(cMov.get("folio"));

                Expression<String> expediente = cb.<String>coalesce()
                                .value(cDoc.get("expediente"))
                                .value(cMov.get("expediente"));

                Expression<String> materiaRaw = cb.<String>coalesce()
                                .value(matDoc.get("nombre"))
                                .value(matMov.get("nombre"));
                Expression<String> materiaNombre = capitalizeExpr(cb, materiaRaw);

                Expression<String> organoJ = cb.<String>coalesce()
                                .value(jcDoc.get("nombre"))
                                .value(jcMov.get("nombre"));

                Expression<SelloEstatus> selloEstatus = cb.<SelloEstatus>coalesce()
                                .value(cDoc.get("selloEstatus"))
                                .value(cMov.get("selloEstatus"));

                Expression<EstadoCarpeta> estatus = cb.<EstadoCarpeta>coalesce()
                                .value(cDoc.get("estatus"))
                                .value(cMov.get("estatus"));

                Expression<String> estaEnJuzgado = cb.<String>selectCase()
                                .when(
                                                cb.or(
                                                                cb.equal(m.get("estado"), "CAPTURA"),
                                                                cb.equal(m.get("estado"), "SALIDA"),
                                                                cb.equal(m.get("estado"), "DEVUELTO_A_OFICIALIA")),
                                                "En Juzgado")
                                .otherwise(cb.literal(""));

                // ====== WHERE base y últimos ======
                Predicate baseEstados = m.get("estado").in(estados);
                Predicate ultimoMovimiento = buildUltimoMovimientoPredicate(cb, cq, m, doc, cMov, estados);

                // ====== Filtros (record) ======
                Predicate filtrosExtras = buildFiltersPredicate(cb, cq,
                                m, doc, cMov, cDoc, jcMov, jcDoc, matMov, matDoc, filtro);

                // ====== SELECT DTO por constructor (usa tu ctor “largo”) ======
                Expression<Integer> idDocumentoFinal = buildIdDocumentoExpr(cb, cq, doc, cMov);
                Expression<Boolean> hasFile = buildHasFileExpr(cb, cq, doc, cMov);

                cq.select(cb.construct(
                                BandejaEntradaResponse.class,
                                m.get("id").as(Integer.class), // movimientoId
                                idDocumentoFinal, // idDocumento
                                idCarpeta, // idCarpeta (coalesce)
                                folio, // folio (coalesce)
                                expediente, // expediente (coalesce)
                                materiaNombre, // materia (coalesce)
                                doc.get("tipoDocumento"), // TipoDocumento
                                cMov.get("tipoCarpeta"), // TipoCarpeta
                                organoJ, // organoJurisdiccional (coalesce)
                                m.get("fechaAsignacion"), // fechaRegistro
                                selloEstatus, // selloEstatus (coalesce)
                                estatus, // estatus (coalesce)
                                hasFile, // hasFile (case/exists)
                                estaEnJuzgado, // estaEnJuzgado (case)
                                m.get("motivo") // motivoDevolucion
                ));

                Predicate estatusOk = cb.or(
                                cb.and(cb.isNotNull(cMov), cMov.get("estatus").in(ESTATUS_OK)),
                                cb.and(cb.isNotNull(doc), cDoc.get("estatus").in(ESTATUS_OK)));

                cq.where(cb.and(baseEstados, estatusOk, ultimoMovimiento, filtrosExtras));

                // ====== ORDER BY (global, antes de paginar) ======
                List<Order> orders = buildOrderBy(cb, cq, m, doc, cMov, cDoc, jcMov, jcDoc, matMov, matDoc,
                                pageable.getSort());
                if (!orders.isEmpty()) {
                        cq.orderBy(orders);
                }

                // ====== Ejecutar datos (paginado) ======
                TypedQuery<BandejaEntradaResponse> tq = em.createQuery(cq);
                tq.setFirstResult((int) pageable.getOffset());
                tq.setMaxResults(pageable.getPageSize());
                List<BandejaEntradaResponse> content = tq.getResultList();

                // ====== COUNT (mismo WHERE) ======
                CriteriaQuery<Long> countQ = cb.createQuery(Long.class);
                Root<Movimiento> mC = countQ.from(Movimiento.class);
                Join<Movimiento, Documento> docC = mC.join("documento", JoinType.LEFT);
                Join<Movimiento, Carpeta> cMovC = mC.join("carpeta", JoinType.LEFT);
                Join<Documento, Carpeta> cDocC = docC.join("carpeta", JoinType.LEFT);
                Join<Carpeta, Juzgado> jcMovC = cMovC.join("juzgado", JoinType.LEFT);
                Join<Carpeta, Juzgado> jcDocC = cDocC.join("juzgado", JoinType.LEFT);
                Join<Juzgado, Materia> matMovC = jcMovC.join("materia", JoinType.LEFT);
                Join<Juzgado, Materia> matDocC = jcDocC.join("materia", JoinType.LEFT);

                Predicate baseEstadosC = mC.get("estado").in(estados);
                Predicate ultimoMovimientoC = buildUltimoMovimientoPredicate(cb, countQ, mC, docC, cMovC, estados);
                Predicate filtrosExtrasC = buildFiltersPredicate(cb, countQ,
                                mC, docC, cMovC, cDocC, jcMovC, jcDocC, matMovC, matDocC, filtro);

                countQ.select(cb.countDistinct(mC));
                Predicate estatusOkC = cb.or(
                                cMovC.get("estatus").in(ESTATUS_OK),
                                cDocC.get("estatus").in(ESTATUS_OK));

                countQ.where(cb.and(baseEstadosC, estatusOkC, ultimoMovimientoC, filtrosExtrasC));

                long total = em.createQuery(countQ).getSingleResult();

                return new PageImpl<>(content, pageable, total);
        }

        // ----------------- HELPERS -----------------

        /**
         * Replica tu condición:
         * (doc IS NULL y último por carpeta) OR (doc IS NOT NULL y último por
         * documento)
         */
        private <T> Predicate buildUltimoMovimientoPredicate(
                        CriteriaBuilder cb,
                        CriteriaQuery<T> cq,
                        Root<Movimiento> m,
                        Join<Movimiento, Documento> doc,
                        Join<Movimiento, Carpeta> cMov,
                        List<String> estados) {
                // --- Tipado fuerte de paths del root actual ---
                Expression<LocalDateTime> mFecha = m.get("fechaAsignacion").as(LocalDateTime.class);
                Expression<Integer> mId = m.get("id").as(Integer.class);

                // ========= Caso A: doc IS NULL (último por carpeta) =========
                // Subquery de fecha máxima
                Subquery<LocalDateTime> sqAFecha = cq.subquery(LocalDateTime.class);
                Root<Movimiento> m2 = sqAFecha.from(Movimiento.class);
                Expression<LocalDateTime> m2Fecha = m2.get("fechaAsignacion").as(LocalDateTime.class);

                sqAFecha.select(cb.greatest(m2Fecha));
                sqAFecha.where(
                                cb.equal(m2.get("carpeta"), cMov),
                                m2.get("estado").in(estados));

                // Subquery de id máximo en esa misma fecha
                Subquery<Integer> sqAId = cq.subquery(Integer.class);
                Root<Movimiento> m2b = sqAId.from(Movimiento.class);
                Expression<LocalDateTime> m2bFecha = m2b.get("fechaAsignacion").as(LocalDateTime.class);
                Expression<Integer> m2bId = m2b.get("id").as(Integer.class);

                sqAId.select(cb.greatest(m2bId));
                sqAId.where(
                                cb.equal(m2b.get("carpeta"), cMov),
                                m2b.get("estado").in(estados),
                                cb.equal(m2bFecha, mFecha));

                Predicate casoA = cb.and(
                                cb.isNull(doc),
                                cb.equal(mFecha, sqAFecha),
                                cb.equal(mId, sqAId));

                // ========= Caso B: doc IS NOT NULL (último por documento) =========
                // Subquery de fecha máxima
                Subquery<LocalDateTime> sqBFecha = cq.subquery(LocalDateTime.class);
                Root<Movimiento> m3 = sqBFecha.from(Movimiento.class);
                Expression<LocalDateTime> m3Fecha = m3.get("fechaAsignacion").as(LocalDateTime.class);

                sqBFecha.select(cb.greatest(m3Fecha));
                sqBFecha.where(
                                cb.equal(m3.get("documento"), doc),
                                m3.get("estado").in(estados));

                // Subquery de id máximo en esa misma fecha
                Subquery<Integer> sqBId = cq.subquery(Integer.class);
                Root<Movimiento> m3b = sqBId.from(Movimiento.class);
                Expression<LocalDateTime> m3bFecha = m3b.get("fechaAsignacion").as(LocalDateTime.class);
                Expression<Integer> m3bId = m3b.get("id").as(Integer.class);

                sqBId.select(cb.greatest(m3bId));
                sqBId.where(
                                cb.equal(m3b.get("documento"), doc),
                                m3b.get("estado").in(estados),
                                cb.equal(m3bFecha, mFecha));

                Predicate casoB = cb.and(
                                cb.isNotNull(doc),
                                cb.equal(mFecha, sqBFecha),
                                cb.equal(mId, sqBId));

                return cb.or(casoA, casoB);
        }

        /**
         * Construye los filtros del record BandejaEntradaFilter:
         * folio, expediente, materia, organoJurisdiccional, fechaRegistro, tipoEntrada,
         * key
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

                Expression<String> folio = cb.<String>coalesce()
                                .value(cDoc.get("folio"))
                                .value(cMov.get("folio"));

                Expression<String> expediente = cb.<String>coalesce()
                                .value(cDoc.get("expediente"))
                                .value(cMov.get("expediente"));

                Expression<String> materiaRaw = cb.<String>coalesce()
                                .value(matDoc.get("nombre"))
                                .value(matMov.get("nombre"));
                Expression<String> materiaNombre = capitalizeExpr(cb, materiaRaw);

                Expression<String> organoJ = cb.<String>coalesce()
                                .value(jcDoc.get("nombre"))
                                .value(jcMov.get("nombre"));

                if (notBlank(f.folio())) {
                        ps.add(likeIgnoreCase(cb, folio, f.folio()));
                }
                if (notBlank(f.expediente())) {
                        ps.add(likeIgnoreCase(cb, expediente, f.expediente()));
                }
                if (notBlank(f.materia())) {
                        ps.add(likeIgnoreCase(cb, materiaNombre, f.materia()));
                }
                if (notBlank(f.organoJurisdiccional())) {
                        ps.add(likeIgnoreCase(cb, organoJ, f.organoJurisdiccional()));
                }

                if (f.fechaRegistro() != null) {
                        LocalDateTime start = f.fechaRegistro().withHour(0).withMinute(0).withSecond(0).withNano(0);
                        LocalDateTime end = start.plusDays(1);
                        ps.add(cb.greaterThanOrEqualTo(m.get("fechaAsignacion"), start));
                        ps.add(cb.lessThan(m.get("fechaAsignacion"), end));
                }

                // tipoEntrada: (doc != null ? doc.tipoDocumento : cMov.tipoCarpeta) == valor
                if (notBlank(f.tipoEntrada())) {
                        var docEnum = parseEnumSafe(TipoDocumento.class, f.tipoEntrada());
                        var carpEnum = parseEnumSafe(TipoCarpeta.class, f.tipoEntrada());

                        List<Predicate> ors = new ArrayList<>();

                        docEnum.ifPresent(e -> ors.add(cb.and(
                                        cb.isNotNull(doc),
                                        cb.equal(doc.get("tipoDocumento"), e) // <-- compara contra la constante enum
                        )));

                        carpEnum.ifPresent(e -> ors.add(cb.and(
                                        cb.isNull(doc),
                                        cb.equal(cMov.get("tipoCarpeta"), e) // <-- idem
                        )));

                        if (ors.isEmpty()) {
                                ps.add(cb.disjunction());
                        } else {
                                ps.add(cb.or(ors.toArray(new Predicate[0])));
                        }
                }

                // key: OR sobre varias columnas
                if (notBlank(f.key())) {
                        // Si aplica la regla especial "X.folio", la usamos y NO agregamos el OR
                        // genérico:
                        boolean applied = tryAddCompositeKeyPredicate(cb, cq, ps, doc, cMov, f.key());
                        if (!applied) {
                                // Comportamiento actual (OR genérico sobre varios campos)
                                String k = normalizeLike(f.key());
                                List<Predicate> ors = new ArrayList<>();
                                ors.add(cb.like(cb.lower(folio), k));
                                ors.add(cb.like(cb.lower(expediente), k));
                                ors.add(cb.like(cb.lower(materiaNombre), k));
                                ors.add(cb.like(cb.lower(organoJ), k));
                                ors.add(cb.like(cb.lower(m.get("motivo")), k));
                                ps.add(cb.or(ors.toArray(new Predicate[0])));
                        }
                }

                return ps.isEmpty() ? cb.conjunction() : cb.and(ps.toArray(new Predicate[0]));
        }

        /**
         * Traduce Pageable.getSort() a orderBy(...) sobre expresiones equivalentes
         * a los campos del filtro.
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

                // Expresiones reutilizables (mismas que en SELECT)
                Expression<String> folio = cb.<String>coalesce()
                                .value(cDoc.get("folio"))
                                .value(cMov.get("folio"));

                Expression<String> expediente = cb.<String>coalesce()
                                .value(cDoc.get("expediente"))
                                .value(cMov.get("expediente"));

                Expression<String> materiaNombre = cb.<String>coalesce()
                                .value(matDoc.get("nombre"))
                                .value(matMov.get("nombre"));

                Expression<String> organoJ = cb.<String>coalesce()
                                .value(jcDoc.get("nombre"))
                                .value(jcMov.get("nombre"));

                // tipoEntrada como String para ordenar
                Expression<String> tipoEntradaStr = cb.<String>selectCase()
                                .when(cb.isNotNull(doc), doc.get("tipoDocumento").as(String.class))
                                .otherwise(cMov.get("tipoCarpeta").as(String.class));

                for (Sort.Order o : sort) {
                        String prop = o.getProperty();
                        boolean asc = o.isAscending();

                        switch (prop) {

                                case "folio" -> orders.add(asc ? cb.asc(cb.lower(folio)) : cb.desc(cb.lower(folio)));
                                case "expediente" ->
                                        orders.add(asc ? cb.asc(cb.lower(expediente)) : cb.desc(cb.lower(expediente)));
                                case "materia" -> orders.add(asc ? cb.asc(cb.lower(materiaNombre))
                                                : cb.desc(cb.lower(materiaNombre)));
                                case "organoJurisdiccional" ->
                                        orders.add(asc ? cb.asc(cb.lower(organoJ)) : cb.desc(cb.lower(organoJ)));
                                case "fechaRegistro" ->
                                        orders.add(asc ? cb.asc(m.get("fechaAsignacion"))
                                                        : cb.desc(m.get("fechaAsignacion")));
                                case "tipoEntrada" -> orders.add(asc
                                                ? cb.asc(cb.lower(tipoEntradaStr))
                                                : cb.desc(cb.lower(tipoEntradaStr)));
                                // fallback: si te interesan otras columnas, agrégalas aquí
                                default -> {
                                } // ignora propiedades desconocidas
                        }
                }
                return orders;
        }

        // ---------- utilidades pequeñas ----------

        private static boolean notBlank(String s) {
                return s != null && !s.isBlank();
        }

        private static Predicate likeIgnoreCase(CriteriaBuilder cb, Expression<String> path, String value) {
                return cb.like(cb.lower(path), normalizeLike(value));
        }

        private static String normalizeLike(String value) {
                return "%" + value.toLowerCase() + "%";
        }

        private static <E extends Enum<E>> Optional<E> parseEnumSafe(Class<E> type, String raw) {
                if (raw == null)
                        return Optional.empty();
                String norm = normalizeEnumToken(raw);
                try {
                        return Optional.of(Enum.valueOf(type, norm));
                } catch (IllegalArgumentException ex) {
                        return Optional.empty();
                }
        }

        private static String normalizeEnumToken(String s) {
                // Normaliza: trim, mayúsculas, espacios/guiones a "_", y sin acentos (útil si
                // vienen como "Promoción")
                String t = s.trim().toUpperCase(Locale.ROOT)
                                .replace(' ', '_')
                                .replace('-', '_');
                t = Normalizer.normalize(t, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
                return t;
        }

        private static <E extends Enum<E>> List<E> byOrdinals(Class<E> type, int... ords) {
                E[] all = type.getEnumConstants();
                List<E> out = new ArrayList<>();
                for (int o : ords)
                        out.add(all[o]);
                return out;
        }

        private static final List<EstadoCarpeta> ESTATUS_OK = byOrdinals(EstadoCarpeta.class, 0, 14);

        private <T> Expression<Integer> buildIdDocumentoExpr(
                        CriteriaBuilder cb,
                        CriteriaQuery<T> cq,
                        Join<Movimiento, Documento> doc,
                        Join<Movimiento, Carpeta> cMov) {
                // Subquery: documento "canónico" por carpeta (según tipoCarpeta)
                Subquery<Integer> sqDocFallback = cq.subquery(Integer.class);
                Root<Documento> d = sqDocFallback.from(Documento.class);
                Join<Documento, Carpeta> dCarp = d.join("carpeta", JoinType.INNER);

                Predicate mismaCarpeta = cb.equal(dCarp.get("id").as(Integer.class), cMov.get("id").as(Integer.class));

                // Mapeo de tu servicio:
                // DEMANDA → tipoDocumento IS NULL
                // EXHORTO → EXHORTO
                // APELACION → APELACION
                // PIEZA → PROMOCION
                Predicate mapeo = cb.or(
                                cb.and(cb.equal(cMov.get("tipoCarpeta"), TipoCarpeta.DEMANDA),
                                                cb.isNull(d.get("tipoDocumento"))),
                                cb.and(cb.equal(cMov.get("tipoCarpeta"), TipoCarpeta.EXHORTO),
                                                cb.equal(d.get("tipoDocumento"), TipoDocumento.EXHORTO)),
                                cb.and(cb.equal(cMov.get("tipoCarpeta"), TipoCarpeta.APELACION),
                                                cb.equal(d.get("tipoDocumento"), TipoDocumento.APELACION)),
                                cb.and(cb.equal(cMov.get("tipoCarpeta"), TipoCarpeta.PIEZA),
                                                cb.equal(d.get("tipoDocumento"), TipoDocumento.PROMOCION)));

                // Si hubiera varios, elegimos el de id mayor (ajusta a MIN si prefieres)
                sqDocFallback.select(cb.greatest(d.get("id").as(Integer.class)));
                sqDocFallback.where(mismaCarpeta, mapeo);

                // COALESCE: si m.documento existe, usa su id; si no, usa el fallback
                return cb.<Integer>coalesce()
                                .value(doc.get("id").as(Integer.class))
                                .value(sqDocFallback);
        }

        private <T> Expression<Boolean> buildHasFileExpr(
                        CriteriaBuilder cb,
                        CriteriaQuery<T> cq,
                        Join<Movimiento, Documento> doc,
                        Join<Movimiento, Carpeta> cMov) {
                // EXISTS del documento canónico con ruta (mismo mapeo que arriba)
                Subquery<Integer> sq = cq.subquery(Integer.class);
                Root<Documento> d = sq.from(Documento.class);
                Join<Documento, Carpeta> dCarp = d.join("carpeta", JoinType.INNER);

                Predicate mismaCarpeta = cb.equal(dCarp.get("id"), cMov.get("id"));

                Predicate mapeo = cb.or(
                                cb.and(cb.equal(cMov.get("tipoCarpeta"), TipoCarpeta.DEMANDA),
                                                cb.isNull(d.get("tipoDocumento"))),
                                cb.and(cb.equal(cMov.get("tipoCarpeta"), TipoCarpeta.EXHORTO),
                                                cb.equal(d.get("tipoDocumento"), TipoDocumento.EXHORTO)),
                                cb.and(cb.equal(cMov.get("tipoCarpeta"), TipoCarpeta.APELACION),
                                                cb.equal(d.get("tipoDocumento"), TipoDocumento.APELACION)),
                                cb.and(cb.equal(cMov.get("tipoCarpeta"), TipoCarpeta.PIEZA),
                                                cb.equal(d.get("tipoDocumento"), TipoDocumento.PROMOCION)));

                sq.select(cb.literal(1));
                sq.where(mismaCarpeta, mapeo, cb.isNotNull(d.get("ruta")));

                // Regla: si HAY doc → doc.ruta != null; si NO hay doc → existe fallback con
                // ruta
                return cb.<Boolean>selectCase()
                                .when(cb.isNotNull(doc), cb.isNotNull(doc.get("ruta")))
                                .otherwise(cb.exists(sq));
        }

        // Capitaliza la primera letra y pone el resto en minúsculas.
        // Null-safe y evita TRIM como función nativa del dialecto.
        private Expression<String> capitalizeExpr(CriteriaBuilder cb, Expression<String> expr) {
                // Coalesce a cadena vacía para evitar nulls
                Expression<String> safe = cb.<String>coalesce()
                                .value(expr)
                                .value(cb.literal(""));

                // TRIM nativo de Criteria (no function("trim", ...))
                Expression<String> trimmed = cb.trim(Trimspec.BOTH, safe);

                Expression<Integer> len = cb.length(trimmed);
                Expression<String> first = cb.upper(cb.substring(trimmed, 1, 1));

                // Si la longitud > 1, toma el resto; si no, vacío
                Expression<String> rest = cb.lower(
                                cb.<String>selectCase()
                                                .when(cb.gt(len, 1), cb.substring(trimmed, 2))
                                                .otherwise(cb.literal("")));

                // Si hay al menos 1 char, concatena First + Rest; en otro caso regresa trimmed
                return cb.<String>selectCase()
                                .when(cb.gt(len, 0), cb.concat(first, rest))
                                .otherwise(trimmed);
        }

}