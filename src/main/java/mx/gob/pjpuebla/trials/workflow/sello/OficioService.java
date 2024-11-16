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
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Component
@RequiredArgsConstructor
public class OficioService {

    private final DigitalizacionService digitalizacionService;
    private final DocumentoContenidoService documentoContenidoService;

    public byte[] getOficio(Integer oficioId) throws JRException, IOException, WriterException {
        DocumentoContenido documentoContenido = documentoContenidoService.getContenidoByOficioId(oficioId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.LETTER, 36.0F, 36.0F, 120.0F, 36.0F);
        PdfWriter pdf = PdfWriter.getInstance(document, baos);

        pdf.setPageEvent(new PdfPageEventHelper() {
            @Override
            public void onEndPage(PdfWriter writer, Document document) {
                try {
                    Image image = Image.getInstanceFromClasspath("jasper/header.jpg");
                    image.setAlignment(Element.ALIGN_RIGHT);
                    image.setAbsolutePosition(document.leftMargin(), writer.getPageSize().getTop(document.topMargin()) + 10);
                    image.scaleAbsolute(280f, 76f);
                    document.add(image);

                    PdfContentByte canvas = writer.getDirectContentUnder();
                    BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);

                    String watermarkText = "SISTEMA ELECTRÓNICO DE CONTROL Y GESTIÓN JUDICIAL";
                    PdfGState gState = new PdfGState();
                    gState.setFillOpacity(0.3f);
                    canvas.setGState(gState);

                    canvas.beginText();
                    canvas.setFontAndSize(baseFont, 20);
                    float x = document.left() - 10;
                    float y = (document.bottom() + document.top()) / 2;
                    canvas.showTextAligned(Element.ALIGN_CENTER, watermarkText, x, y, 90);
                    canvas.endText();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        });

        document.open();

        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(String.valueOf(documentoContenido.getId()), BarcodeFormat.QR_CODE, 68, 68);
            ByteArrayOutputStream qrbaos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", qrbaos);
            Image qrcode = Image.getInstance(qrbaos.toByteArray());
            qrcode.setAbsolutePosition(document.right() - 68, pdf.getPageSize().getTop(document.topMargin()) + 10);
            document.add(qrcode);

            PdfContentByte canvas = pdf.getDirectContent();
            BaseFont font = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
            canvas.beginText();
            canvas.setFontAndSize(font, 10);
            canvas.setTextMatrix(document.right() - 150, pdf.getPageSize().getTop(document.topMargin()) + 50);
            canvas.showText(String.valueOf(documentoContenido.getId()));
            canvas.setTextMatrix(document.right() - 150, pdf.getPageSize().getTop(document.topMargin()) + 35);
            canvas.showText("No. Oficio: " + documentoContenido.getId());
            canvas.endText();
        } catch (WriterException e) {
            log.error(e.getMessage(), e);
        }

        List<String> imagenes = extraerImagenes(documentoContenido.getTexto());
        String textoLimpio = limpiarHTML(documentoContenido.getTexto());

        HTMLWorker htmlWorker = new HTMLWorker(document);
        htmlWorker.parse(new StringReader(textoLimpio));

        for (String imageUrl : imagenes) {
            try {
                Image image = Image.getInstance(imageUrl);
                image.scaleToFit(350, 350);
                image.setAlignment(Element.ALIGN_LEFT);
                document.add(image);
            } catch (Exception e) {
                log.error("Error al agregar imagen: ", e);
            }
        }

        document.close();
        return baos.toByteArray();
    }


    private List<String> extraerImagenes(String html) {
        List<String> imageUrls = new ArrayList<>();
        String[] partes = html.split("<img");

        for (int i = 1; i < partes.length; i++) {
            int srcIndex = partes[i].indexOf("src=\"");
            if (srcIndex != -1) {
                int start = srcIndex + 5;
                int end = partes[i].indexOf("\"", start);
                if (end != -1) {
                    String imageUrl = partes[i].substring(start, end);
                    imageUrls.add(imageUrl);
                }
            }
        }
        return imageUrls;
    }

    private String limpiarHTML(String html) {
        return html.replaceAll("<img[^>]*>", "");
    }


}
