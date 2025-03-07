package mx.gob.pjpuebla.trials.workflow.generadorQR;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

@RequiredArgsConstructor
@Component
public class GeneradorQRService {

    @Value("classpath:jasper/expedientesQR.jasper")
    private Resource expedienteQR;

    private JRBeanCollectionDataSource beanCollectionDataSource;

    private final CarpetaRepository carpetaRepository;
    private final PersonaService personaService;

    public byte[] exportToPdf(Integer expMin, Integer expMax, Integer year)  throws JRException, IOException{
        Persona personaLogueada = personaService.getAuditor();

        List<GeneradorQRRecord> expedientes = carpetaRepository.findByExpMinAndExMaxAndYear(expMin, expMax, year, personaLogueada.getJuzgado().getId());
        List<GeneradorQRDTO> generadorFinal = new ArrayList<>();
        
        List<String> tempCodes = new ArrayList<>();

        for (GeneradorQRRecord r : expedientes) {
            tempCodes.add(r.code());

            if (tempCodes.size() == 3) {
                generadorFinal.add(new GeneradorQRDTO(tempCodes.get(0), tempCodes.get(1), tempCodes.get(2)));
                tempCodes.clear();
            }
        }

        // Si hay elementos restantes (caso cuando la lista no es múltiplo de 3)
        if (!tempCodes.isEmpty()) {
            GeneradorQRDTO dto = new GeneradorQRDTO();
            if (tempCodes.size() > 0)
                dto.setCodigo1(tempCodes.get(0));
            if (tempCodes.size() > 1)
                dto.setCodigo2(tempCodes.get(1));
            generadorFinal.add(dto);
        }

        beanCollectionDataSource = new JRBeanCollectionDataSource(generadorFinal);

        return JasperExportManager.exportReportToPdf(getJasperReport(expedienteQR));
    }

    private JasperPrint getJasperReport(Resource reporte) throws JRException, IOException {
        Map<String, Object> parameters = new HashMap<>();

        return JasperFillManager.fillReport(
                reporte.getInputStream(),
                parameters,
                beanCollectionDataSource);
    }

}
