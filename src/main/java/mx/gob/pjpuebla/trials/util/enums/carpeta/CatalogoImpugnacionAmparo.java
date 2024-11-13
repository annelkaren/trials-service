package mx.gob.pjpuebla.trials.util.enums.carpeta;

import lombok.Getter;

@Getter
public enum CatalogoImpugnacionAmparo {
    CONFIRMA("Confirma"),
    REVOCA("Revoca"),
    MODIFICA("Modifica");

    private final String etiqueta;

    CatalogoImpugnacionAmparo(String etiqueta){
        this.etiqueta = etiqueta;
    }
}
