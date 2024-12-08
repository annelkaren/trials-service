package mx.gob.pjpuebla.trials.workflow.carpeta;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.etapaprocesal.EtapaProcesal;
import mx.gob.pjpuebla.trials.core.etapaprocesal.EtapaProcesalRepository;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.procedimientos.Procedimiento;
import mx.gob.pjpuebla.trials.core.rubros.Rubro;
import mx.gob.pjpuebla.trials.core.tipopieza.TipoPieza;
import mx.gob.pjpuebla.trials.core.tipopieza.TipoPiezaRepository;
import mx.gob.pjpuebla.trials.core.rubros.RubroRecord;
import mx.gob.pjpuebla.trials.core.rubros.RubroRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.error.ConflictException;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.enums.*;
import mx.gob.pjpuebla.trials.util.enums.carpeta.*;
import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoBandejaRecepcionRecord;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.workflow.audiencias.AudienciaService;
import mx.gob.pjpuebla.trials.workflow.audiencias.record.ExtraAudienciaSelloRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.carpetadetalle.CarpetaDetalle;
import mx.gob.pjpuebla.trials.workflow.carpeta.carpetadetalle.CarpetaDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.carpetaetapas.CarpetaEtapas;
import mx.gob.pjpuebla.trials.workflow.carpeta.carpetaetapas.CarpetaEtapasRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.*;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalle;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoDetalleCarpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoDetalleCarpetaResponse;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoRecepcionMovimientosRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoService;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Transactional
@RequiredArgsConstructor
@Service
public class CarpetaService {

    private final CarpetaRepository carpetaRepository;
    private final PersonaDocumentoRepository personaDocumentoRepository;
    private final DocumentoRepository documentoRepository;
    private final AnexoRepository anexoRepository;
    private final PersonaService personaService;
    private final MovimientoService movimientoService;
    private final AudienciaService audienciaService;
    private final TipoPiezaRepository tipoPiezaRepository;
    private final CarpetaDetalleRepository carpetaDetalleRepository;
    private final CarpetaEtapasRepository carpetaEtapasRepository;
    private final TipoJuicioRepository tipoJuicioRepository;
    private final EtapaProcesalRepository etapaProcesalRepository;
    private final RubroRepository rubroRepository;
    private final DocumentoDetalleRepository documentoDetalleRepository;

    private static final String DOC_NOT_FOUND = "Documento no encontrado";
    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";


    public CarpetaResponseRecord getCarpetaResponseByNumExpYearJuzgado(String expediente, Integer juzgadoId) {
        // Usar una variable auxiliar para la modificación de juzgadoId
        final Integer finalJuzgadoId;

        // Validar juzgadoId
        if (juzgadoId == null) {
            Persona auditor = personaService.getAuditor();
            if (auditor == null || auditor.getJuzgado() == null) {
                throw new IllegalArgumentException("No se puede determinar el juzgado.");
            }
            finalJuzgadoId = auditor.getJuzgado().getId();
        } else {
            finalJuzgadoId = juzgadoId;
        }

        Carpeta carpeta = carpetaRepository.findByExpedienteAndJuzgadoId(expediente, finalJuzgadoId)
                .orElseThrow(() -> new NotFoundException("Carpeta no encontrada", expediente + " - " + finalJuzgadoId));
        String actor = getNombrePersonaByIdAndParte(carpeta.getId(), "Actor");
        String demandado = getNombrePersonaByIdAndParte(carpeta.getId(), "Demandado");
        return new CarpetaResponseRecord(carpeta.getId(), actor, demandado);
    }

    protected String getNombrePersonaByIdAndParte(Integer id, String parte) {
        List<Rol> rol = List.of(Rol.PRINCIPAL);
        PersonaDocumentoRecord persona = personaDocumentoRepository.findPersonaAndTipoParteByCarpetaId(id, parte, rol);

        if (persona == null) {
            return "";
        }

        String nombre = persona.nombre() != null ? persona.nombre() : "";
        String apellidoPaterno = persona.apellidoPaterno() != null ? persona.apellidoPaterno() : "";
        String apellidoMaterno = persona.apellidoMaterno() != null ? persona.apellidoMaterno() : "";

        return String.format("%s %s %s", nombre, apellidoPaterno, apellidoMaterno).trim();
    }

