package mx.gob.pjpuebla.trials.workflow.sello;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenido;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenidoRepository;
import net.sf.jasperreports.engine.JRException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Component
@RequiredArgsConstructor
public class OficioService {

    public byte[] getOficio(Integer oficioId) throws JRException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.LETTER);
        PdfWriter pdf = PdfWriter.getInstance(document, baos);
        document.open();
        Image jpg = Image.getInstanceFromClasspath("jasper/header.jpg");
        jpg.scalePercent(25f);//25%
        document.add(jpg);

        HTMLWorker htmlWorker = new HTMLWorker(document);
        htmlWorker.parse(new StringReader(string));
        document.close();
        return baos.toByteArray();
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
