package mx.gob.pjpuebla.trials.workflow.sello;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@Component
@RequiredArgsConstructor
public class OficioService {

    @Value("classpath:jasper/OficioCarta.jasper")
    private Resource oficioCarta;

    @Value("classpath:jasper/OficioOficio.jasper")
    private Resource oficioOficio;

    public byte[] getOficio(boolean formato, Integer oficioId) throws JRException, IOException {
        Resource oficio = formato ? oficioOficio : oficioCarta;
        return JasperExportManager.exportReportToPdf(getReport(oficio, oficioId));
    }


    private JasperPrint getReport(Resource resource, Integer oficioId) throws IOException, JRException {
        List<String> heder = setHeder(2134323, oficioId);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("hederLogo", "jasper/header.jpg");
        parameters.put("noFolio", heder.get(1));
        parameters.put("noCodigo", heder.get(0));
        parameters.put("noQR", "34729");
        parameters.put("textHtml", bodyText());

        return JasperFillManager.fillReport(
                resource.getInputStream(),
                parameters,
                new JREmptyDataSource());
    }

    public List<String> setHeder(Integer code, Integer noOficio) {
        return Arrays.asList(
                "<b>" + code + "</b>",
                "<b>No. Oficio: " + noOficio + "</b>"
        );
    }

    public String bodyText(){
        return  """
                <h1>El agujero aplastante</h1>
                <p style="line-height: 1.5;" >Por Chris Mills</p>
                <h2>Capítulo 1: La oscura noche</h2>
                <p>
                  Era una noche oscura. En algún lugar, un búho ululó. La lluvia azotó el ...
                </p>
                <h2>Capítulo 2: El silencio eterno</h2>
                <p>Nuestro protagonista ni susurrar pudo al ver esa sombría figura ...</p>
                <h3>El espectro habla</h3>
                <p>
                  Habían pasado varias horas más, cuando de repente el espectro se incorporó y
                  exclamó: "¡Por favor, ten piedad de mi alma!"
                </p>
                """;
    }

}
