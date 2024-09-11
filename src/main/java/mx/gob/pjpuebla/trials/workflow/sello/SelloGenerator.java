package mx.gob.pjpuebla.trials.workflow.sello;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.anexos.Anexo;
import mx.gob.pjpuebla.trials.core.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.core.documentos.Documento;
import mx.gob.pjpuebla.trials.core.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaRepository;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.*;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Component
@RequiredArgsConstructor
public class SelloGenerator {

    private final PersonaRepository personaRepository;
    private final AuditorAware<Jwt> auditorAware;
    private final DocumentoRepository documentoRepository;
    private final AnexoRepository anexoRepository;

    public byte[] exportToPdf(Integer id) throws JRException, FileNotFoundException {
        Documento documento = documentoRepository.findById(id).orElseThrow();
        List<Anexo> anexos = anexoRepository.findAllByDocumentoId(documento.getId());
        return JasperExportManager.exportReportToPdf(getReport(documento, anexos));
    }

    private JasperPrint getReport(Documento documento, List<Anexo> anexos) throws FileNotFoundException, JRException {
        String date = getDate(documento.getAudit().getFechaAlta());
        String verificationCode = generateVerificationCode(documento, anexos, date);

        // Cargar el reporte Jasper
        JasperReport reportStream = (JasperReport) JRLoader.loadObject(Objects.requireNonNull(getClass().getResource("/jasper/sello_report.jasper")));


        Map<String, Object> parameters = new HashMap<>();
        parameters.put("expediente", documento.getExpediente());
        parameters.put("fechaHoraRecepcion", date);
        parameters.put("folio", documento.getFolio());
        parameters.put("anexos", getStringAnexos(anexos));
        parameters.put("cadenaVerificacion", verificationCode);
        parameters.put("nombreEntidad", "PENDIENTE");
        parameters.put("nombreJuzgado", documento.getJuzgado().getNombre());
        parameters.put("capturista", getCapturista());
        parameters.put("reimpresion", isReimpresion(documento.getAudit().getUsuarioAlta(), documento.getAudit().getFechaAlta()));
        parameters.put("marcaAgua", "src/main/resources/jasper/escudo.png");
        parameters.put("logotipoHeder", "src/main/resources/jasper/header.jpg");

        // Rellenar el reporte
        return JasperFillManager.fillReport(
                reportStream,
                parameters,
                new JREmptyDataSource());
    }

    private String getCapturista() {
        Jwt jwt = auditorAware.getCurrentAuditor().orElseThrow();
        String user = jwt.getSubject();
        Persona persona = personaRepository.findByUsuario(user).orElseThrow(() -> new NotFoundException("Persona no encontrada", "usuario"));
        String apellidoMaterno = persona.getApellidoMaterno();
        apellidoMaterno = (apellidoMaterno != null && !apellidoMaterno.isEmpty()) ? String.valueOf(apellidoMaterno.charAt(0)) : "";
        return persona.getNombre().charAt(0) + "" + persona.getApellidoPaterno().charAt(0) + apellidoMaterno;
    }

    private String getDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return date.format(formatter);
    }

    private boolean isReimpresion(String auditor, LocalDateTime date) {
        boolean isSameAuditor;
        boolean isSameDate;
        LocalDate today = LocalDate.now();
        Jwt jwt = auditorAware.getCurrentAuditor().orElseThrow();
        String currentUser = jwt.getSubject();
        isSameAuditor = (auditor.equals(currentUser));
        isSameDate = (date.toLocalDate().equals(today));
        return !(isSameAuditor && isSameDate);
    }

    public String generateVerificationCode(Documento documento, List<Anexo> anexos, String date) {
        String verificationStringCode = String.join("|",
                //documento.getJuzgado().getNombre(),
                documento.getExpediente(),
                documento.getFolio(),
                date,
                getAnexos(anexos)
        );
        return Base64.getEncoder().encodeToString(verificationStringCode.getBytes());
    }

    private String getAnexos(List<Anexo> anexos) {
        return anexos.stream()
                .map(Anexo::getNombre)
                .collect(Collectors.joining(","));
    }

    private String getStringAnexos(List<Anexo> anexos) {
        List<String> list = anexos.stream()
                .map(Anexo::getNombre)
                .map(nombre -> "- " + nombre + " <br/>")
                .toList();
        return String.join("", list);
    }

}
