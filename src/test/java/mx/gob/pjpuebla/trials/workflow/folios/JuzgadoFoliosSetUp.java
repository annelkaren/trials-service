package mx.gob.pjpuebla.trials.workflow.folios;

import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;

import java.time.LocalDate;

import static mx.gob.pjpuebla.trials.core.juzgados.JuzgadoSetUp.createJuzgado;

public class JuzgadoFoliosSetUp {

    private JuzgadoFoliosSetUp() {
    }

    public static JuzgadoFolios createJuzgadoFolios() {
        return new JuzgadoFolios()
                .setId(1)
                .setTipoCarpeta(TipoCarpeta.DEMANDA)
                .setYear(LocalDate.now().getYear())
                .setValue(1)
                .setJuzgado(createJuzgado(Estado.ACTIVE))
                ;
    }
}
