package mx.gob.pjpuebla.trials.core.reljuzgadotipojuicio;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoSetUp;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;

public class RelJuzgadoTipoJuicioSetUp {
    private RelJuzgadoTipoJuicioSetUp() {
    }

    public static RelJuzgadoTipoJuicio createRelJuzgadoTipoJuicio() {
        RelJuzgadoTipoJuicio relJuzgadoTipoJuicio = new RelJuzgadoTipoJuicio();
        relJuzgadoTipoJuicio.setId(1)
                .setJuzgado(JuzgadoSetUp.createJuzgado())
                .setTipoJuicio(TipoJuicioSetUp.createTipoJuicio());
        return relJuzgadoTipoJuicio;
    }

    public static RelJuzgadoTipoJuicio createRelJuzgadoTipoJuicio(Juzgado juzgado, TipoJuicio tipoJuicio) {
        RelJuzgadoTipoJuicio relJuzgadoTipoJuicio = new RelJuzgadoTipoJuicio();
        relJuzgadoTipoJuicio.setId(1)
                .setJuzgado(juzgado)
                .setTipoJuicio(tipoJuicio);
        return relJuzgadoTipoJuicio;
    }
}
