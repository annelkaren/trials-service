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
import mx.gob.pjpuebla.trials.util.enums.Migrado;
import mx.gob.pjpuebla.trials.workflow.documentos.DigitalizacionService;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoService;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenido;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenidoService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class AcuerdoService {

    private final DocumentoContenidoService documentoContenidoService;
    private final DocumentoService documentoService;
    private final DigitalizacionService digitalizacionService;

    public byte[] getAcuerdoPdf(Integer documentoId) throws IOException, DocumentException {

        //evaluamos si es un documento migrado o no:
        Documento documento = documentoService.findByDocumento(documentoId);

        if(documento.getMigrado().name().equals(Migrado.SI.name())){
            return digitalizacionService.getDocumentoMigrado(documento);
        }


        // 1. Obtener el contenido del documento
        DocumentoContenido documentoContenido = documentoContenidoService.getContenidoByOficioId(documentoId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Rectangle pageSize = documentoContenido.getTamanioPapel() == 'o' ? PageSize.LEGAL : PageSize.LETTER;

        // 2. Crear documento con márgenes específicos
        Document document = new Document(pageSize, 113.386F, 65.0F, 60.0F, 36.0F);
        PdfWriter pdf = PdfWriter.getInstance(document, baos);

        // 3. Generar el código QR y la fuente UNA SOLA VEZ para reutilizarlos en cada página
        final Image qrcodeImage;
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(String.valueOf(documentoContenido.getId()),
                    BarcodeFormat.QR_CODE, 58, 58);
            ByteArrayOutputStream qrBaos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", qrBaos);
            qrcodeImage = Image.getInstance(qrBaos.toByteArray());
        } catch (WriterException e) {
            log.error("No se pudo generar el código QR. Se omitirá del PDF.", e);
            
            throw new DocumentException(e);
        }
        
        final BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);

        // 4. Configurar el evento de página para añadir contenido repetitivo
        pdf.setPageEvent(new PdfPageEventHelper() {
            @Override
            public void onEndPage(PdfWriter writer, Document document) {
                try {
                    PdfContentByte canvasOnTop = writer.getDirectContent();
                    PdfContentByte canvasUnder = writer.getDirectContentUnder();

                    // --- CONTENIDO QUE VA DETRÁS DEL TEXTO (Marcas de agua) ---
                    addBackgroundWatermarks(document, canvasUnder, baseFont);
                    
                    // --- CONTENIDO QUE VA ENCIMA (Textos de margen y QR) ---
                    addForegroundContent(document, canvasOnTop, baseFont, qrcodeImage);

                } catch (DocumentException e) {
                   
                    throw new ExceptionConverter(e);
                }
            }
        });

        // 5. Abrir el documento y procesar el contenido HTML
        document.open();
        procesarHTMLConImagenes(documentoContenido.getTexto(), document);

        // 6. Cerrar el documento para finalizar
        document.close();
        return baos.toByteArray();
    }
    
    /**
     * Añade las marcas de agua que van DETRÁS del contenido principal.
     */
    private void addBackgroundWatermarks(Document document, PdfContentByte canvas, BaseFont baseFont) {
        // Marca de agua vertical
        canvas.saveState(); // Guardar el estado gráfico actual
        PdfGState gStateVertical = new PdfGState();
        gStateVertical.setFillOpacity(0.3f);
        canvas.setGState(gStateVertical);
        canvas.beginText();
        canvas.setFontAndSize(baseFont, 18);
        canvas.showTextAligned(Element.ALIGN_CENTER, "SISTEMA ELECTRÓNICO DE CONTROL Y GESTIÓN JUDICIAL", document.left() - 22, (document.bottom() + document.top()) / 2, 90);
        canvas.endText();
        canvas.restoreState(); // CORREGIDO: Restaurar el estado gráfico

        // Marca de agua central
        canvas.saveState(); // Guardar el estado gráfico actual
        PdfGState gStateCenter = new PdfGState();
        gStateCenter.setFillOpacity(0.1f);
        canvas.setGState(gStateCenter);
        canvas.beginText();
        canvas.setFontAndSize(baseFont, 60);
        float centerX = (document.left() + document.right()) / 2;
        float centerY = (document.top() + document.bottom()) / 2;
        canvas.showTextAligned(Element.ALIGN_CENTER, "Poder Judicial", centerX, centerY + 80, 0);
        canvas.showTextAligned(Element.ALIGN_CENTER, "del", centerX, centerY, 0);
        canvas.showTextAligned(Element.ALIGN_CENTER, "Estado de Puebla", centerX, centerY - 80, 0);
        canvas.endText();
        canvas.restoreState(); 
    }

    /**
     * Añade los elementos que van ENCIMA del contenido, como textos en los márgenes y el QR.
     */
    private void addForegroundContent(Document document, PdfContentByte canvas, BaseFont baseFont, Image qrcode) throws DocumentException {
        // Texto rotado en el margen derecho
        canvas.beginText();
        canvas.setFontAndSize(baseFont, 18); 
        canvas.showTextAligned(Element.ALIGN_CENTER, "El suscrito, Secretario, informa que el texto que precede corresponde a la ", document.right() + 32, document.top() - 280, 270);
        canvas.showTextAligned(Element.ALIGN_CENTER, "resolución emitida, conforme a la certificación que obra en el expediente", document.right() + 10, document.top() - 280, 270);
        canvas.endText();

        // Código QR en la esquina inferior derecha
        if (qrcode != null) {
            qrcode.setAbsolutePosition(document.right() - 6, document.bottom() + 15);
            canvas.addImage(qrcode);
        }
    }

    /**
     * Procesa una cadena HTML, separando el texto de las imágenes para renderizarlos correctamente.
     * Este método es más robusto que un simple split.
     */
    public void procesarHTMLConImagenes(String html, Document document) throws IOException, DocumentException {
        Pattern pattern = Pattern.compile("(<img[^>]+>)", Pattern.CASE_INSENSITIVE);
        String[] partes = pattern.split(html);
        Matcher matcher = pattern.matcher(html);

        int parteIndex = 0;
        while (matcher.find()) {
            if (parteIndex < partes.length && !partes[parteIndex].trim().isEmpty()) {
                procesarHTML(partes[parteIndex], document);
            }
            parteIndex++;
            
            String imgTag = matcher.group(1);
            procesarImagenDesdeTag(imgTag, document);
        }
        
        if (parteIndex < partes.length && !partes[parteIndex].trim().isEmpty()) {
            procesarHTML(partes[parteIndex], document);
        }
    }
    
    /**
     * Parsea un fragmento de HTML (sin imágenes) y lo añade al documento.
     */
    private void procesarHTML(String htmlFragment, Document document) throws DocumentException, IOException {
        HTMLWorker htmlWorker = new HTMLWorker(document);
        htmlWorker.parse(new StringReader(htmlFragment));
    }
    
    /**
     * Extrae la URL de una etiqueta <img> y la añade al documento.
     */
    private void procesarImagenDesdeTag(String imgTag, Document document) {
        String imageUrl = extraerImagenUrl(imgTag);
        if (imageUrl != null) {
            try {
                Image image = Image.getInstance(imageUrl);
                image.scaleToFit(350, 350);
                image.setAlignment(Element.ALIGN_LEFT);
                document.add(image);
            } catch (Exception e) {
                log.error("Error al agregar imagen desde URL [{}]: {}", imageUrl, e.getMessage(), e);
            }
        }
    }

    /**
     * Extrae el valor del atributo src de una etiqueta de imagen.
     */
    private String extraerImagenUrl(String imgTag) {
        Pattern srcPattern = Pattern.compile("src=\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);
        Matcher srcMatcher = srcPattern.matcher(imgTag);
        if (srcMatcher.find()) {
            return srcMatcher.group(1);
        }
        return null;
    }

}