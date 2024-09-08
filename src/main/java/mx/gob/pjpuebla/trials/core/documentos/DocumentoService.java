package mx.gob.pjpuebla.trials.core.documentos;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.anexos.Anexo;
import mx.gob.pjpuebla.trials.core.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.core.personasdocumentos.PersonaDocumentoDTO;
import mx.gob.pjpuebla.trials.core.personasdocumentos.PersonaDocumentoRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;
import mx.gob.pjpuebla.trials.util.TipoDocumento;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final JuzgadoRepository juzgadoRepository;
    private final TipoJuicioRepository tipoJuicioRepository;
    private final AnexoRepository anexoRepository;
    private final PersonaDocumentoRepository personaDocumentoRepository;
    private final TipoPartesRepository tipoPartesRepository;

    public DocumentoRecord create(DocumentoDTO documentoDTO) {
        Documento documento = new Documento();

        //TODO generacion de folio
        documento.setFolio("000001");
        //TODO. Asignación de juzgado
        documento.setJuzgado(juzgadoRepository.findById(51).orElseThrow(() -> new NoSuchElementException("Juzgado no encontrado")));
        documento.setTipoDocumento(TipoDocumento.DEMANDA);
        documento.setStatus("Recepción documentos");
        documento.setTipoJuicio(tipoJuicioRepository.findById(documentoDTO.getTipoJuicioId())
                .orElseThrow(() -> new NoSuchElementException("Tipo Juicio no encontrado")));
        documento = documentoRepository.save(documento);

        for (PersonaDocumentoDTO persona : documentoDTO.getPersonaDocumento()) {
            PersonaDocumento entity = new PersonaDocumento();
            entity.setNombre(persona.getNombre());
            entity.setApellidoPaterno(persona.getApelidoPaterno());
            entity.setApellidoMaterno(persona.getApellidoMaterno());
            entity.setPseudonimo(persona.getPseudonimo());
            entity.setTipoPersona(persona.getTipoPersona());
            entity.setTipoPartes(tipoPartesRepository.findById(persona.getTipoparte()).orElseThrow(() -> new NoSuchElementException("Tipo parte del demandado no encontrada")));
            entity.setDocumento(documento);
            personaDocumentoRepository.save(entity);
        }

        for (String anexo : documentoDTO.getAnexos()) {
            Anexo entity = new Anexo();
            entity.setNombre(anexo);
            entity.setDocumento(documento);
            anexoRepository.save(entity);
        }
        return new DocumentoRecord(documento.getId(), documento.getFolio(), documento.getTipoDocumento());
    }
}

