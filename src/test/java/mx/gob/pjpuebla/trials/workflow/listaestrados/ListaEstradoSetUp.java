package mx.gob.pjpuebla.trials.workflow.listaestrados;

import mx.gob.pjpuebla.trials.core.personas.PersonaSetUp;

import java.time.LocalDateTime;

public class ListaEstradoSetUp {

    private ListaEstradoSetUp(){

    }

    public static ListaEstrado createLisEstrado() {
        return new ListaEstrado()
                .setId(1)
                .setPersona(PersonaSetUp.createPersona())
                .setFechaAlta(LocalDateTime.now());
    }
}
