package mx.gob.pjpuebla.trials.workflow.cargaTrabajo;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoService;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoBandejaRecepcionRecord;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.core.io.Resource;


@Transactional
@RequiredArgsConstructor
@Service
public class CargaTrabajoService {

    private final DocumentoService documentoService;
    private final PersonaService personaService;

    @Value("classpath:jasper/cargaTrabajo.jasper")
    private Resource cargaTrabajo;

    private JRBeanCollectionDataSource beanCollectionDataSource;

    public byte[] exportToPdf() throws JRException, IOException {
        final LocalDate fechaHoy = LocalDate.now();

        // Extraer la construcción del nombre a un método separado
        final String nombrePersona = construirNombreCompleto(personaService.getAuditor());

        // Obtener los datos y transformarlos en DTOs en un solo paso
        List<CargaTrabajoDTO> cargaTrabajoDTOList = documentoService
                .getAllBandejaRecepcion("", Pageable.unpaged())
                .getContent()
                .stream()
                .map(item -> mapearADTO(item, fechaHoy, nombrePersona))
                .sorted(Comparator.comparing(dto -> getEstiloPrioridad(dto.getEstilo())))
                .collect(Collectors.toList());

        beanCollectionDataSource = new JRBeanCollectionDataSource(cargaTrabajoDTOList);
        

        return JasperExportManager.exportReportToPdf(getJasperReport(cargaTrabajo));
    }


        private JasperPrint getJasperReport(Resource reporte) throws JRException, IOException{
        Map<String, Object> parameters = new HashMap<>();
      
        return JasperFillManager.fillReport(
            reporte.getInputStream(),
                parameters,
                beanCollectionDataSource);
    }

    // Método auxiliar para construir el nombre completo
    private String construirNombreCompleto(Persona persona) {
        StringBuilder nombreBuilder = new StringBuilder()
                .append(persona.getNombre())
                .append(" ")
                .append(persona.getApellidoMaterno());

        if (persona.getApellidoPaterno() != null && !persona.getApellidoPaterno().isEmpty()) {
            nombreBuilder.append(" ").append(persona.getApellidoPaterno());
        }

        return nombreBuilder.toString();
    }

    // Método auxiliar para mapear un registro a DTO
    private CargaTrabajoDTO mapearADTO(DocumentoBandejaRecepcionRecord item, LocalDate fechaHoy, String nombrePersona) {
        LocalDate fechaLimite = item.fechaHoraEnvio().plusHours(item.horas()).toLocalDate();
        String estilo = determinarEstilo(fechaHoy, fechaLimite);

        return new CargaTrabajoDTO(
                item.folio(),
                item.expediente(),
                item.tipoEntrada(),
                item.fechaHoraEnvio().toLocalDate(),
                fechaLimite,
                nombrePersona,
                estilo);
    }

    // Método auxiliar para determinar el estilo según días restantes
    private String determinarEstilo(LocalDate fechaHoy, LocalDate fechaLimite) {
        long diasHastaLimite = ChronoUnit.DAYS.between(fechaHoy, fechaLimite);

        if (diasHastaLimite < 0)
            return "rojo";
        if (diasHastaLimite == 1)
            return "amarillo";
        return "verde";
    }

    // Método auxiliar para la prioridad de ordenamiento
    private int getEstiloPrioridad(String estilo) {
        switch (estilo) {
            case "rojo":
                return 1;
            case "amarillo":
                return 2;
            case "verde":
                return 3;
            default:
                return 4;
        }
    }
}
