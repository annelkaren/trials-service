package mx.gob.pjpuebla.trials.workflow.notificaciones;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.notificaciones.records.ListaExpedientesRutaDTO;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

@Service
@Component
@RequiredArgsConstructor
public class ListadoExpedientesRutaService {

    @Value("classpath:jasper/ListaExpedientesRuta.jasper")
    private Resource listaExpedientes;

    private JRBeanCollectionDataSource beanCollectionDataSource;
    private NotificacionRepository notificacionRepository;


    public byte[] exportToPdf() throws IOException, JRException {
        // Instancia de ObjectMapper para trabajar con JSON
        ObjectMapper objectMapper = new ObjectMapper();

        List<ListaExpedientesRutaDTO> listaExpedenteRutaDTO = notificacionRepository
                .findAllNotificacionesByEstadoEnRutaAndTipoNotificacionDomicilio().stream()
                .map(element -> {
                    try {
                        // Obtener expediente (element[0])
                        String expediente = (String) element[0];

                        // Obtener rubros (element[1]) y convertirlo en una lista de Strings
                        JsonNode rubrosNode = objectMapper.readTree((String) element[1]).get("rubros"); 
                        String rubros = rubrosNode.isArray() ? String.join(", ",
                                StreamSupport.stream(rubrosNode.spliterator(), false)
                                        .map(JsonNode::asText) // Convertir cada rubro a String
                                        .collect(Collectors.toList()))
                                : ""; // Si no es un array, asignar un valor vacío

                        // Obtener nota (element[2])
                        String nota = (String) element[2];

                        // Retornar el DTO con los valores procesados
                        return new ListaExpedientesRutaDTO(expediente, rubros, nota);
                    } catch (JsonProcessingException e) {
                        // Manejo de la excepción si ocurre un error al procesar el JSON
                        e.printStackTrace();
                        return null; // Devolver null o un valor predeterminado si hay error
                    }
                })
                .collect(Collectors.toList()); // Recoger todos los DTOs en una lista

            beanCollectionDataSource = new JRBeanCollectionDataSource(listaExpedenteRutaDTO); 
        // Aquí podrías proceder con el reporte (por ejemplo, usando JasperReports)
        return JasperExportManager.exportReportToPdf(getJasperReport(listaExpedientes));
    }

    private JasperPrint getJasperReport(Resource reporte) throws JRException, IOException {
        Map<String, Object> parameters = new HashMap<>();

        parameters.put("p_image_background", "jasper/escudo.png");

        return JasperFillManager.fillReport(
                reporte.getInputStream(),
                parameters,
                beanCollectionDataSource);
    }
}
