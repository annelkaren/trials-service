package mx.gob.pjpuebla.trials.workflow.documentos;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoService;
import mx.gob.pjpuebla.trials.util.enums.EstadoDocumento;
import mx.gob.pjpuebla.trials.util.enums.Rol;
import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoDTO;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.TipoDocumento;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final JuzgadoService juzgadoService;
    private final JuzgadoRepository juzgadoRepository;
    private final TipoJuicioRepository tipoJuicioRepository;
    private final AnexoRepository anexoRepository;
    private final PersonaDocumentoRepository personaDocumentoRepository;
    private final TipoPartesRepository tipoPartesRepository;

    public DocumentoRecord createDemanda(DocumentoDTO documentoDTO) {
        Documento documento = new Documento();
        Juzgado juzgado;

        documento.setTipoJuicio(tipoJuicioRepository.findById(documentoDTO.getTipoJuicioId())
                .orElseThrow(() -> new NotFoundException("Tipo Juicio no encontrado", "tipoJuicioId")));
        documento.setFolio(getFolio("D"));

        juzgado = juzgadoService.getConexidadJuzgado(documentoDTO.actor, documentoDTO.getDemandado(), documento.getTipoJuicio());

        if (juzgado == null) {
            juzgado = juzgadoService.getJuzgado(documento.getTipoJuicio());
        }

        documento.setJuzgado(juzgado);

        documento.setExpediente(juzgadoService.getNumeroExpediente(documento.getJuzgado().getId()).numeroExpediente());
        documento.setTipoDocumento(TipoDocumento.DEMANDA);
        documento.setEstatus(EstadoDocumento.CAPTURA);
        //TODO. Falta definir reglas de este estatus
        documento.setEstatusProcesal("Recepción documentos");
        documento.setTipoJuicio(tipoJuicioRepository.findById(documentoDTO.getTipoJuicioId())
                .orElseThrow(() -> new NotFoundException("Tipo Juicio no encontrado", "tipoJuicioId")));
        documento = documentoRepository.save(documento);

        juzgadoRepository.actualizarContadorAsignaciones(juzgado.getId());

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

    private void createPersonaDocumento(PersonaDocumentoDTO persona, Documento documento) {
        String tipoParte = (persona.getTipoParte().equals(1)) ? "Actor" : "Demandado";
        PersonaDocumento entity = new PersonaDocumento();
        entity.setNombre(persona.getNombre());
        entity.setApellidoPaterno(persona.getApellidoPaterno());
        entity.setApellidoMaterno(persona.getApellidoMaterno());
        entity.setPseudonimo(persona.getPseudonimo());
        entity.setTipoPersona(persona.getTipoPersona());
        entity.setRol(Rol.PRINCIPAL);
        entity.setTipoPartes(tipoPartesRepository.findByNombreAndTipoJuicioId(tipoParte, documento.getTipoJuicio().getId())
                .orElseThrow(() -> new NotFoundException("Tipo parte no encontrada", "TipoParteId")));
        entity.setDocumento(documento);
        personaDocumentoRepository.save(entity);
    }

    /**
     * Devuelve un numero de folio
     *
     * @param tipo E-exhorto, D-demanda, P-promocion.
     * @return
     */
    private String getFolio(String tipo) {
        Long valNum;
        switch (tipo) {
            case "E":           // Case para exhorto
                valNum = documentoRepository.getNextValExhorto();
                break;
            case "D":           // Case para demanda
                valNum = documentoRepository.getNextValDemanda();
                break;
            case "P":           // Case para promocion
                valNum = documentoRepository.getNextValPromocion();
                break;
            default:
                throw new IllegalArgumentException("Tipo de documento no válido: " + tipo);
        }
        return valNum.toString();
    }

}

