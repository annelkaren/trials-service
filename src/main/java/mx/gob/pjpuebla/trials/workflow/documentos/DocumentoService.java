package mx.gob.pjpuebla.trials.workflow.documentos;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoService;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.Rol;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoGridRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoResponseRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoSaveRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoItemRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    private final CarpetaRepository carpetaRepository;

    @Transactional(readOnly = true)
    public Page<DocumentoGridRecord> getAll(String key, Pageable pageable) {
        key = (key != null) ? key.toLowerCase() : "";
        Page<Documento> page = documentoRepository.findByEstatusCaptura(key, pageable);

        List<DocumentoGridRecord> list = page.getContent().stream()
                .map(documento ->
                        new DocumentoGridRecord(documento.getId(),
                                documento.getCarpeta().getFolio(),
                                documento.getCarpeta().getExpediente(),
                                documento.getCarpeta().getJuzgado().getMateria().getNombre(),
                                documento.getCarpeta().getTipoCarpeta().name(),
                                documento.getAudit().getFechaAlta(),
                                documento.getCarpeta().getSelloEstatus(),
                                (documento.getRuta() != null)))
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    public DocumentoRecord updateStatus(Integer id, Integer status) {
        Documento documento = documentoRepository.findById(id).orElseThrow(() -> new NotFoundException("Documento no encontrado", "documentoId" + id));
        EstadoCarpeta value = EstadoCarpeta.values()[status];
        documento.getCarpeta().setEstatus(value);
        carpetaRepository.save(documento.getCarpeta());
        return new DocumentoRecord(documento.getId(), documento.getCarpeta().getFolio(), documento.getCarpeta().getTipoCarpeta());
    }

    public DocumentoRecord createDemanda(DocumentoSaveRecord documentoRecord) {
        Documento documento = new Documento();
        Carpeta carpeta = new Carpeta();

        carpeta.setTipoJuicio(tipoJuicioRepository.findById(documentoRecord.tipoJuicioId())
                .orElseThrow(() -> new NotFoundException("Tipo Juicio no encontrado", documentoRecord.tipoJuicioId().toString())));
        carpeta.setFolio(getFolio("D"));
        carpeta.setJuzgado(juzgadoService.getConexidadJuzgado(documentoRecord.actor(), documentoRecord.demandado(), carpeta.getTipoJuicio()));

        if (carpeta.getJuzgado() == null) {
            carpeta.setJuzgado(juzgadoService.getJuzgado(carpeta.getTipoJuicio()));
        }

        carpeta.setExpediente(juzgadoService.getNumeroExpediente(carpeta.getJuzgado().getId()).numeroExpediente());
        carpeta.setTipoCarpeta(TipoCarpeta.DEMANDA);
        carpeta.setEstatus(EstadoCarpeta.CAPTURA);
        carpeta.setSelloEstatus(SelloEstatus.VALIDO);
        carpeta = carpetaRepository.save(carpeta);
        documento.setCarpeta(carpeta);
        documento = documentoRepository.save(documento);

        createPersonaDocumento(documentoRecord.actor(), carpeta);
        createPersonaDocumento(documentoRecord.demandado(), carpeta);

        for (String anexo : documentoRecord.anexos()) {
            Anexo entity = new Anexo();
            entity.setNombre(anexo);
            entity.setDocumento(documento);
            anexoRepository.save(entity);
        }

        juzgadoService.actualizarCarga(carpeta.getJuzgado());

        return new DocumentoRecord(documento.getId(), carpeta.getFolio(), documento.getCarpeta().getTipoCarpeta());
    }

    private void createPersonaDocumento(PersonaDocumentoItemRecord persona, Carpeta carpeta) {
        String tipoParte = (persona.tipoParte().equals(1)) ? "Actor" : "Demandado";
        PersonaDocumento entity = new PersonaDocumento();
        entity.setNombre(persona.nombre());
        entity.setApellidoPaterno(persona.apellidoPaterno());
        entity.setApellidoMaterno(persona.apellidoMaterno());
        entity.setPseudonimo(persona.pseudonimo());
        entity.setTipoPersona(persona.tipoPersona());
        entity.setRol(Rol.PRINCIPAL);
        entity.setTipoPartes(tipoPartesRepository.findByNombreAndTipoJuicioId(tipoParte, carpeta.getTipoJuicio().getId())
                .orElseThrow(() -> new NotFoundException("Tipo parte no encontrada", "TipoParteId")));
        entity.setCarpeta(carpeta);
        personaDocumentoRepository.save(entity);
    }

    public DocumentoRecord editarAnexos(Integer documentoId, List<String> nuevosAnexos, String motivoEdita) {

        Documento documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new NotFoundException("Documento no encontrado", "documentoId " + documentoId));
        documento.setMotivoEdita(motivoEdita);
        documento.getCarpeta().setSelloEstatus(SelloEstatus.NO_VALIDO);

        List<Anexo> anexosActuales = anexoRepository.findAllByDocumentoId(documentoId);
        anexosActuales.stream()
                .filter(anexo -> !nuevosAnexos.contains(anexo.getNombre()))
                .forEach(anexoRepository::delete);

        for (String anexo : nuevosAnexos) {
            if (anexosActuales.stream().noneMatch(existingAnexo -> existingAnexo.getNombre().equals(anexo))) {
                Anexo nuevoAnexo = new Anexo();
                nuevoAnexo.setNombre(anexo);
                nuevoAnexo.setDocumento(documento);
                anexoRepository.save(nuevoAnexo);
            }
        }
        carpetaRepository.save(documento.getCarpeta());
        documentoRepository.save(documento);

        return new DocumentoRecord(documentoId, documento.getCarpeta().getFolio(), documento.getCarpeta().getTipoCarpeta());
    }

    public DocumentoResponseRecord getDemandaById(Integer id) {

        Documento documento = documentoRepository.findById(id).orElseThrow(() -> new NotFoundException("Documento no encontrado", id.toString()));
        List<PersonaDocumentoRecord> personas = personaDocumentoRepository.findPersonasByCarpetaId(documento.getCarpeta().getId(), Rol.PRINCIPAL);
        List<String> anexos = anexoRepository.findNombresAnexosByDocumentoId(id);

        PersonaDocumentoRecord actor = null;
        PersonaDocumentoRecord demandado = null;

        for (PersonaDocumentoRecord persona : personas) {
            if ("Actor".equalsIgnoreCase(persona.tipoParte())) {
                actor = persona;
            } else if ("Demandado".equalsIgnoreCase(persona.tipoParte())) {
                demandado = persona;
            }
        }
        return new DocumentoResponseRecord(actor, demandado, anexos);
    }

    private String getFolio(String tipo) {
        Long valNum = switch (tipo) {
            case "E" ->           // Case para exhorto
                    documentoRepository.getNextValExhorto();
            case "D" ->           // Case para demanda
                    documentoRepository.getNextValDemanda();
            case "P" ->           // Case para promocion
                    documentoRepository.getNextValPromocion();
            default -> throw new IllegalArgumentException("Tipo de documento no válido: " + tipo);
        };
        return valNum.toString();
    }
}

