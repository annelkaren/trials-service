package mx.gob.pjpuebla.trials.util.enums.carpeta;

import lombok.Getter;

@Getter
public enum CatalogoTipoDefensor {
    DEFENSOR_PUBLICO("Defensor Público"),
    DEFENSOR_PRIVADO("Defensor Privado");

    private final String etiqueta;

    CatalogoTipoDefensor(String etiqueta) {
        this.etiqueta = etiqueta;
    }
}
