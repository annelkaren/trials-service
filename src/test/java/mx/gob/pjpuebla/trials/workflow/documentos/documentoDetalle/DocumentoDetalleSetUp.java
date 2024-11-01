package mx.gob.pjpuebla.trials.workflow.documentos.documentoDetalle;

import mx.gob.pjpuebla.trials.util.enums.EstadoAcuse;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.records.DocumentoDetalleRecord;

import java.time.LocalDate;
public class DocumentoDetalleSetUp {
    
    private DocumentoDetalleSetUp(){

    }

    public static DocumentoDetalleRecord createDocumentoDetalleRecord(){
        return new DocumentoDetalleRecord(1, null, EstadoAcuse.CREADO, LocalDate.now(), "comentario ");
    }
}
