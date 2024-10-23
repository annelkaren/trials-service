package mx.gob.pjpuebla.trials.util.enums.catalogos;

import lombok.Getter;
import mx.gob.pjpuebla.trials.workflow.carpeta.CatalogoEnum;

@Getter
public enum CatalogoTipoDefensor implements CatalogoEnum {
    DEFENSOR_PUBLICO(0, "Defensor Publico"),
    DEFENSOR_PRIVADO(1, "Defensor Privado")
    ;

    private final int clave;
    private final String valor;

    CatalogoTipoDefensor(int clave, String valor) {
        this.clave = clave;
        this.valor = valor;
    }
}
