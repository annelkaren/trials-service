package mx.gob.pjpuebla.trials.workflow.notificaciones;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;
import mx.gob.pjpuebla.trials.workflow.notificaciones.records.ListaExpedientesRutaDTO;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

@Service
@RequiredArgsConstructor
public class ListadoExpedientesRutaService {

    @Value("classpath:jasper/ListaExpedientesRuta.jasper")
    private Resource listaExpedientes;

    private JRBeanCollectionDataSource beanCollectionDataSource;
    private final NotificacionRepository notificacionRepository;
    private final PersonaService personaService;
    private Integer numElementos;

    public byte[] exportToPdf() throws IOException, JRException {
        // Instancia de ObjectMapper para trabajar con JSON

        List<ListaExpedientesRutaDTO> listaExpedenteRutaDTO = notificacionRepository
                .findAllNotificacionesByEstadoEnRutaAndTipoNotificacionDomicilio()
                .stream()
                .map(element -> {

                    // Obtener expediente (element[0])
                    String expediente = (String) element[0];

                    // Obtener rubros (element[1]) y convertirlo en una lista de Strings
                    DocumentoData data = (DocumentoData) element[1];
                    List<String> rubrosList = data.getRubros();

                    String rubros = String.join(", ", rubrosList);

                    // Obtener nota (element[2])
                    String nota = (String) element[2];

                    // Retornar el DTO con los valores procesados
                    return new ListaExpedientesRutaDTO(expediente, rubros, nota);

                })
                .collect(Collectors.toList()); // Recoger todos los DTOs en una lista

        numElementos = listaExpedenteRutaDTO.size();
        beanCollectionDataSource = new JRBeanCollectionDataSource(listaExpedenteRutaDTO);

        // Aquí podrías proceder con el reporte (por ejemplo, usando JasperReports)
        return JasperExportManager.exportReportToPdf(getJasperReport(listaExpedientes));
    }

    private JasperPrint getJasperReport(Resource reporte) throws JRException, IOException {
        Map<String, Object> parameters = new HashMap<>();
        Persona usuario = personaService.getAuditor();
        parameters.put("fecha", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        parameters.put("usuario",
                usuario.getNombre() + " " + usuario.getApellidoPaterno() + " " + (usuario.getApellidoMaterno() != null ? usuario.getApellidoMaterno() : "") );
        parameters.put("numElementos", numElementos);
        parameters.put("p_image_background", "jasper/escudo.png");

        return JasperFillManager.fillReport(
                reporte.getInputStream(),
                parameters,
                beanCollectionDataSource);
    }
}