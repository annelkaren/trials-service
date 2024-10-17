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

    public byte[] getOficio(boolean formato) throws JRException, IOException {
        System.out.println("Está entrando en getOficio. Formato: " + formato);
        Resource oficio = formato ? oficioOficio : oficioCarta;
        return JasperExportManager.exportReportToPdf(getReport(oficio));
    }


    private JasperPrint getReport(Resource resource) throws IOException, JRException {
        List<String> heder = setHeder(2134323, 21354254);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("hederLogo", "jasper/header.jpg");
        parameters.put("noFolio", heder.get(1));
        parameters.put("noCodigo", heder.get(0));
        parameters.put("noQR", "mlkmlkm");
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

        return
                "    <h1>Oficio No. 123/2024</h1>\n" +
                "    <p><b>Fecha:</b> 17 de octubre de 2024</p>\n" +
                "    <p><b>De:</b> Dirección General</p>\n" +
                "    <p><b>Para:</b> Nombre del Destinatario</p>\n" +
                "    <p><b>Asunto:</b> Solicitud de Información</p>\n" +
                "\n" +
                "    <p>Estimado/a <b>Nombre del Destinatario:</b></p>\n" +
                "\n" +
                "    <p>Por medio de la presente, me permito solicitar la información necesaria sobre el estado actual de los proyectos en curso, a fin de poder realizar un seguimiento adecuado de los mismos.</p>\n" +
                "\n" +
                "    <p>Agradezco de antemano su atención a esta solicitud y quedo a la espera de su pronta respuesta.</p>\n" +
                "\n" +
                "    <p>Atentamente,</p>\n" +
                "    <p><b>Nombre del Remitente</b><br>\n" +
                "    Cargo<br>\n" +
                "    Dirección General</p>\n";
    }

}
