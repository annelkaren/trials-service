package mx.gob.pjpuebla.trials.litigante;

import java.time.LocalDate;
public record DocumentoResponseRecord(
    String id,
    LocalDate fechaResolucion,
    String rubros,
    String rutaArchivo
) {
    //Creación de constructor para enlazar datos de sistema SECGJ PHP
    public DocumentoResponseRecord(Integer id, LocalDate fechaResolucion, String rubros, String rutaArchivo) {
        this(String.valueOf(id), fechaResolucion, rubros, rutaArchivo);
    }
}
