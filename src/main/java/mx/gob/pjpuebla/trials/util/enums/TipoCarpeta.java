package mx.gob.pjpuebla.trials.util.enums;

import lombok.Getter;

@Getter
public enum TipoCarpeta {
    DEMANDA("Demanda", "Demandas"),
    EXHORTO("Exhorto", "Exhortos"),
    APELACION("Apelación","Apelaciones"),
    DESPACHO("Despacho","Despachos"),
    APELACION_MUNICIPAL("Apelación municipal","Apelaciones municipales"),
    AMPARO("Amparo", "Amparos"),
    CARTA_ROGATORIA("Carta rogatoria", "Cartas rogatorias"),
    COOPERACION_JUDICIAL_E_INTERNACIONAL("Cooperación judicial e internacional", "Cooperaciones judiciales e internacionales"),
    OFICIO("Oficio","Oficios"),
    PIEZA("Pieza", "Piezas");

    private final String etiqueta;
    private final String plural;

    TipoCarpeta(String etiqueta, String plural){
        this.etiqueta=etiqueta;
        this.plural=plural;
    }
}
