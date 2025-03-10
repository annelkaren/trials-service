package mx.gob.pjpuebla.trials.litigante;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.litigante.responselitigante.*;
import mx.gob.pjpuebla.trials.litigante.responsepromociones.PromocionAutorizadaRecord;
import mx.gob.pjpuebla.trials.litigante.responsepromociones.PromocionesElectronicasLitigante;
import mx.gob.pjpuebla.trials.litigante.responsepromociones.PromocionesLitiganteRecord;
import mx.gob.pjpuebla.trials.util.enums.EstadoNotificacion;
import mx.gob.pjpuebla.trials.workflow.asistenciaaudiencia.AsistenciaAudienciaRepository;
import mx.gob.pjpuebla.trials.workflow.audiencias.record.AudienciasExpedienteRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.notificaciondetalle.NotificacionesDetalles;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.notificaciondetalle.NotificacionesDetallesRepository;
import mx.gob.pjpuebla.trials.workflow.notificaciones.NotificacionRepository;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

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
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    private String getLitiganteUsername() {
        Jwt jwt = auditorAware.getCurrentAuditor().orElseThrow();
        return jwt.getClaims().get("preferred_username").toString();
    }

    public ExpedienteAutorizadoRecord getAcuerdosSentencias() {
        // Obtiene correo de persona litigante
        String userName = getLitiganteUsername();

        // Obtiene Notificaciones desde Notificaciones Detalles
        List<NotificacionesDetalles> notificacionesDetallesList = notificacionesDetallesRepository.getAllByUsername(
                userName,
                TipoNotificacion.CORREO_ELECTRONICO);
        if (notificacionesDetallesList.isEmpty())
            throw new NotFoundException("Acuerdos-Sentencias no encontradas para el usuario", userName);

        // Agrupa NotificacionesDetalles por numero de expediente
        Map<String, List<NotificacionesDetalles>> groupExpedientes = notificacionesDetallesList.stream()
                .filter(nd -> nd.getNotificacion().getDocumento().getCarpeta() != null)
                .collect(Collectors.groupingBy(nd -> nd.getNotificacion().getDocumento().getCarpeta().getExpediente()));

        // Asigna fecha para actualizar campo consulta
        LocalDateTime actual = LocalDateTime.now();

        // Arma la lista para las notificaciones que sean de la persona registrada
        List<AcuerdoSentenciaRecord> acuerdoSentenciaRecordList = new ArrayList<>();
        for (Map.Entry<String, List<NotificacionesDetalles>> entry : groupExpedientes.entrySet()) {
            List<NotificacionesDetalles> detallesPorExpediente = entry.getValue();
            List<DocumentoExpedienteRecord> documentoExpedienteRecordList = detallesPorExpediente.stream()
                    .map(nd -> {
                        Integer documentoId = nd.getNotificacion().getDocumento().getId();

                        nd.setFechaConsulta(actual);
                        if (nd.getFechaCompletado() == null)
                            nd.setFechaCompletado(actual);
                        notificacionesDetallesRepository.save(nd);

                        nd.getNotificacion().setEstadoNotificacion(EstadoNotificacion.COMPLETADO);
                        notificacionRepository.save(nd.getNotificacion());

                        return new DocumentoExpedienteRecord(
                                documentoId,
                                nd.getFechaCompletado() != null ? nd.getFechaCompletado().format(formatoFecha) : "",
                                nd.getFechaCompletado() != null ? nd.getFechaCompletado().format(formatoTiempo) : "",
                                "/api/litigante/documento/" + documentoId);
                    })
                    .collect(Collectors.toList());
            acuerdoSentenciaRecordList.add(new AcuerdoSentenciaRecord(entry.getKey(), documentoExpedienteRecordList));
        }

        return new ExpedienteAutorizadoRecord(acuerdoSentenciaRecordList);
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
        return partes.length == 2 ? partes : new String[]{"", ""};
    }

    public ExpedienteResponseRecord getExpedienteDetails() {
        String username = getLitiganteUsername();

        LitiganteExpedientesRecord carpeta = personaDocumentoRepository.findByUsername(username, "", Pageable.unpaged())
                .getContent().stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Expediente no encontrado"));

        List<Documento> documentos = documentoRepository.findByCarpetaIdAndTipoDocumentoIn(
                carpeta.id(),
                Arrays.asList(TipoDocumento.ACUERDO, TipoDocumento.SENTENCIA));

        List<DocumentoResponseRecord> documentoResponseRecords = documentos.stream()
                .map(doc -> {
                    LocalDateTime fechaYHora = notificacionesDetallesRepository.findFechaYHoraByDocumentoId(doc);

                    return new DocumentoResponseRecord(
                            String.valueOf(doc.getId()),
                            fechaYHora.toLocalDate(),
                            fechaYHora.toLocalTime(),
                            "/api/litigante/documento/" + doc.getId());
                })
                .collect(Collectors.toList());

        Long notificacionesPendientes = notificacionesDetallesRepository.countNotificacionesPorLeer(carpeta.id(),
                username);

        return new ExpedienteResponseRecord(
                carpeta.numeroExpediente(),
                carpeta.materia(),
                carpeta.tipoJuicio(),
                carpeta.juzgado(),
                notificacionesPendientes,
                documentoResponseRecords);
    }

    public Page<PromocionAutorizadaRecord> getPromocionesLitigante(Pageable pageable) {
        String userName = getLitiganteUsername();
        Page<Documento> docPromociones = documentoRepository.findPromocionesLitigante(userName, pageable);

        return docPromociones.map(documento -> {

            boolean isValid = documentoRepository.existsByExpedienteAndAcuerdoAndAsociateCorreo(
                    documento.getCarpeta().getExpediente(),
                    documento.getFolio(),
                    userName);

            if (!isValid) {
                throw new IllegalStateException("El expediente y el acuerdo no están asociados al usuario.");
            }

            PromocionesElectronicasLitigante promocionElectronica = createPromocionElectronicaLitigante(documento);

            PromocionesLitiganteRecord promocionesLitiganteRecord = new PromocionesLitiganteRecord(
                    documento.getCarpeta().getExpediente(),
                    List.of(promocionElectronica));
            return new PromocionAutorizadaRecord(List.of(promocionesLitiganteRecord));
        });
    }

    /*  SE CREA METODO PARA CORREGIR SCAN DE QODANA */
    public PromocionesElectronicasLitigante createPromocionElectronicaLitigante(Documento documento) {
        String[] partesExpediente = documento.getCarpeta().getExpediente().split("/");

        String numeroExpediente = partesExpediente[0];
        String anioExpediente = partesExpediente.length > 1 ? partesExpediente[1] : "";

        return new PromocionesElectronicasLitigante(
                documento.getFolio(),
                documento.getPersona().getCorreoElectronico(),
                documento.getRuta(),
                documento.getAudit().getFechaAlta().toLocalDate(),
                documento.getAudit().getFechaAlta().toLocalTime(),
                "/opt/pjp/files/" + anioExpediente + "/" + documento.getCarpeta().getJuzgado().getNombre() + "/"
                        + numeroExpediente + "/" + documento.getRuta());

    }

}
