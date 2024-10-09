package mx.gob.pjpuebla.trials.workflow.documentos;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoService;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.salas.SalaAudienciaRecord;
import mx.gob.pjpuebla.trials.core.salas.SalaService;
import mx.gob.pjpuebla.trials.core.tipoaudiencia.TipoAudiencia;
import mx.gob.pjpuebla.trials.core.tipoaudiencia.TipoAudienciaService;
import mx.gob.pjpuebla.trials.util.enums.*;
import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.workflow.audiencias.AudienciaService;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.ApelacionPersonaRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.ApelacionRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.*;
import mx.gob.pjpuebla.trials.workflow.folios.JuzgadoFolios;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoService;
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
    private final TipoAudienciaService tipoAudienciaService;
    private final SalaService salaService;
    private final AudienciaService audienciaService;
    private final MovimientoService movimientoService;
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
        TipoJuicio tpoJuicio = tipoJuicioRepository.findById(documentoRecord.tipoJuicioId())
                .orElseThrow(() -> new NotFoundException("Tipo Juicio no encontrado", documentoRecord.tipoJuicioId().toString()));
        carpeta.setTipoJuicio(tpoJuicio);

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
        movimientoService.createMovimento(carpeta, null, carpeta.getPersona());

        documento.setCarpeta(carpeta);
        //SETEAMOS JSON - SOLO PARA DEMANDA FAMILIAR

        documento.setData(documentoRecord.general());
        documento = documentoRepository.save(documento);

        createPersonaDocumento(documentoRecord.actor(), carpeta);
        createPersonaDocumento(documentoRecord.demandado(), carpeta);

        addAnexos(documentoRecord.anexos(), documento);
        juzgadoService.actualizarCarga(carpeta.getJuzgado());

        //flujo para demanda de oralidad:
        if (tpoJuicio.getMateria().getNombre().equals("FAMILIAR") && tpoJuicio.getTipoSistema().getNombre().equals("Oral")) {
            crearAudienciaOralidad(documentoRecord, carpeta, tpoJuicio);
        }

        return new DocumentoRecord(documento.getId(), carpeta.getFolio(), documento.getCarpeta().getTipoCarpeta());
    }

    private void crearAudienciaOralidad(DocumentoSaveRecord documentoRecord, Carpeta carpeta, TipoJuicio tpoJuicio) {
        TipoAudiencia tipoAudiencia = tipoAudienciaService.obtenerTipoAudiencia("Audiencia Inicial");
        PersonaDocumentoRecord actor = new PersonaDocumentoRecord(
                documentoRecord.actor().nombre(),
                documentoRecord.actor().apellidoPaterno(),
                documentoRecord.actor().apellidoMaterno(),
                documentoRecord.actor().pseudonimo(),
                documentoRecord.actor().tipoPersona(),
                "",
                "",
                "",
                "",
                "Actor",
                documentoRecord.actor().tipoParte(),
                carpeta.getId());

        PersonaDocumentoRecord demandado = new PersonaDocumentoRecord(
                documentoRecord.demandado().nombre(),
                documentoRecord.demandado().apellidoPaterno(),
                documentoRecord.demandado().apellidoMaterno(),
                documentoRecord.demandado().pseudonimo(),
                documentoRecord.demandado().tipoPersona(),
                "",
                "",
                "",
                "",
                "Demandado",
                documentoRecord.demandado().tipoParte(),
                carpeta.getId());

        SalaAudienciaRecord salaAudienciaConexidad = salaService.asignarSalaConexidad(actor, demandado, tpoJuicio, tipoAudiencia);
        if (salaAudienciaConexidad != null) {
            audienciaService.create(salaAudienciaConexidad, tipoAudiencia, carpeta);
        } else {

            SalaAudienciaRecord salaAudiencia = salaService.asignarSala(carpeta.getJuzgado(), tipoAudiencia);
            audienciaService.create(salaAudiencia, tipoAudiencia, carpeta);
        }
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

    @Transactional
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
        addAnexos(documentoPromocionRecord.anexos(), documento);
        return new DocumentoPromocionResponseRecord(documento.getId(), documentoData.getPromocionFolio(), documento.getTipoDocumento());
    }

    @Transactional
    public DocumentoRecord createExhorto(DocumentoExhortoRecord documentoExhortoRecord) {
        Carpeta carpeta = new Carpeta();
        Documento documento = new Documento();

        carpeta.setEstatus(EstadoCarpeta.CAPTURA);
        carpeta.setFolio(getFolio("E"));
        carpeta.setTipoCarpeta(TipoCarpeta.EXHORTO);

        TipoJuicio tipoJuicio = tipoJuicioRepository.findByNombreIgnoreCase("EXHORTO")
                .orElseThrow(() -> new NotFoundException("Tipo de juicio no encontrado con nombre: Exhorto", "EXHORTO"));
        carpeta.setTipoJuicio(tipoJuicio);

        carpeta.setJuzgado(juzgadoService.getJuzgado(tipoJuicio));
        carpeta.setExpediente(generateNumExpediente(carpeta.getJuzgado(), TipoCarpeta.EXHORTO));
        carpeta.setSelloEstatus(SelloEstatus.VALIDO);
        carpeta = carpetaRepository.save(carpeta);

        DocumentoData data = new DocumentoData();
        data.setExhortoObservaciones(documentoExhortoRecord.observaciones());
        data.setExhortoProcedencia(documentoExhortoRecord.procedencia());
        documento.setData(data);
        documento.setCarpeta(carpeta);
        documento = documentoRepository.save(documento);

        addAnexos(documentoExhortoRecord.anexos(), documento);
        return new DocumentoRecord(documento.getId(), carpeta.getFolio(), documento.getCarpeta().getTipoCarpeta());
    }

    private void addAnexos(List<String> anexos, Documento documento) {
        for (String anexo : anexos) {
            Anexo entity = new Anexo();
            entity.setNombre(anexo);
            entity.setDocumento(documento);
            anexoRepository.save(entity);
        }
    }

    public DocumentoRecord createApelacion(ApelacionRecord apelacionRecord) {
        Documento documento = new Documento();
        Carpeta carpeta = new Carpeta();

        Carpeta carpetaParent = carpetaRepository.findById(apelacionRecord.carpetaId())
                .orElseThrow(() -> new NotFoundException("Carpeta no encontrada", "carpetaId: " + apelacionRecord.carpetaId()));
        carpeta.setTipoJuicio(tipoJuicioRepository.findById(carpetaParent.getTipoJuicio().getId())
                .orElseThrow(() -> new NotFoundException("Tipo Juicio no encontrado", "tipoJuicioId: " + carpetaParent.getTipoJuicio().getId())));

        carpeta.setJuzgado(juzgadoService.getJuzgado(carpeta.getTipoJuicio())); // TODO.ASIGNAR JUZGADO CORRECTAMENTE
        carpeta.setFolio("1"); //TODO. ASIGNAR FOLIO CORRECTAMENTE
        carpeta.setExpediente(generateNumExpediente(carpeta.getJuzgado(), TipoCarpeta.APELACION));
        carpeta.setTipoCarpeta(TipoCarpeta.APELACION);
        carpeta.setEstatus(EstadoCarpeta.CAPTURA);
        carpeta.setSelloEstatus(SelloEstatus.VALIDO);
        carpeta = carpetaRepository.save(carpeta);

        documento.setCarpeta(carpeta);
        DocumentoData data = new DocumentoData();
        data.setApelacionOtroActorNombre(apelacionRecord.otroNombreActor());
        data.setApelacionOtroDemandadoNombre(apelacionRecord.otroNombreDemandado());
        data.setApelacionAntecedenteCarpeta(apelacionRecord.carpetaId().toString());
        documento.setData(data);
        documento = documentoRepository.save(documento);

        for (Anexo anexo : apelacionRecord.anexos()) {
            Anexo entity = new Anexo();
            entity.setNombre(anexo.getNombre());
            entity.setDocumento(documento);
            anexoRepository.save(entity);
        }

        for (ApelacionPersonaRecord persona : apelacionRecord.apelacionPersonaRecords()) {
            PersonaDocumento entity = new PersonaDocumento();
            entity.setNombre(persona.nombre());
            entity.setApellidoPaterno(persona.apellidoPaterno());
            entity.setApellidoMaterno(persona.apellidoMaterno());
            entity.setPseudonimo(persona.pseudonimo());
            entity.setTipoPersona(persona.tipoPersona());
            entity.setRol(Rol.SECUNDARIO);
            entity.setTipoPartes(tipoPartesRepository.findById(persona.tipoPartes())
                    .orElseThrow(() -> new NotFoundException("Tipo Parte no encontrado", "tipoParteId: " + persona.tipoPartes())));
            entity.setCarpeta(carpeta);
            personaDocumentoRepository.save(entity);
        }
        return new DocumentoRecord(documento.getId(), carpeta.getFolio(), documento.getCarpeta().getTipoCarpeta());
    }
}

