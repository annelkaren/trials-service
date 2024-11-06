package mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos.records.AcuerdoRecord;

public class AcuerdoRecordSetUp {

    private AcuerdoRecordSetUp() {

    }

    public static AcuerdoRecord create() {
        List<String> rubros = new ArrayList<>();
        rubros.add("rubro 1");

        List<Integer> promociones = new ArrayList<>();
        promociones.add(1);

        List<String> recomendaciones = new ArrayList<>();
        recomendaciones.add("recomendación 1");

        return new AcuerdoRecord( 1, 1, "tipo acuerdo test", LocalDate.now(), "etapa procesal prueba", rubros, promociones,
                recomendaciones, 'o', "<p>prueba</p>", "n");
    }
}
