package mx.gob.pjpuebla.trials.litigante;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.litigante.responselitigante.*;
import mx.gob.pjpuebla.trials.util.enums.EstadoNotificacion;
import mx.gob.pjpuebla.trials.workflow.notificaciondetalle.NotificacionesDetalles;
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

    private final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter formatoTiempo = DateTimeFormatter.ofPattern("HH:mm:ss");

    public Page<LitiganteExpedientesRecord> getExpedientesRelacionados(Pageable pageable) {
        String username = getLitiganteUsername();
        Page<LitiganteExpedientesRecord> page = personaDocumentoRepository.findByUsername(username, pageable);
        List<LitiganteExpedientesRecord> list = page.stream()
                .map(pd ->
                        pd.additionalData(
                                String.join(", ", personaDocumentoRepository.findTipoPartePrincipalByCarpetaId(pd.id(), "Actor")),
                                String.join(", ", personaDocumentoRepository.findTipoPartePrincipalByCarpetaId(pd.id(), "Demandado")),
                                notificacionesDetallesRepository.countNotificacionesPorLeer(pd.id(), username)))
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    private String getLitiganteUsername() {
        Jwt jwt = auditorAware.getCurrentAuditor().orElseThrow();
        return jwt.getClaims().get("preferred_username").toString();
    }

    public ExpedienteAutorizadoRecord getAcuerdosSentencias() {
        //Obtiene correo de persona litigante
        String userName = getLitiganteUsername();

        //Obtiene Notificaciones desde Notificaciones Detalles
        List<NotificacionesDetalles> notificacionesDetallesList = notificacionesDetallesRepository.getAllByUsername(
                userName,
                TipoNotificacion.CORREO_ELECTRONICO);
        if (notificacionesDetallesList.isEmpty())
            throw new NotFoundException("Acuerdos-Sentencias no encontradas para el usuario", userName);

        //Agrupa NotificacionesDetalles por numero de expediente
        Map<String, List<NotificacionesDetalles>> groupExpedientes = notificacionesDetallesList.stream()
                .filter(nd -> nd.getNotificacion().getDocumento().getCarpeta() != null)
                .collect(Collectors.groupingBy(nd -> nd.getNotificacion().getDocumento().getCarpeta().getExpediente()));

        //Asigna fecha para actualizar campo consulta
        LocalDateTime actual = LocalDateTime.now();

        //Arma la lista para las notificaciones que sean de la persona registrada
        List<AcuerdoSentenciaRecord> acuerdoSentenciaRecordList = new ArrayList<>();
        for (Map.Entry<String, List<NotificacionesDetalles>> entry : groupExpedientes.entrySet()) {
            List<NotificacionesDetalles> detallesPorExpediente = entry.getValue();
            List<DocumentoExpedienteRecord> documentoExpedienteRecordList = detallesPorExpediente.stream()
                    .map(nd -> {
                        Integer documentoId = nd.getNotificacion().getDocumento().getId();

                        nd.setFechaConsulta(actual);
                        if(nd.getFechaCompletado() == null) nd.setFechaCompletado(actual);
                        notificacionesDetallesRepository.save(nd);

                        nd.getNotificacion().setEstadoNotificacion(EstadoNotificacion.COMPLETADO);
                        notificacionRepository.save(nd.getNotificacion());

                        return new DocumentoExpedienteRecord(
                                documentoId,
                                nd.getFechaCompletado() != null ? nd.getFechaCompletado().format(formatoFecha) : "",
                                nd.getFechaCompletado() != null ? nd.getFechaCompletado().format(formatoTiempo) : "",
                                "/api/litigante/documento/" + documentoId
                        );
                    })
                    .collect(Collectors.toList());
            acuerdoSentenciaRecordList.add(new AcuerdoSentenciaRecord(entry.getKey(), documentoExpedienteRecordList));
        }

        return new ExpedienteAutorizadoRecord(acuerdoSentenciaRecordList);
    }
}
