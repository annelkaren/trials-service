package mx.gob.pjpuebla.trials.workflow.carpeta.records;

import mx.gob.pjpuebla.trials.core.rubros.RubroRecord;
import mx.gob.pjpuebla.trials.util.enums.PresentacionImputado;
import mx.gob.pjpuebla.trials.util.enums.SolicitudAudiencia;
import mx.gob.pjpuebla.trials.util.enums.carpeta.CatalogoDeterminacionJurisdiccional;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record SaveExpedienteDetalleRecord(
        CatalogoDeterminacionJurisdiccional determinacion,
        String fechaAdmision,
        String fechaDesechado,
        String ubicacion,
        String asunto,
        String fase,
        String observaciones,
        String sentencia,
        String promovente,
        String numeroCarpetaInvestigacion,
        String numeroOficio,
        String lugarHecho,
        LocalDate fechaHecho,
        Integer cantidadPrincipal,
        String moneda,
        Integer numeroHijos,
        Integer numeroHijosMenoresEdad,
        String actaMatrimonio,
        String lugarRegistroMatrimonio,
        String entidad,
        String municipio,
        String localidad,
        String fechaRegistro,
        LocalTime horaFormal,
        LocalTime horaMaterial,
        String lugarDisposicion,
        PresentacionImputado presentacionImputado,
        SolicitudAudiencia solicitudAudiencia,
        String fechaPresentacionImputado,
        String ultimoDomicilioFamiliar,
        String domicilioAcreedor,
        String domicilioFamiliar,
        String domicilioDemandado,
        String domicilioMenorEdad,
        Integer tipoJuicioHijoId,
        EtapaProcesalRecord etapaProcesal,
        List<RubroRecord> rubros,
        String cujus,
        LocalDate fechaEjecutoria
) implements Serializable {
}
