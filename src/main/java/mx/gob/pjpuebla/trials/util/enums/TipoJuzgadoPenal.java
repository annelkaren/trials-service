package mx.gob.pjpuebla.trials.util.enums;

import lombok.Getter;

@Getter
public enum TipoJuzgadoPenal {
    CONTROL("Control"),
    EJECUCION("Ejecución"),
    ENJUICIAMIENTO("Enjuiciamiento");

    private final String etiqueta;

    TipoJuzgadoPenal(String etiqueta){
        this.etiqueta = etiqueta;
    }
}
