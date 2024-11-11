package mx.gob.pjpuebla.trials.workflow.sello;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.lowagie.text.*;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.workflow.documentos.DigitalizacionService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;

@Slf4j
@Service
@Component
@RequiredArgsConstructor
public class OficioService {

    private final DigitalizacionService digitalizacionService;

    public byte[] getOficio(Integer oficioId) throws JRException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.LETTER, 36.0F, 36.0F, 120.0F, 36.0F);
        PdfWriter pdf = PdfWriter.getInstance(document, baos);
        pdf.setPageEvent(new PdfPageEventHelper() {
//            @Override
//            public void onStartPage(PdfWriter writer, Document document) {
//                try {
//                    Image image = Image.getInstanceFromClasspath("jasper/header.jpg");
//                    image.setAlignment(Element.ALIGN_RIGHT);
//                    image.setAbsolutePosition(10, 20);
//                    image.scalePercent(25f);//25%
//                    writer.getDirectContent().addImage(image, true);
//                } catch (IOException e) {
//                    log.error(e.getMessage(), e);
//                }
//                //ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, new Phrase(""), 20, 800, 0);
//            }

            @Override
            public void onEndPage(PdfWriter writer, Document document) {
                try {
                    Image image = Image.getInstanceFromClasspath("jasper/header.jpg");
                    image.setAlignment(Element.ALIGN_RIGHT);
                    image.setAbsolutePosition(document.leftMargin(), writer.getPageSize().getTop(document.topMargin())+10);
                    image.scaleAbsolute(280f, 76f);
                    document.add(image);
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
//                ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, new Phrase(""), 20, 800, 0);
            }
        });

        document.open();
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode("2134323", BarcodeFormat.QR_CODE, 68, 68);
            ByteArrayOutputStream qrbaos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", qrbaos);
            Image qrcode = Image.getInstance(qrbaos.toByteArray());
            qrcode.setAbsolutePosition(document.right()-68, pdf.getPageSize().getTop(document.topMargin())+10);
            document.add(qrcode);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
        HTMLWorker htmlWorker = new HTMLWorker(document);
        htmlWorker.parse(new StringReader(bodyText()));
        document.close();
        return baos.toByteArray();
    }

    public String bodyText() {
        return """ 
                    <h1>TEXTO EJEMPLO</h1>
                    Lorem ipsum odor amet, consectetuer adipiscing elit. Etiam senectus tellus justo aptent senectus sem ut habitant eros. Torquent massa mauris odio risus vel sodales volutpat laoreet diam. Per bibendum est lectus velit adipiscing. Tristique dui ullamcorper habitant primis interdum interdum semper diam. Pulvinar eget nam maecenas vehicula ridiculus. Dignissim posuere potenti integer ligula quisque condimentum sollicitudin.
                    <p>
                    Scelerisque ultricies rutrum est himenaeos adipiscing nascetur. Imperdiet per class sem tellus, senectus mollis placerat habitant. Interdum congue diam nibh congue habitasse elementum iaculis. Ac consequat netus nec etiam est. Himenaeos senectus id lacus ligula condimentum, eget ullamcorper tellus finibus. Mi feugiat erat tellus est leo augue molestie molestie. Venenatis fermentum laoreet aenean purus at.
                    <p>
                
                    Felis suspendisse potenti massa commodo congue arcu. Mauris felis ante cras habitant placerat natoque pharetra semper. Pulvinar duis primis euismod litora fermentum pulvinar vivamus. Facilisis torquent faucibus porttitor; volutpat dapibus montes et maecenas. Accumsan et vivamus fames eleifend lectus cursus luctus maecenas. Laoreet neque torquent maximus praesent congue sodales pellentesque eros. Interdum eros suspendisse ultrices tortor tempor.
                    <p>
                
                    Dolor euismod conubia phasellus ultricies aenean eu tristique. Morbi nisi conubia curabitur ultricies aenean cubilia. Purus curabitur tempor et posuere inceptos nostra. Placerat mi mollis praesent fringilla montes maecenas neque volutpat id. Nullam ex sed consectetur erat inceptos habitasse mi. Phasellus nisl quam diam; suspendisse nisi rhoncus. Eros venenatis vehicula vehicula vestibulum dolor elit molestie. Malesuada cursus praesent id blandit imperdiet egestas et leo feugiat. Tortor duis arcu auctor vehicula; condimentum cras nostra parturient.
                    <p>
                
                    Tortor nulla enim ultrices orci; inceptos lacus semper. Ullamcorper nibh montes maecenas lacinia morbi rhoncus purus. Platea odio luctus nibh proin; dolor ligula euismod lacinia. Maecenas consequat potenti maximus nibh euismod eget mollis diam dui. Auctor pretium lorem eget ac hac arcu fusce? Nisi lacus vehicula efficitur; maecenas efficitur urna. Felis ut fames per vestibulum sollicitudin quisque montes. Proin curae lacinia augue consectetur ad bibendum dictumst eleifend.
                    <p>
                <table style="border-collapse: collapse; width: 100%; height: 134.5px;" border="1">
                <colgroup><col style="width: 20%;"><col style="width: 20%;"><col style="width: 20%;"><col style="width: 20%;"><col style="width: 20%;"></colgroup>
                    <tbody>
                    <tr style="height: 34.375px;">
                    <td style="text-align: center;"><span style="background-color: #f1c40f; font-family: 'comic sans ms', sans-serif;">TITULO 1</span></td>
                    <td style="text-align: center;"><span style="background-color: #f1c40f; font-family: 'comic sans ms', sans-serif;">TITULO 2</span></td>
                    <td style="text-align: center;"><span style="background-color: #f1c40f; font-family: 'comic sans ms', sans-serif;">TITULO 3</span></td>
                    <td style="text-align: center;"><span style="background-color: #f1c40f; font-family: 'comic sans ms', sans-serif;">TITULO 4</span></td>
                    <td style="text-align: center;"><span style="background-color: #f1c40f; font-family: 'comic sans ms', sans-serif;">TITULO 5</span></td>
                    </tr>
                    <tr style="height: 33.375px;">
                    <td style="background-color: #ba372a; border-color: #169179;"> </td>
                    <td>x </td>
                    <td>x </td>
                    <td>x </td>
                    <td>x </td>
                    </tr>
                    <tr style="height: 33.375px;">
                    <td>y </td>
                    <td colspan="3">y </td>
                    <td>y </td>
                    </tr>
                    <tr style="height: 33.375px;">
                    <td>z </td>
                    <td>z </td>
                    <td> z</td>
                    <td>z </td>
                    <td>z </td>
                    </tr>
                    </tbody>
                </table>
                
                    Tortor porttitor montes eu euismod cursus varius. Curae id taciti lacus luctus eleifend accumsan ipsum luctus. Congue habitasse dignissim aptent et suscipit. Efficitur laoreet magna metus finibus eget adipiscing semper. Sapien suspendisse lobortis ac sagittis mattis libero conubia dapibus. Malesuada arcu class ultricies congue senectus mus facilisi. Tellus ultrices duis; quisque mattis adipiscing proin. Taciti auctor ligula eget massa at tellus.
                    <p>
                
                    Purus proin blandit ex egestas facilisis tempor. Libero amet et nisl; aptent luctus curae vel. Viverra vel et vel arcu ex feugiat porta turpis rhoncus. Vitae taciti litora ad a luctus, laoreet ac cubilia. Volutpat laoreet posuere purus blandit, lectus tempus habitant fringilla. Efficitur accumsan pretium inceptos ornare torquent litora maximus ullamcorper dis. Sagittis bibendum eget enim metus donec, natoque eu. Nunc mus nulla ornare sit aliquet? Ultrices accumsan porttitor aenean aptent sollicitudin nascetur posuere. Habitant consequat nullam inceptos amet pretium eget tempor eleifend ligula.
                    <p>
                
                    Ac tincidunt cubilia cursus mollis imperdiet maximus mauris curae hendrerit. Semper nam suspendisse lacinia dui egestas. Cubilia nibh ornare bibendum et mus ex laoreet quam semper. Diam neque vestibulum himenaeos fusce tempor ante mi. Lacinia ac ultrices metus sociosqu sollicitudin pharetra pellentesque sociosqu. Molestie parturient justo lobortis ultricies blandit. Proin tempus ornare commodo consectetur cras nisi euismod. Cubilia netus tortor porttitor conubia ad lobortis consectetur nostra. Eget accumsan mattis convallis viverra aliquam vehicula.
                    <p>
                
                    Nisi vitae malesuada eleifend himenaeos, euismod ornare. Maecenas facilisi dapibus euismod bibendum fringilla a; fusce neque blandit. Porttitor at urna senectus natoque nisi. Diam pretium pellentesque vestibulum ut sed cubilia mauris. Et sollicitudin risus mauris cras curae, commodo vivamus. Inceptos elementum magna sem litora venenatis, platea primis. Tortor himenaeos rutrum netus finibus a sollicitudin nunc. Habitasse commodo felis; metus inceptos nec placerat sed maximus. Nisl pretium quam semper purus fusce, morbi ultricies.
                    <p>
                
                    Vel a in vitae, adipiscing pharetra id et. Nec per ex pellentesque scelerisque in. Condimentum habitant magna augue; commodo felis at. Quisque quam elit sit lorem condimentum mattis dis. Parturient pharetra tincidunt lacinia praesent lacinia id sollicitudin. Tincidunt elit lobortis natoque penatibus finibus netus habitasse. Suscipit ipsum porta ante tellus ullamcorper justo etiam. Diam montes gravida at nec gravida, tempus urna maximus.
                    <p><strong>IMAGEN: <br><br><img style="display: block; margin-left: auto; margin-right: auto;" src="https://www.ngenespanol.com/wp-content/uploads/2023/06/que-es-el-grupo-local-y-cuantas-galaxias-hay-en-el-770x431.jpg" alt="GALAXIA" width="700" height="392"></strong></p>
                """;
    }

}
