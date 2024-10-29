package mx.gob.pjpuebla.trials.workflow.sello;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenido;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenidoRepository;
import net.sf.jasperreports.engine.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Component
@RequiredArgsConstructor
public class OficioService {

    private final DocumentoContenidoRepository documentoContenidoRepository;

    @Value("classpath:jasper/oficioXD.jasper")
    private Resource oficioCarta;

    @Value("classpath:jasper/oficioXD.jasper")
    private Resource oficioOficio;

    public byte[] getOficio(Integer oficioId) throws JRException, IOException {
        DocumentoContenido documentoContenido = documentoContenidoRepository.findByDocumentoId(oficioId)
                .orElseThrow(() -> new NotFoundException("Oficio no encontrado", oficioId.toString()));
        Resource oficio = documentoContenido.getTamanioPapel() == 'o' ? oficioOficio : oficioCarta;
        return JasperExportManager.exportReportToPdf(getReport(oficio, oficioId));
    }

    public JasperPrint getReport(Resource resource, Integer oficioId) throws IOException, JRException {
        List<String> heder = setHeder(2134323, oficioId);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("htmlText", "<p>Hola Oficio Trabaja</p>");  // Parámetro para el reporte

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

    public String bodyText() {
        return """ 
                <table style="border-collapse: collapse; width: 100%; height: 134.5px;" border="1"><colgroup><col style="width: 20%;"><col style="width: 20%;"><col style="width: 20%;"><col style="width: 20%;"><col style="width: 20%;"></colgroup>
                    <tbody>
                    <tr style="height: 34.375px;">
                    <td style="text-align: center;"><span style="background-color: #f1c40f; font-family: 'comic sans ms', sans-serif;">TITULO 1</span></td>
                    <td style="text-align: center;"><span style="background-color: #f1c40f; font-family: 'comic sans ms', sans-serif;">TITULO2 </span></td>
                    <td style="text-align: center;"><span style="background-color: #f1c40f; font-family: 'comic sans ms', sans-serif;">TITULO 3</span></td>
                    <td style="text-align: center;"><span style="background-color: #f1c40f; font-family: 'comic sans ms', sans-serif;">TITULO 4</span></td>
                    <td style="text-align: center;"><span style="background-color: #f1c40f; font-family: 'comic sans ms', sans-serif;">TITULO 5</span></td>
                    </tr>
                    <tr style="height: 33.375px;">
                    <td style="background-color: #ba372a; border-color: #169179;"> </td>
                    <td> </td>
                    <td> </td>
                    <td> </td>
                    <td> </td>
                    </tr>
                    <tr style="height: 33.375px;">
                    <td> </td>
                    <td colspan="3"> </td>
                    <td> </td>
                    </tr>
                    <tr style="height: 33.375px;">
                    <td> </td>
                    <td> </td>
                    <td> </td>
                    <td> </td>
                    <td> </td>
                    </tr>
                    </tbody>
                    </table>
                    <p style="text-align: center;"><em><strong>TEXTO EJEMPLO</strong></em></p>
                    <p style="text-align: center;"> </p>
                    <p style="text-align: center;"> </p>
                    <p><strong>IMAGEN: <br><br><img style="display: block; margin-left: auto; margin-right: auto;" src="https://www.ngenespanol.com/wp-content/uploads/2023/06/que-es-el-grupo-local-y-cuantas-galaxias-hay-en-el-770x431.jpg" alt="GALAXIA" width="700" height="392"></strong></p>
                """;
    }

}
