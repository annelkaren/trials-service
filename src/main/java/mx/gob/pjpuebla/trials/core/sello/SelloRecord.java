package mx.gob.pjpuebla.trials.core.sello;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.util.TipoDocumento;

import java.util.List;

public record SelloRecord(

        Integer id,
        String folio,
        String expediente,
        Juzgado juzgado
      /*  String fechaHora,
       List<String>listanexos1,
        String listanexos,
        String nombreEntidad,
        String capturista*/
) {
}
