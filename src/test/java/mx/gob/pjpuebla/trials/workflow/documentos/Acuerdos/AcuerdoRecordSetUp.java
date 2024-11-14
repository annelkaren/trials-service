package mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos.records.AcuerdoPromocionesRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos.records.AcuerdoRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos.records.AcuerdosRecord;

public class AcuerdoRecordSetUp {

    private AcuerdoRecordSetUp() {

    }

    public static AcuerdoRecord create() {
        List<String> rubros = new ArrayList<>();
        rubros.add("rubro 1");

        List<AcuerdoPromocionesRecord> promociones = new ArrayList<>();
        promociones.add(new AcuerdoPromocionesRecord(1, "prueba", "archivoName", "recomendacion 1"));


        return new AcuerdoRecord( 1, 1, 1, "tipo acuerdo test", LocalDate.now(), "etapa procesal prueba", rubros, promociones,
                 'o', "<p>prueba</p>");
    }

    public static List<AcuerdosRecord> createAcuerdoRecord(){
        List<AcuerdosRecord> acuerdoRecord = new ArrayList<>();
        acuerdoRecord.add(new AcuerdosRecord(1, LocalDate.now(), "Hola", EstadoCarpeta.CREADO));
        return acuerdoRecord;
    }
}
