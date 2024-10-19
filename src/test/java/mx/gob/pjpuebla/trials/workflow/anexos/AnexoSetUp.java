package mx.gob.pjpuebla.trials.workflow.anexos;

import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public static List<Anexo> createAnexos(Documento documento) {
        List<Anexo> anexos = new ArrayList<>();

        // Crear y agregar varios anexos a la lista
        for (int i = 1; i <= 2; i++) { // Por ejemplo, crear 3 anexos
            Anexo anexo = new Anexo()
                    .setId(i)
                    .setNombre("Anexo " + i)
                    .setDocumento(documento);

            anexo.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(),
                    "6b13785f-d213-4585-a76b-437ffe57c9c7",
                    "6b13785f-d213-4585-a76b-437ffe57c9c7"));
            anexos.add(anexo);
        }

        return anexos;
    }
}
