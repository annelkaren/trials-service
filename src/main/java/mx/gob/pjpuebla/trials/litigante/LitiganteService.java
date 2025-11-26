package mx.gob.pjpuebla.trials.litigante;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.migracion.readers.acuerdos.AcuerdosMigracionRepository;
import mx.gob.pjpuebla.migracion.readers.detalle.DetallesMigracionRepository;
import mx.gob.pjpuebla.migracion.readers.entradas.EntradasMigracionRepository;
import mx.gob.pjpuebla.migracion.readers.usuario.UsuarioMigracionRepository;
import mx.gob.pjpuebla.migracion.utils.UtilsMigracion;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRepository;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRecord;
import mx.gob.pjpuebla.trials.litigante.responselitigante.*;
import mx.gob.pjpuebla.trials.litigante.responsepromociones.PromocionesLitiganteRecord;
import mx.gob.pjpuebla.trials.util.PageableUtils;
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
                
                List<LitiganteExpedientesRecord> page = personaDocumentoRepository.findByUsernameList(username, key);
                List<LitiganteExpedientesRecord> list = page.stream()
                                .map(pd -> pd.additionalData(
                                                String.join(", ",
                                                                personaDocumentoRepository
                                                                                .findTipoPartePrincipalByCarpetaId(
                                                                                                pd.id(), "Actor")),
                                                String.join(", ",
                                                                personaDocumentoRepository
                                                                                .findTipoPartePrincipalByCarpetaId(
                                                                                                pd.id(), "Demandado")),
                                                notificacionesDetallesRepository.countNotificacionesPorLeer(pd.id(),
                                                                username)))
                                .toList();

                // Busca expedientes relacionados en SECJ PHP:
                List<LitiganteExpedientesRecord> listSecjPhp = entradasMigracionRepository
                                .findExpedientesRelacionadosLegacy(username, key).stream()
                                .map(p -> new LitiganteExpedientesRecord(
                                                p.getId(), p.getNumeroExpediente(), p.getMateria(), p.getTipoJuicio(),
                                                p.getActorPrincipal(), p.getDemandadoPrincipal(), p.getJuzgado(),
                                                p.getNotificacionesPendientes(), p.getSede(), p.getCu()))
                                .toList();
                // FIN busqueda y adicion a la lista

                List<LitiganteExpedientesRecord> combinados = new ArrayList<>(list);
                combinados.addAll(listSecjPhp);

                PageableUtils.ordenarLista(combinados, pageable.getSort());

                return PageableUtils.crearPagina(combinados, pageable);

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
                List<NotificacionesDetalles> notificacionesDetalles = notificacionesDetallesRepository.getAllByUsername(
                                userName, TipoNotificacion.CORREO_ELECTRONICO);

                List<AcuerdoSentenciaRecord> list = notificacionesDetalles.stream()
                                .map(notification -> new AcuerdoSentenciaRecord(
                                                notification.getId(),
                                                notification.getNotificacion().getDocumento().getCarpeta()
                                                                .getExpediente(),
                                                notification.getNotificacion().getFechaNotificado(),
                                                notification.getNotificacion().getDocumento().getCarpeta().getJuzgado()
                                                                .getNombre(),
                                                notification.getNotificacion().getDocumento().getId(),
                                                StringUtils.capitalize(notification.getNotificacion()
                                                                .getEstadoNotificacion().name()
                                                                .replace("_", " ").toLowerCase()),
                                                notification.getNotificacion().getDocumento().getMigrado()))
                                .toList();

                // Se incorpora notificaciones de expedientes del sistema SECJ PHP:
                List<AcuerdoSentenciaRecord> listNotiSecjPhp = usuarioMigracionRepository
                                .notificacionesLitigante(userName);
                List<AcuerdoSentenciaRecord> combinados = new ArrayList<>(list);
                combinados.addAll(listNotiSecjPhp);
               

                // Primero ordenamos todo junto para que las fechas se mezclen bien
                PageableUtils.ordenarLista(combinados, pageable.getSort());

                // Luego cortamos la página exacta que el usuario va a ver
                Page<AcuerdoSentenciaRecord> paginaFinal = PageableUtils.crearPagina(combinados, pageable);

                // LÓGICA DE NEGOCIO: MARCAR COMO LEÍDOS
                // Solo marcamos los que el usuario REALMENTE está viendo en esta página
                marcarNotificacionesComoLeidas(paginaFinal.getContent(), notificacionesDetalles);

                return paginaFinal;
        }

        private void marcarNotificacionesComoLeidas(List<AcuerdoSentenciaRecord> registrosVisibles,
                        List<NotificacionesDetalles> todasLasEntidades) {
                // Extraemos los IDs que se están mostrando en esta página
                Set<Integer> idsEnPantalla = registrosVisibles.stream()
                                .map(AcuerdoSentenciaRecord::notificacionId) 
                                .collect(Collectors.toSet());

                List<NotificacionesDetalles> paraActualizar = new ArrayList<>();

                // Buscamos las entidades originales que coincidan con los IDs visibles
                for (NotificacionesDetalles entidad : todasLasEntidades) {
                        // Verificamos si esta entidad es una de las que se mostró Y si está POR_LEER
                        if (idsEnPantalla.contains(entidad.getId()) &&
                                        entidad.getNotificacion()
                                                        .getEstadoNotificacion() == EstadoNotificacion.POR_LEER) {

                                entidad.setFechaCompletado(LocalDateTime.now());
                                entidad.getNotificacion().setEstadoNotificacion(EstadoNotificacion.COMPLETADO);

                                // Guardamos la referencia para hacer un batch update (más eficiente)
                                paraActualizar.add(entidad);
                        }
                }

                // Guardamos todos los cambios de un golpe
                if (!paraActualizar.isEmpty()) {
                        notificacionesDetallesRepository.saveAll(paraActualizar);
                }

        }

        public Page<LitiganteExpedienteListAudienciasRecord> getExpedientesAudienciasRelacionados(Pageable pageable) {
                String username = getLitiganteUsername();
                List<LitiganteExpedienteAudienciaRecord> list = asistenciaAudienciaRepository.getAllAudicenciasByUser(
                                username,
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
                                .findPromocionesElectronicasLitiganteLegacy(userName, key)
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
                                                p.getJuzgado(),
                                                true,
                                                p.getIdDetalle()))
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
                                        "/opt/pjp/files/" + anioExpediente + "/"
                                                        + documento.getCarpeta().getJuzgado().getNombre() + "/"
                                                        + numeroExpediente + "/" + documento.getRuta(),
                                        documento.getCarpeta().getJuzgado().getNombre());
                }).toList();

                // combinamos tanto promociones del litigante legacy (PHP) como las del sistema
                // java:
                List<PromocionesLitiganteRecord> combinados = new ArrayList<>();
                combinados.addAll(promocionesLegacy);
                combinados.addAll(promocionesLitigante);

                // Ordenamiento en memoria:
                PageableUtils.ordenarLista(combinados, pageable.getSort());

                return PageableUtils.crearPagina(combinados, pageable);
        }

        public DocumentoPromocionRecord getPromocionById(Integer promocionId) {
                Documento promocion = documentoRepository.findById(promocionId)
                                .orElseThrow(() -> new NotFoundException("Promoción no encontrada",
                                                promocionId.toString()));
                DocumentoContenido contenido = documentoContenidoRepository.findByDocumentoId(promocionId)
                                .orElseThrow(() -> new NotFoundException("Documento no encontrada",
                                                promocionId.toString()));
                return new DocumentoPromocionRecord(promocion.getCarpeta().getId(), TipoPromocion.CORREO_ELECTRONICO,
                                null,
                                contenido.getTexto());
        }

        public Page<LibroGobiernoRecord> getConsultaLibroGobierno(
                        String nombre, String aPaterno, String aMaterno, Pageable pageable) {
                return personaDocumentoRepository.findByNombreCompleto(nombre.trim(), aPaterno.trim(), aMaterno.trim(),
                                pageable);
        }

        public Page<SentenciasPublicasRecord> getSentenciasPublicas(
                        Integer materiaId, Pageable pageable) {
                Page<SentenciasPublicasRecord> page = documentoDetalleRepository.findSentenciasByMateriaId(materiaId,
                                pageable);
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
                                .orElseThrow(() -> new NotFoundException("Materia no encontrado",
                                                materiaId.toString()));
                if (materia.getNombre().equalsIgnoreCase(TipoCarpeta.EXHORTO.name())) {
                        expediente = "E".concat(expediente);
                }
                return expediente;
        }

        public ExhortoRecord getExpedienteById(Integer carpetId) {
                Carpeta expediente = carpetaRepository.findById(carpetId)
                                .orElseThrow(() -> new NotFoundException("Expediente no encontrado",
                                                carpetId.toString()));
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
