package mx.gob.pjpuebla.trials.core.sello;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.anexos.Anexo;
import mx.gob.pjpuebla.trials.core.documentos.Documento;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaRepository;
import net.sf.jasperreports.engine.*;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Base64;
import java.util.stream.Collectors;

@Service
@Component
@RequiredArgsConstructor
public class SelloGenerator {

    private final PersonaRepository personaRepository;
    private final AuditorAware<Jwt> auditorAware;

    public byte[] exportToPdf(Documento documento,  List<Anexo> listAnexo) throws JRException, FileNotFoundException {
        return JasperExportManager.exportReportToPdf(getReport(documento, listAnexo));
    }

    private JasperPrint getReport(Documento documento,  List<Anexo>  listAnexo) throws FileNotFoundException, JRException {
        String codeVerificacion = generateVerificationString(documento, listAnexo);

        List<String> listAnexoStrings = listAnexo.stream()
                .map(Anexo::getNombre)
                .toList();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("expediente", documento.getExpediente());
        parameters.put("fechaHoraRecepcion", "");
        parameters.put("folio", documento.getFolio());
        parameters.put("anexos", listAnexoStrings);
        parameters.put("cadenaVerificacion", codeVerificacion);
        parameters.put("nombreEntidad", "PENDIENTE");
        parameters.put("nombreJuzgado", documento.getJuzgado().getNombre());
        parameters.put("capturista ", getCapturista());
        parameters.put("marcaAgua", "src/main/resources/jasper/escudo.png");
        parameters.put("logotipoHeder", "src/main/resources/jasper/header.jpg");

        return JasperFillManager.fillReport(JasperCompileManager.compileReport(
                ResourceUtils.getFile("classpath:jasper/sello_report.jrxml")
                        .getAbsolutePath()), parameters, new JREmptyDataSource());
    }

    private String getCapturista() {
        String usuario = auditorAware.getCurrentAuditor().get().getSubject();
        Persona persona = personaRepository.findByUsuario(usuario);
        String apellidoMaterno = persona.getApellidoMaterno();
        apellidoMaterno = (apellidoMaterno != null && !apellidoMaterno.isEmpty()) ? String.valueOf(apellidoMaterno.charAt(0)) : "";
        return persona.getNombre().charAt(0) + "" + persona.getApellidoPaterno().charAt(0) + apellidoMaterno;
    }

    public String generateVerificationString(Documento documento,List<Anexo>  listAnexo ) {
        LocalDateTime fechaActual = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String fechaActualString = fechaActual.format(formatter);

        String listaNombres = listAnexo.stream()
                .map(Anexo::getNombre)
                .collect(Collectors.joining(","));

        String concatenatedString = String.join("|",
                documento.getJuzgado().getNombre(),
                documento.getExpediente(),
                documento.getFolio(),
                fechaActualString,
                listaNombres
        );
        return Base64.getEncoder().encodeToString(concatenatedString.getBytes());
    }

}
