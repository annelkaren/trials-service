package mx.gob.pjpuebla.trials.workflow.generadorQR;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.Resource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;

/**
 * Servicio para la generación de reportes en formato PDF con códigos QR.
 * Este servicio permite generar reportes continuos y reportes específicos
 * basados en coordenadas (casillas) para la colocación de códigos QR.
 */
@RequiredArgsConstructor
@Component
public class GeneradorQRService {

    @Value("classpath:jasper/codigoQR.jasper")
    private Resource expedienteQR;

    private JRBeanCollectionDataSource beanCollectionDataSource;

    private final CarpetaRepository carpetaRepository;
    private final PersonaService personaService;

    /**
     * Genera un reporte continuo de códigos QR para un rango de expedientes en un
     * año específico.
     *
     * @param expMin El número mínimo del expediente.
     * @param expMax El número máximo del expediente.
     * @param year   El año de los expedientes.
     * @return Un arreglo de bytes que representa el reporte en formato PDF.
     * @throws JRException Si ocurre un error al generar el reporte con
     *                     JasperReports.
     * @throws IOException Si ocurre un error de entrada/salida al leer el recurso
     *                     del reporte.
     */
    public byte[] getContinuityReport(Integer expMin, Integer expMax, Integer year) throws JRException, IOException {
        Persona personaLogueada = personaService.getAuditor();

        // Obtiene los resultado desde la consulta
        List<Object[]> resultado = carpetaRepository.findByExpMinAndExMaxAndYear(
                expMin, expMax, year, personaLogueada.getJuzgado().getId());

        List<QrExpedienteProjection> expedientes = resultado.stream()
                .map(arr -> new QrExpedienteProjection((String) arr[0], (String) arr[1])).toList();

        if (expedientes.isEmpty()) {
            throw new NotFoundException("No existen expedientes con los criterios de busqueda.",
                    "Exp min: " + expMin + " Exp. max: " + expMax + " Año: " + year);
        }

        // Lista final de DTOs
        List<GeneradorQRDTO> generadorFinal = new ArrayList<>();

        // Recorremos los expedientes en bloques de 30
        for (int i = 0; i < expedientes.size(); i += 30) {
            // Creamos un nuevo DTO para cada bloque de 30 elementos
            GeneradorQRDTO generador = new GeneradorQRDTO();

            // Obtenemos una sublista de 30 elementos (o menos si es el último bloque)
            List<QrExpedienteProjection> sublista = expedientes.subList(i, Math.min(i + 30, expedientes.size()));

            // Asignamos los códigos al DTO
            asignarCodigosADTO(generador, sublista);

            // Agregamos el DTO a la lista final
            generadorFinal.add(generador);
        }

        // Convierte la lista en un DataSource para JasperReports
        beanCollectionDataSource = new JRBeanCollectionDataSource(generadorFinal);

        // Exporta el reporte a PDF
        return JasperExportManager.exportReportToPdf(getJasperReport(expedienteQR));
    }

    /**
     * Asigna dinámicamente los códigos QR a los campos de un DTO usando reflexión.
     * Este método permite asignar los valores de una lista de códigos a los campos
     * "codigo1", "codigo2", ..., "codigo30" del DTO.
     *
     * @param generador El DTO al que se asignarán los códigos.
     * @param codigos   La lista de códigos QR a asignar.
     * @throws RuntimeException Si ocurre un error al acceder a los campos del DTO.
     */
    private void asignarCodigosADTO(GeneradorQRDTO generador, List<QrExpedienteProjection> codigos) {
        try {
            for (int i = 0; i < codigos.size(); i++) {

                String codigoField = "codigo" + (i + 1);
                String textoField = "texto" + (i + 1);

                Field codigo = GeneradorQRDTO.class.getDeclaredField(codigoField);
                Field texto = GeneradorQRDTO.class.getDeclaredField(textoField);

                codigo.setAccessible(true);
                texto.setAccessible(true);

                codigo.set(generador, codigos.get(i).getQr());
                texto.set(generador, codigos.get(i).getExpediente());
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Error al asignar códigos al DTO", e);
        }
    }

    /**
     * Genera un reporte de código QR para un expediente específico, colocando el
     * código
     * en una casilla (coordenada) determinada.
     *
     * @param expediente El número del expediente.
     * @param casilla    La casilla (1-30) donde se colocará el código QR.
     * @return Un arreglo de bytes que representa el reporte en formato PDF.
     * @throws JRException Si ocurre un error al generar el reporte con
     *                     JasperReports.
     * @throws IOException Si ocurre un error de entrada/salida al leer el recurso
     *                     del reporte.
     */
    public byte[] getReportByCoordinates(String expediente, Integer casilla) throws JRException, IOException {
        // Obtener la persona logueada
        Persona personaLogueada = personaService.getAuditor();

        // Obtener la carpeta del expediente
        Carpeta carpeta = carpetaRepository
                .findByExpedienteAndJuzgadoId(expediente, personaLogueada.getJuzgado().getId())
                .orElseThrow(() -> new NotFoundException("Expediente no encontrado", "expediente: " + expediente));

        if (carpeta == null) {
            throw new NotFoundException("No existen expedientes con los criterios de busqueda.",
                    "Expediente " + expediente);
        }

        // Obtener el código QR y texto de la carpeta
        String codigo = getExpedienteCarpeta(carpeta);
        String texto = carpeta.getExpediente() + "\n" + carpeta.getJuzgado().getNombre().toLowerCase();

        // Crear la lista de 30 posiciones con QrExpedienteProjection (null excepto una)
        List<QrExpedienteProjection> codigos = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            if (i == casilla - 1) {
                codigos.add(new QrExpedienteProjection(codigo, texto));
            } else {
                codigos.add(new QrExpedienteProjection(null, null));
            }
        }

        GeneradorQRDTO dto = new GeneradorQRDTO();
        asignarCodigosADTO(dto, codigos);

        List<GeneradorQRDTO> generadorFinal = List.of(dto);
        beanCollectionDataSource = new JRBeanCollectionDataSource(generadorFinal);

        return JasperExportManager.exportReportToPdf(getJasperReport(expedienteQR));
    }

    /**
     * Genera el código QR para una carpeta basado en su tipo y folio.
     *
     * @param carpeta La carpeta de la cual se generará el código QR.
     * @return El código QR generado.
     */
    private String getExpedienteCarpeta(Carpeta carpeta) {
        TipoCarpeta tipoCarpeta = carpeta.getTipoCarpeta();
        String folio = carpeta.getFolio();

        return switch (tipoCarpeta) {
            case DEMANDA -> "D" + '.' + folio;
            case EXHORTO -> "E" + '.' + folio;
            case AMPARO -> "A" + '.' + folio;
            default -> folio;
        };
    }

    /**
     * Prepara el reporte JasperPrint para su exportación a PDF.
     *
     * @param reporte El recurso del reporte Jasper (.jasper).
     * @return Un objeto JasperPrint listo para ser exportado.
     * @throws JRException Si ocurre un error al llenar el reporte.
     * @throws IOException Si ocurre un error de entrada/salida al leer el recurso
     *                     del reporte.
     */
    private JasperPrint getJasperReport(Resource reporte) throws JRException, IOException {
        Map<String, Object> parameters = new HashMap<>();

        return JasperFillManager.fillReport(
                reporte.getInputStream(),
                parameters,
                beanCollectionDataSource);
    }
}