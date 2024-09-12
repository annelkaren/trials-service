package mx.gob.pjpuebla.trials.core.documentos;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.anexos.Anexo;
import mx.gob.pjpuebla.trials.core.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.core.personasdocumentos.PersonaDocumentoDTO;
import mx.gob.pjpuebla.trials.core.personasdocumentos.PersonaDocumentoRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;
import mx.gob.pjpuebla.trials.util.TipoDocumento;
import mx.gob.pjpuebla.trials.util.enums.Estado;

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
        documento.setFolio("1");
        //TODO. Asignación de juzgado
        documento.setExpediente("000001/2024");
        documento.setTipoDocumento(TipoDocumento.DEMANDA);
        documento.setStatus("Recepción documentos");
        documento.setTipoJuicio(tipoJuicioRepository.findById(documentoDTO.getTipoJuicioId())
                .orElseThrow(() -> new NoSuchElementException("Tipo Juicio no encontrado")));
        
        
        documento = documentoRepository.save(documento);

        createPersonaDocumento(documentoDTO.getActor(), documento);
        createPersonaDocumento(documentoDTO.getDemandado(), documento);

        for (String anexo : documentoDTO.getAnexos()) {
            Anexo entity = new Anexo();
            entity.setNombre(anexo);
            entity.setDocumento(documento);
            anexoRepository.save(entity);
        }
        return new DocumentoRecord(documento.getId(), documento.getFolio(), documento.getTipoDocumento());
    }

    private void createPersonaDocumento(PersonaDocumentoDTO persona, Documento documento){
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


    private Integer getConexidadJuzgado(PersonaDocumento actor, PersonaDocumento demandado, TipoJuicio tipoJuicio){
        List<Documento> documentos = new ArrayList<>();

        List<PersonaDocumento> registrosActor = personaDocumentoRepository
        .findByNombreAndApellidoPaternoAndApellidoMaternoAndPseudonimoAndTipoParte(actor.getNombre(), actor.getApellidoPaterno(), actor.getApellidoMaterno(), actor.getPseudonimo(), actor.getTipoPartes().getId());

        List<PersonaDocumento> registrosDemandado = personaDocumentoRepository
        .findByNombreAndApellidoPaternoAndApellidoMaternoAndPseudonimoAndTipoParte(demandado.getNombre(), demandado.getApellidoPaterno(), demandado.getApellidoMaterno(), demandado.getPseudonimo(), demandado.getTipoPartes().getId());

        if (registrosActor.isEmpty() || registrosDemandado.isEmpty()){
            return null;
        }

        for(PersonaDocumento tmp:registrosActor){
            documentos.add(tmp.getDocumento());
        }
        
        for(PersonaDocumento tmp:registrosDemandado){
            Documento documento;

            if (!documentos.contains(tmp.getDocumento()))
                continue;

            documento = tmp.getDocumento();

            if (juzgadoRepository.findByMateriaAndEstado(tipoJuicio.getMateria(), Estado.ACTIVE).contains(documento.getJuzgado()))
                return documento.getJuzgado().getId();
        }

        return null;
    }

    private Juzgado getJuzgado(){
        return null;
    }
}

