package mx.gob.pjpuebla.trials.workflow.sello;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import net.sf.jasperreports.engine.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Component
@RequiredArgsConstructor
public class CaratulaGenerator {

    private final PersonaDocumentoRepository personaDocumentoRepository;
    private final DocumentoRepository documentoRepository;
    private final JuzgadoRepository  juzgadoRepository;
    @Value("classpath:jasper/CaratulaReport.jasper")
    private Resource caratula;

    public byte[] exportToPdf(Integer id) throws JRException, IOException {
          Documento documento = documentoRepository.findById(id).orElseThrow();
          //List<PersonaDocumento> personaDocumento = personaDocumentoRepository.findByDocumentoId(documento.getId());
          return JasperExportManager.exportReportToPdf(getReport(documento));
    }

    private JasperPrint getReport(Documento documento) throws IOException, JRException  {
        String[] resultado = getNoExpendienteYear(documento.getExpediente());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("juzgado", documento.getJuzgado().getNombre());
        parameters.put("expediente", resultado[0]);
        parameters.put("year", resultado[1]);
        parameters.put("actor", "Nombre del actor");
        parameters.put("demandado", "Nombre del demandado");
        parameters.put("codigoQR", "");
        parameters.put("logotipoHeder", "src/main/resources/jasper/header.jpg");
        parameters.put("numeroExpediente", documento.getExpediente());

        return JasperFillManager.fillReport(
                caratula.getInputStream(),
                parameters,
                new JREmptyDataSource());
    }

    private String[] getNoExpendienteYear(String expediente){
        String[] parts = expediente.split("/");
        return  parts;

    }
}
