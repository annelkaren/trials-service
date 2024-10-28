package mx.gob.pjpuebla.trials.workflow.documentos.documentoContenido;

import java.time.LocalDate;

import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoOficioDigitalizacionRecord;

public class DocumentoContenidoSetUp {
    
    private DocumentoContenidoSetUp(){

    }

        public static DocumentoOficioDigitalizacionRecord documentoOficioDigitalizacionRecordSetUp(){
        return new DocumentoOficioDigitalizacionRecord(
                "2",
                 "00000/2024", 
                 LocalDate.now(), 
                 1,
                  1,
                   LocalDate.now(), 
                   EstadoCarpeta.ASIGNADO,
                 "asunto prueba",
                    'c',
                  'S',
                    "acuse",
                    "comentarios...",
                    "<p>Hola mundo </p>");
    }
}
