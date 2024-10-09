package mx.gob.pjpuebla.trials.workflow.movimientos;

import org.springframework.stereotype.Component;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

@Component
public class MovimientoReporteGenerator {

    @Value("classpath:jasper/selloReport.jasper")
    private Resource listaSalidaOF;
    private JRBeanCollectionDataSource beanCollectionDataSource;

    public byte[] getReporteSalida(List<MovimientoSalidaRecord> movimientos) throws JRException, IOException{
        beanCollectionDataSource = new JRBeanCollectionDataSource(movimientos);

        return JasperExportManager.exportReportToPdf(getJasperReport());

    }

    private JasperPrint getJasperReport() throws JRException, IOException{
        return JasperFillManager.fillReport(
            listaSalidaOF.getInputStream(),
                null,
                beanCollectionDataSource);
    }

}
