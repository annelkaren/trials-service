package mx.gob.pjpuebla.trials.util.enums;

import lombok.Getter;

@Getter
public enum TipoDocumento {
    PROMOCION("Promoción","Promociones"), 
    OFICIO("Oficio", "Oficios"),
    ACUERDO("Acuerdo", "Acuerdos"),
    AMPARO("Amparo", "Amparos"),
    EXHORTO_SALIDA("Exhorto de salida","Exhortos de salida"),
    SENTENCIA("Sentencia", "Sentencias"),
    SENTENCIA_PUBLICA("Sentencia pública","Sentencias públicas"),
    DOCUMENTO_IDENTIFICACION("Documento de identificación","Documentos de identificación"),
    APELACION("Apelación", "Apelaciones"),
    PRUEBA_AUDIENCIA("Prueba audiencia", "Pruebas audiencia"),
    EXHORTO("Exhorto", "Exortos");

    private final String etiqueta;
    private final String plural;

    TipoDocumento(String etiqueta, String plural) {
        this.etiqueta = etiqueta;
        this.plural = plural;
    }
}
