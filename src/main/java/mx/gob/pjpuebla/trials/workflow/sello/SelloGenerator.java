package mx.gob.pjpuebla.trials.workflow.sello;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoTipoJuiciosRecord;
import mx.gob.pjpuebla.trials.core.oficialias.Oficialia;
import mx.gob.pjpuebla.trials.core.oficialias.OficialiaRepository;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaRepository;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumetoJuzgadoRecord;
import net.sf.jasperreports.engine.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Component
@RequiredArgsConstructor
public class SelloGenerator {

    private final PersonaRepository personaRepository;
    private final AuditorAware<Jwt> auditorAware;
    private final DocumentoRepository documentoRepository;
    private final AnexoRepository anexoRepository;
    private final JuzgadoRepository juzgadoRepository;
    private final CarpetaRepository carpetaRepository;
    private final OficialiaRepository oficialiaRepository;
    @Value("classpath:jasper/selloReport.jasper")
    private Resource sello;
    boolean isPromocionOralidadExhorto;
    boolean isOralidadFamiliar;

    public byte[] exportToPdf(Integer id) throws JRException, IOException {
        Documento documento = documentoRepository.findById(id).orElseThrow();
        if (documento.getCarpeta().getSelloEstatus() == SelloEstatus.NO_VALIDO) {
            documento.getCarpeta().setSelloEstatus(SelloEstatus.VALIDO);
            documentoRepository.save(documento);
        }
        List<Anexo> anexos = anexoRepository.findAllByDocumentoId(documento.getId());
        return JasperExportManager.exportReportToPdf(getReport(documento, anexos));
    }

    private JasperPrint getReport(Documento documento, List<Anexo> anexos) throws IOException, JRException {
        String date = getDate(documento.getAudit().getFechaAlta());

        String verificationCode = generateVerificationCode(documento, anexos, date);

        updateExpedientePorTipoJuicio(documento);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("expediente", documento.getCarpeta().getExpediente());
        parameters.put("fechaHoraRecepcion", date);
        parameters.put("folio", documento.getCarpeta().getFolio());
        parameters.put("documentoFolio", tipoDocumentoFolio(documento));
        parameters.put("anexos", getStringAnexos(anexos));
        parameters.put("cadenaVerificacion", verificationCode);
        parameters.put("nombreEntidad", getCentroTrabajoCapturista());
        parameters.put("nombreJuzgado", documento.getCarpeta().getJuzgado().getNombre());
        parameters.put("capturista", getCapturista());
        parameters.put("reimpresion", isReimpresion(documento.getAudit().getUsuarioAlta(), documento.getAudit().getFechaAlta()));
        parameters.put("marcaAgua", "jasper/escudo.png");
        parameters.put("logotipoHeder", "jasper/header.jpg");
        parameters.put("isOralProExh", isPromocionOralidadExhorto); //es - Promocion - Oralidad - Exhorto
        parameters.put("sala", "Pendiente sala");
        parameters.put("fechaHoraAudiencia", "Pendiente fechaHoraAudiencia");
        parameters.put("tipoJuicio", documento.getCarpeta().getTipoJuicio().getNombre());
        parameters.put("actor", "Pendiente ");
        parameters.put("curpActor", "Pendiente");
        parameters.put("celularActor", "Pendiente");
        parameters.put("correoActor", "Pendiente");
        parameters.put("demandado", "Pendiente");
        parameters.put("curpDemandado", "Pendiente");
        parameters.put("domicilioDemandado", "Pendiente");
        parameters.put("domiciliofamiliar", "Pendiente Domicilio familiar");
        parameters.put("relacionExpediente", "Pendiente relacion Expediente");
        parameters.put("juez", "Pendiente");
        parameters.put("isOralidad", isOralidadFamiliar); // es oralidad familiar

        return JasperFillManager.fillReport(
                sello.getInputStream(),
                parameters,
                new JREmptyDataSource());
    }

    private String getCapturista() {
        Jwt jwt = auditorAware.getCurrentAuditor().orElseThrow();
        String user = jwt.getSubject();
        Persona persona = personaRepository.findByUsuario(user).orElseThrow(() -> new NotFoundException("Persona no encontrada", "usuario"));
        String apellidoMaterno = persona.getApellidoMaterno();
        apellidoMaterno = (apellidoMaterno != null && !apellidoMaterno.isEmpty()) ? String.valueOf(apellidoMaterno.charAt(0)) : "";
        return persona.getNombre().charAt(0) + "" + persona.getApellidoPaterno().charAt(0) + apellidoMaterno;
    }

