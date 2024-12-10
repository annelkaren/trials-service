package mx.gob.pjpuebla.trials.workflow.notificaciones;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.util.enums.EstadoNotificacion;
import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.notificaciones.DTO.NotificacionDto;
import mx.gob.pjpuebla.trials.workflow.notificaciones.records.NotificacionResponseRecord;
import mx.gob.pjpuebla.trials.workflow.notificaciones.records.NotificacionSaveRecord;
import mx.gob.pjpuebla.trials.workflow.notificacionesDetalles.NotificacionesDetalles;
import mx.gob.pjpuebla.trials.workflow.notificacionesDetalles.NotificacionesDetallesRepository;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import mx.gob.pjpuebla.trials.error.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final PersonaDocumentoRepository personaDocumentoRepository;
    private final DomicilioRepository domicilioRepository;
    private final DocumentoRepository documentoRepository;
    private final NotificacionesDetallesRepository notificacionesDetallesRepository;

    public Page<NotificacionRecord> getAllNotificaciones(String key, Pageable pageable) {
        key = (key != null) ? key.toLowerCase() : "";

        TipoNotificacion tipoNotificacion = TipoNotificacion.ESTRADO;
        if (!key.isEmpty()) {
            try {
                tipoNotificacion = TipoNotificacion.valueOf(key.toUpperCase());
            } catch (IllegalArgumentException e) {
                return Page.empty(pageable);
            }
        }
        Page<Notificacion> page = notificacionRepository.getNotificacionByTipo(tipoNotificacion, pageable);
        List<NotificacionRecord> list = page.getContent().stream()
                .map(item -> new NotificacionRecord(
                        item.getCarpeta().getExpediente(),
                        item.getConcepto(),
                        item.getNotas(),
                        item.getTipoNotificacion(),
                        item.getFechaPublicacion(),
                        item.getFechaResolucion()))
                .toList();

        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional
    @PostMapping
    public void create(NotificacionDto notificacionData) throws Exception {
        PersonaDocumento persona = personaDocumentoRepository.findById(notificacionData.getPersonId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "PersonaDocumento no encontrado para el ID: " + notificacionData.getPersonId()));

        persona.setTipoNotificacion(notificacionData.getMetodo());

        if (notificacionData.getMetodo().equals(TipoNotificacion.CORREO_ELECTRONICO)) {
            if (notificacionData.getUsarCorreoRegistrado()) {
                persona.setCorreoNotificacion(null);
                persona.setFnDomicilio(null);
            } else {
                persona.setCorreoNotificacion(notificacionData.getCorreo());
                persona.setFnDomicilio(null);
            }
        }

        if (notificacionData.getMetodo().equals(TipoNotificacion.NINGUNO)
                || notificacionData.getMetodo().equals(TipoNotificacion.ESTRADO)) {
            persona.setCorreoNotificacion(null);
            persona.setFnDomicilio(null);
        }

        if (notificacionData.getMetodo().equals(TipoNotificacion.DOMICILIO)) {
            if (notificacionData.getIdDomicilio() != null) {
                Domicilio domicilio = domicilioRepository.findById(notificacionData.getIdDomicilio())
                        .orElseThrow(() -> new RuntimeException("Domicilio no encontrado"));
                persona.setCorreoNotificacion(null);
                persona.setFnDomicilio(domicilio);
            } else {
                Domicilio newDomicilio = new Domicilio();
                newDomicilio.setCalle(notificacionData.getCalle());
                newDomicilio.setCiudad(notificacionData.getCiudad());
                newDomicilio.setCodigoPostal(notificacionData.getCodigoPostal());
                newDomicilio.setColonia(notificacionData.getColonia());
                newDomicilio.setEstadoRepublica(notificacionData.getEstadoRepublica());
                newDomicilio.setExterior(notificacionData.getExterior());
                newDomicilio.setInterior(notificacionData.getInterior());
                newDomicilio.setLatitud(notificacionData.getLatitud());
                newDomicilio.setLongitud(notificacionData.getLongitud());
                newDomicilio.setMunicipio(notificacionData.getMunicipio());
                persona.setCorreoNotificacion(null);

                try {
                    Domicilio domicilioGuardado = domicilioRepository.save(newDomicilio);
                    Domicilio domicilio = domicilioRepository.findById(domicilioGuardado.getId())
                            .orElseThrow(() -> new RuntimeException("Domicilio no encontrado"));
                    persona.setFnDomicilio(domicilio);
                } catch (Exception e) {
                    throw new RuntimeException("Error al registrar la notificación", e);
                }

            }
        }

        try {
            personaDocumentoRepository.save(persona);
        } catch (Exception e) {
            throw new RuntimeException("Error al registrar la notificación", e);
        }
    }

    @Transactional
    public NotificacionResponseRecord createRegistroNotificacion(NotificacionSaveRecord notificacion) {
        // Validar existencia del documento
        Documento documento = documentoRepository.findById(notificacion.documentoId())
                .orElseThrow(() -> new NotFoundException("Documento no encontrado", "documentoId"));

        // Cargar todas las personas en una sola consulta
        List<Integer> personaIds = notificacion.personasDocumentosId();
        List<PersonaDocumento> personas = personaDocumentoRepository.findAllById(personaIds);

        // Validar que todos los IDs fueron encontrados
        if (personas.size() != personaIds.size()) {
            List<Integer> noEncontrados = personaIds.stream()
                    .filter(id -> personas.stream().noneMatch(persona -> persona.getId().equals(id)))
                    .toList();
            throw new NotFoundException("Error al obtener la lista de personas seleccionadas", "personaIds: " + noEncontrados);
        }

        // Crear los detalles de notificaciones Y NOTIFICACIONES
        List<NotificacionesDetalles> detalles = new ArrayList<>();
        for (PersonaDocumento persona : personas) {

            Notificacion notif = new Notificacion()
                    .setNotas(notificacion.notas())
                    .setEstadoNotificacion(EstadoNotificacion.PENDIENTE_DE_ASIGNAR)
                    .setTipoNotificacion(persona.getTipoNotificacion())
                    .setDocumento(documento);
            notif = notificacionRepository.save(notif);

            NotificacionesDetalles detalle = new NotificacionesDetalles()
                    .setNotificacion(notif) 
                    .setPersonaDocumento(persona);
            detalles.add(detalle);
        }

        // Guardar todos los detalles en un solo paso
        notificacionesDetallesRepository.saveAll(detalles);

        // Respuesta con más información
        return new NotificacionResponseRecord(200,
                String.format("Notificación creada con éxito. Detalles creados: %d", detalles.size()));
    }

}
