package mx.gob.pjpuebla.migracion.readers.exhortoForaneo;

import java.time.LocalDate;

import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;

public record ExhortoSalidaMigracionRecordSave2(
    String tramite,
    String destino, 
    String observaciones,
    LocalDate fechaDevolucion, 
    Carpeta carpeta,
    String folio
) {}


//intercambiar 

// observaciones obse
//fechaDevolucion fechaDev

// agregar fecha de entrega que es fechaEn

// fecha_re - hora_re 

// numero + año = expediente 

// folio seria id_ec

