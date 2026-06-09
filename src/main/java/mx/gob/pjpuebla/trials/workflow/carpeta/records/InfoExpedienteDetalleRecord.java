package mx.gob.pjpuebla.trials.workflow.carpeta.records;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public record InfoExpedienteDetalleRecord(
        String determinacion,
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
        String presentacionImputado,
        String solicitudAudiencia,
        LocalDate fechaPresentacionImputado,
        String ultimoDomicilioFamiliar,
        String domicilioAcreedor,
        String domicilioFamiliar,
        String domicilioDemandado,
        String domicilioMenorEdad,
        Integer tipoJuicioHijoId,
        String tipoJuicioHijo,
        String cujus,
        LocalDate fechaEjecutoria,
        Integer ponencia,
        String tipoCarpeta,
        Long juez
) implements Serializable {
}
