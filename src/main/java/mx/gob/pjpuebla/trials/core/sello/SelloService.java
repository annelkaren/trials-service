package mx.gob.pjpuebla.trials.core.sello;

import mx.gob.pjpuebla.trials.core.anexos.Anexo;
import mx.gob.pjpuebla.trials.core.documentos.Documento;
import mx.gob.pjpuebla.trials.core.documentos.DocumentoRecord;
import mx.gob.pjpuebla.trials.core.personas.PersonaRepository;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.core.anexos.AnexoRepository;
import net.sf.jasperreports.engine.JRException;
import org.springframework.stereotype.Service;
import java.io.FileNotFoundException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SelloService {

    private final SelloGenerator selloGenerator;
    private final DocumentoRepository documentoRepository;
    private final AnexoRepository anexoRepository;

    @Transactional(readOnly = true)
    public Documento findDocById(Integer id) {
        return documentoRepository.findById(id).orElseThrow();
    }

    public byte[] exportPdf(Integer id) throws JRException, FileNotFoundException {
        Documento documento = findDocById(id);
        return selloGenerator.exportToPdf(documento);
    }
}
