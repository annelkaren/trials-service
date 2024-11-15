package mx.gob.pjpuebla.trials.workflow.sello;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoTipoJuiciosRecord;
import mx.gob.pjpuebla.trials.core.oficialias.Oficialia;
import mx.gob.pjpuebla.trials.core.oficialias.OficialiaRepository;
import mx.gob.pjpuebla.trials.util.enums.Rol;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.workflow.audiencias.AudienciaService;
import mx.gob.pjpuebla.trials.workflow.audiencias.record.ExtraAudienciaSelloRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.RelacionExpedientesRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaRepository;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoJuzgadoRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
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
    private final PersonaDocumentoRepository personaDocumentoRepository;
    private final AudienciaService audienciaService;

    @Value("classpath:jasper/selloReport.jasper")
    private Resource sello;
    boolean isPromocionOralidadExhorto;
    boolean isOralidadFamiliar;
    private String expedienteRelacionados;
    private final Set<String> expedientesSet = new HashSet<>();

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
        expedientesSet.add(documento.getCarpeta().getExpediente());

        PersonaDocumentoRecord actor = getInfoPersona(documento.getCarpeta().getId(), "Actor");
        PersonaDocumentoRecord demandado = getInfoPersona(documento.getCarpeta().getId(), "Demandado");
        ExtraAudienciaSelloRecord audiencia = audienciaService.getAudienciaAndSalaAndDomicilio(documento);

        String expediente= updateExpedientePorTipoJuicio(documento);
        expedientesByDemandadoActor(demandado.nombre(), actor.nombre(), documento.getCarpeta().getTipoJuicio().getMateria().getId());
        String relacionExpediente = (expedienteRelacionados != null && !expedienteRelacionados.isEmpty()) ? expedienteRelacionados : "";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("expediente",expediente);
        parameters.put("fechaHoraRecepcion", date);
        parameters.put("folio", (documento.getTipoDocumento() != null) ? documento.getFolio() : documento.getCarpeta().getFolio());
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
        parameters.put("sala", audiencia.nombreSala());
        parameters.put("fechaHoraAudiencia", audiencia.fechaAudiencia());
        parameters.put("tipoJuicio", documento.getCarpeta().getTipoJuicio().getNombre());
        parameters.put("actor", actor.nombre());
        parameters.put("curpActor", actor.curp());
        parameters.put("celularActor", actor.celular());
        parameters.put("correoActor", actor.correoElectronico());
        parameters.put("demandado", demandado.nombre());
        parameters.put("curpDemandado", demandado.curp());
        parameters.put("domicilioDemandado", demandado.domicilio());
        parameters.put("domiciliofamiliar", audiencia.domicilio());
        parameters.put("relacionExpediente", relacionExpediente);
        parameters.put("juez", audiencia.nombreJuez());
        parameters.put("isOralidad", isOralidadFamiliar); // es oralidad familiar

        isPromocionOralidadExhorto = false;
        isOralidadFamiliar = false;
        expedienteRelacionados = "";
        expedientesSet.clear();

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
        return list.isEmpty() ? "- Sin anexos" : String.join("", list);
    }

    private String tipoDocumentoFolio(Documento documento) {

        String tipoCarpetaDocumento;
        String result;
        String prefijo;

        if (documento.getTipoDocumento() != null) {
            tipoCarpetaDocumento = documento.getTipoDocumento().name();
            prefijo = tipoCarpetaDocumento.equals("PROMOCION") ? "P" : "";
        } else {
            tipoCarpetaDocumento = documento.getCarpeta().getTipoCarpeta().name();
            prefijo = switch (tipoCarpetaDocumento) {
                case "DEMANDA" -> "D";
                case "APELACION" -> "A";
                case "EXHORTO" -> "E";
                default -> "";
            };

        }
        result = prefijo + "-" + documento.getCarpeta().getFolio();
        return result;
    }

    private String getCentroTrabajoCapturista() {
        Jwt jwt = auditorAware.getCurrentAuditor().orElseThrow();//No es el centro de trabajo del usuario es de donde se creo
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
        } else if (persona.getOficialia() != null) {
            Optional<Oficialia> oficialiaOptional = oficialiaRepository.findById(persona.getOficialia().getId());
            if (oficialiaOptional.isPresent()) {
                nombreCapturista = oficialiaOptional.get().getNombre();
            } else {
                throw new NotFoundException("Oficialia no encontrada", "oficialiaId");
            }
        } else {
            nombreCapturista = "";
        }
        return nombreCapturista;
    }

    public String updateExpedientePorTipoJuicio(Documento documento) {
        Optional<Carpeta> carpetaOptional = carpetaRepository.findById(documento.getCarpeta().getId());
        DocumentoJuzgadoRecord docJuzDis = documentoRepository.findDistritoJuzgadoByDocumentoId(documento.getId());
        String expediente;
        if (carpetaOptional.isPresent()) {
            Carpeta carpeta = carpetaOptional.get();
            if (documento.getCarpeta().getTipoJuicio().getNombre().toLowerCase().contains("oralidad")
                    && documento.getCarpeta().getTipoJuicio().getNombre().toLowerCase().contains("familiar")
            ) {
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
                isPromocionOralidadExhorto = true;
                isOralidadFamiliar = true;
              expediente = expenienteOralFamiliar;
            } else if (Objects.equals(documento.getCarpeta().getTipoCarpeta(), TipoCarpeta.EXHORTO)) {
                expediente = carpeta.getExpediente() + " - Exhorto";
                isPromocionOralidadExhorto = false;
            } else if (Objects.equals(documento.getTipoDocumento(), TipoDocumento.PROMOCION)) {
                expediente = carpeta.getExpediente() + " - Promocion";
                isPromocionOralidadExhorto = false;
            } else {
                expediente = documento.getCarpeta().getExpediente();
            }
        } else {
            throw new NotFoundException("Carpeta no encontrada", "carpetaId");
        }
        return expediente;
    }

    public PersonaDocumentoRecord getInfoPersona(Integer id, String parte) {
        List<Rol> rol = List.of(Rol.PRINCIPAL);
        PersonaDocumentoRecord personaDocumentoRecord = personaDocumentoRepository.findPersonaAndTipoParteByCarpetaId(id, parte, rol);

        if (personaDocumentoRecord == null) {
            return new PersonaDocumentoRecord("", "", "", "", "", "", "", "", "", parte, null, id);
        }

        String nombre = personaDocumentoRecord.nombre() != null ? personaDocumentoRecord.nombre() : "";
        String apellidoPaterno = personaDocumentoRecord.apellidoPaterno() != null ? personaDocumentoRecord.apellidoPaterno() : "";
        String apellidoMaterno = personaDocumentoRecord.apellidoMaterno() != null ? personaDocumentoRecord.apellidoMaterno() : "";
        String celular = formatCelular(personaDocumentoRecord.celular());
        String nombreCompleto = String.format("%s %s %s", nombre, apellidoPaterno, apellidoMaterno).trim();
        String curp = personaDocumentoRecord.curp() != null ? personaDocumentoRecord.curp() : "";
        String domicilio = personaDocumentoRecord.domicilio() != null ? personaDocumentoRecord.domicilio() : "";
        String correoElectronico = personaDocumentoRecord.correoElectronico() != null ? personaDocumentoRecord.correoElectronico() : "";

        return new PersonaDocumentoRecord(
                nombreCompleto,
                apellidoPaterno,
                apellidoMaterno,
                personaDocumentoRecord.pseudonimo(),
                personaDocumentoRecord.tipoPersona(),
                curp,
                domicilio,
                celular,
                correoElectronico,
                personaDocumentoRecord.tipoParte(),
                personaDocumentoRecord.tipoParteId(),
                personaDocumentoRecord.carpetaId()
        );
    }

    public List<RelacionExpedientesRecord> getAllExpedientesRelacionados(String nombre, String apellidoP, String apellidoM) {
        return personaDocumentoRepository.getAllExpedienteRelacionadosByPersonaId(nombre, apellidoM, apellidoP, 1);
    }

    private void expedientesByDemandadoActor(String persona1, String persona2, Integer materiaId) {
        String[] partesPersona1 = persona1.split(" ");
        String nombre1 = partesPersona1[0];
        String apellidoP1 = partesPersona1.length > 1 ? partesPersona1[1] : "";
        String apellidoM1 = partesPersona1.length > 2 ? partesPersona1[2] : "";

        String[] partesPersona2 = persona2.split(" ");
        String nombre2 = partesPersona2[0];
        String apellidoP2 = partesPersona2.length > 1 ? partesPersona2[1] : "";
        String apellidoM2 = partesPersona2.length > 2 ? partesPersona2[2] : "";

        List<RelacionExpedientesRecord> listDemandado = personaDocumentoRepository.getAllExpedienteRelacionadosByPersonaId(nombre1, apellidoM1, apellidoP1, materiaId);
        List<RelacionExpedientesRecord> listActor = personaDocumentoRepository.getAllExpedienteRelacionadosByPersonaId(nombre2, apellidoM2, apellidoP2, materiaId);

        Set<String> expedientesActor = listActor.stream()
                .map(RelacionExpedientesRecord::expediente)
                .collect(Collectors.toSet());

        List<RelacionExpedientesRecord> expedientesComunes = listDemandado.stream()
                .filter(expediente -> expedientesActor.contains(expediente.expediente()))
                .toList();

        agregarExpedientes(expedientesComunes);
    }

    private String agregarExpedientes(List<RelacionExpedientesRecord> expedientes) {
        List<String> list = expedientes.stream()
                .filter(expediente -> expedientesSet.add(expediente.expediente())) // Agregar solo si no es repetido
                .map(expediente -> String.format("%s: %s %s <br/>",
                        (expediente.nombreJuzgado() != null ? expediente.nombreJuzgado() : "Sin Juzgado"),
                        expediente.expediente(),
                        (expediente.nombreTipoJuicio() != null ? expediente.nombreTipoJuicio() : "Tipo Desconocido"))
                )
                .toList();

        // Concatenar resultados
        if (!list.isEmpty()) {
            expedienteRelacionados += String.join("", list);
            expedienteRelacionados = expedienteRelacionados.replace("null", "");
        }

        return expedienteRelacionados;
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

    public String formatCelular(String celular) {
        if (celular == null) {
            return "";
        }

        String cleaned = celular.replaceAll("\\D", "");

        if (cleaned.length() == 10) {
            return String.format("(%s) %s-%s",
                    cleaned.substring(0, 3),
                    cleaned.substring(3, 6),
                    cleaned.substring(6, 10)
            );
        } else {
            return celular;
        }
    }

}
