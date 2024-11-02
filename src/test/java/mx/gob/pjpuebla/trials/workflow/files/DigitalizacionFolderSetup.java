package mx.gob.pjpuebla.trials.workflow.files;

import mx.gob.pjpuebla.trials.workflow.documentos.records.DigitalizacionRecord;

public class DigitalizacionFolderSetup {
    
    private DigitalizacionFolderSetup() {

    }

    public static DigitalizacionRecord getDigitalizacion(){
        return new DigitalizacionRecord(1, "/ruta/archivo/ejemplo", "Archivo prueba");
    }
}