    private String getDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return date.format(formatter);
    }

    private boolean isReimpresion(String auditor, LocalDateTime date) {
        boolean isSameAuditor;
        boolean isSameDate;
        LocalDate today = LocalDate.now();
        Jwt jwt = auditorAware.getCurrentAuditor().orElseThrow();
        String currentUser = jwt.getSubject();
        isSameAuditor = (auditor.equals(currentUser));
        isSameDate = (date.toLocalDate().equals(today));
        return !(isSameAuditor && isSameDate);
    }

    public String generateVerificationCode(Documento documento, List<Anexo> anexos, String date) {
        String verificationStringCode = String.join("|",
                documento.getCarpeta().getJuzgado().getNombre(),
                documento.getCarpeta().getExpediente(),
                documento.getCarpeta().getFolio(),
                date,
                getAnexos(anexos)
        );
        return Base64.getEncoder().encodeToString(verificationStringCode.getBytes());
    }

    private String getAnexos(List<Anexo> anexos) {
        return anexos.stream()
                .map(Anexo::getNombre)
                .collect(Collectors.joining(","));
    }

    private String getStringAnexos(List<Anexo> anexos) {
        List<String> list = anexos.stream()
                .map(Anexo::getNombre)
                .map(nombre -> "- " + nombre + " <br/>")
                .toList();
        return String.join("", list);
    }

    private String tipoDocumentoFolio(Documento documento) {
        int tipoDocumentoOrdinal = documento.getCarpeta().getTipoCarpeta().ordinal();
        return tipoDocumentoOrdinal + "-" + documento.getCarpeta().getFolio();
    }

    private String getCentroTrabajoCapturista() {
        Jwt jwt = auditorAware.getCurrentAuditor().orElseThrow();
        String user = jwt.getSubject();
        Persona persona = personaRepository.findByUsuario(user).orElseThrow(() -> new NotFoundException("Persona no encontrada", "usuario"));
        String nombreCapturista;
        if (persona.getJuzgado() != null) {
            Optional<Juzgado> juzgadoOptional = juzgadoRepository.findById(persona.getJuzgado().getId());
            if (juzgadoOptional.isPresent()) {
                nombreCapturista = juzgadoOptional.get().getNombre();
            } else {
                throw new NotFoundException("Juzgado no encontrado", "juzgadoId");
            }
        }
        else if (persona.getOficialia() != null) {
            Optional<Oficialia> oficialiaOptional = oficialiaRepository.findById(persona.getOficialia().getId());
            if (oficialiaOptional.isPresent()) {
                nombreCapturista = oficialiaOptional.get().getNombre();
            } else {
                throw new NotFoundException("Oficialia no encontrada", "oficialiaId");
            }
        } else {
            nombreCapturista = "";
        }
        return  nombreCapturista;
    }
    public Documento updateExpedientePorTipoJuicio(Documento documento){
        Optional<Carpeta> carpetaOptional = carpetaRepository.findById(documento.getCarpeta().getId());
        DocumetoJuzgadoRecord docJuzDis = documentoRepository.findDistritoJuzgadoByDocumentoId(documento.getId());

        if (carpetaOptional.isPresent()) {
            Carpeta carpeta = carpetaOptional.get();
            if(documento.getCarpeta().getTipoJuicio().getNombre().toLowerCase().contains("oralidad")
                    && documento.getCarpeta().getTipoJuicio().getNombre().toLowerCase().contains("familiar")
            ){
                List<JuzgadoTipoJuiciosRecord> listTipoJuicios = juzgadoRepository.findTipoJuiciosByJuzgadoId(documento.getCarpeta().getJuzgado().getId());
                List<String> listInicialesTipoJuicio = obtenerIniciales(listTipoJuicios);
                String inicialesTipoJuicios = String.join("-", listInicialesTipoJuicio);
                String inicialesJuzgado = obtenerInicialesDeString(docJuzDis.nombreJuzgado());
                String inicialesDistrito = obtenerInicialesDeString(docJuzDis.nombreDistrito());
                String expenienteOralFamiliar = String.join("/",
                        inicialesJuzgado,
                        inicialesDistrito,
                        carpeta.getExpediente(),
                        inicialesTipoJuicios
                );
                isPromocionOralidadExhorto = (documento.getCarpeta().getTipoJuicio().getNombre().toLowerCase().contains("oralidad"));
                isOralidadFamiliar = true;
                carpeta.setExpediente(expenienteOralFamiliar);
            }else if (Objects.equals(documento.getCarpeta().getTipoCarpeta(), TipoCarpeta.EXHORTO)) {
                carpeta.setExpediente(carpeta.getExpediente() + "- Exhorto");
                isPromocionOralidadExhorto = (documento.getCarpeta().getTipoCarpeta().equals(TipoCarpeta.EXHORTO));
            }else if (Objects.equals(documento.getCarpeta().getTipoCarpeta(), TipoCarpeta.PROMOCION)){
                carpeta.setExpediente(carpeta.getExpediente() + "- Promocion");
                isPromocionOralidadExhorto = (documento.getCarpeta().getTipoCarpeta().equals(TipoCarpeta.PROMOCION));
            }
            documento.setCarpeta(carpeta);
        } else {
            throw new NotFoundException("Carperta no encontrada", "carpetaId");
        }
        return documento;
    }

    public static List<String> obtenerIniciales(List<JuzgadoTipoJuiciosRecord> tipoJuiciosLista) {
        return tipoJuiciosLista.stream()
                .map(juzgadoTipoJuicio -> obtenerInicialesDeString(juzgadoTipoJuicio.tipoJuicios()))
                .toList();
    }

    public static String obtenerInicialesDeString(String str) {
        return Arrays.stream(str.split(" "))
                .filter(word -> !word.trim().isEmpty())
                .map(word -> {
                    String inicial = word.replaceAll("[^a-zA-Z]", "");
                    return inicial.isEmpty() ? "" : inicial.substring(0, 1).toUpperCase();
                })
                .collect(Collectors.joining());
    }

}
