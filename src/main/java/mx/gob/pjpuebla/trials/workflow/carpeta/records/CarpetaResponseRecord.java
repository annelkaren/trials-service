package mx.gob.pjpuebla.trials.workflow.carpeta.records;

import java.io.Serializable;
import java.util.List;

import mx.gob.pjpuebla.trials.util.enums.Estado;

public record CarpetaResponseRecord(
                Integer idCarpeta,
                String actor,
                String demandado,
                String tipoJuicio,
                List<String> victimas,
                List<String> imputados,
                Estado estadoJuzgado,
                String estadoCarpeta,
                Integer estatusSolicitud,
                String mensajeSolicitud) implements Serializable {

        public CarpetaResponseRecord(Integer estatusSolicitud, String mensajeSolicitud) {
                this(null, null, null, null, null, null, null, null, estatusSolicitud, mensajeSolicitud);
        }
}
