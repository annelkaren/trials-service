package mx.gob.pjpuebla.trials.workflow.sello;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
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

    public byte[] getOficio(Integer oficioId) throws JRException, IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.LETTER);
        PdfWriter pdf = PdfWriter.getInstance(document, baos);
        document.open();
        Image jpg = Image.getInstanceFromClasspath("jasper/header.jpg");
        jpg.scalePercent(25f);//25%
        document.add(jpg);

        HTMLWorker htmlWorker = new HTMLWorker(document);

        String string = """ 
                //TODO. Obtener cuerpo del oficio de base de datos
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


        htmlWorker.parse(new StringReader(string));
        document.close();
        return baos.toByteArray();
    }



}
