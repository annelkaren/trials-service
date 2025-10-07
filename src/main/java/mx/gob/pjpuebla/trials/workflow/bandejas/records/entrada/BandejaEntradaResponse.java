package mx.gob.pjpuebla.trials.workflow.bandejas.records.entrada;

import java.time.LocalDateTime;

import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;

public record BandejaEntradaResponse(
    Integer movimientoId,
    Integer idDocumento,
    Integer idCarpeta,
    String folio,
    String expediente,
    String materia,
    String tipoEntrada,
    String organoJurisdiccional,
    LocalDateTime fechaRegistro,
    SelloEstatus selloEstatus,
    EstadoCarpeta estatus,
    boolean hasFile,
    String estaEnJuzgado,
    String motivoDevolucion
) {

    public BandejaEntradaResponse(Integer movimientoId, Integer idDocumento, Integer idCarpeta, String folio, String expediente, String materia,
            TipoDocumento tipoDocumento, TipoCarpeta tipoCarpeta, String organoJurisdiccional, LocalDateTime fechaRegistro,
            SelloEstatus selloEstatus, EstadoCarpeta estatus, boolean hasFile, String estaEnJuzgado,
            String motivoDevolucion) {
        this(movimientoId, idDocumento, idCarpeta, folio, expediente, materia,
                tipoDocumento != null ? tipoDocumento.getEtiqueta() : tipoCarpeta.getEtiqueta(),
                organoJurisdiccional, fechaRegistro, selloEstatus, estatus, hasFile, estaEnJuzgado,
                motivoDevolucion);

    }
}
