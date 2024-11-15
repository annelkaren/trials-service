package mx.gob.pjpuebla.trials.core.documentoidentificacion;

import mx.gob.pjpuebla.trials.util.Audit;

import java.time.LocalDateTime;
import java.util.List;

public class DocumentoIdentificacionSetUp {

    private DocumentoIdentificacionSetUp(){}

    public static List<DocumentoIdentificacion> createIdentificaciones() {
        DocumentoIdentificacion documentoIdentificacion1 = new DocumentoIdentificacion()
                .setId(1)
                .setName("Gafete Institucional defensoría pública")
                .setVersion(0);
        documentoIdentificacion1.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(),
                "6b13785f-d213-4585-a76b-437ffe57c9c7",
                "6b13785f-d213-4585-a76b-437ffe57c9c7"));

        DocumentoIdentificacion documentoIdentificacion2 = new DocumentoIdentificacion()
                .setId(2)
                .setName("Gafete Institucional centro de meditación")
                .setVersion(0);
        documentoIdentificacion2.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(),
                "7a23456f-d213-4585-a76b-437ffe57c9c8",
                "7a23456f-d213-4585-a76b-437ffe57c9c8"));

        return List.of(documentoIdentificacion1, documentoIdentificacion2);
    }

    public static IdentificacionDocRecord createIdentificacionDoc() {
        return new IdentificacionDocRecord(
                1,
                "Gafete Institucional defensoría pública"
        );
    }
}
