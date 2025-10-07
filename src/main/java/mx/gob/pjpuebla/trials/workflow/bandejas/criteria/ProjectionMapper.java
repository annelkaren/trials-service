package mx.gob.pjpuebla.trials.workflow.bandejas.criteria;


import jakarta.persistence.criteria.*;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.entrada.BandejaEntradaResponse;

import static mx.gob.pjpuebla.trials.workflow.bandejas.BandejaCriteriaHelper.capitalizeExpr;

import org.springframework.lang.Nullable;

/**
 * Centraliza la proyección (SELECT) a BandejaEntradaResponse.
 * Recibe las Joins y, opcionalmente, expresiones ya calculadas (idDocumento/hasFile/estaEnJuzgado)
 * para no duplicar lógica de negocio en el repositorio.
 */
public final class ProjectionMapper {

    private ProjectionMapper() {}

    /**
     * Selección completa (constructor expression) para BandejaEntradaResponse.
     *
     * @param cb   CriteriaBuilder
     * @param cq   CriteriaQuery destino (tipo BandejaEntradaResponse)
     * @param j    joins tipadas (BandejaCriteriaSupport.buildJoins)
     * @param idDocumentoExpr expresión de idDocumento ya resuelta (tu helper canónico)
     * @param hasFileExpr     expresión hasFile ya resuelta (tu helper canónico)
     * @param estaEnJuzgadoOverride si no es null, se usa; si es null, se aplica el CASE por defecto
     */
    public static Selection<BandejaEntradaResponse> bandejaResponse(
            CriteriaBuilder cb,
            CriteriaQuery<BandejaEntradaResponse> cq,
            BandejaCriteriaSupport.Joins j,
            Expression<Integer> idDocumentoExpr,
            Expression<Boolean> hasFileExpr,
            @Nullable Expression<String> estaEnJuzgadoOverride
    ) {
        // Folio por presencia (doc si hay, si no carpeta)
        Expression<String> folio = cb.<String>coalesce()
                .value(j.doc().get("folio"))
                .value(j.cMov().get("folio"));

        // Expediente
        Expression<String> expediente = cb.<String>coalesce()
                .value(j.cDoc().get("expediente"))
                .value(j.cMov().get("expediente"));

        // Materia y órgano jurisdiccional (capitalizados)
        Expression<String> materiaRaw = cb.<String>coalesce()
                .value(j.matDoc().get("nombre"))
                .value(j.matMov().get("nombre"));
        Expression<String> materiaCap = capitalizeExpr(cb, materiaRaw);

        Expression<String> organoRaw = cb.<String>coalesce()
                .value(j.jcDoc().get("nombre"))
                .value(j.jcMov().get("nombre"));
        Expression<String> organoCap = capitalizeExpr(cb, organoRaw);

        // Sello/Estatus
        Expression<SelloEstatus> sello = cb.<SelloEstatus>coalesce()
                .value(j.cDoc().get("selloEstatus"))
                .value(j.cMov().get("selloEstatus"));

        Expression<EstadoCarpeta> estatus = cb.<EstadoCarpeta>coalesce()
                .value(j.cDoc().get("estatus"))
                .value(j.cMov().get("estatus"));

        // CASE por defecto para "estaEnJuzgado" (si no te pasan override)
        Expression<String> estaEnJuzDefault = cb.<String>selectCase()
                .when(cb.equal(j.m().get("estado"), "CAPTURA"), "En Juzgado")
                .when(cb.equal(j.m().get("estado"), "SALIDA"), "En Juzgado")
                .when(cb.equal(j.m().get("estado"), "DEVUELTO_A_OFICIALIA"), "En Juzgado")
                .otherwise(cb.literal(""));

        Expression<String> estaEnJuz = (estaEnJuzgadoOverride != null) ? estaEnJuzgadoOverride : estaEnJuzDefault;

        // idCarpeta: coalesce(cDoc.id, cMov.id)
        Expression<Integer> idCarpeta = cb.<Integer>coalesce()
                .value(j.cDoc().get("id").as(Integer.class))
                .value(j.cMov().get("id").as(Integer.class));

        // IMPORTANTE: el DTO espera TipoDocumento y TipoCarpeta por separado; no cambies el orden
        return cb.construct(
                BandejaEntradaResponse.class,
                j.m().get("id").as(Integer.class),               // movimientoId
                idDocumentoExpr,                                  // idDocumento (canónico)
                idCarpeta,                                        // idCarpeta
                folio,                                            // folio mostrado
                expediente,
                materiaCap,                                       // materia (Cap)
                j.doc().get("tipoDocumento"),                     // TipoDocumento (NO tocar)
                j.cMov().get("tipoCarpeta"),                      // TipoCarpeta (NO tocar)
                organoCap,                                        // órgano (Cap)
                j.m().get("fechaAsignacion"),                     // fechaRegistro (LocalDateTime)
                sello,
                estatus,
                hasFileExpr,                                      // hasFile (canónico)
                estaEnJuz,
                j.m().get("motivo")
        );
    }
}
