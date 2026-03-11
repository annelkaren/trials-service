package mx.gob.pjpuebla.trials.workflow.acuseNotificacion;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.Sexo;
import mx.gob.pjpuebla.trials.workflow.emailLogs.EmailLogs;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.NotificacionSalaDestinatario;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.NotificacionSalaDestinatarioRepository;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.NotificacionesSalas;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

@Service
@Component
@RequiredArgsConstructor
public class AcuseNotificacionService {

    private static final DateTimeFormatter formateador = DateTimeFormatter
            .ofPattern("d 'de' MMMM 'de' yyyy 'a las ' HH:mm:ss", new Locale("es", "MX"));

    private final NotificacionSalaDestinatarioRepository notificacionSalaDestinatarioRepository;
    private final PersonaService personaService;
    private final JuzgadoRepository juzgadoRepository;

    @Value("classpath:jasper/acuseNotificacion.jasper")
    private Resource acuseNotificacion;

    public byte[] getAcuseNotificacionService(Integer notificacionSalaDestinatarioId) throws JRException, IOException {

        NotificacionSalaDestinatario notificacionSalaDestinatario = notificacionSalaDestinatarioRepository
                .findById(notificacionSalaDestinatarioId)
                .orElseThrow(() -> new NotFoundException("NotificacionSalaDestinatario no encontrada",
                        "notificacionSalaDestinatarioId"));

        Map<String, Object> parameters = getParameters(notificacionSalaDestinatario);

        JasperPrint reporteJasper = JasperFillManager.fillReport(
                acuseNotificacion.getInputStream(),
                parameters,
                new JREmptyDataSource());

        return JasperExportManager.exportReportToPdf(reporteJasper);
    }

    private Map<String, Object> getParameters(NotificacionSalaDestinatario notificacionSalaDestinatario) {

        Persona persona = personaService.getAuditor();
        NotificacionesSalas notificacionSala = notificacionSalaDestinatario.getNotificacionSala();
        EmailLogs emailLogs = notificacionSalaDestinatario.getEmailLog();

        String sexo = persona.getSexo() == Sexo.FEMENINO ? "F" : "M";
        String fechaVisualizacion = emailLogs.getFechaDescargaVinculo() != null
                ? emailLogs.getFechaDescargaVinculo().format(formateador).toString()
                : "No visualizado";

        Juzgado juzgado = juzgadoRepository.findById(notificacionSala.getSala().getId())
                .orElseThrow(() -> new NotFoundException("Juzgado no encontrado", "juzgadoId"));
        String nombreSala = juzgado.getJuzgadoPadre() == null ? juzgado.getShortName()
                : juzgado.getJuzgadoPadre().getShortName();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("asunto", emailLogs.getSubject());
        parameters.put("para", emailLogs.getToEmail());
        parameters.put("fechaEnvio", notificacionSala.getFechaEnvio().format(formateador).toString());
        parameters.put("fechaLectura", emailLogs.getFechaLectura().format(formateador).toString());
        parameters.put("fechaVisualizacion", fechaVisualizacion);
        parameters.put("casaDeJusticia", persona.getJuzgado().getSede().getNombre());
        parameters.put("fechaGeneracionDocumento", FechaTextoUtil.obtenerFechaEnTexto());
        parameters.put("nombreDestinatario", notificacionSalaDestinatario.getNombreDestinatario());
        parameters.put("tipoParte", notificacionSalaDestinatario.getTipoParte());
        parameters.put("nombreSala", nombreSala);
        parameters.put("nombreNotificador", getNombrePersona(persona));
        parameters.put("sexo", sexo);
        parameters.put("logo", "jasper/logo_negro.png");

        return parameters;
    }

    private String getNombrePersona(Persona persona) {
        String nombre = persona.getNombre();
        String apellidoPaterno = persona.getApellidoPaterno();
        String apellidoMaterno = persona.getApellidoMaterno();

        String resultado = nombre + " " + apellidoPaterno;

        if (apellidoMaterno != null && !apellidoMaterno.isEmpty()) {
            resultado += " " + apellidoMaterno;
        }

        return resultado.toUpperCase();
    }

}
