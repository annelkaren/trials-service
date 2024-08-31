package mx.gob.pjpuebla.trials.core.domicilios;

public record DomicilioRecord(
        Long id,
        String calle,
        String exterior,
        String interior,
        String estadoRepublica,
        String municipio,
        String localidad,
        String colonia,
        String codigoPostal,
        String referencia) {
}
