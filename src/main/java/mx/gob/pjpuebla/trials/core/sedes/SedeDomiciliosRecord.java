package mx.gob.pjpuebla.trials.core.sedes;

public record SedeDomiciliosRecord(
        Integer IdSedes,
        String  sedeNombre,
       String  calle,
       String  interior,
       String  exterior,
       String  colonia,
       String  codigoPostal,
       String  municipio,
       String  estadoRepublica,
       String  referencia,
       String  localidad
) {
}
