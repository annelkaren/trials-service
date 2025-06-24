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
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenido;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenidoService;
import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.io.Resource;

@Slf4j
@Service
@Component
@RequiredArgsConstructor
public class AcuerdoService {

    @Value("classpath:jasper/AcuerdoSentencia.jrxml")
    private Resource sentenciaJrxml;

    private final DocumentoContenidoService documentoContenidoService;

public byte[] getAcuerdoPdf(Integer documentoId) throws IOException, JRException {
    try (InputStream jrxmlStream = sentenciaJrxml.getInputStream()) {

        DocumentoContenido documentoContenido = documentoContenidoService.getContenidoByOficioId(documentoId);

        JasperDesign design = JRXmlLoader.load(jrxmlStream);

        int backgroundHeight = 802; // fijo, por tu diseño
        int top = design.getTopMargin();       // 20
        int bottom = design.getBottomMargin(); // 20

        // Asegura que el contenido quepa
        int pageHeight = (documentoContenido.getTamanioPapel() == 'o')
            ? 1008  // Legal
            : Math.max(792, top + backgroundHeight + bottom); // Carta, pero mínimo lo necesario

        design.setPageHeight(pageHeight);

        // Asegura que el background tenga el alto correcto
        JRBand background = design.getBackground();
        if (background instanceof JRDesignBand) {
            ((JRDesignBand) background).setHeight(backgroundHeight);
        }

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("contenidoDocumento", documentoContenido.getTexto());
        parametros.put("qrText", "portal de litigantes");

        JasperReport report = JasperCompileManager.compileReport(design);
        JRDataSource dataSource = new JREmptyDataSource(1);
        JasperPrint print = JasperFillManager.fillReport(report, parametros, dataSource);

        return JasperExportManager.exportReportToPdf(print);
    }
}

    public byte[] getAcuerdoPdf2(Integer documentoId) throws IOException {
        DocumentoContenido documentoContenido = documentoContenidoService.getContenidoByOficioId(documentoId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Rectangle pageSize = documentoContenido.getTamanioPapel() == 'o' ? PageSize.LEGAL : PageSize.LETTER;

        Document document = new Document(pageSize, 65.0F, 65.0F, 60.0F, 36.0F);
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
                    float x1 = document.left() - 22;
                    float y1 = (document.bottom() + document.top()) / 2;
                    canvas.showTextAligned(Element.ALIGN_CENTER, watermarkText, x1, y1, 90);
                    canvas.endText();

                    PdfGState gState1 = new PdfGState();
                    gState1.setFillOpacity(0.1f);
                    canvas.setGState(gState1);

                    String watermarkText2 = "Poder Judicial";
                    String watermarkText22 = "del";
                    String watermarkText23 = "Estado de Puebla";
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

            PdfContentByte canvas = pdf.getDirectContent();
            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);

            String watermarkText3 = "El suscrito, Secretario, informa que el texto que precede corresponde a la ";
            String watermarkText4 = "resolución emitida, conforme a la certificación que obra en el expediente";

            canvas.beginText();
            canvas.setFontAndSize(baseFont, 18);
            float x3 = document.right() + 32;
            float y3 = (document.top() - 280);
            canvas.showTextAligned(Element.ALIGN_CENTER, watermarkText3, x3, y3, 270);
            canvas.endText();

            canvas.beginText();
            canvas.setFontAndSize(baseFont, 18);
            float x4 = document.right() + 10;
            float y4 = (document.top() - 280);
            canvas.showTextAligned(Element.ALIGN_CENTER, watermarkText4, x4, y4, 270);
            canvas.endText();

            bitMatrix = new MultiFormatWriter().encode(String.valueOf(documentoContenido.getId()),
                    BarcodeFormat.QR_CODE, 58, 58);
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

    public void procesarHTMLConImagenes(String html, Document document) throws IOException, DocumentException {
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
