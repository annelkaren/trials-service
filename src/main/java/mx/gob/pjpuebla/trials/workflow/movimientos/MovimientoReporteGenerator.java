package mx.gob.pjpuebla.trials.workflow.movimientos;

import mx.gob.pjpuebla.trials.workflow.transferencias.Transferencia;
import org.springframework.stereotype.Component;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

@Component
public class MovimientoReporteGenerator {

    @Value("classpath:jasper/ListaSalidaOF.jasper")
    private Resource listaSalidaOF;
    private JRBeanCollectionDataSource beanCollectionDataSource;

    public byte[] getReporteSalida(List<MovimientoSalidaRecord> movimientos) throws JRException, IOException{
        List<MovimientoSalidaDTO> movimientoSalidaDTOS = movimientos.stream().map(
                MovimientoSalidaDTO::new
        ).toList();

        beanCollectionDataSource = new JRBeanCollectionDataSource(movimientoSalidaDTOS);

        return JasperExportManager.exportReportToPdf(getJasperReport());

    }

    private JasperPrint getJasperReport() throws JRException, IOException{
        Map<String, Object> parameters = new HashMap<>();

        parameters.put("p_image_background","jasper/escudo.png");

        return JasperFillManager.fillReport(
            listaSalidaOF.getInputStream(),
                parameters,
                beanCollectionDataSource);
    }

    public byte[] getReporteTransferencia(Transferencia transferencia) throws JRException, IOException{
        List<MovimientoSalidaDTO> movimientoSalidaDTOS = movimientos.stream().map(
                MovimientoSalidaDTO::new
        ).toList();

        beanCollectionDataSource = new JRBeanCollectionDataSource(movimientoSalidaDTOS);

        return JasperExportManager.exportReportToPdf(getJasperReport());

    }

}
