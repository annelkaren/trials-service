package mx.gob.pjpuebla.trials.core.listaestrados;

import java.time.LocalDateTime;

public class ListaEstradoSetUp {

    private ListaEstradoSetUp(){

    }

    public static ListaEstrado createLisEstrado() {
        return new ListaEstrado()
                .setId(1)
                .setUsuarioAlta("Alex")
                .setFechaAlta(LocalDateTime.now());
    }
}
