package mx.gob.pjpuebla.trials.workflow.movimientos;

import mx.gob.pjpuebla.trials.workflow.transferencias.Transferencia;
import mx.gob.pjpuebla.trials.workflow.transferencias.records.TransferenciaDTO;
import mx.gob.pjpuebla.trials.workflow.transferencias.records.TransferenciaRecordResponse;

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
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

@Component
public class MovimientoReporteGenerator {

    @Value("classpath:jasper/ListaSalidaOF.jasper")
    private Resource listaSalidaOF;

    @Value("classpath:jasper/ListaTransferencia.jasper")
    private Resource listaTransferencia;

    private JRBeanCollectionDataSource beanCollectionDataSource;

    public byte[] getReporteSalida(List<MovimientoSalidaRecord> movimientos) throws JRException, IOException{
        List<MovimientoSalidaDTO> movimientoSalidaDTOS = movimientos.stream().map(
                MovimientoSalidaDTO::new
        ).toList();

        beanCollectionDataSource = new JRBeanCollectionDataSource(movimientoSalidaDTOS);

        return JasperExportManager.exportReportToPdf(getJasperReport(listaSalidaOF));

    }

    private JasperPrint getJasperReport(Resource reporte) throws JRException, IOException{
        Map<String, Object> parameters = new HashMap<>();

        parameters.put("p_image_background","jasper/escudo.png");

        return JasperFillManager.fillReport(
            reporte.getInputStream(),
                parameters,
                beanCollectionDataSource);
    }

    public byte[] getReporteTransferencia(TransferenciaRecordResponse transferenciaRecordResponse) throws JRException, IOException{
        AtomicInteger index = new AtomicInteger(1);

        List<TransferenciaDTO> transferenciaDTOS = transferenciaRecordResponse.expedientes().stream().map(
            t-> new TransferenciaDTO()
            .setFolio(index.getAndIncrement())
            .setJuzgado(transferenciaRecordResponse.juzgado())
            .setFecha(transferenciaRecordResponse.fechaTransferencia())
            .setPersonaEntrega(String.format("%s, %s", transferenciaRecordResponse.personaEntrega(), transferenciaRecordResponse.personaEntregaPuesto()))
            .setPersonaRecibe(String.format("%s, %s", transferenciaRecordResponse.personaRecibe(), transferenciaRecordResponse.personaRecibePuesto()))
            .setTotal(transferenciaRecordResponse.totalExpedientes())
            .setExpediente(t.expediente())
            .setConcepto(t.concepto())
            .setFechaTermino(t.fechaTermino())
        ).toList();

        beanCollectionDataSource = new JRBeanCollectionDataSource(transferenciaDTOS);

        return JasperExportManager.exportReportToPdf(getJasperReport(listaTransferencia));

    }

}
