package mx.gob.pjpuebla.trials.workflow.notificacionesSalas;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoService;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.InstanciaJuzgado;
import mx.gob.pjpuebla.trials.workflow.acuseNotificacion.AcuseNotificacionService;
import mx.gob.pjpuebla.trials.workflow.documentos.DigitalizacionService;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DigitalizacionRecord;
import mx.gob.pjpuebla.trials.workflow.emailLogs.EmailGatewayService;
import mx.gob.pjpuebla.trials.workflow.emailLogs.EmailLogs;
import mx.gob.pjpuebla.trials.workflow.emailLogs.EmailVerificacionService;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records.NotificacionSalaCreateRecord;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records.NotificacionSalaCreateResponseRecord;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records.NotificacionSalaDestinatarioCreateRecord;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records.NotificacionSalaDestinatarioRecord;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records.NotificacionSalaDetalleRecord;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records.NotificacionesSalasRecord;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfReader;
import net.sf.jasperreports.engine.JRException;

@RequiredArgsConstructor
@Service
@Slf4j
public class NotificacionesSalasServices {
    private static final long MAX_FILE_SIZE = 50L * 1024L * 1024L;
    private static final Set<String> TIPO_ARCHIVOS_PERMITIDOS = Set.of("application/pdf");


    private final NotificacionesSalasRepository notificacionesSalasRepository;
    private final NotificacionSalaDestinatarioRepository notificacionSalaDestinatarioRepository;
    private final DigitalizacionService digitalizacionService;
    private final EmailGatewayService emailGatewayService;
    private final AcuseNotificacionService acuseNotificacionService;
    private final JuzgadoService juzgadoService;
    private final PersonaService personaService;
    private final EmailVerificacionService emailVerificacionService;

    @Value("${app.public-api-base-url}")
    private String publicApiBaseUrl;

