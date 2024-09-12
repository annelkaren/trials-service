package mx.gob.pjpuebla.trials.workflow.documentos;

import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DocumentoServiceTest {

    @Mock
    DocumentoRepository documentoRepository;
    @Mock
    TipoJuicioRepository tipoJuicioRepository;
    @Mock
    AnexoRepository anexoRepository;
    @Mock
    PersonaDocumentoRepository personaDocumentoRepository;
    @Mock
    TipoPartesRepository tipoPartesRepository;

    private TipoJuicio tipoJuicio;
}
