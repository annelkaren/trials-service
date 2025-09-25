package mx.gob.pjpuebla.migracion.readers.actores;

public record ActoresMigracionResponse(
    String nombre, 
    String apellidoPaterno,
    String apellidoMaterno,
    String pseudonimo,
    String tipoPersona,
    Integer rol,
    Integer carpeta, // esta seria la foranea de la tabla tbl_carpetas del nuevo sistema
    Integer tipoParte, //foranea del tipo parte
    String tipoParteText
) {}
