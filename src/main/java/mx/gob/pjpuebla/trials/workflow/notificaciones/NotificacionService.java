package mx.gob.pjpuebla.trials.workflow.notificaciones;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.EmailService;
import mx.gob.pjpuebla.trials.util.enums.EstadoNotificacion;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalle;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.listaestrados.ListaEstrado;
import mx.gob.pjpuebla.trials.workflow.listaestrados.ListaEstradoRepository;
import mx.gob.pjpuebla.trials.workflow.notificaciones.records.*;
import mx.gob.pjpuebla.trials.workflow.notificaciondetalle.NotificacionesDetalles;
import mx.gob.pjpuebla.trials.workflow.notificaciondetalle.NotificacionesDetallesRepository;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class NotificacionService {

    @Value("${app.portal-notificaciones}")
    private String portalNotificaciones; // Ruta raíz de la digitalización

    private final NotificacionRepository notificacionRepository;
    private final PersonaDocumentoRepository personaDocumentoRepository;
    private final DomicilioRepository domicilioRepository;
    private final PersonaService personaService;
    private final ListaEstradoRepository listaEstradoRepository;
    private final DocumentoRepository documentoRepository;
    private final NotificacionesDetallesRepository notificacionesDetallesRepository;
    private final EmailService emailService;
    private final DocumentoDetalleRepository documentoDetalleRepository;

    public Page<NotificacionRecord> getAllNotificaciones(String tipo, String estado, Pageable pageable) {
        tipo = (tipo != null) ? tipo.toLowerCase() : "";
        estado = (estado != null) ? estado.toLowerCase() : "";

        TipoNotificacion tipoNotificacion = TipoNotificacion.ESTRADO;
        if (!tipo.isEmpty()) {
            try {
                tipoNotificacion = TipoNotificacion.valueOf(tipo.toUpperCase());
            } catch (IllegalArgumentException e) {
                return Page.empty(pageable);
            }
        }
        EstadoNotificacion estadoNotificacion = EstadoNotificacion.PENDIENTE_DE_ASIGNAR;
        if (!estado.isEmpty()) {
            try {
                estadoNotificacion = EstadoNotificacion.valueOf(estado.toUpperCase());
            } catch (IllegalArgumentException e) {
                return Page.empty(pageable);
            }
        }
        Page<Notificacion> page = notificacionRepository.getNotificacionByTipo(tipoNotificacion, estadoNotificacion,
                pageable);

        List<NotificacionRecord> list = page.getContent().stream()
                .map(notificacion -> {
                    Optional<DocumentoDetalleRecord> documentoDetalleRecord;
                    documentoDetalleRecord = notificacionRepository
                            .findDocumentoDetalleByDocumentoId(notificacion.getDocumento().getId());
                    List<String> concepto;
                    // Validación adicional, si es sentencia se envia el extracto de sentencia por
                    // el contrario se envian los rumbros en concepto.

                    if (notificacion.getDocumento().getTipoDocumento().equals(TipoDocumento.SENTENCIA)) {

                        DocumentoDetalle docDetalle = documentoDetalleRepository.findByDocumentoId(notificacion.getDocumento().getId()).orElse(null);
                        if(docDetalle != null){
                            String extracto = docDetalle.getExtractoSentencia();
                            concepto = List.of(extracto.substring(0, Math.min(extracto.length(), 25)));
                        }else{
                            concepto = List.of();
                        }

                    } else {
                        List<String> rubros = notificacion.getDocumento().getData() != null
                                ? notificacion.getDocumento().getData().getRubros()
                                : new ArrayList<>();
                        concepto = formatConcepto(rubros);
                    }

                    NotificacionesDetalles notificacionesDetalles = notificacionesDetallesRepository.findByNotificacionId(notificacion.getId())
                            .orElseThrow(() -> new NotFoundException("Notificacion Detalle no encontrado", "id"));

                    PersonaDocumento persona = notificacionesDetalles.getPersonaDocumento();
                    String domicilio = persona.getFnDomicilio() != null ? persona.getFnDomicilio().getLineaDomicilio() : "Sin Domicilio" ;

                    return new NotificacionRecord(
                            notificacion.getId(),
                            notificacion.getDocumento().getCarpeta().getExpediente(),
                            concepto,
                            notificacion.getNotas(),
                            notificacion.getTipoNotificacion(),
                            documentoDetalleRecord.orElse(null),
                            notificacion.getDocumento().getTipoDocumento(),
                            notificacion.getDocumento().getId(),
                            notificacion.getDocumento().getCarpeta().getId(),
                            notificacion.getFechaSalida(),
                            notificacion.getFechaNotificado(),
                            domicilio
                    );
                })
                .toList();

        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    public NotificacionDetalleRecord getNotificacionDetalle(Integer id) {
        NotificacionesDetalles notificacionesDetalles = notificacionesDetallesRepository.findByNotificacionId(id)
                .orElseThrow(() -> new NotFoundException("Notificacion Detalle no encontrado", "id"));

        PersonaDocumento persona = notificacionesDetalles.getPersonaDocumento();
        String domicilio = persona.getFnDomicilio() != null ? persona.getFnDomicilio().getLineaDomicilio() : "Sin Domicilio" ;
        String nombre = persona.getNombre() + " " + persona.getApellidoPaterno() + (persona.getApellidoMaterno() == null ? "" : " " + persona.getApellidoMaterno());

        return new NotificacionDetalleRecord(
                persona.getTipoPartes().getNombre(),
                nombre,
                domicilio
        );
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

    public void createNotaNotificacion(Integer id, String notas) {

        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notificacion no encontrada", "notificacionId: " + id));

        notificacion.setNotas(notas);
        notificacionRepository.save(notificacion);
    }

    public void createListaEstrado(List<Integer> notificacionIds, Date fechaVencimiento) {

        if (notificacionIds == null || notificacionIds.isEmpty()) {
            throw new IllegalArgumentException("Debe proporcionar al menos un ID de notificación.");
        }
        Persona persona = personaService.getAuditor();

        ListaEstrado listaEstrado = new ListaEstrado();

        listaEstrado.setPersona(persona);
        listaEstrado.setFechaVencimiento(fechaVencimiento);
        listaEstrado.setFechaAlta(LocalDateTime.now());
        listaEstrado = listaEstradoRepository.save(listaEstrado);

        List<Notificacion> notificaciones = notificacionRepository.findAllById(notificacionIds);

        for (Notificacion notificacion : notificaciones) {
            notificacion.setEstadoNotificacion(EstadoNotificacion.ASIGNADO);
            notificacion.setListaEstrado(listaEstrado);
        }

        notificacionRepository.saveAll(notificaciones);

    }

    public List<String> formatConcepto(List<String> rubros) {
        if (rubros == null || rubros.isEmpty()) {
            return new ArrayList<>();
        }
        int num = rubros.size();
        String mensaje = num > 1 ? String.format(" y %d más", num - 1) : "";

        List<String> result = new ArrayList<>();
        result.add(rubros.get(0));

        if (!mensaje.isEmpty()) {
            result.add(mensaje);
        }

        return result;
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
            throw new NotFoundException("Error al obtener la lista de personas seleccionadas",
                    "personaIds: " + noEncontrados);
        }

        // Crear los detalles de notificaciones Y NOTIFICACIONES
        List<NotificacionesDetalles> detalles = new ArrayList<>();
        for (PersonaDocumento persona : personas) {
            EstadoNotificacion estadoNotificacion = persona.getTipoNotificacion().equals(TipoNotificacion.CORREO_ELECTRONICO) ? EstadoNotificacion.POR_LEER : EstadoNotificacion.PENDIENTE_DE_ASIGNAR;

            Notificacion notif = new Notificacion()
                    .setNotas(notificacion.notas())
                    .setEstadoNotificacion(estadoNotificacion)
                    .setTipoNotificacion(persona.getTipoNotificacion())
                    .setDocumento(documento);
            notif = notificacionRepository.save(notif);

            NotificacionesDetalles detalle = new NotificacionesDetalles()
                    .setNotificacion(notif)
                    .setPersonaDocumento(persona);
            detalles.add(detalle);

            // ENVIO DE NOTIFICACION SI EL TIPO DE NOTIFICACION ES CORREO ELECTRONICO:
            if (notif.getTipoNotificacion().equals(TipoNotificacion.CORREO_ELECTRONICO)) {
                String email = persona.getCorreoNotificacion() != null && !persona.getCorreoNotificacion().isBlank()
                        ? persona.getCorreoNotificacion()
                        : persona.getCorreoElectronico();
                String nombreParticipante = persona.getNombre() + " " + persona.getApellidoPaterno() + " "
                        + persona.getApellidoPaterno();
                String numCarpeta = documento.getCarpeta().getExpediente();
                String nombreJuzgado = documento.getCarpeta().getJuzgado().getNombre();
                String tipoDocumento = documento.getTipoDocumento().name();

                sendNotificacion(email, nombreParticipante, numCarpeta, nombreJuzgado, tipoDocumento);
            }
        }

        // Guardar todos los detalles en un solo paso
        notificacionesDetallesRepository.saveAll(detalles);

        // Respuesta con más información
        return new NotificacionResponseRecord(200,
                String.format("Notificación creada con éxito. Detalles creados: %d", detalles.size()));
    }

    private Boolean sendNotificacion(String email, String nombreParticipante, String numCarpeta, String nombreJuzgado,
            String tipoDocumento) {

         Map<String, Object> sendEmail = new HashMap<>();

         sendEmail.put("nombreParticipante", nombreParticipante);
         sendEmail.put("numCarpeta", numCarpeta);
         sendEmail.put("nombreJuzgado", nombreJuzgado);
         sendEmail.put("tipoDocumento", tipoDocumento);
         sendEmail.put("portalNotificaciones", portalNotificaciones);

         emailService.sendMail(
         List.of(email),
         Collections.emptyList(),
         Collections.emptyList(),
         "Notificación pendiente",
         "NotificacionParticipantes.ftl",
         sendEmail);

        return true;
    }

    @Transactional
    public void updateBatchNotificacionEnRuta(List<Integer> ids, String estado){
        LocalDateTime fecha = LocalDateTime.now();

        List<Notificacion> notificaciones = notificacionRepository.findAllById(ids);

        if (notificaciones.size() != ids.size()) {
            throw new NotFoundException("Algunas notificaciones no fueron encontradas",
                    "ids: " + ids);
        }
        notificaciones.forEach(notificacion -> {
            notificacion.setFechaSalida(fecha)
                    .setEstadoNotificacion(EstadoNotificacion.valueOf(estado));
        });

        notificacionRepository.saveAll(notificaciones);
    }
}
