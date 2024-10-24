package mx.gob.pjpuebla.trials.util.enums.carpeta;

import lombok.Getter;

@Getter
public enum CatalogoTiposDomicilio {
    PARTICULAR("Particular"),
    PROCESAL("Procesal"),
    TRABAJO("Trabajo");

    private final String etiqueta;

    CatalogoTiposDomicilio(String etiqueta) {
        this.etiqueta = etiqueta;
    }
}
