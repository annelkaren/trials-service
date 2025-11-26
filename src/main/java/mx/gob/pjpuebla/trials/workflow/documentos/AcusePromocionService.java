package mx.gob.pjpuebla.trials.workflow.documentos;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.migracion.readers.detalle.DetallesMigracionRepository;
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
@Slf4j
public class AcusePromocionService {

        private final DocumentoRepository documentoRepository;
        private final MovimientoRepository movimientoRepository;
        private final PersonaRepository personaRepository;
        private final DetallesMigracionRepository detallesMigracionRepository;

        @Value("classpath:jasper/AcusePromociones.jasper")
        private Resource acusePromocion;

        public byte[] exportToPdf(Integer legacy, String tipo, Integer documentoId) throws JRException, IOException {
                Map<String, Object> parameters;
                Boolean isEnvio = tipo.equals("envio");

                if (legacy == 0) {

                        Documento promocion = documentoRepository.findById(documentoId).orElseThrow(
                                        () -> new NotFoundException("Documento no encontrado", "documentoId"));

                        Carpeta expediente = promocion.getCarpeta();
                        Persona litigante = personaRepository.findByUsuario(promocion.getAudit().getUsuarioAlta())
                                        .orElseThrow(
                                                        () -> new NotFoundException("Persona no encontrada",
                                                                        "usuario"));

                        Optional<AcusePromocionDetailRecord> acusePromocionDetailOpt = movimientoRepository
                                        .findAllAcusePromocionDetails(documentoId)
                                        .stream().findFirst();

                        parameters = getParametersJava(expediente, promocion, litigante, acusePromocionDetailOpt.orElse(null),
                                        isEnvio);
                } else {
                        parameters = getParametersPhp(tipo, documentoId, isEnvio);
                }

                parameters.put("p_logo_header", "jasper/logo_negro.png");
                parameters.put("p_logo_body", "jasper/logo_transparente.png");

                JasperPrint reporteJasper = JasperFillManager.fillReport(
                                acusePromocion.getInputStream(),
                                parameters,
                                new JREmptyDataSource());

                return JasperExportManager.exportReportToPdf(
                                reporteJasper);

        }

        private Map<String, Object> getParametersJava(Carpeta expediente, Documento promocion, Persona litigante,
                        AcusePromocionDetailRecord acusePromocionDetail, Boolean isEnvio) {

                String fechaRecepcion = "";
                String horaRecepcion = "";
                String nombreReceptor = "";
                String puestoReceptor = "";
                String fechaEnvio = promocion.getAudit().getFechaAlta()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String horaEnvio = promocion.getAudit().getFechaAlta()
                                .format(DateTimeFormatter.ofPattern("hh:mm a", new Locale("es", "MX")))
                                .toLowerCase();

                if (acusePromocionDetail != null) {
                        fechaRecepcion = acusePromocionDetail.fechaRecepcion()
                                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                        horaRecepcion = acusePromocionDetail.fechaRecepcion()
                                        .format(DateTimeFormatter.ofPattern("hh:mm a", new Locale("es", "MX")))
                                        .toLowerCase();

                        nombreReceptor = acusePromocionDetail.receptor();
                        puestoReceptor = acusePromocionDetail.puestoReceptor();
                }

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("p_juzgado", expediente.getJuzgado().getNombre());
                parameters.put("p_expediente", expediente.getExpediente());
                parameters.put("p_folio", promocion.getFolio());
                parameters.put("p_fecha_envio", fechaEnvio);
                parameters.put("p_hora_envio", horaEnvio);
                parameters.put("p_promovente", litigante.getCorreoElectronico());
                parameters.put("p_tipo_promocion", "");
                parameters.put("p_is_envio", isEnvio);
                parameters.put("p_fecha_recepcion", fechaRecepcion);
                parameters.put("p_hora_recepcion", horaRecepcion);
                parameters.put("p_puesto_recibe", puestoReceptor);
                parameters.put("p_recibe", nombreReceptor);

                return parameters;
        }

        private Map<String, Object> getParametersPhp(String tipo, Integer detalleId, Boolean isEnvio) {

                Optional<AcusePromocionDetailRecord> acusePromocionDetailOpt = detallesMigracionRepository
                                .findDetalleAcusePromocionById(detalleId);
                Map<String, Object> parameters = new HashMap<>();

                if (acusePromocionDetailOpt.isEmpty()) {
                        log.info("No se encontró detalle de acuse de promoción para el ID: {}", detalleId);
                        return parameters;
                }

                AcusePromocionDetailRecord acusePromocionDetail = acusePromocionDetailOpt.get();

                String fechaRecepcion = acusePromocionDetail.fechaRecepcion() != null
                                ? acusePromocionDetail.fechaRecepcion()
                                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                : "";

                String horaRecepcion = acusePromocionDetail.horaRecepcion() != null
                                ? acusePromocionDetail.horaRecepcion()
                                : "";

                parameters.put("p_juzgado", acusePromocionDetail.juzgado());
                parameters.put("p_expediente", acusePromocionDetail.expediente());
                parameters.put("p_folio", acusePromocionDetail.folio().toString());
                parameters.put("p_fecha_envio", acusePromocionDetail.fechaEnvio());
                parameters.put("p_hora_envio", acusePromocionDetail.horaEnvio());
                parameters.put("p_promovente", acusePromocionDetail.promovente());
                parameters.put("p_tipo_promocion", acusePromocionDetail.tipoPromocion());
                parameters.put("p_is_envio", isEnvio);
                parameters.put("p_fecha_recepcion", fechaRecepcion);
                parameters.put("p_hora_recepcion", horaRecepcion);
                parameters.put("p_puesto_recibe", acusePromocionDetail.puestoReceptor());
                parameters.put("p_recibe", acusePromocionDetail.receptor());

                return parameters;
        }

}
