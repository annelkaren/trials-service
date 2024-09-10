package mx.gob.pjpuebla.trials.core.sello;

import lombok.RequiredArgsConstructor;
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
import java.util.HashMap;
import java.util.Map;

@Service
@Component
@RequiredArgsConstructor
public class SelloGenerator {

    private final PersonaRepository personaRepository;
    private final AuditorAware<Jwt> auditorAware;

    public byte[] exportToPdf(Documento documento) throws JRException, FileNotFoundException {
        return JasperExportManager.exportReportToPdf(getReport(documento));
    }

    private JasperPrint getReport(Documento documento) throws FileNotFoundException, JRException {
        String codeVerificacion = "";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("expediente", documento.getExpediente());
        parameters.put("fechaHoraRecepcion", "");
        parameters.put("folio", documento.getFolio());
        // parameters.put("anexos","");
        //parameters.put("anexosList","");
        parameters.put("cadenaVerificacion", codeVerificacion);
        parameters.put("nombreEntidad", "PENDIENTE");
        parameters.put("nombreJuzgado", documento.getJuzgado().getNombre());
        parameters.put("capturista ", getCapturista());
        parameters.put("marcaAgua", "src/main/resources/jasper/escudo.png");
        parameters.put("logotipoHeder", "src/main/resources/jasper/header.jpg");
        parameters.put("TextStrig", "TextString Trabaja");

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
/*
    public String generateVerificationString(Documento documento) {
        String concatenatedString = String.join("|",
                selloModel.getJuzgado(),
                selloModel.getExpediente(),
                selloModel.getFolio(),
                selloModel.getFechaHora(),
                selloModel.getListanexos()
        );
        return Base64.encodeBytes(concatenatedString.getBytes());
    }
*/
}
