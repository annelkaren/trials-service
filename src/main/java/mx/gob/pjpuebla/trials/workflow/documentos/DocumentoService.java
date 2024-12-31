package mx.gob.pjpuebla.trials.workflow.documentos;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.instituciones.Institucion;
import mx.gob.pjpuebla.trials.core.instituciones.InstitucionRepository;
import mx.gob.pjpuebla.trials.core.conceptos.Concepto;
import mx.gob.pjpuebla.trials.core.conceptos.ConceptoRepository;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoService;
import mx.gob.pjpuebla.trials.core.oficialias.Oficialia;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaRepository;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.roles.RoleService;
import mx.gob.pjpuebla.trials.core.salas.SalaAudienciaRecord;
import mx.gob.pjpuebla.trials.core.salas.SalaService;
import mx.gob.pjpuebla.trials.core.tipoaudiencia.TipoAudiencia;
import mx.gob.pjpuebla.trials.core.tipoaudiencia.TipoAudienciaService;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioDemandasRecord;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;
import mx.gob.pjpuebla.trials.error.ConflictException;
import mx.gob.pjpuebla.trials.error.ConstraintViolationException;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.EmailService;
import mx.gob.pjpuebla.trials.util.Messages;
import mx.gob.pjpuebla.trials.util.enums.*;
import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRecepcionRecord;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.workflow.audiencias.AudienciaRepository;
import mx.gob.pjpuebla.trials.workflow.audiencias.AudienciaService;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.carpetadetalle.CarpetaDetalle;
import mx.gob.pjpuebla.trials.workflow.carpeta.carpetadetalle.CarpetaDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaService;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.ApelacionPersonaRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.ApelacionRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.CarpetaResponseRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.PiezaRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.amparos.AmparoGetRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.amparos.AmparoRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.amparos.AmparoRecordResponse;
import mx.gob.pjpuebla.trials.workflow.documentos.amparos.AmparoUpdateRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalle;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.records.*;
import mx.gob.pjpuebla.trials.workflow.etiquetas.EtiquetaService;
import mx.gob.pjpuebla.trials.workflow.folios.DocumentoFoliosService;
import mx.gob.pjpuebla.trials.workflow.folios.JuzgadoFolios;
import mx.gob.pjpuebla.trials.workflow.movimientos.Movimiento;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoRepository;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoService;
import mx.gob.pjpuebla.trials.workflow.personadetalle.PersonaDetalle;
import mx.gob.pjpuebla.trials.workflow.personadetalle.PersonaDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoItemRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.sello.SelloGenerator;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;

