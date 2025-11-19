package mx.gob.pjpuebla.trials.litigante;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.migracion.readers.acuerdos.AcuerdosMigracionRepository;
import mx.gob.pjpuebla.migracion.readers.detalle.DetallesMigracion;
import mx.gob.pjpuebla.migracion.readers.detalle.DetallesMigracionRepository;
import mx.gob.pjpuebla.migracion.readers.entradas.EntradasMigracionRepository;
import mx.gob.pjpuebla.migracion.readers.usuario.UsuarioMigracionRepository;
import mx.gob.pjpuebla.migracion.utils.UtilsMigracion;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRepository;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRecord;
import mx.gob.pjpuebla.trials.litigante.responselitigante.*;
import mx.gob.pjpuebla.trials.litigante.responsepromociones.PromocionesLitiganteRecord;
import mx.gob.pjpuebla.trials.util.enums.*;
import mx.gob.pjpuebla.trials.workflow.asistenciaaudiencia.AsistenciaAudienciaRepository;
import mx.gob.pjpuebla.trials.workflow.audiencias.record.AudienciasExpedienteRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenido;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenidoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalle;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoPromocionRecord;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoService;
import mx.gob.pjpuebla.trials.workflow.notificaciondetalle.NotificacionesDetalles;
import mx.gob.pjpuebla.trials.workflow.notificaciondetalle.NotificacionesDetallesRepository;
import mx.gob.pjpuebla.trials.workflow.notificaciones.NotificacionRepository;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class LitiganteService {

    private final AuditorAware<Jwt> auditorAware;
    private final PersonaDocumentoRepository personaDocumentoRepository;
    private final NotificacionesDetallesRepository notificacionesDetallesRepository;
    private final NotificacionRepository notificacionRepository;
    private final AsistenciaAudienciaRepository asistenciaAudienciaRepository;
    private final DocumentoRepository documentoRepository;
    private final DocumentoContenidoRepository documentoContenidoRepository;
    private final DocumentoDetalleRepository documentoDetalleRepository;
    private final CarpetaRepository carpetaRepository;
    private final MovimientoService movimientoService;
    private final MateriaRepository materiaRepository;
    private final UsuarioMigracionRepository usuarioMigracionRepository;
    private final EntradasMigracionRepository entradasMigracionRepository;
    private final AcuerdosMigracionRepository acuerdosMigracionRepository;
    private final DetallesMigracionRepository detallesMigracionRepository;

    private final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter formatoTiempo = DateTimeFormatter.ofPattern("HH:mm:ss");

    public Page<LitiganteExpedientesRecord> getExpedientesRelacionados(String key, Pageable pageable) {
        String username = getLitiganteUsername();
        Page<LitiganteExpedientesRecord> page = personaDocumentoRepository.findByUsername(username, key, pageable);
        List<LitiganteExpedientesRecord> list = page.stream()
                .map(pd -> pd.additionalData(
                        String.join(", ",
                                personaDocumentoRepository.findTipoPartePrincipalByCarpetaId(pd.id(), "Actor")),
                        String.join(", ",
                                personaDocumentoRepository.findTipoPartePrincipalByCarpetaId(pd.id(), "Demandado")),
                        notificacionesDetallesRepository.countNotificacionesPorLeer(pd.id(), username)))
                .collect(Collectors.toList());

        // Busca expedientes relacionados en SECJ PHP:
        List<LitiganteExpedientesRecord> listSecjPhp = entradasMigracionRepository
                .findExpedientesRelacionadosLegacy(username).stream()
                .map(p -> new LitiganteExpedientesRecord(
                        p.getId(), p.getNumeroExpediente(), p.getMateria(), p.getTipoJuicio(),
                        p.getActorPrincipal(), p.getDemandadoPrincipal(), p.getJuzgado(),
                        p.getNotificacionesPendientes(), p.getSede(), p.getCu()))
                .toList();
        list.addAll(listSecjPhp);

        // FIN busqueda y adicion a la lista

        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    public List<LitiganteExpedientesRecord> getAllExpedientesRelacionados() {
        String username = getLitiganteUsername();
        return personaDocumentoRepository.findAllByUsername(username);
    }

    private String getLitiganteUsername() {
        Jwt jwt = auditorAware.getCurrentAuditor().orElseThrow();
        return jwt.getClaims().get("preferred_username").toString();
    }

    public Page<AcuerdoSentenciaRecord> getAcuerdosSentencias(Pageable pageable) {
        // Obtiene correo de persona litigante
        String userName = getLitiganteUsername();

        // Obtiene Notificaciones desde Notificaciones Detalles
        Page<NotificacionesDetalles> page = notificacionesDetallesRepository.getAllByUsername(
                userName,
                TipoNotificacion.CORREO_ELECTRONICO, pageable);

        List<AcuerdoSentenciaRecord> list = page.getContent().stream()
                .map(notification -> new AcuerdoSentenciaRecord(
                        notification.getId(),
                        notification.getNotificacion().getDocumento().getCarpeta().getExpediente(),
                        notification.getNotificacion().getFechaNotificado(),
                        notification.getNotificacion().getDocumento().getCarpeta().getJuzgado().getNombre(),
                        notification.getNotificacion().getDocumento().getId(),
                        StringUtils.capitalize(notification.getNotificacion().getEstadoNotificacion().name()
                                .replace("_", " ").toLowerCase()),
                        notification.getNotificacion().getDocumento().getMigrado()))
                .collect(Collectors.toList());

        // Se incorpora notificaciones de expedientes del sistema SECJ PHP:
        List<AcuerdoSentenciaRecord> listNotiSecjPhp = usuarioMigracionRepository.notificacionesLitigante(userName);
        list.addAll(Optional.ofNullable(listNotiSecjPhp).orElse(Collections.emptyList()));
        // FIn incorporación.

        for (NotificacionesDetalles item : page.getContent()) {
            if (item.getNotificacion().getEstadoNotificacion().equals(EstadoNotificacion.POR_LEER)) {
                item.setFechaCompletado(LocalDateTime.now());
                item.getNotificacion().setEstadoNotificacion(EstadoNotificacion.COMPLETADO);
                notificacionesDetallesRepository.save(item);
                notificacionRepository.save(item.getNotificacion());
            }
        }
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    public Page<LitiganteExpedienteListAudienciasRecord> getExpedientesAudienciasRelacionados(Pageable pageable) {
        String username = getLitiganteUsername();
        List<LitiganteExpedienteAudienciaRecord> list = asistenciaAudienciaRepository.getAllAudicenciasByUser(username,
                pageable);

        Map<String, LitiganteExpedienteListAudienciasRecord> groupedAudiencias = new HashMap<>();

        for (LitiganteExpedienteAudienciaRecord listAudienciaRecord : list) {
            String key = listAudienciaRecord.id() + "-" + listAudienciaRecord.numeroExpediente();
            AudienciasExpedienteRecord audienciaRecord = crearAudienciaRecord(listAudienciaRecord);

            if (groupedAudiencias.containsKey(key)) {
                groupedAudiencias.get(key).audiencias().add(audienciaRecord);
            } else {
                groupedAudiencias.put(key, new LitiganteExpedienteListAudienciasRecord(
                        listAudienciaRecord.id(),
                        listAudienciaRecord.numeroExpediente(),
                        listAudienciaRecord.materia(),
                        listAudienciaRecord.tipoJuicio(),
                        listAudienciaRecord.juzgado(),
                        new ArrayList<>(List.of(audienciaRecord))));
            }
        }

        List<LitiganteExpedienteListAudienciasRecord> result = new ArrayList<>(groupedAudiencias.values());
        return new PageImpl<>(result, pageable, result.size());
    }

    /**
     * Extrae la lógica de creación de un AudienciasExpedienteRecord en un método
     * separado para corregir Scan de Qodana.
     */
    private static AudienciasExpedienteRecord crearAudienciaRecord(LitiganteExpedienteAudienciaRecord record) {
        String[] fechaHoraInicio = separarFechaYHora(record.fechaInicio().toString());
        String[] fechaHoraFin = separarFechaYHora(record.fechaFin().toString());

        return new AudienciasExpedienteRecord(
                record.numeroAudiencia(),
                fechaHoraInicio[0], fechaHoraInicio[1],
                fechaHoraFin[0], fechaHoraFin[1]);
    }

    private static String[] separarFechaYHora(String fechaHora) {
        String[] partes = fechaHora.split("T");
        return partes.length == 2 ? partes : new String[] { "", "" };
    }

    public Page<DocumentoResponseRecord> getExpedienteDetails(Integer carpetaId, Pageable pageable) {
        Page<Documento> documentos = documentoRepository.findByCarpetaIdAndTipoDocumentoIn(
                carpetaId,
                Arrays.asList(TipoDocumento.ACUERDO, TipoDocumento.SENTENCIA), pageable);
        List<DocumentoResponseRecord> list = new ArrayList<>();
        for (Documento doc : documentos.getContent()) {
            DocumentoDetalle detalle = documentoDetalleRepository.findByDocumentoId(doc.getId()).get();
            list.add(new DocumentoResponseRecord(
                    String.valueOf(doc.getId()),
                    detalle.getFechaResolucion(),
                    doc.getData().getRubros().toString().replace("[", "").replace("]", ""),
                    "/api/litigante/documento/" + doc.getId()));
        }

        return new PageImpl<>(list, pageable, documentos.getTotalElements());
    }

    public Page<DocumentoResponseRecord> getExpedienteDetailsLegacy(String cu, Pageable pageable) {
        // Obtiene correo de persona litigante
        String userName = getLitiganteUsername();
        return acuerdosMigracionRepository.findDetailsExpedienteLitigante(userName, cu, pageable);
    }

    public Page<PromocionesLitiganteRecord> getPromocionesLitigante(String key, Pageable pageable) {
        String userName = getLitiganteUsername();
        List<Documento> docPromociones = documentoRepository.findPromocionesLitigante(userName, key);

        // Obtiene promociones de litigantes del sistema SECGJ PHP:
        List<PromocionesLitiganteRecord> promocionesLegacy = detallesMigracionRepository
                .findPromocionesElectronicasLitiganteLegacy(userName)
                .stream()
                .map(p -> new PromocionesLitiganteRecord(
                        p.getId(),
                        p.getNumeroExpediente(),
                        p.getNumeroPromocionE(),
                        p.getUsuarioOrigen(),
                        p.getNombreArchivo(),
                        p.getFechaSubida(),
                        UtilsMigracion.convertirHora(p.getHoraSubida()),
                        p.getRutaArchivo(),
                        p.getJuzgado()))
                .toList();

        // FIN OBTENCION DE DATOS:

        // Mapeamos docPromociones a objeto List<PromocionesLitiganteRecord>
        List<PromocionesLitiganteRecord> promocionesLitigante = docPromociones.stream().map(documento -> {
            String[] partesExpediente = documento.getCarpeta().getExpediente().split("/");
            String numeroExpediente = partesExpediente[0];
            String anioExpediente = partesExpediente.length > 1 ? partesExpediente[1] : "";

            return new PromocionesLitiganteRecord(
                    documento.getId(),
                    documento.getCarpeta().getExpediente(),
                    documento.getFolio(),
                    documento.getPersona().getCorreoElectronico(),
                    documento.getRuta(),
                    documento.getAudit().getFechaAlta().toLocalDate(),
                    documento.getAudit().getFechaAlta().toLocalTime(),
                    "/opt/pjp/files/" + anioExpediente + "/" + documento.getCarpeta().getJuzgado().getNombre() + "/"
                            + numeroExpediente + "/" + documento.getRuta(),
                    documento.getCarpeta().getJuzgado().getNombre());
        }).toList();

        // combinamos tanto promociones del litigante legacy (PHP) como las del sistema java:
        List<PromocionesLitiganteRecord> combinados = new ArrayList<>();
        combinados.addAll(promocionesLegacy);
        combinados.addAll(promocionesLitigante);

        // TODO revisar paginador

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), combinados.size());

        List<PromocionesLitiganteRecord> pageContent = start >= combinados.size() ? List.of()
                : combinados.subList(start, end);

        return new PageImpl<>(pageContent, pageable, combinados.size());
    }

    public DocumentoPromocionRecord getPromocionById(Integer promocionId) {
        Documento promocion = documentoRepository.findById(promocionId)
                .orElseThrow(() -> new NotFoundException("Promoción no encontrada", promocionId.toString()));
        DocumentoContenido contenido = documentoContenidoRepository.findByDocumentoId(promocionId)
                .orElseThrow(() -> new NotFoundException("Documento no encontrada", promocionId.toString()));
        return new DocumentoPromocionRecord(promocion.getCarpeta().getId(), TipoPromocion.CORREO_ELECTRONICO, null,
                contenido.getTexto());
    }

    public Page<LibroGobiernoRecord> getConsultaLibroGobierno(
            String nombre, String aPaterno, String aMaterno, Pageable pageable) {
        return personaDocumentoRepository.findByNombreCompleto(nombre.trim(), aPaterno.trim(), aMaterno.trim(),
                pageable);
    }

    public Page<SentenciasPublicasRecord> getSentenciasPublicas(
            Integer materiaId, Pageable pageable) {
        Page<SentenciasPublicasRecord> page = documentoDetalleRepository.findSentenciasByMateriaId(materiaId, pageable);
        List<SentenciasPublicasRecord> list = page.getContent().stream()
                .map(m -> m.format())
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    public List<LitiganteExpedientesRecord> getExpedientes(
            Integer materiaId, String expediente, String anio, Integer distritoId) {
        expediente = getExpedienteNumber(expediente);
        expediente = verifyMateria(expediente, materiaId);
        expediente = expediente.concat("/").concat(anio).toUpperCase();
        return carpetaRepository.getExpedientesByMateria(materiaId, expediente, distritoId);
    }

    private String getExpedienteNumber(String expediente) {
        try {
            Integer number = Integer.parseInt(expediente);
            return String.format("%06d", number);
        } catch (NullPointerException ex) {
            return expediente;
        }
    }

    private String verifyMateria(String expediente, Integer materiaId) {
        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new NotFoundException("Materia no encontrado", materiaId.toString()));
        if (materia.getNombre().equalsIgnoreCase(TipoCarpeta.EXHORTO.name())) {
            expediente = "E".concat(expediente);
        }
        return expediente;
    }

    public ExhortoRecord getExpedienteById(Integer carpetId) {
        Carpeta expediente = carpetaRepository.findById(carpetId)
                .orElseThrow(() -> new NotFoundException("Expediente no encontrado", carpetId.toString()));
        List<TipoPartesRecord> partes = personaDocumentoRepository.findPartesByCarpetaId(carpetId);
        List<TipoPartesRecord> list = partes.stream()
                .map(parte -> parte.hideNames())
                .toList();
        List<HistorialRecord> historial = movimientoService.getHistorialByExpediente(carpetId);
        List<PiezaRecord> piezas = carpetaRepository.findPiezasByCarpetaId(carpetId, TipoCarpeta.PIEZA);
        piezas.add(0, new PiezaRecord(carpetId, expediente.getExpediente(), "Expediente principal"));
        return new ExhortoRecord(expediente.getJuzgado().getNombre(), expediente.getExpediente(),
                historial.get(0).hora(), expediente.getJuzgado().getNombre(), historial.get(0).nombre(),
                list, historial, piezas, historial.get(0).cargo());
    }

    public List<HistorialRecord> getHistorialByPieza(Integer carpetId) {
        return movimientoService.getHistorialByExpediente(carpetId);
    }
}
