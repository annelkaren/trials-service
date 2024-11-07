package mx.gob.pjpuebla.trials.workflow.sello;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenido;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenidoService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

@Service
@Component
@RequiredArgsConstructor
public class OficioService {

//    @Value("classpath:jasper/oficioXD.jasper")
//    private Resource oficioCarta;
//
//    @Value("classpath:jasper/oficioXD.jasper")
//    private Resource oficioOficio;
    private final DocumentoContenidoService documentoContenidoService;

    public byte[] getOficio(Integer oficioId) throws JRException, IOException {
        DocumentoContenido documentoContenido = documentoContenidoService.getContenidoByOficioId(oficioId);
        Rectangle pageSize = documentoContenido.getTamanioPapel() == 'o' ? PageSize.LEGAL : PageSize.LETTER;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(pageSize);
        PdfWriter pdf = PdfWriter.getInstance(document, baos);
        document.open();
        Image jpg = Image.getInstanceFromClasspath("jasper/header.jpg");
        jpg.scalePercent(25f);//25%
        document.add(jpg);

        HTMLWorker htmlWorker = new HTMLWorker(document);

        String string = """ 
                //TODO. Eliminar este texto solo es de ejemplo
                <table style="border-collapse: collapse; width: 100%; height: 134.5px;" border="1"><colgroup><col style="width: 20%;"><col style="width: 20%;"><col style="width: 20%;"><col style="width: 20%;"><col style="width: 20%;"></colgroup>
                <tbody>
                <tr style="height: 34.375px;">
                <td style="text-align: center;"><span style="background-color: #f1c40f; font-family: 'comic sans ms', sans-serif;">TITULO 1</span></td>
                <td style="text-align: center;"><span style="background-color: #f1c40f; font-family: 'comic sans ms', sans-serif;">TITULO2&nbsp;</span></td>
                <td style="text-align: center;"><span style="background-color: #f1c40f; font-family: 'comic sans ms', sans-serif;">TITULO 3</span></td>
                <td style="text-align: center;"><span style="background-color: #f1c40f; font-family: 'comic sans ms', sans-serif;">TITULO 4</span></td>
                <td style="text-align: center;"><span style="background-color: #f1c40f; font-family: 'comic sans ms', sans-serif;">TITULO 5</span></td>
                </tr>
                <tr style="height: 33.375px;">
                <td style="background-color: #ba372a; border-color: #169179;">&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                </tr>
                <tr style="height: 33.375px;">
                <td>&nbsp;</td>
                <td colspan="3">&nbsp;</td>
                <td>&nbsp;</td>
                </tr>
                <tr style="height: 33.375px;">
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                </tr>
                </tbody>
                </table>
                <p style="text-align: center;"><em><strong>TEXTO EJEMPLO</strong></em></p>
                <p style="text-align: center;">&nbsp;</p>
                <p style="text-align: center;">&nbsp;</p>
                """;

        htmlWorker.parse(new StringReader(documentoContenido.getTexto()));
        document.close();
        return baos.toByteArray();
    }
}
