package mx.gob.pjpuebla.trials.workflow.documentos;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoService;
import mx.gob.pjpuebla.trials.util.enums.EstadoDocumento;
import mx.gob.pjpuebla.trials.util.enums.Rol;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;
import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRecord;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@RequiredArgsConstructor
@Service
public class DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final JuzgadoService juzgadoService;
    private final TipoJuicioRepository tipoJuicioRepository;
    private final AnexoRepository anexoRepository;
    private final PersonaDocumentoRepository personaDocumentoRepository;
    private final TipoPartesRepository tipoPartesRepository;

    @Transactional(readOnly = true)
    public Page<DocumentoGridRecord> getAll(String key, Pageable pageable) {
        key = (key != null) ? key.toLowerCase() : "";
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
                                (documento.getRuta() != null)))
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
            documento.setJuzgado(juzgadoService.getJuzgado(documento.getTipoJuicio()));
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

        juzgadoService.actualizarCarga(documento.getJuzgado());

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

    public AnexoRecord editarAnexos(Integer documentoId, List<String> nuevosAnexos, String motivoEdita) {

        Documento documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new NotFoundException("Documento no encontrado", "documentoId"));
        documento.setMotivoEdita(motivoEdita);
        documento.setSelloEstatus(SelloEstatus.NO_VALIDO);

        List<Anexo> anexosActuales = anexoRepository.findAllByDocumentoId(documentoId);
        anexosActuales.stream()
                .filter(anexo -> !nuevosAnexos.contains(anexo.getNombre()))
                .forEach(anexo -> anexoRepository.delete(anexo));

        for (String anexo : nuevosAnexos) {
            if (anexosActuales.stream().noneMatch(existingAnexo -> existingAnexo.getNombre().equals(anexo))) {
                Anexo nuevoAnexo = new Anexo();
                nuevoAnexo.setNombre(anexo);
                nuevoAnexo.setDocumento(documento);
                anexoRepository.save(nuevoAnexo);
            }
        }
        documentoRepository.save(documento);

        return new AnexoRecord(nuevosAnexos, motivoEdita);
    }

    public  DocumentoResponseRecord getEditDocumentoAnexo(Integer id) {

        List<DocumentoAnexoRecord> documentoAnexos = personaDocumentoRepository.findDocumentoAnexoByDocumentoId(id);
        List<String> anexos = personaDocumentoRepository.findNombresAnexosByDocumentoId(id);

        PersonaDocumentoDTO actorDTO = new PersonaDocumentoDTO();
        PersonaDocumentoDTO demandadoDTO = new PersonaDocumentoDTO();

        documentoAnexos.forEach(anexo -> {

            if ("Actor".equals(anexo.tipoParteNombre())) {
                actorDTO.setNombre(anexo.nombre());
                actorDTO.setApellidoPaterno(anexo.apellidoPaterno());
                actorDTO.setApellidoMaterno(anexo.apellidoMaterno());
                actorDTO.setPseudonimo(anexo.pseudonimo());
                actorDTO.setTipoPersona(anexo.tipoPersona());
                actorDTO.setTipoParte(anexo.tipoParteId());
            } else if ("Demandado".equals(anexo.tipoParteNombre())) {
                demandadoDTO.setNombre(anexo.nombre());
                demandadoDTO.setApellidoPaterno(anexo.apellidoPaterno());
                demandadoDTO.setApellidoMaterno(anexo.apellidoMaterno());
                demandadoDTO.setPseudonimo(anexo.pseudonimo());
                demandadoDTO.setTipoPersona(anexo.tipoPersona());
                demandadoDTO.setTipoParte(anexo.tipoParteId());
            }
        });
        return new DocumentoResponseRecord(actorDTO, demandadoDTO, anexos);

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


    public void actualizarCargaJuzgado(DocumentoRecord documentoRecord) {
        Documento documento = documentoRepository.findById(documentoRecord.id()).orElseThrow();

        juzgadoService.actualizarCarga(documento.getJuzgado());
    }
}

