package mx.gob.pjpuebla.trials.workflow.notificaciones;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.roles.RoleRecord;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.EmailService;
import mx.gob.pjpuebla.trials.util.enums.*;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
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
    @Value("${app.root-folder}")
    private String rootFolder; // Ruta raíz de la digitalización
    private String basePath; // Ruta base para la digitalización
    private static final String EXTENSION_ARCHIVO = ".pdf";

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

        Persona personaAuth = personaService.getAuditor();
        List<Juzgado> juzgados = personaAuth.getJuzgado() != null ? List.of(personaAuth.getJuzgado())
                : personaAuth.getOficialia().getJuzgados();

        Page<Notificacion> page = notificacionRepository.getNotificacionByTipo(tipoNotificacion, estadoNotificacion,
                pageable, juzgados);

        List<NotificacionRecord> list = page.getContent().stream()
                .map(notificacion -> {
                    List<DocumentoDetalleRecord> documentoDetalleRecord = notificacionRepository
                            .findDocumentoDetalleByDocumentoId(notificacion.getDocumento().getId());
                    DocumentoDetalleRecord documentoDetalle = documentoDetalleRecord.isEmpty() ? null
                            : documentoDetalleRecord.get(0);
                    List<String> concepto;
                    // Validación adicional, si es sentencia se envia el extracto de sentencia por
                    // el contrario se envian los rumbros en concepto.

                    if (notificacion.getDocumento().getTipoDocumento().equals(TipoDocumento.SENTENCIA)) {

                        DocumentoDetalle docDetalle = documentoDetalleRepository
                                .findByDocumentoId(notificacion.getDocumento().getId()).orElse(null);
                        if (docDetalle != null) {
                            String extracto = docDetalle.getExtractoSentencia();
                            concepto = List.of(extracto.substring(0, Math.min(extracto.length(), 25)));
                        } else {
                            concepto = List.of();
                        }

                    } else {
                        List<String> rubros = notificacion.getDocumento().getData() != null
                                ? notificacion.getDocumento().getData().getRubros()
                                : new ArrayList<>();
                        concepto = formatConcepto(rubros);
                    }

                    NotificacionesDetalles notificacionesDetalles = notificacionesDetallesRepository
                            .findByNotificacionId(notificacion.getId())
                            .orElseThrow(() -> new NotFoundException("Notificacion Detalle no encontrado", "id"));

                    PersonaDocumento persona = notificacionesDetalles.getPersonaDocumento();
                    String domicilio = persona.getFnDomicilio() != null ? persona.getFnDomicilio().getLineaDomicilio()
                            : "Sin Domicilio";

                    return new NotificacionRecord(
                            notificacion.getId(),
                            notificacion.getDocumento().getCarpeta().getExpediente(),
                            concepto,
                            notificacion.getNotas(),
                            notificacion.getTipoNotificacion(),
                            documentoDetalle,
                            notificacion.getDocumento().getTipoDocumento(),
                            notificacion.getDocumento().getId(),
                            notificacion.getDocumento().getCarpeta().getId(),
                            notificacion.getFechaSalida(),
                            notificacion.getFechaNotificado(),
                            domicilio,
                            notificacionesDetalles.getPersonaDocumento().getNombre(),
                            notificacionesDetalles.getPersonaDocumento().getTipoPartes().getNombre());
                })
                .toList();

        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    public NotificacionDetalleRecord getNotificacionDetalle(Integer id) {
        NotificacionesDetalles notificacionesDetalles = notificacionesDetallesRepository.findByNotificacionId(id)
                .orElseThrow(() -> new NotFoundException("Notificacion Detalle no encontrado", "id"));

        PersonaDocumento persona = notificacionesDetalles.getPersonaDocumento();
        String domicilio = persona.getFnDomicilio() != null ? persona.getFnDomicilio().getLineaDomicilio()
                : "Sin Domicilio";
        String nombre = persona.getNombre() + " " + persona.getApellidoPaterno()
                + (persona.getApellidoMaterno() == null ? "" : " " + persona.getApellidoMaterno());

        return new NotificacionDetalleRecord(
                persona.getTipoPartes().getNombre(),
                nombre,
                domicilio);
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
                Domicilio newDomicilio = createDomicilio(notificacionData);

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

    /* Se crea metodo de domicilio para corregir scan de Qodana */
    public Domicilio createDomicilio(NotificacionDto notificacionData) {
        return new Domicilio()
                .setCalle(notificacionData.getCalle())
                .setCiudad(notificacionData.getCiudad())
                .setCodigoPostal(notificacionData.getCodigoPostal())
                .setColonia(notificacionData.getColonia())
                .setEstadoRepublica(notificacionData.getEstadoRepublica())
                .setExterior(notificacionData.getExterior())
                .setInterior(notificacionData.getInterior())
                .setLatitud(notificacionData.getLatitud())
                .setLongitud(notificacionData.getLongitud())
                .setMunicipio(notificacionData.getMunicipio());
    }

    public void createNotaNotificacion(Integer id, String notas) {

        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notificacion no encontrada", "notificacionId: " + id));

        notificacion.setNotas(notas);
        notificacionRepository.save(notificacion);
    }

    public void createListaEstrado(List<Integer> notificacionIds, LocalDate fechaVencimiento) {

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

            TipoNotificacion notificacionSeleccionada = persona.getTipoNotificacion();
            EstadoNotificacion estadoNotificacion = determinarEstadoNotificacion(notificacionSeleccionada);

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
                String nombreParticipante = getNombreParticipante(persona);
                String numCarpeta = documento.getCarpeta().getExpediente();
                String nombreJuzgado = documento.getCarpeta().getJuzgado().getNombre();
                String tipoDocumento = documento.getTipoDocumento().name();

                createLitigante(persona, email);
                sendNotificacion(email, nombreParticipante, numCarpeta, nombreJuzgado, tipoDocumento);
            }
        }

        // Guardar todos los detalles en un solo paso
        notificacionesDetallesRepository.saveAll(detalles);

        // Respuesta con más información
        return new NotificacionResponseRecord(200,
                String.format("Notificación creada con éxito. Detalles creados: %d", detalles.size()));
    }

    private EstadoNotificacion determinarEstadoNotificacion(TipoNotificacion tipo) {
        return switch (tipo) {
            case CORREO_ELECTRONICO -> EstadoNotificacion.POR_LEER;
            case DOMICILIO -> EstadoNotificacion.POR_NOTIFICAR;
            default -> EstadoNotificacion.PENDIENTE_DE_ASIGNAR;
        };
    }

    private String getNombreParticipante(PersonaDocumento personaDocumento) {
        String nombre = personaDocumento.getNombre() != null ? personaDocumento.getNombre() : "";
        String apellidoPaterno = personaDocumento.getApellidoPaterno() != null ? personaDocumento.getApellidoPaterno()
                : "";
        String apellidoMaterno = personaDocumento.getApellidoMaterno() != null ? personaDocumento.getApellidoMaterno()
                : "";

        return nombre + " " + apellidoPaterno + " " + apellidoMaterno;
    }

    private String getValueOrEmpty(String value) {
        return value != null && !value.isBlank() ? value : "   ";
    }

    private void createLitigante(PersonaDocumento personaDocumento, String email) {
        Persona persona = new Persona();

        persona.setDomicilio(personaDocumento.getFnDomicilio());
        persona.setNombre(getValueOrEmpty(personaDocumento.getNombre()));
        persona.setApellidoPaterno(getValueOrEmpty(personaDocumento.getApellidoPaterno()));
        persona.setApellidoMaterno(getValueOrEmpty(personaDocumento.getApellidoMaterno()));
        persona.setCurp(personaDocumento.getCurp());
        persona.setCorreoElectronico(getValueOrEmpty(email));
        persona.setEstado(Estado.ACTIVE);
        persona.setRolPrincipal("LITIGANTE");

        personaService.createLitigante(persona, Collections.singletonList(new RoleRecord("LITIGANTE", "LITIGANTE")));
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
    public void updateBatchNotificacionEnRuta(List<Integer> ids, String estado) {
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

    @Transactional
    public Page<AcuerdoNotificacionesRecord> acuerdoNotificaciones(Integer idNotificacion, Pageable pageable) {

        Page<NotificacionesDetalles> notificacionesDetallesPage = notificacionesDetallesRepository
                .findByNotificacionDocumentoId(idNotificacion, pageable);

        return notificacionesDetallesPage.map(detalle -> new AcuerdoNotificacionesRecord(
                detalle.getNotificacion().getId(),
                detalle.getPersonaDocumento().getNombre() + " " +
                        detalle.getPersonaDocumento().getApellidoPaterno() +
                        (detalle.getPersonaDocumento().getApellidoMaterno() != null &&
                                !detalle.getPersonaDocumento().getApellidoMaterno().isEmpty()
                                        ? " " + detalle.getPersonaDocumento().getApellidoMaterno()
                                        : ""),
                detalle.getNotificacion().getTipoNotificacion(),
                detalle.getNotificacion().getEstadoNotificacion(),
                detalle.getNotificacion().getNotas()));
    }

    @Transactional
    public void digitalizarActaDomicilio(NotificacionActaRecord notificacionActaRecord, MultipartFile file) {
        this.basePath = rootFolder + "/digitalizacion/";
        Notificacion notificacion = notificacionRepository.findById(notificacionActaRecord.id()).orElseThrow();
        EstadoNotificacion estadoNotificacion = EstadoNotificacion.valueOf(notificacionActaRecord.estadoNotificacion());
        notificacion.setEstadoNotificacion(estadoNotificacion);

        Path rutaArchivo = crearDirectorio(notificacion);
        String nombreUnicoArchivo = generarNombreArchivo();

        try {
            if (!Files.exists(rutaArchivo)) {
                Files.createDirectories(rutaArchivo);
            }

            Files.write(rutaArchivo.resolve(nombreUnicoArchivo), file.getBytes());
            log.info("Archivo cargado en el servidor con nombre: {}", nombreUnicoArchivo);

        } catch (IOException e) {
            log.error("Error al guardar el archivo: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al guardar el archivo en el servidor", e);
        }
        notificacion.setUrlDocumento(nombreUnicoArchivo);
        notificacionRepository.save(notificacion);
    }

    public byte[] getActaDocumento(Integer notificacionId) throws IOException {
        this.basePath = this.rootFolder + "/digitalizacion/";

        Notificacion notificacion = notificacionRepository.findById(notificacionId).orElse(null);
        assert notificacion != null;
        Path rutaArchivo = crearDirectorio(notificacion).resolve(notificacion.getUrlDocumento());

        if (Files.exists(rutaArchivo)) {
            return Files.readAllBytes(rutaArchivo);
        } else {
            throw new IOException(
                    "El archivo relacionado con la audiencia " + notificacion.getId() + " no existe en el directorio");
        }
    }

    private Path crearDirectorio(Notificacion notificacion) {
        this.basePath = this.rootFolder + "/digitalizacion/";
        String expediente = notificacion.getDocumento().getCarpeta().getExpediente();
        expediente = expediente.replace("/", "");
        String numero = expediente.substring(0, expediente.length() - 4);
        numero = String.format("%06d", Integer.parseInt(numero));
        String juzgado = notificacion.getDocumento().getCarpeta().getJuzgado().getNombre();
        return Paths.get(basePath, juzgado, numero, "acuerdos", notificacion.getId().toString());
    }

    private String generarNombreArchivo() {
        return "ActaPruebaa_" + UUID.randomUUID() + EXTENSION_ARCHIVO;
    }

    public List<Integer> notificacionesTurnado(List<Integer> carpetaIds) {
        List<Integer> carpetaIdsSinNotificaciones = new ArrayList<>();
        for (Integer carpetaId : carpetaIds) {
            List<Notificacion> notificaciones = notificacionRepository.findNotificacionesTurnado(carpetaId);
            if (notificaciones.isEmpty()) {
                carpetaIdsSinNotificaciones.add(carpetaId);
            }
        }
        return carpetaIdsSinNotificaciones;
    }
}
