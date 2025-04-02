package mx.gob.pjpuebla.trials.core.catalogos.sedes.records;

public record SedeDomiciliosRecord(
        Integer sedeId,
        String sedeNombre,
        String calle,
        String interior,
        String exterior,
        String colonia,
        String codigoPostal,
        String municipio,
        String estadoRepublica,
        String referencia,
        String localidad
) {
}
