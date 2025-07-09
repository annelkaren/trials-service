package mx.gob.pjpuebla.trials.workflow.listaestrados;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ListaEstradoGenerator {

    @Value("classpath:jasper/ListaEstrados.jasper")
    private Resource listaEstradosOF;
    private JRBeanCollectionDataSource beanCollectionDataSource;

    public byte[] getReporteListaEstrados(List<ListaEstradoDTO> listaEstradosDTO, String leyendaFooter) throws JRException, IOException{
        beanCollectionDataSource = new JRBeanCollectionDataSource(listaEstradosDTO);
        return JasperExportManager.exportReportToPdf(getJasperReport(leyendaFooter));
    }

    private JasperPrint getJasperReport(String leyendaFooter) throws JRException, IOException{
        Map<String, Object> parameters = new HashMap<>();

        parameters.put("logotipoHeder","jasper/header.jpg");
        parameters.put("p_image_background","jasper/escudo.png");
        parameters.put("p_leyenda_footer", leyendaFooter);

        return JasperFillManager.fillReport(
                listaEstradosOF.getInputStream(),
                parameters,
                beanCollectionDataSource);
    }

}
