package mx.gob.pjpuebla.trials.core.templates;

import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoSetUp;
import mx.gob.pjpuebla.trials.util.enums.TipoTempletes;

public class TemplatesSetUp {
    private TemplatesSetUp(){

    }

    public static Templates createTemplates(){
        return  new Templates()
                .setId(1)
                .setNombre("Template 1")
                .setContenido("Contenido 1")
                .setJuzgado(JuzgadoSetUp.createJuzgado())
                .setTipo(TipoTempletes.ACUERDO);
    }
}
