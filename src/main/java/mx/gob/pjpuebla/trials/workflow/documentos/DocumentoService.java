package mx.gob.pjpuebla.trials.workflow.documentos;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoService;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.roles.RoleService;
import mx.gob.pjpuebla.trials.core.salas.SalaAudienciaRecord;
import mx.gob.pjpuebla.trials.core.salas.SalaService;
import mx.gob.pjpuebla.trials.core.tipoaudiencia.TipoAudiencia;
import mx.gob.pjpuebla.trials.core.tipoaudiencia.TipoAudienciaService;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.*;
import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.workflow.audiencias.AudienciaService;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.ApelacionPersonaRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.ApelacionRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.*;
import mx.gob.pjpuebla.trials.workflow.etiquetas.EtiquetaService;
import mx.gob.pjpuebla.trials.workflow.folios.JuzgadoFolios;
import mx.gob.pjpuebla.trials.workflow.movimientos.Movimiento;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoService;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoItemRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
    private final PersonaService personaService;
    private final EtiquetaService etiquetaService;
    private final RoleService roleService;
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

    @Transactional(readOnly = true)
    public Page<DocumentoSalidaResponseRecord> getAllBandejaSalida(String key, Pageable pageable) {
        key = (key != null) ? key.toLowerCase() : "";
        Persona persona = personaService.getAuditor();
        Page<DocumentoSalidaRecord> page = documentoRepository.findByEstatusSalida(
                key,
                (persona.getOficialia() != null) ? persona.getOficialia().getId() : null,
                (persona.getJuzgado() != null) ? persona.getJuzgado().getId() : null,
                pageable);
        List<DocumentoSalidaResponseRecord> list = page.getContent().stream()
                .map(item ->
                        new DocumentoSalidaResponseRecord(item.movid(),
                                item.id(),
                                item.folio(),
                                item.expediente(),
                                item.juzgadoId(),
                                item.juzgado(),
                                item.materia(),
                                ((item.tipoCarpeta() != null) ? item.tipoCarpeta().name() : ((item.tipoDocumento() != null) ? item.tipoDocumento().name() : null)),
                                item.fechaRegistro(),
                                item.selloEstatus(),
                                item.estatus()
                        ))
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    public DocumentoRecord updateStatus(Integer id, Integer status) {
        Documento documento = documentoRepository.findById(id).orElseThrow(() -> new NotFoundException(DOC_NOT_FOUND, "documentoId" + id));
        EstadoCarpeta value = EstadoCarpeta.values()[status];
        documento.getCarpeta().setEstatus(value);
        carpetaRepository.save(documento.getCarpeta());
        boolean isPromocion = documento.getTipoDocumento() != null && documento.getTipoDocumento().equals(TipoDocumento.PROMOCION);
        movimientoService.createMovimento(
                isPromocion ? null : documento.getCarpeta(),
                isPromocion ? documento : null,
                personaService.getAuditor(),
                EstadoCarpeta.values()[status].name());
        return new DocumentoRecord(documento.getId(), documento.getCarpeta().getFolio(), documento.getCarpeta().getTipoCarpeta());
    }

    public DocumentoRecord createDemanda(DocumentoSaveRecord documentoRecord) {
        Persona persona = personaService.getAuditor();
        Documento documento = new Documento();
        Carpeta carpeta = new Carpeta();
        TipoJuicio tpoJuicio = tipoJuicioRepository.findById(documentoRecord.tipoJuicioId())
                .orElseThrow(() -> new NotFoundException("Tipo Juicio no encontrado", documentoRecord.tipoJuicioId().toString()));
        carpeta.setTipoJuicio(tpoJuicio);

        carpeta.setJuzgado(juzgadoService.getConexidadJuzgado(documentoRecord.actor(), documentoRecord.demandado(), carpeta.getTipoJuicio()));
        carpeta.setFolio(getFolio("D"));
        carpeta.setTipoCarpeta(TipoCarpeta.DEMANDA);

        if (carpeta.getJuzgado() == null) {
            carpeta.setJuzgado(juzgadoService.getJuzgado(carpeta.getTipoJuicio(), carpeta.getTipoCarpeta()));
        }

        carpeta.setExpediente(generateNumExpediente(carpeta.getJuzgado(), TipoCarpeta.DEMANDA));
        carpeta.setEstatus(EstadoCarpeta.CAPTURA);
        carpeta.setSelloEstatus(SelloEstatus.VALIDO);
        carpeta.setFechaAsignacion(LocalDateTime.now());
        carpeta.setPersona(persona);
        carpeta = carpetaRepository.save(carpeta);
        movimientoService.createMovimento(carpeta, null, persona, EstadoCarpeta.CAPTURA.name());

        documento.setCarpeta(carpeta);
        //SETEAMOS JSON - SOLO PARA DEMANDA FAMILIAR

        documento.setData(documentoRecord.general());
        documento.setFechaAsignacion(LocalDateTime.now());
        documento.setPersona(persona);
        documento = documentoRepository.save(documento);

        createPersonaDocumento(documentoRecord.actor(), carpeta);
        createPersonaDocumento(documentoRecord.demandado(), carpeta);

        addAnexos(documentoRecord.anexos(), documento);
        juzgadoService.actualizarCarga(carpeta.getJuzgado(), carpeta.getTipoCarpeta());

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
        documento.setFolio(getFolio("P"));

        DocumentoData documentoData = new DocumentoData();
        documentoData.setTipoPromocion(documentoPromocionRecord.tipoPromocion());

        documento.setData(documentoData);
        documento.setPersona(personaService.getAuditor());
        documento.setFechaAsignacion(LocalDateTime.now());
        documento.setTipoDocumento(TipoDocumento.PROMOCION);

        documento = documentoRepository.save(documento);
        addAnexos(documentoPromocionRecord.anexos(), documento);
        movimientoService.createMovimento(null, documento, documento.getPersona(), EstadoCarpeta.CAPTURA.name());

        return new DocumentoPromocionResponseRecord(documento.getId(), documento.getFolio(), documento.getTipoDocumento());
    }

    @Transactional
    public DocumentoRecord createExhorto(DocumentoExhortoRecord documentoExhortoRecord) {
        Persona auditor = personaService.getAuditor();
        Carpeta carpeta = new Carpeta();
        Documento documento = new Documento();

        carpeta.setEstatus(EstadoCarpeta.CAPTURA);
        carpeta.setFolio(getFolio("E"));
        carpeta.setTipoCarpeta(TipoCarpeta.EXHORTO);

        TipoJuicio tipoJuicio = tipoJuicioRepository.findByNombreIgnoreCase("EXHORTO")
                .orElseThrow(() -> new NotFoundException("Tipo de juicio no encontrado con nombre: Exhorto", "EXHORTO"));
        carpeta.setTipoJuicio(tipoJuicio);
        carpeta.setJuzgado(juzgadoService.getJuzgado(tipoJuicio, carpeta.getTipoCarpeta()));
        carpeta.setExpediente(generateNumExpediente(carpeta.getJuzgado(), TipoCarpeta.EXHORTO));
        carpeta.setSelloEstatus(SelloEstatus.VALIDO);
        carpeta.setFechaAsignacion(LocalDateTime.now());
        carpeta.setPersona(auditor);
        carpeta = carpetaRepository.save(carpeta);

        DocumentoData data = new DocumentoData();
        data.setExhortoObservaciones(documentoExhortoRecord.observaciones());
        data.setExhortoProcedencia(documentoExhortoRecord.procedencia());
        documento.setData(data);
        documento.setCarpeta(carpeta);
        documento.setPersona(auditor);
        documento.setFechaAsignacion(LocalDateTime.now());
        documento = documentoRepository.save(documento);

        addAnexos(documentoExhortoRecord.anexos(), documento);
        juzgadoService.actualizarCarga(carpeta.getJuzgado(), carpeta.getTipoCarpeta());
        movimientoService.createMovimento(carpeta, null, auditor, EstadoCarpeta.CAPTURA.name());

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
        Persona auditor = personaService.getAuditor();
        Documento documento = new Documento();
        Carpeta carpeta = new Carpeta();

        Carpeta carpetaParent = carpetaRepository.findById(apelacionRecord.carpetaId())
                .orElseThrow(() -> new NotFoundException("Carpeta no encontrada", "carpetaId: " + apelacionRecord.carpetaId()));
        carpeta.setTipoJuicio(tipoJuicioRepository.findById(carpetaParent.getTipoJuicio().getId())
                .orElseThrow(() -> new NotFoundException("Tipo Juicio no encontrado", "tipoJuicioId: " + carpetaParent.getTipoJuicio().getId())));

        carpeta.setTipoCarpeta(TipoCarpeta.APELACION);
        carpeta.setJuzgado(juzgadoService.getJuzgado(carpeta.getTipoJuicio(), carpeta.getTipoCarpeta())); // TODO.ASIGNAR JUZGADO CORRECTAMENTE
        carpeta.setFolio("1"); //TODO. ASIGNAR FOLIO CORRECTAMENTE
        carpeta.setExpediente(generateNumExpediente(carpeta.getJuzgado(), TipoCarpeta.APELACION));
        carpeta.setEstatus(EstadoCarpeta.CAPTURA);
        carpeta.setSelloEstatus(SelloEstatus.VALIDO);
        carpeta.setPersona(auditor);
        carpeta.setFechaAsignacion(LocalDateTime.now());
        carpeta = carpetaRepository.save(carpeta);

        documento.setCarpeta(carpeta);
        DocumentoData data = new DocumentoData();
        data.setApelacionOtroActorNombre(apelacionRecord.otroNombreActor());
        data.setApelacionOtroDemandadoNombre(apelacionRecord.otroNombreDemandado());
        data.setApelacionAntecedenteCarpeta(apelacionRecord.carpetaId().toString());
        documento.setData(data);
        documento.setPersona(auditor);
        documento.setFechaAsignacion(LocalDateTime.now());
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
        juzgadoService.actualizarCarga(carpeta.getJuzgado(), carpeta.getTipoCarpeta());
        movimientoService.createMovimento(carpeta, null, auditor, EstadoCarpeta.CAPTURA.name());
        return new DocumentoRecord(documento.getId(), carpeta.getFolio(), documento.getCarpeta().getTipoCarpeta());
    }

    public Page<DocumentoBandejaRecepcionRecord> getAllBandejaRecepcion(String key, Pageable pageable) {
        key = (key != null) ? key.toLowerCase() : "";
        Persona currentUser = personaService.getAuditor();
        if (roleService.hasRole(currentUser.getUsuario(), "OFICIAL_MAYOR")) {
            return renderOficialMayorData(key, pageable, currentUser);
        }
        return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    private Page<DocumentoBandejaRecepcionRecord> renderOficialMayorData(String key, Pageable pageable, Persona currentUser) {
        Page<Movimiento> page = movimientoService.getAllBandejaRecepcion(
                pageable, currentUser.getJuzgado().getId(),
                Arrays.asList(EstadoCarpeta.TURNADO, EstadoCarpeta.RECEPCION), key,
                Arrays.asList(EstadoCarpeta.TURNADO.name(), EstadoCarpeta.RECEPCION.name()));
        List<DocumentoBandejaRecepcionRecord> list = new ArrayList<>();
        for (Movimiento movimiento : page.getContent()) {
            Carpeta carpeta = movimiento.getCarpeta();
            Documento documento = (movimiento.getDocumento() != null) ? movimiento.getDocumento() :
                    documentoRepository.findByCarpetaIdAndTipoDocumentoIsNull(carpeta.getId());
            String folio = (documento.getTipoDocumento() == null) ? documento.getCarpeta().getFolio() : documento.getFolio();
            String tipoEntrada = etiquetaService.renderEtiquetaRecepcion("nuevoNombre", documento);
            String origen = getOrigen(movimiento, currentUser);
            String concepto = "";//TODO agregar concepto
            DocumentoBandejaRecepcionRecord record = new DocumentoBandejaRecepcionRecord(
                    documento.getId(), folio, documento.getCarpeta().getExpediente(), tipoEntrada, origen,
                    concepto, movimiento.getFechaAsignacion());
            list.add(record);
        }
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    protected String getOrigen(Movimiento movimiento, Persona persona) {
        if (persona.getJuzgado() != null) {
            if (movimiento.getJuzgado() != null && Objects.equals(movimiento.getJuzgado().getId(), persona.getJuzgado().getId())) {
                return persona.getNombre() + " " + persona.getApellidoPaterno();
            } else {
                return persona.getJuzgado().getNombre();
            }
        }
        if (persona.getOficialia() != null) {
            if (movimiento.getOficialia() != null && Objects.equals(movimiento.getOficialia().getId(), persona.getOficialia().getId())) {
                return persona.getNombre() + " " + persona.getApellidoPaterno();
            } else {
                return persona.getOficialia().getNombre();
            }
        }
        return "";
    }

    public Page<DocumentoAsignadoResponseRecord> getAllAsignado(String key, Pageable pageable){
        key = (key != null) ? key.toLowerCase() : "";
        Persona persona = personaService.getAuditor();
        Page<DocumentoAsignadoRecord> page = documentoRepository.findByPersonaAsignada(key, persona, pageable); 
        boolean esOficialMayor = roleService.hasRole(persona.getUsuario(), "OFICIAL_MAYOR");

        List<DocumentoAsignadoResponseRecord> list = page.getContent().stream()
                .map(item ->
                        new DocumentoAsignadoResponseRecord(
                            item.id(),
                            item.expediente(),
                            esOficialMayor?item.folioDocumento():item.folioCarpeta(),
                            esOficialMayor?item.tipoCarpeta().name():item.tipoDocumento().name(),
                            item.concepto().getNombre(),
                            item.fechaTurnado(),
                            item.fechaTurnado().plusDays(item.concepto().getDias()),
                            item.estatus().name(),
                            item.observaciones()
                        ))
                .toList();
                
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }
}