    public void verificarEstatusCorreo(Integer destinatarioId) {
        NotificacionSalaDestinatario destinatario = notificacionSalaDestinatarioRepository.findById(destinatarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Destinatario no encontrado."));

        if (destinatario.getEmailLog() != null) {
            emailVerificacionService.verificarYActualizarUnico(destinatario.getEmailLog());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El destinatario no tiene un registro de correo asociado.");
        }
    }

    public Page<NotificacionesSalasRecord> getPageNotificaciones(Pageable pageable, String q, String numeroExpediente,
            String nombreDestinatario, String correoElectronico, LocalDateTime fechaEnvioFrom,
            LocalDateTime fechaEnvioTo, LocalDateTime fechaTerminoFrom, LocalDateTime fechaTerminoTo,
            Integer salaId) {

        String usuario = personaService.getAuditor().getUsuario();

        return notificacionesSalasRepository.findPageNotificaciones(pageable, q, numeroExpediente, nombreDestinatario,
                correoElectronico, fechaEnvioFrom, fechaEnvioTo, fechaTerminoFrom, fechaTerminoTo, salaId, usuario);
    }

    @Transactional
    public NotificacionSalaCreateResponseRecord createNotificacion(NotificacionSalaCreateRecord request,
            List<MultipartFile> archivos) {

        validaciones(request, archivos);

        String toca = request.toca().trim();
        Juzgado sala = validateAndGetSala(request.salaId());
        String contenidoCorreo = request.contenidoCorreo().trim();

        NotificacionesSalas notificacion = new NotificacionesSalas()
                .setToca(toca)
                .setSala(sala)
                .setFechaTermino(request.fechaTermino() != null ? request.fechaTermino().atStartOfDay() : null)
                .setContenidoCorreo(contenidoCorreo)
                .setFechaEnvio(LocalDateTime.now())
                .setEstado(Estado.ACTIVE);

        notificacion = notificacionesSalasRepository.save(notificacion);

        byte[] mergedPdf = mergePdfFilesEnOrden(archivos);
        DigitalizacionRecord digitalizacionRecord = digitalizacionService.guardarArchivoNotificacionSala(mergedPdf,
                sala.getId(),
                notificacion.getId(),
                "notificacion_sala_unida.pdf");

        notificacion.setRutaArchivo(digitalizacionRecord.nombreArchivo());
        notificacion = notificacionesSalasRepository.save(notificacion);
        String publicDownloadUrl = buildPublicDownloadUrl(digitalizacionRecord.nombreArchivo());
        String htmlFinalCorreo = appendDownloadLink(contenidoCorreo, publicDownloadUrl);

        int exitosos = 0;
        int fallidos = 0;
        for (NotificacionSalaDestinatarioCreateRecord destinatarioRequest : request.destinatarios()) {
            NotificacionSalaDestinatario destinatario = new NotificacionSalaDestinatario();
            destinatario.setNotificacionSala(notificacion);
            destinatario.setNombreDestinatario(destinatarioRequest.nombreDestinatario().trim());
            destinatario.setCorreoElectronico(destinatarioRequest.correoElectronico().trim());
            destinatario.setTipoParte(destinatarioRequest.tipoParte().trim());
            destinatario.setEstado(Estado.ACTIVE);

            try {
                String asunto = "NOTIFICACIÓN TOCA " + notificacion.getToca() + ", "
                        + notificacion.getSala().getNombre().toUpperCase()
                        + " DEL TRIBUNAL SUPERIOR DE JUSTICIA, PODER JUDICIAL DEL ESTADO DE PUEBLA.";

                Persona personaLogueada = personaService.getAuditor();
                String replyToName = personaLogueada.getNombre() + " " + personaLogueada.getApellidoPaterno() + " "
                        + personaLogueada.getApellidoMaterno() != null ? personaLogueada.getApellidoMaterno() : "";
                String replyToEmail = personaLogueada.getCorreoElectronico();

                EmailLogs emailLog = enviarCorreoNotificacion(
                        asunto,
                        destinatarioRequest.nombreDestinatario().trim(),
                        htmlFinalCorreo,
                        destinatarioRequest.correoElectronico().trim(),
                        replyToName,
                        replyToEmail);
                destinatario.setEmailLog(emailLog);
                exitosos++;
            } catch (Exception ex) {
                log.warn("No se pudo enviar correo para destinatario {} en notificacionSala {}: {}",
                        destinatarioRequest.correoElectronico(),
                        notificacion.getId(),
                        ex.getMessage());
                fallidos++;
            }

            notificacionSalaDestinatarioRepository.save(destinatario);
        }

        return new NotificacionSalaCreateResponseRecord(
                notificacion.getId(),
                notificacion.getToca(),
                notificacion.getSala().getId(),
                notificacion.getSala().getNombre(),
                notificacion.getFechaEnvio(),
                notificacion.getFechaTermino(),
                digitalizacionRecord.nombreArchivo(),
                request.destinatarios().size(),
                exitosos,
                fallidos);
    }

    @Transactional(readOnly = true)
    public NotificacionSalaDetalleRecord getDetalle(Integer idNotificacionSala) {

        NotificacionesSalas notificacion = notificacionesSalasRepository.findById(idNotificacionSala)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notificacion no encontrada."));

        List<NotificacionSalaDestinatarioRecord> destinatarios = notificacionSalaDestinatarioRepository
                .findSalaDestinatarioRecord(idNotificacionSala);

        return new NotificacionSalaDetalleRecord(
                notificacion.getId(),
                notificacion.getToca(),
                notificacion.getSala().getId(),
                notificacion.getSala().getNombre(),
                notificacion.getFechaEnvio(),
                notificacion.getFechaTermino(),
                notificacion.getContenidoCorreo(),
                notificacion.getRutaArchivo(),
                extractFileName(notificacion.getRutaArchivo()),
                destinatarios);
    }

    private EmailLogs enviarCorreoNotificacion(String asunto, String nombreDestinatario, String contenidoCorreoHtml,
            String correoElectronico, String replyToName, String replyToEmail) {

        return emailGatewayService.sendAndLog(
                correoElectronico,
                nombreDestinatario,
                asunto,
                contenidoCorreoHtml,
                null,
                null,
                replyToName,
                replyToEmail);
    }

    public byte[] downloadArchivo(Integer idNotificacionSala) throws java.io.IOException {

        NotificacionesSalas notificacion = notificacionesSalasRepository.findById(idNotificacionSala)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notificacion no encontrada."));

        if (notificacion.getRutaArchivo() == null || notificacion.getRutaArchivo().isBlank()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La notificacion no tiene archivo.");
        }

        String rutaArchivo = "notificacionesSalas/sala_" + notificacion.getSala().getId() + "/"
                + notificacion.getRutaArchivo();

        return digitalizacionService.getArchivoNotificacionSala(rutaArchivo);
    }

    public byte[] downloadArchivoPublico(String nombreArchivo) {
        validarNombreArchivo(nombreArchivo);

        NotificacionesSalas notificacion = notificacionesSalasRepository.findByRutaArchivo(nombreArchivo)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Archivo no encontrado repositorio."));

        String rutaArchivo = "notificacionesSalas/sala_" + notificacion.getSala().getId() + "/"
                + notificacion.getRutaArchivo();

        if (rutaArchivo == null || rutaArchivo.isBlank()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Archivo no encontrado.");
        }

        try {
            return digitalizacionService.getArchivoNotificacionSala(rutaArchivo);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Archivo no encontrado.");
        }
    }

    public byte[] getAcuseNotificacion(Integer notificacionSalaDestinatarioId) throws JRException, IOException {
        return acuseNotificacionService.getAcuseNotificacionService(notificacionSalaDestinatarioId);
    }

    public byte[] getAcuseNotificacionNoEntregada(Integer notificacionSalaDestinatarioId)
            throws JRException, IOException {
        return acuseNotificacionService.getAcuseNotificacionNoEntregadaService(notificacionSalaDestinatarioId);
    }

    private void validaciones(NotificacionSalaCreateRecord request, List<MultipartFile> archivos) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La informacion de la notificacion es obligatoria.");
        }

