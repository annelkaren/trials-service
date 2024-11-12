package mx.gob.pjpuebla.trials.util.enums.carpeta;

import lombok.Getter;

@Getter
public enum CatalogoSentidoAmparo {
    CONCEDE("Se concede"),
    NIEGA("Se Niega"),
    SOBRESEE("Se sobresee"),
    EFECTOS("Para efectos");

    private final String etiqueta;

    CatalogoSentidoAmparo(String etiqueta){
        this.etiqueta = etiqueta;
    }
}
