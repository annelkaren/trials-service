package mx.gob.pjpuebla.trials.workflow.documentos;

import mx.gob.pjpuebla.trials.core.instituciones.Institucion;
import mx.gob.pjpuebla.trials.core.instituciones.InstitucionSetUp;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.EstadoAnexo;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoBandejaRecepcionRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.BandejaRecepcionRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoOficioDigitalizacionRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoSaveRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.IndicadoresRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonasDocumentosSetUp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DocumentoSetUp {
    private DocumentoSetUp() {
    }

    public static Documento create(TipoJuicio tipoJuicio) {
        Institucion institucion = InstitucionSetUp.createInstitucion(Estado.ACTIVE);

        Carpeta carpeta = new Carpeta()
                .setId(1)
                .setVersion(1)
                .setFolio("1")
                .setExpediente("000001/2024")
                .setEstatus(EstadoCarpeta.CAPTURA)
                .setTipoJuicio(tipoJuicio)
                .setSelloEstatus(SelloEstatus.VALIDO);

        Documento documento = new Documento()
                .setId(1)
                .setVersion(1)
                .setCarpeta(carpeta)
                .setInstitucion(institucion);
    
           
        documento.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7",
                "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return documento;
    }

    public static DocumentoSaveRecord createDocumentoSaveRecord(Integer tipoJuicio) {
        List<String> anexos = Arrays.asList("Anexo 1", "Anexo 2", "Anexo3");
        DocumentoData docData = new DocumentoData();

        return new DocumentoSaveRecord(
                PersonasDocumentosSetUp.createPersonaDocumentoItemRecord(),
                PersonasDocumentosSetUp.createPersonaDocumentoItemRecord(),
                anexos,
                tipoJuicio,
                docData);
    }

    public static BandejaRecepcionRecord createBandejaRecepcion() {
        List<AnexoBandejaRecepcionRecord> anexos = new ArrayList<>(Arrays.asList(
                new AnexoBandejaRecepcionRecord(1, "INE", EstadoAnexo.NORECIBIDO),
                new AnexoBandejaRecepcionRecord(2, "CURP", EstadoAnexo.NORECIBIDO)));
        return new BandejaRecepcionRecord(1, "1", "00001", TipoCarpeta.DEMANDA, "ruta/carpeta", anexos);
    }

    public static List<AnexoBandejaRecepcionRecord> createAnexosDocumento() {
        return new ArrayList<>(Arrays.asList(
            new AnexoBandejaRecepcionRecord(1, "INE", EstadoAnexo.NORECIBIDO),
            new AnexoBandejaRecepcionRecord(2, "CURP", EstadoAnexo.NORECIBIDO)));
    }

    public static Documento create_data(TipoJuicio tipoJuicio) {
        DocumentoData data = new DocumentoData().setDomicilio("Example Domicilio");
        Carpeta carpeta = new Carpeta()
                .setId(1)
                .setVersion(1)
                .setFolio("1")
                .setExpediente("000001/2024")
                .setEstatus(EstadoCarpeta.CAPTURA)
                .setTipoJuicio(tipoJuicio)
                .setSelloEstatus(SelloEstatus.VALIDO);
        Documento documento = new Documento()
                .setId(1)
                .setVersion(1)
                .setData(data)
                .setCarpeta(carpeta);
        documento.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7",
                "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return documento;
    }

    public static IndicadoresRecord createIndicadoresRecord(){
        return new IndicadoresRecord(2,7,9,5);
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
                    "CARTA",
                  false,
                    "acuse",
                    "comentarios...",
                    "<p>Hola mundo </p>");
    }
}