        if (request.toca() == null || request.toca().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El numero de expediente es obligatorio.");
        }
        if (request.salaId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La sala es obligatoria.");
        }
        if (request.contenidoCorreo() == null || request.contenidoCorreo().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El contenido del correo es obligatorio.");
        }
        if (request.destinatarios() == null || request.destinatarios().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe registrar al menos un destinatario.");
        }

        if (archivos == null || archivos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe adjuntar al menos un archivo.");
        }

        for (int i = 0; i < archivos.size(); i++) {
            MultipartFile archivo = archivos.get(i);
            int index = i + 1;

            if (archivo == null || archivo.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El archivo " + index + " no puede estar vacio.");
            }
            if (archivo.getContentType() == null || !TIPO_ARCHIVOS_PERMITIDOS.contains(archivo.getContentType())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El archivo " + index + " debe ser un PDF.");
            }
            if (archivo.getSize() > MAX_FILE_SIZE) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El archivo " + index + " no puede superar los 50 MB.");
            }
        }

        for (int i = 0; i < request.destinatarios().size(); i++) {
            NotificacionSalaDestinatarioCreateRecord destinatario = request.destinatarios().get(i);
            int index = i + 1;
            if (destinatario == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "La informacion del destinatario " + index + " es obligatoria.");
            }
            if (destinatario.nombreDestinatario() == null || destinatario.nombreDestinatario().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El nombre del destinatario " + index + " es obligatorio.");
            }
            if (destinatario.correoElectronico() == null || destinatario.correoElectronico().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El correo del destinatario " + index + " es obligatorio.");
            }

            if (destinatario.tipoParte() == null || destinatario.tipoParte().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El tipo de parte del destinatario " + index + " es obligatorio.");
            }
        }
    }

    private byte[] mergePdfFilesEnOrden(List<MultipartFile> archivos) {
        Document outputDocument = new Document();
        PdfCopy pdfCopy = null;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            pdfCopy = new PdfCopy(outputDocument, outputStream);
            outputDocument.open();

            for (int i = 0; i < archivos.size(); i++) {
                MultipartFile archivo = archivos.get(i);
                int index = i + 1;
                String nombreArchivo = archivo.getOriginalFilename() == null || archivo.getOriginalFilename().isBlank()
                        ? "archivo_" + index + ".pdf"
                        : archivo.getOriginalFilename();

                try (InputStream inputStream = archivo.getInputStream()) {
                    appendPdf(pdfCopy, inputStream);
                } catch (IOException | DocumentException e) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "El archivo " + index + " (" + nombreArchivo + ") no es un PDF valido.",
                            e);
                }
            }

            outputDocument.close();
            pdfCopy.close();
            return outputStream.toByteArray();
        } catch (IOException | DocumentException e) {
            log.error("Error al unir los PDFs de notificacion de sala.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "No fue posible unir los archivos PDF.");
        } finally {
            if (outputDocument.isOpen()) {
                outputDocument.close();
            }
            if (pdfCopy != null) {
                pdfCopy.close();
            }
        }
    }

    private void appendPdf(PdfCopy pdfCopy, InputStream inputStream) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(inputStream);
        int totalPages = reader.getNumberOfPages();
        for (int page = 1; page <= totalPages; page++) {
            pdfCopy.addPage(pdfCopy.getImportedPage(reader, page));
        }
        pdfCopy.freeReader(reader);
        reader.close();
    }

    private String sanitizeFilename(String name) {
        if (name == null || name.isBlank()) {
            return "adjunto";
        }
        return name.replaceAll("[\\\\/]+", "_");
    }

    private String extractFileName(String rutaArchivo) {
        if (rutaArchivo == null || rutaArchivo.isBlank()) {
            return null;
        }
        String normalized = rutaArchivo.replace("\\", "/");
        int idx = normalized.lastIndexOf('/');
        return idx >= 0 ? normalized.substring(idx + 1) : normalized;
    }

    private String buildPublicDownloadUrl(String nombreArchivo) {
        String encodedNombreArchivo = UriUtils.encodePathSegment(nombreArchivo, StandardCharsets.UTF_8);
        String baseUrl = publicApiBaseUrl != null ? publicApiBaseUrl.trim() : "";

        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl + "/notificaciones/download/" + encodedNombreArchivo;
    }

    private String appendDownloadLink(String contenidoCorreoHtml, String downloadUrl) {
        StringBuilder html = new StringBuilder(contenidoCorreoHtml == null ? "" : contenidoCorreoHtml);
        html.append("<hr/>")
                .append("<p><strong>Descargar documento:</strong> ")
                .append("<a href=\"")
                .append(downloadUrl)
                .append("\">")
                .append(downloadUrl)
                .append("</a></p>");
        return html.toString();
    }

    private void validarNombreArchivo(String nombreArchivo) {
        if (nombreArchivo == null || nombreArchivo.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre de archivo invalido.");
        }
        if (nombreArchivo.contains("/") || nombreArchivo.contains("\\") || nombreArchivo.contains("..")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre de archivo invalido.");
        }
    }

    private Juzgado validateAndGetSala(Integer salaId) {
        Juzgado sala = juzgadoService.requiredJuzgadoById(salaId);
        if (sala.getEstado() != Estado.ACTIVE || sala.getInstanciaJuzgado() != InstanciaJuzgado.SEGUNDA_INSTANCIA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La sala debe estar activa y ser de segunda instancia.");
        }
        return sala;
    }
}