    @Transactional(readOnly = true)
    public List<ApelacionRecordResponse> getPersonasDocumentoByCarpetaId(Integer carpetaId) {
        return personaDocumentoRepository.findPersonaDocumentoByCarpetaId(carpetaId);
    }

    public BandejaRecepcionRecord getBandejaRecepcionByDocumentoId(Integer documentoId) {

        Documento documento = validacionBandejaRecepcion(documentoId);

        // Buscar la bandeja de recepción por id del documento
        BandejaRecepcionRecord bandeja = carpetaRepository.findByDocumentoId(documento.getId());
        if (bandeja == null) {
            throw new NotFoundException("No se encontró la carpeta con el documentoId: " + documentoId, "documentoId");
        }

        // Obtener y r los anexos de la bandeja de recepción
        List<AnexoBandejaRecepcionRecord> anexos = carpetaRepository.findAnexosByDocumentoId(documento.getId());
        return new BandejaRecepcionRecord(
                documento.getId(),
                bandeja.folio(),
                bandeja.expediente(),
                bandeja.tipo(),
                bandeja.rutaDigitalizacion(),
                anexos);
    }

    public DocumentoRecord actualizarInformacionAnexos(
            DocumentoRecepcionMovimientosRecord docRecepcionMovimientosRecord,
            Integer documentoId
    ) {
        Persona persona = personaService.getAuditor();
        Documento documento = validacionBandejaRecepcion(documentoId);

        List<String> anexosFaltantes = docRecepcionMovimientosRecord.anexos().stream()
                .filter(anexo -> (anexo.estado() == EstadoAnexo.NORECIBIDO || anexo.estado() == null))
                .map(AnexoBandejaRecepcionRecord::nombre)
                .toList();

        // actualizamos los anexos.
        for (AnexoBandejaRecepcionRecord anexo : docRecepcionMovimientosRecord.anexos()) {
            Anexo anexoTemp = anexoRepository.findById(anexo.id()).orElseThrow(
                    () -> new NotFoundException("No se encontró el anexo con id: " + anexo.id(), "anexoId"));
            anexoTemp.setEstado(anexo.estado());
            anexoRepository.save(anexoTemp);
        }

        // actualizamos el estatus en carpeta o documento dependiendo de si es demanda,
        // exhorto o promoción.
        if (documento.getTipoDocumento() == null) {
            documento.getCarpeta().setEstatus(EstadoCarpeta.ASIGNADO);
            documento.getCarpeta().setPersona(persona);
            carpetaRepository.save(documento.getCarpeta());
        } else {
            documento.setEstatus(EstadoCarpeta.ASIGNADO);
            documento.setPersona(persona);
        }

        documento = documentoRepository.save(documento);

        //Crear movimiento
        movimientoService.createMovimentoWithObservaciones(
                (documento.getTipoDocumento() == null) ? documento.getCarpeta() : null,
                (documento.getTipoDocumento() == null) ? null : documento,
                EstadoCarpeta.ASIGNADO.name(),
                docRecepcionMovimientosRecord.observaciones(),
                docRecepcionMovimientosRecord.recomendaciones(),
                setObservacionesAnexos(anexosFaltantes),
                documento.getConcepto().getNombre(),
                documento.getConcepto().getDias().toString()+"d"
        );

        return new DocumentoRecord(documento.getId(), documento.getCarpeta().getFolio(),
                documento.getCarpeta().getTipoCarpeta());
    }

    public Documento validacionBandejaRecepcion(Integer documentoId) {

        // Buscar y validar la existencia de la persona y el documento
        Persona persona = personaService.getAuditor();

        Documento documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new NotFoundException(
                        "No se encontró el documento asociado al documentoId: " + documentoId, "documentoId"));

        // Verificar permisos de acceso al juzgado
        Juzgado juzgado = persona.getJuzgado();
        if (!juzgado.getId().equals(documento.getCarpeta().getJuzgado().getId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "No tiene permiso para visualizar esta información");
        }

