package mx.gob.pjpuebla.trials.workflow.sello;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.lowagie.text.*;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.pdf.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.workflow.documentos.DigitalizacionService;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenido;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenidoService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

@Slf4j
@Service
@Component
@RequiredArgsConstructor
public class AcuerdoService {

    private final DigitalizacionService digitalizacionService;
    private final DocumentoContenidoService documentoContenidoService;

    public byte[] getAcuerdoPdf(Integer documentoId) throws JRException, IOException, WriterException {
        DocumentoContenido documentoContenido = documentoContenidoService.getContenidoByOficioId(documentoId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.LETTER, 36.0F, 65.0F, 60.0F, 36.0F);
        PdfWriter pdf = PdfWriter.getInstance(document, baos);

        pdf.setPageEvent(new PdfPageEventHelper() {
            @Override
            public void onEndPage(PdfWriter writer, Document document) {
                try {
                    PdfContentByte canvas = writer.getDirectContentUnder();
                    BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);

                    PdfGState gState = new PdfGState();
                    gState.setFillOpacity(0.3f);
                    canvas.setGState(gState);


                    String watermarkText = "SISTEMA ELECTRÓNICO DE CONTROL Y GESTIÓN JUDICIAL";
                    canvas.beginText();
                    canvas.setFontAndSize(baseFont, 18);
                    float x1 = document.left() - 18;
                    float y1 = (document.bottom() + document.top()) / 2;
                    canvas.showTextAligned(Element.ALIGN_CENTER, watermarkText, x1, y1, 90);
                    canvas.endText();

                    String watermarkText2 = "Poder Judicial" ;
                    String watermarkText22=  "del" ;
                    String watermarkText23= "Estado de Puebla";
                    canvas.beginText();
                    canvas.setFontAndSize(baseFont, 60);
                    float x2 = (document.left() + document.right()) / 2;
                    float y2 = (document.top() + document.bottom()) / 2 + 80;
                    canvas.showTextAligned(Element.ALIGN_CENTER, watermarkText2, x2, y2, 0);
                    canvas.endText();

                    canvas.beginText();
                    canvas.setFontAndSize(baseFont, 60);
                    float x22 = (document.left() + document.right()) / 2;
                    float y22 = (document.top() + document.bottom()) / 2;
                    canvas.showTextAligned(Element.ALIGN_CENTER, watermarkText22, x22, y22, 0);
                    canvas.endText();

                    canvas.beginText();
                    canvas.setFontAndSize(baseFont, 60);
                    float x23 = (document.left() + document.right()) / 2;
                    float y23 = (document.top() + document.bottom()) / 2 - 80;
                    canvas.showTextAligned(Element.ALIGN_CENTER, watermarkText23, x23, y23, 0);
                    canvas.endText();

                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        });

        document.open();

        BitMatrix bitMatrix;
        try {

            PdfContentByte canvas =  pdf.getDirectContent();
            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);

            String watermarkText3 = "El suscrito, Secretario, informa que el texto que precede corresponde a la ";
            String watermarkText4 = "resolución emitida, conforme a la certificación que obra en el expediente";

            canvas.beginText();
            canvas.setFontAndSize(baseFont, 18);
            float x3 = document.right() + 22;
            float y3 = (document.top() - 280);
            canvas.showTextAligned(Element.ALIGN_CENTER, watermarkText3, x3, y3, 270);
            canvas.endText();

            canvas.beginText();
            canvas.setFontAndSize(baseFont, 18);
            float x4 = document.right() + 3;
            float y4 = (document.top() - 280);
            canvas.showTextAligned(Element.ALIGN_CENTER, watermarkText4, x4, y4, 270);
            canvas.endText();

            bitMatrix = new MultiFormatWriter().encode(String.valueOf(documentoContenido.getId()), BarcodeFormat.QR_CODE, 58, 58);
            ByteArrayOutputStream qrbaos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", qrbaos);
            Image qrcode = Image.getInstance(qrbaos.toByteArray());

            float x = document.right() - 6;
            float y = document.bottom() + 15;

            qrcode.setAbsolutePosition(x, y);

            document.add(qrcode);

        } catch (WriterException e) {
            log.error(e.getMessage(), e);
        }

        procesarHTMLConImagenes(documentoContenido.getTexto(), document);

        document.close();
        return baos.toByteArray();
    }


    private void procesarHTMLConImagenes(String html, Document document) throws IOException, DocumentException {
        String[] partes = html.split("<img");

        for (int i = 0; i < partes.length; i++) {
            String textoLimpio = limpiarHTMLAntesDeImagen(partes[i]);

            if (!textoLimpio.isEmpty()) {
                procesarHTML(textoLimpio, document);
            }

            if (i < partes.length - 1) {
                procesarImagen(partes[i + 1], document);
            }
        }
    }

    private void procesarHTML(String htmlFragment, Document document) throws DocumentException, IOException {
        HTMLWorker htmlWorker = new HTMLWorker(document);
        htmlWorker.parse(new StringReader(htmlFragment));
    }

    private void procesarImagen(String htmlFragment, Document document) {
        String imageUrl = extraerImagenUrl(htmlFragment);

        if (imageUrl != null) {
            try {
                Image image = Image.getInstance(imageUrl);
                image.scaleToFit(350, 350);
                image.setAlignment(Element.ALIGN_LEFT);
                document.add(image);
            } catch (Exception e) {
                log.error("Error al agregar imagen: ", e);
            }
        }
    }

    private String extraerImagenUrl(String htmlFragment) {
        int srcIndex = htmlFragment.indexOf("src=\"");
        if (srcIndex == -1) {
            return null;
        }
        int start = srcIndex + 5;
        int end = htmlFragment.indexOf("\"", start);
        if (end == -1) {
            return null;
        }
        return htmlFragment.substring(start, end);
    }

    private String limpiarHTMLAntesDeImagen(String htmlParte) {
        htmlParte = htmlParte.replaceAll("src[^>]*>", "");
        return htmlParte;
    }

}
