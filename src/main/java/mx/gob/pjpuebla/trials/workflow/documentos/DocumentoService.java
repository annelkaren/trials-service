package mx.gob.pjpuebla.trials.workflow.documentos;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoService;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.*;
import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.records.*;
import mx.gob.pjpuebla.trials.workflow.folios.JuzgadoFolios;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoItemRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
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
    private static final String DOC_NOT_FOUND = "Documento no encontrado";

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
                                documento.getCarpeta().getEstatus(),
                                (documento.getRuta() != null)))
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    public DocumentoRecord updateStatus(Integer id, Integer status) {
        Documento documento = documentoRepository.findById(id).orElseThrow(() -> new NotFoundException(DOC_NOT_FOUND, "documentoId" + id));
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

        carpeta.setJuzgado(juzgadoService.getConexidadJuzgado(documentoRecord.actor(), documentoRecord.demandado(), carpeta.getTipoJuicio()));
        carpeta.setFolio(getFolio("D"));

        if (carpeta.getJuzgado() == null) {
            carpeta.setJuzgado(juzgadoService.getJuzgado(carpeta.getTipoJuicio()));
        }

        carpeta.setExpediente(generateNumExpediente(carpeta.getJuzgado(), TipoCarpeta.DEMANDA));
        carpeta.setTipoCarpeta(TipoCarpeta.DEMANDA);
        carpeta.setEstatus(EstadoCarpeta.CAPTURA);
        carpeta.setSelloEstatus(SelloEstatus.VALIDO);
        carpeta = carpetaRepository.save(carpeta);

        documento.setCarpeta(carpeta);
        //SETEAMOS JSON - SOLO PARA DEMANDA FAMILIAR

        documento.setData(documentoRecord.general());
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

        //campos exlusivos para demanda de tipo familiar 
        entity.setCurp(persona.curp());
        entity.setIne(persona.ine());
        entity.setDomicilio(persona.domicilio());
        entity.setCelular(persona.celular());
        entity.setCorreoElectronico(persona.correoElectronico());

        personaDocumentoRepository.save(entity);
    }

    public DocumentoRecord editarAnexos(Integer documentoId, List<String> nuevosAnexos, String motivoEdita) {

        Documento documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new NotFoundException(DOC_NOT_FOUND, "documentoId " + documentoId));
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

        Documento documento = documentoRepository.findById(id).orElseThrow(() -> new NotFoundException(DOC_NOT_FOUND, id.toString()));
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

    /**
     * Devuelve un numero de folio
     *
     * @param tipo E-exhorto, D-demanda, P-promocion.
     * @return string
     */
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

    public String generateNumExpediente(Juzgado juzgado, TipoCarpeta tipoCarpeta) {
        JuzgadoFolios juzgadoFolios = juzgadoService.getJuzgadoFolios(juzgado, tipoCarpeta);
        juzgadoFolios = juzgadoService.checkYearJuzgadoFolios(juzgadoFolios);
        String letraInicial = switch (tipoCarpeta) {
            case EXHORTO -> "E";
            case DESPACHO -> "D";
            case APELACION_MUNICIPAL -> "T";
            default -> "";
        };
        String numExpedienteExhorto = letraInicial + StringUtils.leftPad(juzgadoFolios.getValue().toString(), 6, '0') + "/" + juzgadoFolios.getYear();
        juzgadoService.increaseValueJuzgadoFolios(juzgadoFolios);
        return numExpedienteExhorto;
    }


    public Page<DocumentoGridRecord> getAllHistorial(Pageable pageable, Documento example) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("folio", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("expediente", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("estatus", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("tipoEntrada", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("materia.nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Page<Documento> paginaDocumentos = documentoRepository.findAll(Example.of(example, exampleMatcher), pageable);


        List<DocumentoGridRecord> listaDocumentoRecords = paginaDocumentos.getContent().stream()
                .map(doc -> new DocumentoGridRecord(
                        doc.getId(),
                        doc.getCarpeta().getFolio(),
                        doc.getCarpeta().getExpediente(),
                        doc.getCarpeta().getJuzgado().getMateria().getNombre(),
                        doc.getTipoDocumento() == null ? doc.getCarpeta().getTipoCarpeta().name() : doc.getTipoDocumento().name(),
                        doc.getAudit().getFechaAlta(),
                        doc.getCarpeta().getSelloEstatus(),
                        doc.getCarpeta().getEstatus(),
                        (doc.getRuta() != null)))
                .toList();

        return new PageImpl<>(listaDocumentoRecords, pageable, paginaDocumentos.getTotalElements());
    }

    public DocumentoPromocionResponseRecord createPromocion(DocumentoPromocionRecord documentoPromocionRecord) {
        Carpeta carpeta = carpetaRepository.findById(documentoPromocionRecord.carpetaId()).orElseThrow(() -> new NotFoundException("Carpeta no encontrada", "carpetaId" + documentoPromocionRecord.carpetaId()));
        Documento documento = new Documento();
        documento.setCarpeta(carpeta);

        DocumentoData documentoData = new DocumentoData();
        documentoData.setPromocionFolio(getFolio("P"));
        documentoData.setTipoPromocion(documentoPromocionRecord.tipoPromocion());

        documento.setData(documentoData);
        documento.setTipoDocumento(TipoDocumento.PROMOCION);

        documento = documentoRepository.save(documento);

        for (String anexo : documentoPromocionRecord.anexos()) {
            Anexo entity = new Anexo();
            entity.setNombre(anexo);
            entity.setDocumento(documento);
            anexoRepository.save(entity);
        }

        return new DocumentoPromocionResponseRecord(documento.getId(), documentoData.getPromocionFolio(), documento.getTipoDocumento());
    }

}

