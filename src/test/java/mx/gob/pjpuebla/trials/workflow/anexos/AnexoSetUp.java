package mx.gob.pjpuebla.trials.workflow.anexos;

import mx.gob.pjpuebla.trials.util.Audit;

import java.time.LocalDateTime;

public class AnexoSetUp {

    private AnexoSetUp() {
    }

    public static Anexo createAnexo() {
        Anexo anexo = new Anexo()
                .setId(1)
                .setNombre("Anexo 1");
        anexo.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7", "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return anexo;
    }
}
