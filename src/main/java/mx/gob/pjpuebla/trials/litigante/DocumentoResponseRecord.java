package mx.gob.pjpuebla.trials.litigante;

import java.time.LocalDate;

import mx.gob.pjpuebla.trials.util.enums.Migrado;
//Se agrega bandera para definir si es un expediente migrado o no.
public record DocumentoResponseRecord(
    String id,
    LocalDate fechaResolucion,
    String rubros,
    String rutaArchivo,
    Migrado migrado
) {
    //Creación de constructor para enlazar datos de sistema SECGJ PHP
    public DocumentoResponseRecord(Integer id, LocalDate fechaResolucion, String rubros, String rutaArchivo) {
        this(String.valueOf(id), fechaResolucion, rubros, rutaArchivo, Migrado.SI);
    }

    //Creacion de constructor por defecto para no influir en el flujo que ya esta:
    public DocumentoResponseRecord(String id, LocalDate fechaResolucion, String rubros, String rutaArchivo) {
        this(id, fechaResolucion, rubros, rutaArchivo, Migrado.NO);
    }
}
