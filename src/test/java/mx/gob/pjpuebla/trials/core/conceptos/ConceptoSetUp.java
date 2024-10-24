package mx.gob.pjpuebla.trials.core.conceptos;

import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.TipoConcepto;

import java.time.LocalDateTime;

public class ConceptoSetUp {

    private ConceptoSetUp() {

    }

    public static Concepto createConcepto() {
        Concepto concepto = new Concepto()
                .setId(1)
                .setVersion(1)
                .setNombre("Adjuntar")
                .setDias(1)
                .setTipoConcepto(TipoConcepto.MATERIA)
                .setJuzgado(null)
                .setEstado(Estado.ACTIVE);
        concepto.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7",
                "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return concepto;
    }

    public static ConceptoRecordResponse createConceptoRecordResponse () {
        return new ConceptoRecordResponse(1, "Adjuntar", 1, TipoConcepto.MATERIA, null, Estado.ACTIVE);
    }
}
