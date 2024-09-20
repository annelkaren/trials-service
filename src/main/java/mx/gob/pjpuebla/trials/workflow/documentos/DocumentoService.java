package mx.gob.pjpuebla.trials.workflow.documentos;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoService;
import mx.gob.pjpuebla.trials.util.enums.EstadoDocumento;
import mx.gob.pjpuebla.trials.util.enums.Rol;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;
import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoGridRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoDTO;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

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

    @Transactional(readOnly = true)
    public Page<DocumentoGridRecord> getAll(String key, Pageable pageable) {
        key = (key != null) ? key : "";
        Page<Documento> page = documentoRepository.findByEstatusCaptura(key, pageable);

        List<DocumentoGridRecord> list = page.getContent().stream()
                .map(documento ->
                        new DocumentoGridRecord(documento.getId(),
                                documento.getFolio(),
                                documento.getExpediente(),
                                documento.getJuzgado().getMateria().getNombre(),
                                documento.getTipoDocumento().name(),
                                documento.getAudit().getFechaAlta(),
                                documento.getSelloEstatus(),
                                (documento.getRuta() != null) ? true : false))
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    public DocumentoRecord updateStatus(Integer id, Integer status) {
        Documento doc = documentoRepository.findById(id).orElseThrow(() -> new NotFoundException("Documento no encontrado", "documentoId" + id));
        EstadoDocumento value = EstadoDocumento.values()[status];
        doc.setEstatus(value);
        documentoRepository.save(doc);
        return new DocumentoRecord(doc.getId(), doc.getFolio(), doc.getTipoDocumento());
    }

    public DocumentoRecord createDemanda(DocumentoDTO documentoDTO) {
        Documento documento = new Documento();

        documento.setTipoJuicio(tipoJuicioRepository.findById(documentoDTO.getTipoJuicioId())
                .orElseThrow(() -> new NotFoundException("Tipo Juicio no encontrado", "tipoJuicioId")));
        documento.setFolio(getFolio("D"));
        //TODO. Asignación de juzgado correctamente
        documento.setJuzgado(juzgadoService.getConexidadJuzgado(documentoDTO.actor, documentoDTO.getDemandado(), documento.getTipoJuicio()));
        if (documento.getJuzgado() == null) {
            documento.setJuzgado(juzgadoRepository.findAll().stream().findFirst().orElse(null));
        }
        documento.setExpediente(juzgadoService.getNumeroExpediente(documento.getJuzgado().getId()).numeroExpediente());
        documento.setTipoDocumento(TipoDocumento.DEMANDA);
        documento.setEstatus(EstadoDocumento.CAPTURA);
        documento.setSelloEstatus(SelloEstatus.VALIDO);
        //TODO. Falta definir reglas de este estatus
        documento.setEstatusProcesal("Recepción documentos");
        documento.setTipoJuicio(tipoJuicioRepository.findById(documentoDTO.getTipoJuicioId())
                .orElseThrow(() -> new NotFoundException("Tipo Juicio no encontrado", "tipoJuicioId")));
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