import org.apache.commons.lang3.StringUtils;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class DocumentoService {

    public static final String ACTOR = "Actor";
    public static final String DEMANDADO = "Demandado";
    public static final String IS_INTERNO = "isInterno";
    private final DocumentoRepository documentoRepository;
    private final JuzgadoService juzgadoService;
    private final TipoJuicioRepository tipoJuicioRepository;
    private final AnexoRepository anexoRepository;
    private final PersonaDocumentoRepository personaDocumentoRepository;
    private final TipoPartesRepository tipoPartesRepository;
    private final CarpetaRepository carpetaRepository;
    private final CarpetaService carpetaService;
    private final TipoAudienciaService tipoAudienciaService;
    private final SalaService salaService;
    private final AudienciaService audienciaService;
    private final MovimientoService movimientoService;
    private final DigitalizacionService digitalizacionService;
    private final MovimientoRepository movimientoRepository;
    private final PersonaService personaService;
    private final PersonaRepository personaRepository;
    private final EtiquetaService etiquetaService;
    private final RoleService roleService;
    private final DocumentoFoliosService documentoFoliosService;
    private final InstitucionRepository institucionRepository;
    private final ConceptoRepository conceptoRepository;
    private final EmailService emailService;
    private final SelloGenerator selloGenerator;
    private final DocumentoDetalleRepository documentoDetalleRepository;
    private final AudienciaRepository audienciaRepository;
    private final CarpetaDetalleRepository carpetaDetalleRepository;
    private final PersonaDetalleRepository personaDetalleRepository;
    private final JuzgadoRepository juzgadoRepository;

    private static final String DOC_NOT_FOUND = "Documento no encontrado";
    private static final String DOC_ID = "documentoId: ";
    private static final String TIPO_JUICIO_NOT_FOUND = "Tipo Juicio no encontrado: ";
    private static final String CARPETA_NOT_FOUND = "Carpeta no encontrada";
    private static final String CONCEPTO_NOT_FOUND = "Concepto no encontrado";

    private Persona personaAsignada = null ;

    @Transactional(readOnly = true)
    public Page<DocumentoGridRecord> getAll(String key, Pageable pageable) {
        key = (key != null) ? key.toLowerCase() : "";
        Persona persona = personaService.getAuditor();
        Integer juzgadoId = (persona.getJuzgado() != null) ? persona.getJuzgado().getId() : null;
        Integer oficialiaId = (persona.getOficialia() != null) ? persona.getOficialia().getId() : null;
        Page<Movimiento> page = movimientoService.getAllBandejaEntrada(pageable, juzgadoId, oficialiaId, key);
        List<DocumentoGridRecord> list = new ArrayList<>();
        for (Movimiento mov : page.getContent()) {
            Documento documento = (mov.getDocumento() != null) ? mov.getDocumento() : documentoRepository.findByCarpetaIdAndTipoDocumentoIsNull(mov.getCarpeta().getId());
            Carpeta carpeta = (mov.getCarpeta() != null) ? mov.getCarpeta() : documento.getCarpeta();
            DocumentoGridRecord documentoGridRecord =
                    new DocumentoGridRecord(
                            documento.getId(),
                            (documento.getTipoDocumento() != null && !Objects.equals(documento.getTipoDocumento(), TipoDocumento.APELACION)) ? documento.getFolio() : carpeta.getFolio(),
                            carpeta.getExpediente(),
                            StringUtils.capitalize(carpeta.getJuzgado().getMateria().getNombre().toLowerCase()),
                            (documento.getTipoDocumento() != null) ?
                                    StringUtils.capitalize(documento.getTipoDocumento().name().toLowerCase()) :
                                    StringUtils.capitalize(carpeta.getTipoCarpeta().name().toLowerCase()),
                            documento.getAudit().getFechaAlta(),
                            carpeta.getSelloEstatus(),
                            (documento.getTipoDocumento() != null) ? documento.getEstatus() : carpeta.getEstatus(),
                            (documento.getRuta() != null),
                            documento.getCarpeta().getJuzgado().getNombre()
                    );
            list.add(documentoGridRecord);
        }
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<DocumentoSalidaResponseRecord> getAllBandejaSalida(String key, Pageable pageable) {
        key = (key != null) ? key.toLowerCase() : "";
        Object[] resultado = procesarTipoCarpeta(key);
        TipoCarpeta tipoCarpetaNombre = (TipoCarpeta) resultado[0];
        TipoDocumento tipoDocumentoNombre = (TipoDocumento) resultado[1];
        Integer folio = (Integer) resultado[2];
        
        Persona persona = personaService.getAuditor();
        Page<DocumentoSalidaRecord> page = documentoRepository.findByEstatusSalida(
                key,
                (persona.getOficialia() != null) ? persona.getOficialia().getId() : null,
                (persona.getJuzgado() != null) ? persona.getJuzgado().getId() : null,
                folio,
                tipoCarpetaNombre,
                tipoDocumentoNombre,
                pageable);
        List<DocumentoSalidaResponseRecord> list = page.getContent().stream()
                .map(item ->
                        new DocumentoSalidaResponseRecord(
                                item.movid(),
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
        Documento documento = documentoRepository.findById(id).orElseThrow(() -> new NotFoundException(DOC_NOT_FOUND, DOC_ID + id));
        EstadoCarpeta value = EstadoCarpeta.values()[status];
        if (documento.getTipoDocumento() == null) {
            documento.getCarpeta().setEstatus(value);
            carpetaRepository.save(documento.getCarpeta());
        } else {
            documento.setEstatus(value);
            documentoRepository.save(documento);
        }
        boolean isDocumento = documento.getTipoDocumento() != null;
        boolean isApelacion = Objects.equals(documento.getTipoDocumento(), TipoDocumento.APELACION);
        movimientoService.createMovimento(
                isDocumento && !isApelacion ? null : documento.getCarpeta(),
                isDocumento && !isApelacion ? documento : null,
                personaService.getAuditor(),
                null,
                EstadoCarpeta.values()[status].name());
        return new DocumentoRecord(documento.getId(), documento.getCarpeta().getFolio(), documento.getCarpeta().getTipoCarpeta());
    }

    public DocumentoRecord createDemanda(DocumentoSaveRecord documentoRecord) {
        Persona persona = personaService.getAuditor();

        Oficialia oficialia = persona.getOficialia();
        if (oficialia == null) {
            throw new NotFoundException("La persona no está relacionada con ninguna oficialía", "persona.getOficialia()");
        }
        List<Juzgado> juzgadosRelacionados = juzgadoRepository.findJuzgadoByOficialiaId(oficialia.getId());
        Documento documento = new Documento();
        Carpeta carpeta = new Carpeta();
        TipoJuicio tipoJuicio = tipoJuicioRepository.findById(documentoRecord.tipoJuicioId())
                .orElseThrow(() -> new NotFoundException(TIPO_JUICIO_NOT_FOUND, documentoRecord.tipoJuicioId().toString()));
        carpeta.setTipoJuicio(tipoJuicio);

        Juzgado juzgadoConexidad = juzgadoService.getConexidadJuzgado(documentoRecord.actor(), documentoRecord.demandado(), carpeta.getTipoJuicio());
        if (juzgadoConexidad != null && !juzgadosRelacionados.contains(juzgadoConexidad)) {
            throw new IllegalArgumentException("El juzgado asignado no está relacionado con la oficialía, juzgado conexidad");
        }
        carpeta.setJuzgado(juzgadoConexidad);
        carpeta.setFolio(getFolio("D"));
        carpeta.setTipoCarpeta(TipoCarpeta.DEMANDA);

        if (carpeta.getJuzgado() == null) {
            Juzgado juzgadoPorJuicio = juzgadoService.getJuzgado(carpeta.getTipoJuicio(), carpeta.getTipoCarpeta(), juzgadosRelacionados);
            if (!juzgadosRelacionados.contains(juzgadoPorJuicio)) {
                throw new IllegalArgumentException("El juzgado asignado no está relacionado con la oficialía, juzgado tipo juicio");
            }
            carpeta.setJuzgado(juzgadoPorJuicio);
        }

        carpeta.setExpediente(generateNumExpediente(carpeta.getJuzgado(), TipoCarpeta.DEMANDA));
        carpeta.setEstatus(EstadoCarpeta.CAPTURA);
        carpeta.setSelloEstatus(SelloEstatus.VALIDO);
        carpeta.setFechaAsignacion(LocalDateTime.now());
        carpeta.setPersona(persona);
        carpeta = carpetaRepository.save(carpeta);
        movimientoService.createMovimento(carpeta, null, persona, null, EstadoCarpeta.CAPTURA.name());

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
        if ("FAMILIAR".contains(tipoJuicio.getMateria().getNombre().toUpperCase())
                && tipoJuicio.getNombre().toUpperCase().contains("ORAL")) {
            crearAudienciaOralidad(documentoRecord, carpeta, tipoJuicio);
            if (documentoRecord.general().getTieneAbogado() == 0) {
                sendEmailFamiliar(documentoRecord, documento, carpeta, tipoJuicio);
            }
        }

        carpetaDetalleRepository.save(new CarpetaDetalle().setCarpeta(carpeta));

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
                ACTOR,
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
                DEMANDADO,
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
        String tipoParte = (persona.tipoParte().equals(1)) ? ACTOR : DEMANDADO;
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

    public DocumentoRecord editarAnexos(Integer documentoId, List<String> nuevosAnexos, String motivoEdita, String procedencia) {

        Documento documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new NotFoundException(DOC_NOT_FOUND, DOC_ID + documentoId));
        documento.getCarpeta().setSelloEstatus(SelloEstatus.NO_VALIDO);

        if (documento.getData() != null && procedencia != null && documento.getData().getExhortoProcedencia() != null) {
            documento.setData(documento.getData().setExhortoProcedencia(procedencia));
        }

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

        if (documento.getTipoDocumento() != null) {
            movimientoService.createMovimento(null, documento, documento.getPersona(), motivoEdita, EstadoCarpeta.EDICION.name());
        } else {
            movimientoService.createMovimento(documento.getCarpeta(), null, documento.getPersona(), motivoEdita, EstadoCarpeta.EDICION.name());
        }
        return new DocumentoRecord(documentoId, documento.getCarpeta().getFolio(), documento.getCarpeta().getTipoCarpeta());
    }

    public DocumentoResponseRecord getDemandaById(Integer id) {

        Documento documento = documentoRepository.findById(id).orElseThrow(() -> new NotFoundException(DOC_NOT_FOUND, id.toString()));
        List<PersonaDocumentoRecord> personas = personaDocumentoRepository.findPersonasByCarpetaId(documento.getCarpeta().getId(), Rol.PRINCIPAL);
        List<String> anexos = anexoRepository.findNombresAnexosByDocumentoId(id);

        PersonaDocumentoRecord actor = null;
        PersonaDocumentoRecord demandado = null;

        for (PersonaDocumentoRecord persona : personas) {
            if (ACTOR.equalsIgnoreCase(persona.tipoParte())) {
                actor = persona;
            } else if (DEMANDADO.equalsIgnoreCase(persona.tipoParte())) {
                demandado = persona;
            }
        }
        return new DocumentoResponseRecord(actor, demandado, anexos, documento.getData(), documento.getCarpeta().getTipoJuicio().getNombre(), documento.getCarpeta().getTipoJuicio().getId());
    }

    /**
     * Devuelve un numero de folio
     *
     * @param tipo E-exhorto, D-demanda, P-promocion, AP-Apelación.
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
            case "ES" ->           // Case para exhorto salida
                    documentoRepository.getNextValExhortoSalida();
            case "AP" -> 
                    documentoRepository.getNextValApelacion();
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

    public Page<DocumentoGridRecord> getAllHistorial(String key, Pageable pageable) {
        key = (key != null) ? key.toLowerCase() : "";
        Persona currentUser = personaService.getAuditor();
        Page<Movimiento> page;
        Integer juzgadoId = (currentUser.getJuzgado() != null) ? currentUser.getJuzgado().getId() : null;
        Integer oficialiaId = (currentUser.getOficialia() != null) ? currentUser.getOficialia().getId() : null;
        page = movimientoRepository.getAllBandejaHistorial(key, juzgadoId, oficialiaId, pageable);

        List<DocumentoGridRecord> listaDocumentoRecords = new ArrayList<>();
        for (Movimiento movimiento : page.getContent()) {
            Documento documento = (movimiento.getDocumento() != null) ? movimiento.getDocumento() : documentoRepository.findByCarpetaIdAndTipoDocumentoIsNull(movimiento.getCarpeta().getId());
            Carpeta carpeta = documento.getCarpeta();
            String folio = (documento.getTipoDocumento() == null) ? carpeta.getFolio() : documento.getFolio();
            DocumentoGridRecord drecord = new DocumentoGridRecord(
                    documento.getId(),
                    folio,
                    carpeta.getExpediente(),
                    StringUtils.capitalize(carpeta.getJuzgado().getMateria().getNombre().toLowerCase()),
                    (documento.getTipoDocumento() == null) ? StringUtils.capitalize(carpeta.getTipoCarpeta().name().toLowerCase()) : StringUtils.capitalize(documento.getTipoDocumento().name().toLowerCase()),
                    movimiento.getFechaAsignacion(),
                    null,
                    EstadoCarpeta.valueOf(movimiento.getEstado()),
                    false,
                    ""
            );
            listaDocumentoRecords.add(drecord);
        }
        return new PageImpl<>(listaDocumentoRecords, pageable, page.getTotalElements());
    }

    @Transactional
    public DocumentoPromocionResponseRecord createPromocion(DocumentoPromocionRecord documentoPromocionRecord, MultipartFile multipartFile) {
        Carpeta carpeta = carpetaRepository.findById(documentoPromocionRecord.carpetaId())
                .orElseThrow(() -> new NotFoundException(CARPETA_NOT_FOUND, String.valueOf(documentoPromocionRecord.carpetaId())));
        Documento documento = new Documento();
        documento.setCarpeta(carpeta);
        documento.setFolio(getFolio("P"));
        documento.setEstatus(EstadoCarpeta.CAPTURA);

        DocumentoData documentoData = new DocumentoData();
        documentoData.setTipoPromocion(documentoPromocionRecord.tipoPromocion());

        documento.setData(documentoData);
        documento.setPersona(personaService.getAuditor());
        documento.setFechaAsignacion(LocalDateTime.now());
        documento.setTipoDocumento(TipoDocumento.PROMOCION);

        documento = documentoRepository.save(documento);
        digitalizacionService.guardarArchivo(multipartFile, documento.getId());
        addAnexos(documentoPromocionRecord.anexos(), documento);
        movimientoService.createMovimento(null, documento, documento.getPersona(), null, EstadoCarpeta.CAPTURA.name());

        return new DocumentoPromocionResponseRecord(documento.getId(), documento.getFolio(), documento.getTipoDocumento());
    }

    @Transactional
    public DocumentoRecord createExhorto(DocumentoExhortoRecord documentoExhortoRecord) {
        Persona auditor = personaService.getAuditor();
        Carpeta carpeta = new Carpeta();
        Documento documento = new Documento();

        Oficialia oficialia = auditor.getOficialia();
        if (oficialia == null) {
            throw new NotFoundException("La persona no está relacionada con ninguna oficialía", "persona.getOficialia()");
        }
        List<Juzgado> juzgadosRelacionadosExhorto = juzgadoRepository.findJuzgadoExhortoByOficialiaId(oficialia.getId());

        carpeta.setEstatus(EstadoCarpeta.CAPTURA);
        carpeta.setFolio(getFolio("E"));
        carpeta.setTipoCarpeta(TipoCarpeta.EXHORTO);

        TipoJuicio tipoJuicio = tipoJuicioRepository.findByNombreIgnoreCase("EXHORTO")
                .orElseThrow(() -> new NotFoundException("Tipo de juicio no encontrado con nombre: Exhorto", "EXHORTO"));
        carpeta.setTipoJuicio(tipoJuicio);
        carpeta.setJuzgado(juzgadoService.getJuzgado(tipoJuicio, carpeta.getTipoCarpeta(), juzgadosRelacionadosExhorto));
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
        movimientoService.createMovimento(carpeta, null, auditor, null, EstadoCarpeta.CAPTURA.name());

        carpetaDetalleRepository.save(new CarpetaDetalle().setCarpeta(carpeta));

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
                .orElseThrow(() -> new NotFoundException(CARPETA_NOT_FOUND, "carpetaId: " + apelacionRecord.carpetaId()));
        carpeta.setTipoJuicio(tipoJuicioRepository.findById(carpetaParent.getTipoJuicio().getId())
                .orElseThrow(() -> new NotFoundException(TIPO_JUICIO_NOT_FOUND, "tipoJuicioId: " + carpetaParent.getTipoJuicio().getId())));

        carpeta.setTipoCarpeta(TipoCarpeta.APELACION);
        carpeta.setJuzgado(juzgadoService.getJuzgado(carpeta.getTipoJuicio(), carpeta.getTipoCarpeta(), null));
        carpeta.setFolio(getFolio("AP")); 
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
        documento.setTipoDocumento(TipoDocumento.APELACION);
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
        movimientoService.createMovimento(carpeta, documento, auditor, null, EstadoCarpeta.CAPTURA.name());
        return new DocumentoRecord(documento.getId(), carpeta.getFolio(), documento.getCarpeta().getTipoCarpeta());
    }

    public Page<DocumentoBandejaRecepcionRecord> getAllBandejaRecepcion(String key, Pageable pageable) {
        key = (key != null) ? key.toLowerCase() : "";
        Persona currentUser = personaService.getAuditor();
        if (roleService.hasRole(currentUser.getUsuario(), "OFICIAL_MAYOR_JUZGADO")) {
            return renderOficialMayorData(key, pageable, currentUser);
        }
        return renderData(key, pageable, currentUser);
    }

    private Page<DocumentoBandejaRecepcionRecord> renderData(String key, Pageable pageable, Persona currentUser) {
        Page<Movimiento> page = movimientoService.getBandejaRecepcion(
                pageable,
                currentUser.getJuzgado().getId(),
                EstadoCarpeta.TURNADO,
                key,
                EstadoCarpeta.TURNADO.name(),
                currentUser
        );
        List<DocumentoBandejaRecepcionRecord> list = new ArrayList<>();
        for (Movimiento movimiento : page.getContent()) {
            Carpeta carpeta = movimiento.getCarpeta();
            String tipoEntrada = etiquetaService.renderEtiquetaRecepcion("nuevoNombre", carpeta);

            DocumentoBandejaRecepcionRecord drecord = new DocumentoBandejaRecepcionRecord(
                    carpeta.getId(),
                    carpeta.getFolio(),
                    carpeta.getExpediente(),
                    tipoEntrada,
                    movimiento.getPersona().getNombre() + " " + movimiento.getPersona().getApellidoPaterno() + " " + ((movimiento.getPersona().getApellidoMaterno() != null) ? movimiento.getPersona().getApellidoMaterno() : ""),
                    carpeta.getConcepto().getNombre(),
                    movimiento.getFechaAsignacion(),
                    true,
                    carpeta.getPrioridad(),
                    carpeta.getHoras()
            );
            list.add(drecord);
        }
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    private Page<DocumentoBandejaRecepcionRecord> renderOficialMayorData(String key, Pageable pageable, Persona currentUser) {
        Page<Movimiento> page = movimientoService.getAllBandejaRecepcion(
                pageable,
                currentUser.getJuzgado().getId(),
                Arrays.asList(EstadoCarpeta.TURNADO, EstadoCarpeta.RECEPCION),
                key,
                Arrays.asList(EstadoCarpeta.TURNADO.name(), EstadoCarpeta.RECEPCION.name())
        );
        List<DocumentoBandejaRecepcionRecord> list = new ArrayList<>();
        for (Movimiento movimiento : page.getContent()) {
            Carpeta carpeta = movimiento.getCarpeta();
            Documento documento = getDocumentoForRenderOficialMayor(movimiento, carpeta); // TODO: cambio en obtencion de documento para incluir apelación validar si es correcto el cambio (movimiento.getDocumento() != null) ? movimiento.getDocumento() : documentoRepository.findByCarpetaIdAndTipoDocumentoIsNull(carpeta.getId());
          
            boolean isPromocion = (documento != null && documento.getTipoDocumento() != null && documento.getTipoDocumento().equals(TipoDocumento.PROMOCION));
            
            String folio = (isPromocion) ? documento.getFolio() : carpeta.getFolio();
            String tipoEntrada = (isPromocion)? etiquetaService.renderEtiquetaRecepcion("nuevoNombre", documento): etiquetaService.renderEtiquetaRecepcion("nuevoNombre", carpeta);
            Map<String, Object> map = getOrigen(movimiento, currentUser);
            String concepto = (isPromocion) ? documento.getConcepto().getNombre() : carpeta.getConcepto().getNombre();
            DocumentoBandejaRecepcionRecord drecord = new DocumentoBandejaRecepcionRecord(
                    documento != null ? documento.getId() : null, //(isPromocion) ? documento.getId():carpeta.getId(), TODO: prueba para corregir anexos en la bandeja de recepcion.
                    folio,
                    (isPromocion) ? documento.getCarpeta().getExpediente(): carpeta.getExpediente(),
                    StringUtils.capitalize(tipoEntrada.toLowerCase()),
                    map.get("name").toString(),
                    concepto,
                    movimiento.getFechaAsignacion(),
                    (Boolean) map.get(IS_INTERNO),
                    null,
                    null
            );
           
            list.add(drecord);
        }
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    protected Documento getDocumentoForRenderOficialMayor(Movimiento movimiento, Carpeta carpeta) {
        if (movimiento.getDocumento() != null) {
            return movimiento.getDocumento();
        }
    
        if (!carpeta.getTipoCarpeta().equals(TipoCarpeta.APELACION)) {
            return documentoRepository.findByCarpetaIdAndTipoDocumentoIsNull(carpeta.getId());
        }
    
        return documentoRepository.findByCarpetaIdAndTipoDocumento(carpeta.getId(), TipoDocumento.APELACION);
    }
    

    protected Map<String, Object> getOrigen(Movimiento movimiento, Persona persona) {
        String origen = movimientoService.getOrigen(
                (movimiento.getDocumento() != null) ? movimiento.getDocumento().getId() : null,
                (movimiento.getCarpeta() != null) ? movimiento.getCarpeta().getId() : null);
        Map<String, Object> map = new HashMap<>();
        map.put(IS_INTERNO, false);
        if (persona.getJuzgado() != null && Objects.equals(origen.toUpperCase(), persona.getJuzgado().getNombre().toUpperCase())) {
            map.put(IS_INTERNO, true);
        }
        if (persona.getOficialia() != null && Objects.equals(origen.toUpperCase(), persona.getOficialia().getNombre().toUpperCase())) {
            map.put(IS_INTERNO, true);
        }
        map.put("name", origen);
        return map;
    }

    public Page<DocumentoAsignadoResponseRecord> getAllAsignado(String key, Pageable pageable) {
        key = (key != null) ? key.toLowerCase() : "";
        Persona persona = personaAsignada != null ? personaAsignada : personaService.getAuditor();
        boolean esOficialMayor = roleService.hasRole(persona.getUsuario(), "OFICIAL_MAYOR_JUZGADO");

        Page<Movimiento> page = documentoRepository.findByPersonaAsignada(key, persona.getJuzgado().getId(), persona, esOficialMayor, pageable);

        List<DocumentoAsignadoResponseRecord> list = new ArrayList<>();
        for (Movimiento mov : page.getContent()) {
            Documento documento = mov.getDocumento();
            boolean isPromocion = (documento != null && documento.getTipoDocumento() != null && documento.getTipoDocumento().equals(TipoDocumento.PROMOCION));
            Carpeta carpeta = (mov.getCarpeta() != null) ? mov.getCarpeta() : documento.getCarpeta();

            DocumentoAsignadoResponseRecord documentoGridRecord =
                    new DocumentoAsignadoResponseRecord(
                            (isPromocion) ? documento.getId() : null,
                            carpeta.getId(),
                            carpeta.getExpediente(),
                            (isPromocion) ? documento.getFolio() : carpeta.getFolio(),
                            StringUtils.capitalize(
                                    (isPromocion) ?
                                            documento.getTipoDocumento().name().toLowerCase() :
                                            carpeta.getTipoCarpeta().name().toLowerCase()),
                            (isPromocion) ? documento.getConcepto().getNombre() : (carpeta.getConcepto() != null) ? carpeta.getConcepto().getNombre() : "-",
                            mov.getFechaAsignacion(),
                            (isPromocion) ? mov.getFechaAsignacion().plusDays(documento.getConcepto().getDias()) : (carpeta.getConcepto() != null) ? mov.getFechaAsignacion().plusDays(carpeta.getConcepto().getDias()) : null,//TODO. Validar si tiene horas sumar en lugar de dias, crear nuevo metodo
                            StringUtils.capitalize((isPromocion) ? documento.getEstatus().name().toLowerCase() : carpeta.getEstatus().name().toLowerCase()),
                            (isPromocion) ? mov.getObservaciones() : getObservaciones(carpeta, mov.getObservaciones()));
            list.add(documentoGridRecord);
        }

        personaAsignada = null;
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    private String getObservaciones(Carpeta carpeta, String observaciones) {
        if (carpeta.getPrioridad() != null && carpeta.getPrioridad().equals(Prioridad.URGENTE)) {
            return StringUtils.capitalize(Prioridad.URGENTE.name().toLowerCase());
        }
        Integer promociones = documentoRepository.countByCarpetaIdAndTipoDocumentoAndEstatus(
                carpeta.getId(), TipoDocumento.PROMOCION, EstadoCarpeta.INTEGRADO);
        if (promociones > 0) {
            return promociones + " promociones nuevas";
        }
        return observaciones;
    }

    public List<DocumentoAsignadoResponseRecord> getAllAsignado(Persona persona, String uuid){
        personaAsignada = persona;
        String key = Objects.toString(uuid, "");

        return getAllAsignado(key, Pageable.unpaged()).getContent();
    }

    protected String sendToBandejaRecepcion(List<Integer> idList, Integer personaCarrito) {
        UUID uuid = UUID.randomUUID();

        List<Movimiento> movimientoList = movimientoRepository.findAllById(idList);
        Persona persona = personaRepository.findById(Long.valueOf(personaCarrito)).orElseThrow(() -> new NotFoundException("Persona no encontrada", "PersonaId: " + personaCarrito));
        Persona personaAuditor = personaService.getAuditor();
        String nombrePersona = String.format("%s %s %s", persona.getNombre(), persona.getApellidoPaterno(), Objects.toString(persona.getApellidoMaterno(), ""));

        for (Movimiento mov : movimientoList) {
            Movimiento movimiento = new Movimiento()
                    .setFechaAsignacion(LocalDateTime.now())
                    .setEstado(EstadoCarpeta.TURNADO.name())
                    .setPersona(personaAuditor)
                    .setUuid(uuid)
                    .setObservaciones(nombrePersona);

            if (mov.getDocumento() != null) {
                Documento documento = mov.getDocumento();
                documento.setFechaAsignacion(LocalDateTime.now())
                        .setPersona(persona)
                        .setEstatus(EstadoCarpeta.TURNADO);
                documento.setConcepto(getConceptoByTipoCarpetaDocumento(documento.getTipoDocumento(), null));
                documento = documentoRepository.save(documento);
                movimiento.setConcepto(documento.getConcepto().getNombre());
                movimiento.setDuracion(documento.getConcepto().getDias().toString() + "d");
                movimiento.setDocumento(documento);
                movimiento.setJuzgado(documento.getCarpeta().getJuzgado());
            } else {
                Carpeta carpeta = mov.getCarpeta();
                carpeta.setFechaAsignacion(LocalDateTime.now())
                        .setPersona(persona)
                        .setConcepto(getConceptoByTipoCarpetaDocumento(null, carpeta.getTipoCarpeta()))
                        .setEstatus(EstadoCarpeta.TURNADO);

                carpeta = carpetaRepository.save(carpeta);
                movimiento.setConcepto(carpeta.getConcepto().getNombre());
                movimiento.setDuracion(carpeta.getConcepto().getDias().toString() + "d");
                movimiento.setCarpeta(carpeta);
                movimiento.setJuzgado(carpeta.getJuzgado());
            }
            this.movimientoRepository.save(movimiento);
        }
        return uuid.toString();
    }

    private Concepto getConceptoByTipoCarpetaDocumento(TipoDocumento tipoDocumento, TipoCarpeta tipoCarpeta) {
        String conceptoDistri = "Distribución";
        String conceptoAdjun = "Adjuntar";

        Concepto concepto = new Concepto();
        if (tipoDocumento == TipoDocumento.PROMOCION) {
            concepto = conceptoRepository.findByNombre(conceptoAdjun).orElseThrow(() -> new NotFoundException(CONCEPTO_NOT_FOUND, conceptoAdjun));
        } else if ((tipoCarpeta == TipoCarpeta.DEMANDA || tipoCarpeta == TipoCarpeta.EXHORTO || tipoCarpeta == TipoCarpeta.APELACION) || tipoCarpeta == TipoCarpeta.PIEZA) {
            concepto = conceptoRepository.findByNombre(conceptoDistri).orElseThrow(() -> new NotFoundException(CONCEPTO_NOT_FOUND, conceptoDistri));
        }
        return concepto;
    }


    public Object[] getQR(String folioDocumentoQR) {
        String[] parte = folioDocumentoQR.split("-");
        if (parte.length != 2) {
            throw new IllegalArgumentException("El código QR tiene un formato inválido.");
        }

        String prefix = parte[0].trim();
        int folio = Integer.parseInt(parte[1].trim());

        return new Object[]{prefix, folio};
    }


    public Object[] procesarTipoCarpeta(String key) {
        String tipoCarpeta = null;
        Integer folio = null;

        if (key.matches("[a-zA-Z]-\\d+")) {
            Object[] qrValues = getQR(key);
            tipoCarpeta = (String) qrValues[0];
            folio = (Integer) qrValues[1];
        }

        TipoCarpeta tipoCarpetaNombre = null;
        TipoDocumento tipoDocumentoNombre = null;

        if (tipoCarpeta != null) {
            switch (tipoCarpeta.toUpperCase()) {
                case "E":
                    tipoCarpetaNombre = TipoCarpeta.EXHORTO;
                    break;
                case "D":
                    tipoCarpetaNombre = TipoCarpeta.DEMANDA;
                    break;
                case "A":
                    tipoCarpetaNombre = TipoCarpeta.APELACION;
                    break;
                case "P":
                    tipoDocumentoNombre = TipoDocumento.PROMOCION;
                    break;
                case "PZ":
                    tipoCarpetaNombre = TipoCarpeta.PIEZA;
                    break;
                default:
                    throw new IllegalArgumentException("El tipo de carpeta es desconocido");
            }
        }

        return new Object[]{tipoCarpetaNombre, tipoDocumentoNombre, folio};
    }

    public IndicadoresRecord getIndicadores() {
        int totalPendientes;
        Integer totalRecibidosHoy = 0;
        Integer totalRecibidosAyer = 0;
        Integer totalOldies = 0;

        Page<DocumentoBandejaRecepcionRecord> page = getAllBandejaRecepcion("", Pageable.unpaged());

        totalPendientes = page.getSize();

        for (DocumentoBandejaRecepcionRecord item : page.getContent()) {
            LocalDate fechaAsignacion = item.fechaHoraEnvio().toLocalDate();

            if (fechaAsignacion.equals(LocalDate.now())) {
                totalRecibidosHoy++;
            } else if (fechaAsignacion.equals(LocalDate.now().minusDays(1))) {
                totalRecibidosAyer++;
            } else {
                totalOldies++;
            }
        }

        return new IndicadoresRecord(totalPendientes, totalRecibidosHoy, totalRecibidosAyer, totalOldies);
    }

    public Integer createOficio(Integer institucionId, LocalDate fechaEmision, String asunto, Integer carpetaId) {

        //Obtenemos folio
        Persona persona = personaService.getAuditor();
        Integer folio = documentoFoliosService.getFolio(TipoDocumento.OFICIO, persona.getJuzgado(), persona.getOficialia());


        //Obtenemos la institución y seteamos información para la Data del documento
        Institucion institucion = institucionRepository.findById(institucionId)
                .orElseThrow(() -> new NotFoundException("Institución no encontrada", "institucionId: " + institucionId));
        DocumentoData docData = new DocumentoData()
                .setTipoOficio(carpetaId == null ? "Administrativo" : "Jurisdiccional");

        Optional<Carpeta> carpeta = carpetaId == null ? Optional.empty() : carpetaRepository.findById(carpetaId);
        //Creamos y guardamos el documento con la información obtenida.
        Documento doc = new Documento()
                .setCarpeta(carpeta.orElse(null))
                .setFolio(String.valueOf(folio))
                .setTipoDocumento(TipoDocumento.OFICIO)
                .setData(docData)
                .setInstitucion(institucion)
                .setEstatus(EstadoCarpeta.CREADO);
        doc = documentoRepository.save(doc);

        DocumentoDetalle documentoDetalle = new DocumentoDetalle()
                .setAsunto(asunto)
                .setFechaEmision(fechaEmision)
                .setDocumento(doc)
                .setEstado(EstadoAcuse.CREADO);
        documentoDetalleRepository.save(documentoDetalle);
        return folio;
    }

    public DocumentoRecepcionRecord getDataDocumentoRecepcion(Integer id) {
      
        Documento doc = documentoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(DOC_NOT_FOUND, DOC_ID + id));
        List<AnexoRecepcionRecord> anexosActuales = anexoRepository.findAnexosByDocumentoId(id);
        addAnexoExtra(anexosActuales, doc);
        String origen = movimientoService.getOrigen(
                (doc.getTipoDocumento() != null) ? doc.getId() : null,
                (doc.getTipoDocumento() != null) ? null : doc.getCarpeta().getId());

        return new DocumentoRecepcionRecord(
                (doc.getTipoDocumento() != null && !Objects.equals(doc.getTipoDocumento(), TipoDocumento.APELACION)) ? doc.getFolio() : doc.getCarpeta().getFolio(),
                doc.getCarpeta().getExpediente(),
                StringUtils.capitalize((doc.getTipoDocumento() != null) ? doc.getTipoDocumento().name().toLowerCase() : doc.getCarpeta().getTipoCarpeta().name().toLowerCase()),
                doc.getRuta(),
                origen,
                anexosActuales
        );
    }

    private void addAnexoExtra(List<AnexoRecepcionRecord> anexos, Documento documento) {
        if (documento.getCarpeta().getTipoJuicio().getMateria().getNombre().equals("LABORAL")) {
            Optional<AnexoRecepcionRecord> anexo = anexos.stream().filter(it -> it.nombre().equalsIgnoreCase("Constancia de no conciliación")).findFirst();
            if (anexo.isEmpty()) {
                Anexo entity = new Anexo();
                entity.setNombre("Constancia de no conciliación");
                entity.setDocumento(documento);
                entity = anexoRepository.save(entity);
                anexos.add(new AnexoRecepcionRecord(entity.getId(), entity.getEstado(), entity.getNombre()));
            }
        }
    }

    public Page<OficioResponseRecord> getAllOficios(String key, Pageable pageable) {
        key = (key != null) ? key.toLowerCase() : "";

        Page<OficioResponseRecord> page = documentoRepository.findAllByTipoDocumento(key, TipoDocumento.OFICIO, pageable);
        List<OficioResponseRecord> list = page.getContent().stream()
                .map(item -> new OficioResponseRecord(
                        item.docId(),
                        item.folio(),
                        item.dependencia(),
                        (item.asunto() != null && item.asunto().length() > 30)
                                ? item.asunto().substring(0, 30) + "..."
                                : item.asunto(),
                        item.estatus(),
                        item.fechaEmision(),
                        item.fechaEntrega(),
                        item.bandAcuse(),
                        item.bandDigitalizado(),
                        item.tamanioPapel()
                ))
                .toList();

        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    public String cancelOficio(Integer idDocumento) {
        Persona personaAuditor = personaService.getAuditor();

        Documento doc = documentoRepository.findById(idDocumento)
                .orElseThrow(() -> new NotFoundException(DOC_NOT_FOUND, DOC_ID + idDocumento));

        documentoRepository.actualizarEstatus(idDocumento, EstadoCarpeta.CANCELADO);

        Movimiento mov = movimientoService.createMovimento(
                null,
                doc,
                personaAuditor,
                null,
                EstadoCarpeta.CANCELADO.name()
        );

        return String.valueOf(mov.getId());
    }

    public MovimientoPersonalJuzgadoRecord movimientoPersonalJuzgado(PersonalJuzgadoRecord personalJuzgadoRecord) {
        Persona persona = personaService.getAuditor();
        Concepto concepto = conceptoRepository.findById(personalJuzgadoRecord.idConcepto())
                .orElseThrow(() -> new NotFoundException(CONCEPTO_NOT_FOUND, "conceptoId" + personalJuzgadoRecord.idConcepto()));
        Carpeta carpeta = carpetaRepository.findById(personalJuzgadoRecord.idDocumentoRecepcion())
                .orElseThrow(() -> new NotFoundException(CARPETA_NOT_FOUND, "carpetaId" + personalJuzgadoRecord.idDocumentoRecepcion()));

        carpeta.setConcepto(concepto);
        carpeta.setEstatus(EstadoCarpeta.ASIGNADO);
        carpeta.setPersona(persona);
        carpetaRepository.save(carpeta);

        String duration = (carpeta.getHoras() != null && carpeta.getHoras() > 0) ? carpeta.getHoras() + "h" : concepto.getDias().toString() + "d";

        Movimiento movimiento = movimientoService.createMovimentoTurnado(carpeta, null, persona, null, EstadoCarpeta.ASIGNADO.name(), concepto.getNombre(), null, duration);

        return new MovimientoPersonalJuzgadoRecord(
                personalJuzgadoRecord.idDocumentoRecepcion(),
                movimiento.getFechaAsignacion(),
                persona.getNombre(),
                movimiento.getMotivo(),
                persona.getJuzgado().getNombre());
    }

    public void sendEmailFamiliar(DocumentoSaveRecord documentoRecord, Documento documento, Carpeta carpeta, TipoJuicio tpoJuicio) {
        Map<String, Object> sendEmail = new HashMap<>();

        String tiposJuicios = documento.getData().getTiposJuicios().stream()
                .map(TipoJuicioDemandasRecord::nombre)
                .collect(Collectors.joining(", "));
        sendEmail.put("juicio", tiposJuicios);

        String salaAudiencia = audienciaRepository.getSalaNombreByCarpetaId(carpeta.getId());
        sendEmail.put("sala", salaAudiencia);

        sendEmail.put("carpetaDigital", selloGenerator.updateExpedientePorTipoJuicio(documento));
        sendEmail.put("tipoJuicio", tpoJuicio.getNombre());

        String apellidoMaternoActor = documentoRecord.actor().apellidoMaterno();
        sendEmail.put("actor", String.join(" ",
                documentoRecord.actor().nombre(),
                documentoRecord.actor().apellidoPaterno(),
                (apellidoMaternoActor != null && !apellidoMaternoActor.isEmpty() ? apellidoMaternoActor : "").trim()
        ));
        sendEmail.put("telefono", documentoRecord.actor().celular());
        sendEmail.put("correo", documentoRecord.actor().correoElectronico());

        String apellidoMaternoDemandado = documentoRecord.demandado().apellidoMaterno();
        sendEmail.put("demandado", String.join(" ",
                documentoRecord.demandado().nombre(),
                documentoRecord.demandado().apellidoPaterno(),
                (apellidoMaternoDemandado != null && !apellidoMaternoDemandado.isEmpty() ? apellidoMaternoDemandado : "").trim()
        ));

        StringBuilder anexosHtml = new StringBuilder("<ul>");

        if (documentoRecord.anexos() == null || documentoRecord.anexos().isEmpty()) {
            anexosHtml.append("<li>Sin anexo</li>");
        } else {

            for (String anexo : documentoRecord.anexos()) {
                anexosHtml.append("<li>").append(anexo).append("</li>");
            }
        }
        anexosHtml.append("</ul>");
        sendEmail.put("anexos", anexosHtml.toString());

        emailService.sendMail(
                List.of("annelkaren@gmail.com"), //TODO. reemplazar por dircifame@htsjpuebla.gob.mx
                Collections.emptyList(),
                Collections.emptyList(),
                "Recepción de Asignación de Juicio",
                "EmailDemandaFamiliar.ftl",
                sendEmail
        );
    }


    public List<MovimientoPersonalJuzgadoRecord> turnadoPersonalJuzgado(List<AsignadoTurnadoRecord> records) {
        List<MovimientoPersonalJuzgadoRecord> resultados = new ArrayList<>();

        for (AsignadoTurnadoRecord item : records) {
            Persona personalJuzgado = personaRepository.findById(item.idPersonalJuzgado().longValue())
                    .orElseThrow(() -> new NotFoundException("Personal no encontrado", "personalJuzgadoId" + item.idPersonalJuzgado()));

            Concepto concepto = conceptoRepository.findById(item.idConcepto())
                    .orElseThrow(() -> new NotFoundException(CONCEPTO_NOT_FOUND, "conceptoId" + item.idConcepto()));

            Carpeta carpeta = carpetaRepository.findById(item.idDocumentoAsignado())
                    .orElseThrow(() -> new NotFoundException(CARPETA_NOT_FOUND, "carpetaId" + item.idDocumentoAsignado()));

            carpeta.setConcepto(concepto);
            carpeta.setPrioridad(item.prioridad());
            float toDays = (float) item.horas() / 24;
            if (toDays != (float) concepto.getDias()) {
                carpeta.setHoras(item.horas());
            } else {
                carpeta.setHoras(null);
            }
            carpeta.setEstatus(EstadoCarpeta.TURNADO);

            carpetaRepository.save(carpeta);

            Persona persona = personaService.getAuditor();

            String duracion = (carpeta.getHoras() != null && carpeta.getHoras() > 0) ? carpeta.getHoras() + "h" : concepto.getDias().toString() + "d";
            Movimiento movimiento = movimientoService.createMovimentoTurnado(carpeta, null, persona, null,
                    EstadoCarpeta.TURNADO.name(), StringUtils.capitalize(concepto.getNombre().toLowerCase()), personalJuzgado, duracion);

            MovimientoPersonalJuzgadoRecord resultado = new MovimientoPersonalJuzgadoRecord(
                    carpeta.getId(),
                    movimiento.getFechaAsignacion(),
                    persona.getNombre(),
                    movimiento.getMotivo(),
                    persona.getJuzgado().getNombre()
            );
            resultados.add(resultado);
        }
        return resultados;
    }

    public IndicadoresRecord getIndicadoresAsignados() {
        int totalAsignados;
        Integer terminoRebasado = 0;
        Integer termino24horas = 0;
        Integer termino3dias = 0;

        Page<DocumentoAsignadoResponseRecord> asignados = getAllAsignado("", Pageable.unpaged());

        totalAsignados = asignados.getSize();

        for (DocumentoAsignadoResponseRecord asignado : asignados) {
            if (asignado.fechaTermino() != null) {
                if (LocalDateTime.now().isAfter(asignado.fechaTermino())) {
                    terminoRebasado++;
                } else if (asignado.fechaTermino().isAfter(LocalDateTime.now()) && asignado.fechaTermino().isBefore(LocalDateTime.now().plusDays(1))) {
                    termino24horas++;
                } else {
                    termino3dias++;
                }
            }
        }

        return new IndicadoresRecord(totalAsignados, terminoRebasado, termino24horas, termino3dias);
    }

    public void deleteAsignado(Integer id) {
        try {
            Optional<PersonaDocumento> personaDocumento = personaDocumentoRepository.findById(id);

            Persona persona = personaService.getAuditor();
            if (personaDocumento.isPresent()) {
                movimientoService.createMovimento(
                        personaDocumento.get().getCarpeta(),
                        null,
                        persona,
                        String.join(" ", "ELIMINADO DE PARTICIPANTE", personaDocumento.get().getNombre()),
                        null
                );
                Optional<PersonaDetalle> personaDetalle = personaDetalleRepository.findByPersonaDocumentoId(personaDocumento.get().getId());
                personaDetalle.ifPresent(detalle -> personaDetalleRepository.deleteById(detalle.getId()));
                personaDocumentoRepository.deleteById(id);
                personaDocumentoRepository.flush();
            } else {
                throw new EntityNotFoundException("No se encontró la persona documento con ID: " + id);
            }
        } catch (DataIntegrityViolationException ex) {
            throw new ConstraintViolationException(Messages.CONSTRAINT_ERROR, "actualizar a asignado" + id);
        }
    }

    public AmparoRecordResponse createAmparo(AmparoRecord amparoRecord) {
        Persona persona = personaService.getAuditor();
        DocumentoData data = new DocumentoData();
        Carpeta carpeta = carpetaRepository.findById(amparoRecord.carpetaId())
                .orElseThrow(() -> new NotFoundException("La Carpeta no existe", "Carpeta"));
        Integer folio = documentoFoliosService.getFolio(TipoDocumento.AMPARO, persona.getJuzgado(), null);

        data.setAmparoFechaPresentacion(amparoRecord.fechaPresentacion());
        data.setAmparoImpugnacion(amparoRecord.impugnacion());
        data.setAmparoQuejoso(amparoRecord.quejoso());
        data.setAmparoTribunalId(amparoRecord.tribunalId());
        data.setAmparoSalaId(amparoRecord.salaId());
        data.setAmparoSentido(amparoRecord.sentidoAmparo());
        data.setAmparoSentidoImpugnacion(amparoRecord.sentidoImpugnacion());
        data.setAmparoTipo(amparoRecord.tipoAmparo());
        data.setAmparoFechaTermino(amparoRecord.fechaTermino());

        Documento amparo = new Documento()
                .setCarpeta(carpeta)
                .setData(data)
                .setFolio(String.valueOf(folio))
                .setEstatus(EstadoCarpeta.ASIGNADO)
                .setFechaAsignacion(LocalDateTime.now())
                .setTipoDocumento(TipoDocumento.AMPARO)
                .setPersona(persona)
                .setConcepto(conceptoRepository.findByNombre("Distribución").orElseThrow());

        documentoRepository.save(amparo);

        Carpeta pieza = carpetaService.createPieza(carpeta.getId(), new PiezaRecord(null, amparoRecord.tipoAmparo(), Collections.singletonList(amparo.getId())));

        return new AmparoRecordResponse(pieza.getId(), amparo.getId(), pieza.getExpediente(), amparo.getFechaAsignacion());
    }

    public DocumentoRecord createDemandaAntigua(DocumentoAntiguoSaveRecord documentoRecord, MultipartFile multipartFile) {
        Persona persona = personaService.getAuditor();
        Carpeta carpeta = new Carpeta();
        Documento documento = new Documento();

        if (persona != null && persona.getJuzgado() != null && persona.getJuzgado().getTipoJuicios() != null) {
            TipoJuicio tipoJuicioTradicional = persona.getJuzgado().getTipoJuicios().stream()
                    .filter(tipoJuicio -> tipoJuicio != null && tipoJuicio.getTipoSistema() != null
                            && "Tradicional".equals(tipoJuicio.getTipoSistema().getNombre()))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Tipo Juicio 'Tradicional' no encontrado para la persona logueada", String.valueOf(persona.getId())));

            if (tipoJuicioTradicional == null) {
                throw new NotFoundException(TIPO_JUICIO_NOT_FOUND, "");
            }
            carpeta.setTipoJuicio(tipoJuicioTradicional);
        } else {
            throw new NotFoundException("No se encontraron juzgado", "");
        }

        carpeta.setJuzgado(persona.getJuzgado());
        carpeta.setFolio(getFolio("D"));
        carpeta.setTipoCarpeta(TipoCarpeta.DEMANDA);
        carpeta.setExpediente(documentoRecord.numero() + "/" + documentoRecord.anio());
        carpeta.setEstatus(EstadoCarpeta.ASIGNADO);
        carpeta.setSelloEstatus(SelloEstatus.VALIDO);
        carpeta.setFechaAsignacion(LocalDateTime.now());
        carpeta.setPersona(persona);
        Concepto concepto = conceptoRepository.findByNombre("Distribución").orElseThrow(() -> new NotFoundException(CONCEPTO_NOT_FOUND, "Distribución"));
        carpeta.setConcepto(concepto);
        carpeta = carpetaRepository.save(carpeta);

        movimientoService.createMovimento(carpeta, null, persona, null, EstadoCarpeta.ASIGNADO.name());
        documento.setCarpeta(carpeta);
        documento.setFechaAsignacion(null);
        documento.setPersona(null);
        documento = documentoRepository.save(documento);
        createPersonaDocumento(documentoRecord.actor(), carpeta);
        createPersonaDocumento(documentoRecord.demandado(), carpeta);
        addAnexos(documentoRecord.anexos(), documento);

        digitalizacionService.guardarArchivo(multipartFile, documento.getId());

        return new DocumentoRecord(documento.getId(), carpeta.getFolio(), documento.getCarpeta().getTipoCarpeta());
    }


    public ExhortoResponseRecord getExhortoById(Integer id) {
        Documento documento = documentoRepository.findById(id).orElseThrow(() -> new NotFoundException(DOC_NOT_FOUND, id.toString()));
        List<String> anexos = anexoRepository.findNombresAnexosByDocumentoId(id);
        return new ExhortoResponseRecord(anexos, documento.getData().getExhortoObservaciones(), documento.getData().getExhortoProcedencia(), documento.getCarpeta().getTipoJuicio().getNombre());
    }

    public DocPromocionInfoRecord getInfoPromocion(Integer docId) {
        Documento doc = documentoRepository.findById(docId)
                .orElseThrow(() -> new NotFoundException(DOC_NOT_FOUND, DOC_ID + docId));

        String[] expediente = doc.getCarpeta().getExpediente().split("/");

        CarpetaResponseRecord carpetaResponseRecord = carpetaService.getCarpetaResponseByNumExpYearJuzgado(
                doc.getCarpeta().getExpediente(), doc.getCarpeta().getJuzgado().getId(), 0);

        List<String> anexos = anexoRepository.findNombresAnexosByDocumentoId(docId);

        return new DocPromocionInfoRecord(
                expediente[0],
                Integer.parseInt(expediente[1]),
                doc.getCarpeta().getJuzgado().getNombre(),
                carpetaResponseRecord.actor(),
                carpetaResponseRecord.demandado(),
                doc.getData().getTipoPromocion().name(),
                anexos
        );
    }

    @Transactional
    public DocumentoPromocionResponseRecord createExhortoSalida(DocumentoExhortoSalidaRecord docExhortoSalidaRecord, MultipartFile multipartFile) {
        Carpeta carpeta = carpetaRepository.findById(docExhortoSalidaRecord.carpetaId())
                .orElseThrow(() -> new NotFoundException("La Carpeta no existe", "Carpeta"));
        Persona auditor = personaService.getAuditor();
        DocumentoData data = new DocumentoData();
        data.setTramite(docExhortoSalidaRecord.tramite())
                .setDestino(docExhortoSalidaRecord.destino())
                .setExhortoObservaciones(docExhortoSalidaRecord.observaciones())
                .setFechaEntrega(docExhortoSalidaRecord.fechaEntrega())
                .setFechaDevolucion(docExhortoSalidaRecord.fechaDevolucion());
        Documento documento = new Documento();
        documento.setData(data)
                .setCarpeta(carpeta)
                .setPersona(auditor)
                .setFolio(getFolio("ES"))
                .setTipoDocumento(TipoDocumento.EXHORTO_SALIDA);
        documento = documentoRepository.save(documento);
        if (multipartFile != null) {
            digitalizacionService.guardarArchivo(multipartFile, documento.getId());
        }
        movimientoService.createMovimento(null, documento, auditor, null, EstadoCarpeta.CREADO.name());
        return new DocumentoPromocionResponseRecord(documento.getId(), documento.getFolio(), documento.getTipoDocumento());
    }

    public DocumentoPromocionResponseRecord adjuntarPromocion(Integer documentoId) {
        Documento documento = documentoRepository.findById(documentoId).orElseThrow(() -> new NotFoundException("La promoción no existe", "documentoId"));

        if (documento.getEstatus() == EstadoCarpeta.ASIGNADO) {
            documento.setEstatus(EstadoCarpeta.INTEGRADO);
            documentoRepository.save(documento);

            return new DocumentoPromocionResponseRecord(documento.getId(), documento.getFolio(), documento.getTipoDocumento());
        }

        throw new ConflictException("No se puede integrar la promoción");
    }

    @Transactional
    public void saveSentenciaPublica(Integer idDocumento, MultipartFile multipartFile) {
        Persona auditor = personaService.getAuditor();

        Documento docSentencia = documentoRepository.findById(idDocumento)
                .orElseThrow(() -> new NotFoundException(DOC_NOT_FOUND, idDocumento.toString()));

        Documento docSentenciaPublica = new Documento();
        docSentenciaPublica
                .setCarpeta(docSentencia.getCarpeta())
                .setPersona(auditor)
                .setTipoDocumento(TipoDocumento.SENTENCIA_PUBLICA)
                .setAcuerdoRespuesta(docSentencia);
        docSentenciaPublica = documentoRepository.save(docSentenciaPublica);

        digitalizacionService.guardarArchivo(multipartFile, docSentenciaPublica.getId());
    }

    public AmparoGetRecord getAmparoById(Integer id) {
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado con id: " + id));

        Carpeta carpeta = carpetaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado con id: " + id));

        Integer carpetaId = carpeta.getId();
        DocumentoData data = documento.getData();

        return new AmparoGetRecord(
                carpetaId,
                data.getAmparoTipo(),
                data.getAmparoFechaPresentacion(),
                data.getAmparoFechaTermino(),
                data.getAmparoImpugnacion(),
                data.getAmparoSentido(),
                data.getAmparoSentidoImpugnacion(),
                data.getAmparoQuejoso(),
                data.getAmparoTribunalId(),
                data.getAmparoSalaId()
        );
    }

    public void updateAmparoData(Integer id, AmparoUpdateRecord amparoUpdateRecord) {
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento not found with id: " + id));

        DocumentoData data = documento.getData();

        data.setAmparoFechaPresentacion(amparoUpdateRecord.fechaPresentacion());
        data.setAmparoFechaTermino(amparoUpdateRecord.fechaTermino());
        data.setAmparoImpugnacion(amparoUpdateRecord.impugnacion());
        data.setAmparoSentido(amparoUpdateRecord.sentidoAmparo());
        data.setAmparoSentidoImpugnacion(amparoUpdateRecord.sentidoImpugnacion());
        data.setAmparoQuejoso(amparoUpdateRecord.quejoso());
        data.setAmparoTribunalId(amparoUpdateRecord.tribunalId());
        data.setAmparoSalaId(amparoUpdateRecord.salaId());
        data.setAmparoTipo(amparoUpdateRecord.tipoAmparo());

        documentoRepository.save(documento);
    }
}