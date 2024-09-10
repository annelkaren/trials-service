package mx.gob.pjpuebla.trials.core.sello;

import com.lowagie.text.pdf.codec.Base64;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.documentos.Documento;
import net.sf.jasperreports.engine.*;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Component
@RequiredArgsConstructor
public class SelloGenerator {

    private final AuditorAware<Jwt> auditorAware;

    public byte[] exportToPdf(Documento documento) throws JRException, FileNotFoundException {
        return JasperExportManager.exportReportToPdf(getReport(documento));
    }

    private JasperPrint getReport(Documento documento)  throws FileNotFoundException, JRException {
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
        parameters.put("capturista ", auditorAware.getCurrentAuditor().get().getId());
        parameters.put("marcaAgua", "src/main/resources/LogoEscudo1.png");
        parameters.put("logotipoHeder", "src/main/resources/LogotipoHeder.jpg");
        parameters.put("TextStrig", "TextString Trabaja");

        return JasperFillManager.fillReport(JasperCompileManager.compileReport(
                ResourceUtils.getFile("classpath:SelloReport.jrxml")
                        .getAbsolutePath()), parameters , new JREmptyDataSource());
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
