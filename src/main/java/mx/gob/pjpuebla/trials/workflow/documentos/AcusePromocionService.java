package mx.gob.pjpuebla.trials.workflow.documentos;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaRepository;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.records.AcusePromocionDetailRecord;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoRepository;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;

@Service
@Component
@RequiredArgsConstructor
public class AcusePromocionService {

        private final DocumentoRepository documentoRepository;
        private final MovimientoRepository movimientoRepository;
        private final PersonaRepository personaRepository;

        @Value("classpath:jasper/AcusePromociones.jasper")
        private Resource acusePromocion;

        public byte[] exportToPdf(Integer legacy, String tipo, Integer documentoId) throws JRException, IOException {

                Documento promocion = documentoRepository.findById(documentoId).orElseThrow(
                                () -> new NotFoundException("Documento no encontrado", "documentoId"));

                Carpeta expediente = promocion.getCarpeta();
                Persona litigante = personaRepository.findByUsuario(promocion.getAudit().getUsuarioAlta()).orElseThrow(
                                () -> new NotFoundException("Persona no encontrada", "usuario"));

                Boolean isEnvio = tipo.equals("envio");

                Optional<AcusePromocionDetailRecord> acusePromocionDetailOpt = movimientoRepository
                                .findAllAcusePromocionDetails(documentoId)
                                .stream().findFirst();

                Map<String, Object> parameters = legacy == 0 ? getParametersJava(expediente, promocion, litigante,
                                acusePromocionDetailOpt, isEnvio) : getParametersPhp(tipo,documentoId);

                JasperPrint reporteJasper = JasperFillManager.fillReport(
                                acusePromocion.getInputStream(),
                                parameters,
                                new JREmptyDataSource());

                return JasperExportManager.exportReportToPdf(
                                reporteJasper);

        }

        private Map<String, Object> getParametersJava(Carpeta expediente, Documento promocion, Persona litigante,
                        Optional<AcusePromocionDetailRecord> acusePromocionDetailOpt, Boolean isEnvio) {

                String fechaRecepcion = "";
                String horaRecepcion = "";
                String nombreReceptor = "";
                String puestoReceptor = "";

                if (acusePromocionDetailOpt.isPresent()) {
                        AcusePromocionDetailRecord acusePromocionDetail = acusePromocionDetailOpt.get();
                        fechaRecepcion = acusePromocionDetail.fechaRecepcion()
                                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        horaRecepcion = acusePromocionDetail.fechaRecepcion().format(
                                        DateTimeFormatter.ofPattern("hh:mm a", new Locale("es", "MX"))).toLowerCase();

                        nombreReceptor = acusePromocionDetail.nombreReceptor();
                        puestoReceptor = acusePromocionDetail.puestoReceptor();

                }

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("p_juzgado", expediente.getJuzgado().getNombre());
                parameters.put("p_expediente", expediente.getExpediente());
                parameters.put("p_folio", promocion.getFolio());
                parameters.put("p_fecha_envio", promocion.getAudit().getFechaAlta().toString());
                parameters.put("p_hora_envio", promocion.getAudit().getFechaAlta().toLocalTime());
                parameters.put("p_promovente", litigante.getCorreoElectronico());
                parameters.put("p_tipo_promocion", "");
                parameters.put("p_is_envio", isEnvio);
                parameters.put("p_fecha_recepcion", fechaRecepcion);
                parameters.put("p_hora_recepcion", horaRecepcion);
                parameters.put("p_puesto_recibe", puestoReceptor);
                parameters.put("p_recibe", nombreReceptor);

                return parameters;
        }

        private Map<String, Object> getParametersPhp(String tipo, Integer documentoId) {
                
                

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("p_tipo", tipo);
                parameters.put("p_documento_id", documentoId);
                return parameters;
        }

}