        return documento;

    }

    public String setObservacionesAnexos(List<String> anexos) {
        if (anexos.isEmpty()) {
            return null;
        }
        String concatenatedAnexos = String.join(", ", anexos);
        return "Hacen falta los siguientes anexos: " + concatenatedAnexos + ". Por favor validar.";
    }

    public List<CarpetaCatalogoRecord> getCatalogoList(String catalogo) {
        return switch (catalogo) {
            case "catalogoCondicionMigratoria" -> Arrays.stream(CatalogoCondicionMigratoria.values())
                    .map(e -> new CarpetaCatalogoRecord(e.name(), e.getEtiqueta()))
                    .toList();
            case "catalogoDeterminacionJurisdiccional" -> Arrays.stream(CatalogoDeterminacionJurisdiccional.values())
                    .map(e -> new CarpetaCatalogoRecord(e.name(), e.getEtiqueta()))
                    .toList();
            case "catalogoTiposDomicilio" -> Arrays.stream(CatalogoTiposDomicilio.values())
                    .map(e -> new CarpetaCatalogoRecord(e.name(), e.getEtiqueta()))
                    .toList();
            case "catalogoGrupoVulnerable" -> Arrays.stream(CatalogoGrupoVulnerable.values())
                    .map(e -> new CarpetaCatalogoRecord(e.name(), e.getEtiqueta()))
                    .toList();
            case "catalogoProfesionOficio" -> Arrays.stream(CatalogoProfesionOficio.values())
                    .map(e -> new CarpetaCatalogoRecord(e.name(), e.getEtiqueta()))
                    .toList();
            case "catalogoFrecuenciaIngreso" -> Arrays.stream(CatalogoFrecuenciaIngreso.values())
                    .map(e -> new CarpetaCatalogoRecord(e.name(), e.getEtiqueta()))
                    .toList();
            case "catalogoTipoDefensor" -> Arrays.stream(CatalogoTipoDefensor.values())
                    .map(e -> new CarpetaCatalogoRecord(e.name(), e.getEtiqueta()))
                    .toList();
            case "catalogoIngresoMensualNeto" -> Arrays.stream(CatalogoIngresoMensualNeto.values())
                    .map(e -> new CarpetaCatalogoRecord(e.name(), e.getEtiqueta()))
                    .toList();
            case "catalogoEstadoCivil" -> Arrays.stream(CatalogoEstadoCivil.values())
                    .map(e -> new CarpetaCatalogoRecord(e.name(), e.getEtiqueta()))
                    .toList();
            case "catalogoDiscapacidades" -> Arrays.stream(CatalogoDiscapacidades.values())
                    .map(e -> new CarpetaCatalogoRecord(e.name(), e.getEtiqueta()))
                    .toList();
            case "catalogoSentidoAmparo" -> Arrays.stream(CatalogoSentidoAmparo.values())
                    .map(e -> new CarpetaCatalogoRecord(e.name(), e.getEtiqueta()))
                    .toList();
            case "catalogoImpugnacionAmparo" -> Arrays.stream(CatalogoImpugnacionAmparo.values())
                    .map(e-> new CarpetaCatalogoRecord(e.name(), e.getEtiqueta()))
                    .toList();
            case "catalogoTipoPiezas" -> this.tipoPiezaRepository.findAll().stream()
                    .map(e-> new CarpetaCatalogoRecord(e.getClave(), e.getTipo()))
                    .toList();
            default -> Collections.emptyList();
        };
    }


    public InfoExpedienteRecord getInfoExpediente(Integer docId) {
        Documento documento = documentoRepository.findById(docId)
                .orElseThrow(() -> new NotFoundException(DOC_NOT_FOUND, docId.toString()));

        //Obtiene nombre de rubros
        List<RubroRecord> rubros = documento.getCarpeta().getRubros().stream()
                .map(rubro -> new RubroRecord(rubro.getId(), rubro.getNombre()))
                .sorted(Comparator.comparing(RubroRecord::name))
                .toList();

        //Obtiene nombre de procedimientos dados los rubros
        String tipoProcedimiento = documento.getCarpeta().getRubros().stream()
                .map(Rubro::getProcedimiento)
                .filter(Objects::nonNull)
                .map(Procedimiento::getNombre)
                .distinct()
                .sorted()
                .collect(Collectors.joining(", "));

        //Obtiene los participantes del expediente
        List<PersonaDataRecord> apelacionRecordResponseList = personaDocumentoRepository.findPersonaDocumentoDataByCarpetaId(documento.getCarpeta().getId());

        //Obtiene el nombre del juez
        ExtraAudienciaSelloRecord extraAudienciaSelloRecord = audienciaService.getAudienciaAndSalaAndDomicilio(documento);

        DateTimeFormatter pattern = DateTimeFormatter.ofPattern(DATE_FORMAT);

        EtapaProcesalRecord etapaProcesalRecord = null;

        Optional<CarpetaEtapas> optionalCarpetaEtapas = carpetaEtapasRepository.findByCarpetaId(documento.getCarpeta().getId());
        if (optionalCarpetaEtapas.isPresent()) {
            CarpetaEtapas carpetaEtapas = optionalCarpetaEtapas.get();
            etapaProcesalRecord = new EtapaProcesalRecord(carpetaEtapas.getEtapaProcesal().getId(), carpetaEtapas.getEtapaProcesal().getNombre());
        }

        return new InfoExpedienteRecord(
                documento.getCarpeta().getExpediente(),
                documento.getCarpeta().getTipoJuicio().getNombre(), //TODO mapear de forma correcta expediente tipo PENAL
                documento.getCarpeta().getTipoJuicio().getId(), //TODO mapear de forma correcta expediente tipo PENAL
                extraAudienciaSelloRecord.nombreJuez(),
                LocalDateTime.now().format(pattern), //TODO añadir fecha presentación
                "Asunto de penal desde Backend", //TODO añadir asunto para expediente tipo PENAL
                tipoProcedimiento,
                rubros,
                etapaProcesalRecord,
                getParticipantes(apelacionRecordResponseList),
                null, //TODO añadir razón de devolución
                documento.getCarpeta().getJuzgado().getMateria().getNombre(),
                documento.getCarpeta().getJuzgado().getMateria().getId(),
                documento.getCarpeta().getTipoJuicio().getTipoSistema()!=null ? documento.getCarpeta().getTipoJuicio().getTipoSistema().getNombre() : null,
                documento.getCarpeta().getJuzgado().getNombre(),
                (documento.getCarpeta().getTipoPieza() != null) ? documento.getCarpeta().getTipoPieza().getTipo() : null
        );
    }

    public static List<ParticipantesRecord> getParticipantes(List<PersonaDataRecord> participantes) {
        Map<String, List<ParticipanteDataRecord>> agrupadoPorTipo = new HashMap<>();
        for (PersonaDataRecord participante : participantes) {
            String nombreCompleto = Stream.of(participante.nombre(), participante.apellidoPaterno(), participante.apellidoMaterno())
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(" "));
            if (!nombreCompleto.isEmpty()) {
                ParticipanteDataRecord persona = new ParticipanteDataRecord(participante.id(), nombreCompleto, participante.rol());
                agrupadoPorTipo.computeIfAbsent(participante.tipoPartesNombre(), k -> new ArrayList<>()).add(persona);
            }
        }
        return agrupadoPorTipo.entrySet().stream()
                .map(entry -> new ParticipantesRecord(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public Carpeta createPieza(Integer carpetaId, PiezaRecord piezaRecord){
        Carpeta carpetaPadre = carpetaRepository.findById(carpetaId).orElseThrow(() -> new NotFoundException("La Carpeta no existe", "carpetaId"));
        TipoPieza tipoPieza = tipoPiezaRepository.findByIdOrClave(piezaRecord.tipoPiezaId(), piezaRecord.clavePieza()).stream().findFirst()
                .orElseThrow(() -> new NotFoundException("El Tipo de Pieza no existe", "tipoPieza"));

        Persona persona = personaService.getAuditor();

        if (piezaRecord.documentos().isEmpty()){
                throw new NotFoundException("No se puede crear una pieza vacía", "documentos");
        }

        Carpeta pieza = new Carpeta();

        String numeroPieza = carpetaPadre.getExpediente()+"/"+ consecutivoPieza(carpetaId, tipoPieza.getClave());

        pieza.setFolio(documentoRepository.getNextValPieza().toString());
        pieza.setExpediente(numeroPieza);
        pieza.setCarpetaPadre(carpetaPadre);
        pieza.setFechaAsignacion(LocalDateTime.now());
        pieza.setTipoCarpeta(TipoCarpeta.PIEZA);
        pieza.setPersona(persona);
        pieza.setSelloEstatus(SelloEstatus.VALIDO);
        pieza.setEstatus(EstadoCarpeta.ASIGNADO);
        pieza.setJuzgado(carpetaPadre.getJuzgado());
        pieza.setTipoJuicio(carpetaPadre.getTipoJuicio());
        pieza.setTipoPieza(tipoPieza);
        pieza.setAudit(new Audit());

        pieza = carpetaRepository.save(pieza);

        asignarPieza(pieza, piezaRecord.documentos());
        movimientoService.createMovimento(pieza, null, persona, "", EstadoCarpeta.ASIGNADO.name());
        return pieza;
    }

    public String consecutivoPieza(Integer carpetaId, String clavePieza){
        if (!carpetaRepository.existsById(carpetaId))
            throw new NotFoundException("La Carpeta con Id " + carpetaId +" no existe","carpetaId");
        if (!tipoPiezaRepository.existsByClave(clavePieza))
            throw new NotFoundException("El tipo de pieza " + clavePieza + " no existe","clavePieza");

        return clavePieza + StringUtils.leftPad(carpetaRepository.getNumeroPieza(carpetaId, clavePieza).toString(),2,'0');
    }

    public void asignarPieza(Carpeta pieza, List<Integer> documentos) {
        Persona persona = personaService.getAuditor();

        for (Integer documentoId : documentos) {
            Documento documento = documentoRepository.findById(documentoId).orElseThrow();

            if (documento.getData()==null){
                documento.setData(new DocumentoData());
            }
            
            documento.setCarpeta(pieza);
            documento.setData(documento.getData().
                    setPieza(pieza.getExpediente()).
                    setEstadoPieza(EstadoCarpeta.ASIGNADO));
            documentoRepository.save(documento);
            movimientoService.createMovimento(null, documento, persona, "Asignar a Pieza", EstadoCarpeta.ASIGNADO.name());
        }

        Documento documento = documentoRepository.findById(documentos.stream().findFirst().orElseThrow()).orElseThrow();

        documento.setEstatus(EstadoCarpeta.ASIGNADO);
        documentoRepository.save(documento);
    }

    public InfoExpedienteDetalleRecord getInfoExpedienteDetalle(Integer docId) {
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern(DATE_FORMAT);

        Documento documento = documentoRepository.findById(docId)
                .orElseThrow(() -> new NotFoundException(DOC_NOT_FOUND, docId.toString()));

        CarpetaDetalle carpetaDetalle = carpetaDetalleRepository.findByCarpetaId(documento.getCarpeta().getId());

        String nombre = documento.getCarpeta().getPersona().getNombre() + " " +
                (documento.getCarpeta().getPersona().getApellidoPaterno()!=null ? documento.getCarpeta().getPersona().getApellidoPaterno() + " " : "")  +
                (documento.getCarpeta().getPersona().getApellidoMaterno()!=null ? documento.getCarpeta().getPersona().getApellidoMaterno() : "") + ", ";

        String rol = (documento.getCarpeta().getPersona().getOcupacion()!=null ? documento.getCarpeta().getPersona().getOcupacion() + ", " : "");

        String ubicacion = documento.getCarpeta().getPersona().getJuzgado() != null ?
                documento.getCarpeta().getPersona().getJuzgado().getNombre() : documento.getCarpeta().getPersona().getOficialia().getNombre();

        return new InfoExpedienteDetalleRecord(
                documento.getCarpeta().getDeterminacionJurisdiccional()!=null ? documento.getCarpeta().getDeterminacionJurisdiccional().name() : null,
                carpetaDetalle.getFechaAdmision()!=null ? carpetaDetalle.getFechaAdmision().format(pattern) : null,
                carpetaDetalle.getFechaDesechado()!=null ? carpetaDetalle.getFechaDesechado().format(pattern) : null,
                nombre + rol + ubicacion,
                carpetaDetalle.getAsunto(),
                null,
                carpetaDetalle.getObservaciones(),
                documento.getCarpeta().getSentencia()!=null ? documento.getCarpeta().getSentencia().name() : null,
                carpetaDetalle.getPromovente(),
                carpetaDetalle.getNumeroCarpetaInvestigacion(),
                carpetaDetalle.getNumeroOficio(),
                carpetaDetalle.getLugarHecho(),
                carpetaDetalle.getFechaHecho(),
                carpetaDetalle.getCantidadPrincipal(),
                carpetaDetalle.getMoneda(),
                carpetaDetalle.getNumeroHijos(),
                carpetaDetalle.getNumeroHijosMenoresEdad(),
                carpetaDetalle.getActaMatrimonio(),
                carpetaDetalle.getLugarRegistroMatrimonio(),
                carpetaDetalle.getEntidad(),
                carpetaDetalle.getMunicipio(),
                carpetaDetalle.getLocalidad(),
                carpetaDetalle.getFechaRegistro()!=null ? carpetaDetalle.getFechaRegistro().format(pattern) : null,
                carpetaDetalle.getHoraFormal(),
                carpetaDetalle.getHoraMaterial(),
                carpetaDetalle.getLugarDisposicion(),
                carpetaDetalle.getPresentacionImputado()!=null ? carpetaDetalle.getPresentacionImputado().name() : null,
                carpetaDetalle.getSolicitudAudiencia()!=null ? carpetaDetalle.getSolicitudAudiencia().name() : null,
                carpetaDetalle.getFechaPresentacionImputado()!=null ? carpetaDetalle.getFechaPresentacionImputado().format(pattern) : null,
                carpetaDetalle.getTipoJuicio()!=null ? carpetaDetalle.getTipoJuicio().getId() : null,
                carpetaDetalle.getTipoJuicio()!=null ? carpetaDetalle.getTipoJuicio().getNombre() : null
        );
    }

    public void saveExpedienteDetalle(
            SaveExpedienteDetalleRecord detalle,
            Integer docId
    ) {
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern(DATE_FORMAT);

        Documento documento = documentoRepository.findById(docId)
                .orElseThrow(() -> new NotFoundException(DOC_NOT_FOUND, docId.toString()));
        CarpetaDetalle carpetaDetalle = carpetaDetalleRepository.findByCarpetaId(documento.getCarpeta().getId());

        //TODO editar fase para PENAL
        //TODO editar juez en caso penal

        documento.getCarpeta().setDeterminacionJurisdiccional(detalle.determinacion()!=null ? detalle.determinacion() : CatalogoDeterminacionJurisdiccional.PRESENTACION);

        //edita tipo juicio
        TipoJuicio tipoJuicioHijo = tipoJuicioRepository.findById(detalle.tipoJuicioHijoId())
                .orElseThrow(() -> new NotFoundException("TipoJuicio no encontrado", detalle.tipoJuicioHijoId().toString()));

        //edita rubros
        Set<Rubro> rubros = detalle.rubros().stream()
                .map(rubroRecord -> rubroRepository.findById(rubroRecord.id())
                        .orElseThrow(() -> new IllegalArgumentException("Rubro no encontrado con id: " + rubroRecord.id())))
                .collect(Collectors.toSet());

        documento.getCarpeta().setRubros(rubros);

        //edita etapa procesal
        EtapaProcesal etapaProcesalDetalle = etapaProcesalRepository.findById(detalle.etapaProcesal().id())
                .orElseThrow(() -> new NotFoundException("Etapa Procesal no encontrada", detalle.etapaProcesal().id().toString()));
        Optional<CarpetaEtapas> optionalCarpetaEtapas = carpetaEtapasRepository.findByCarpetaId(documento.getCarpeta().getId());
        if (optionalCarpetaEtapas.isPresent()) {
            CarpetaEtapas carpetaEtapas = optionalCarpetaEtapas.get();
            //Si el registro Etapa Procesal mas reciente no coincide con el obtenido de detalle registra la nueva etapa procesal
            if (!carpetaEtapas.getEtapaProcesal().getId().equals(etapaProcesalDetalle.getId())) {
                carpetaEtapasRepository.save( new CarpetaEtapas()
                        .setCarpeta(documento.getCarpeta())
                        .setFechaRegistro(LocalDateTime.now())
                        .setEtapaProcesal(etapaProcesalDetalle)
                );
            }
        }else{
            carpetaEtapasRepository.save( new CarpetaEtapas()
                    .setCarpeta(documento.getCarpeta())
                    .setFechaRegistro(LocalDateTime.now())
                    .setEtapaProcesal(etapaProcesalDetalle)
            );
        }

        //TODO falta actualizar domicilios para Familiar Oralidad

        carpetaDetalle
                .setTipoJuicio(tipoJuicioHijo)
                .setAsunto(detalle.asunto())
                .setObservaciones(detalle.observaciones())
                .setPromovente(detalle.promovente())
                .setNumeroCarpetaInvestigacion(detalle.numeroCarpetaInvestigacion())
                .setNumeroCarpetaInvestigacion(detalle.numeroCarpetaInvestigacion())
                .setNumeroOficio(detalle.numeroOficio())
                .setLugarHecho(detalle.lugarHecho())
                .setFechaHecho(detalle.fechaHecho())
                .setCantidadPrincipal(detalle.cantidadPrincipal())
                .setMoneda(detalle.moneda())
                .setNumeroHijos(detalle.numeroHijos())
                .setNumeroHijosMenoresEdad(detalle.numeroHijosMenoresEdad())
                .setActaMatrimonio(detalle.actaMatrimonio())
                .setLugarRegistroMatrimonio(detalle.lugarRegistroMatrimonio())
                .setEntidad(detalle.entidad())
                .setMunicipio(detalle.municipio())
                .setLocalidad(detalle.localidad())
                .setFechaRegistro(detalle.fechaRegistro()!=null ? (LocalDateTime.parse(detalle.fechaRegistro(), pattern)) : null)
                .setHoraFormal(detalle.horaFormal())
                .setHoraMaterial(detalle.horaMaterial())
                .setLugarDisposicion(detalle.lugarDisposicion())
                .setPresentacionImputado(detalle.presentacionImputado())
                .setSolicitudAudiencia(detalle.solicitudAudiencia())
                .setFechaPresentacionImputado(detalle.fechaPresentacionImputado()!=null ? (LocalDateTime.parse(detalle.fechaPresentacionImputado(), pattern)):null);

        carpetaDetalleRepository.save(carpetaDetalle);
        documentoRepository.save(documento);
    }

    public List<PiezaRecordResponse> getPiezas(Integer documentoId){
        return this.carpetaRepository.findPiezasByDocumentoId(documentoId);
    }

    public PiezaRecordResponse adjuntarPiezaDocumentos(Integer piezaId, PiezaRecord piezaRecord){
        Carpeta pieza = carpetaRepository.findById(piezaId).orElseThrow(()-> new NotFoundException("La pieza no existe","piezaId"));

        asignarPieza(pieza, piezaRecord.documentos());

        return new PiezaRecordResponse(pieza.getId(), pieza.getExpediente(), pieza.getTipoPieza().getTipo(), pieza.getEstatus());
    }

    public List<DocumentoDetalleCarpetaResponse> getAllPiezasCarpeta(String key, Integer carpetaId){
        List<DocumentoDetalleCarpeta> list = carpetaRepository.findPiezasByCarpetaPadreId(key, carpetaId);
        Persona persona = personaService.getAuditor();

        return list.stream()
                .map(
                        e -> new DocumentoDetalleCarpetaResponse(
                                e.id(),
                                "PIEZA DE "+e.tipoPieza().getTipo(),
                                e.folio(),
                                e.fechaRegistro(),
                                e.ruta(),
                                personaService.findById(e.personaOrigenId()).permisos().get(0).name(),
                                e.tipoCarpeta().name(),
                                Objects.equals(e.personaOrigenId(), persona.getId()),
                                e.estadoCarpeta().name(),
                                ""
                        )).toList();
    }

    public List<DocumentoDetalleCarpetaResponse> getAllDocumentosCarpeta(String key, Integer carpetaId){
        List<DocumentoDetalleCarpeta> list = documentoRepository.findDocumentosByCarpeta(carpetaId);

        return list.stream()
                .map(
                        e -> new DocumentoDetalleCarpetaResponse(
                                e.id(),
                                e.tipoDocumento()!=null?e.tipoDocumento().getEtiqueta():"DEMANDA",
                                e.folio(),
                                e.fechaRegistro(),
                                e.ruta(),
                                (e.personaOrigenId()!=null)?personaService.findById(e.personaOrigenId()).permisos().get(0).name():"",
                                e.tipoCarpeta().name(),
                                Boolean.FALSE,
                                e.estadoCarpeta()!=null?e.estadoCarpeta().name() : "",
                                ""
                        )).filter(d->key==null||d.tipo().toUpperCase().contains(key)).toList();
    }

    public Page<DocumentoDetalleCarpetaResponse> getAllDocumentosPiezas(String key, Integer carpetaId, Pageable pageable){
        List<DocumentoDetalleCarpetaResponse> documentos = this.getAllDocumentosCarpeta(key, carpetaId);
        List<DocumentoDetalleCarpetaResponse> piezas = this.getAllPiezasCarpeta(key, carpetaId);

        List<DocumentoDetalleCarpetaResponse> lista = Stream.concat(documentos.stream(), piezas.stream()).toList();

        return new PageImpl<>(lista, pageable, lista.size());
    }

    public PiezaRecordResponse acoplarPieza(Integer piezaId, String estadoPiezaReq){
        Carpeta pieza = carpetaRepository.findById(piezaId).orElseThrow(()->new NotFoundException("La pieza no existe","piezaId"));
        EstadoCarpeta estadoPieza = EstadoCarpeta.valueOf(estadoPiezaReq);
        Persona persona = personaService.getAuditor();

        if (pieza.getTipoCarpeta()!=TipoCarpeta.PIEZA){
            throw new ConflictException("No es una pieza");
        }

        if (pieza.getEstatus()==EstadoCarpeta.CANCELADO || pieza.getEstatus()==EstadoCarpeta.INTEGRADO){
            throw new ConflictException("No se puede actualizar el estado de la Pieza");
        }

        // TODO agregar validación para cancelar o integrar la Pieza

        List<Documento> documentos = documentoRepository.findByCarpetaId(piezaId);

        for(Documento doc: documentos){
            doc.setCarpeta(pieza.getCarpetaPadre());
            doc.setData(doc.getData().setEstadoPieza(estadoPieza));

            documentoRepository.save(doc);
        }

        pieza.setEstatus(estadoPieza);
        movimientoService.createMovimento(pieza, null, persona,null, estadoPieza.name());

        pieza = carpetaRepository.save(pieza);

        return new PiezaRecordResponse(pieza.getId(), pieza.getExpediente(), pieza.getTipoPieza().getTipo(), pieza.getEstatus());
    }

    public Page<LibroGobiernoRecord> libroDeGobierno(String key, Pageable pageable) {
        key = (key != null) ? key.toLowerCase() : "";
        Persona persona = personaService.getAuditor();
        Juzgado juzgado = persona.getJuzgado();

        Page<Carpeta> carpetas = carpetaRepository.findByJuzgado(juzgado, key, pageable);

        return carpetas.map(carpeta -> {
            String actor = getNombrePersonaByIdAndParte(carpeta.getId(), "Actor");
            String demandado = getNombrePersonaByIdAndParte(carpeta.getId(), "Demandado");

            return new LibroGobiernoRecord(
                    carpeta.getId(),
                    carpeta.getExpediente(),
                    (carpeta.getAudit() != null && carpeta.getAudit().getFechaAlta() != null) ? carpeta.getAudit().getFechaAlta() : null,
                    carpeta.getTipoJuicio() != null ? carpeta.getTipoJuicio().getNombre() : "Sin Tipo de Juicio",
                    actor,
                    demandado
            );
        });
    }

    public SentenciaPublicaResponseRecord getCarpetaByExpedienteAndSentencia(String expediente) {
        Persona auditor = personaService.getAuditor();
        if (auditor == null || auditor.getJuzgado() == null) {
            throw new IllegalArgumentException("No se puede determinar el juzgado.");
        }

        Documento documento = documentoRepository.findByExpedienteAndTipoDocumento(expediente, TipoDocumento.SENTENCIA, auditor.getJuzgado().getId())
                .orElseThrow(() -> new NotFoundException("Carpeta no encontrada o le falta sentencia", expediente));

        DocumentoDetalle documentoDetalle = documentoDetalleRepository.findByDocumentoId(documento.getId())
                .orElseThrow(() -> new NotFoundException("Detalle documento no encontrado", documento.getId().toString()));

        String actor = getNombrePersonaByIdAndParte(documento.getCarpeta().getId(), "Actor");
        String demandado = getNombrePersonaByIdAndParte(documento.getCarpeta().getId(), "Demandado");
        return new SentenciaPublicaResponseRecord(
                documento.getCarpeta().getId(),
                documento.getId(),
                actor,
                demandado,
                documento.getCarpeta().getJuzgado().getMateria().getNombre(),
                documento.getCarpeta().getJuzgado().getNombre(),
                documentoDetalle.getTipoSentencia().name(),
                documentoDetalle.getTipoResolucion().name(),
                documentoDetalle.getFechaResolucion()
        );
    }
}
