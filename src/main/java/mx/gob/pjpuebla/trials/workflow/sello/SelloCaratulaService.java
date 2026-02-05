package mx.gob.pjpuebla.trials.workflow.sello;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.util.enums.Rol;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import net.sf.jasperreports.engine.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@Component
@RequiredArgsConstructor
public class SelloCaratulaService {

    private final PersonaDocumentoRepository personaDocumentoRepository;
    private final DocumentoRepository documentoRepository;
    @Value("classpath:jasper/CaratulaReport.jasper")
    private Resource caratula;
    private Boolean isExhorto;

    public byte[] exportToPdf(Integer id) throws JRException, IOException {
        Documento documento = documentoRepository.findById(id).orElseThrow();
        return JasperExportManager.exportReportToPdf(getReport(documento));
    }

    private JasperPrint getReport(Documento documento) throws IOException, JRException {

        String[] expendienteYear = documento.getCarpeta().getExpediente().split("/");
        // TODO: Se modifica por el momenrto el flujo de obtención de actores
        // principales
        // En atención de EL Ing. Alfonso se muesrra el primer actor principal y
        // demandado encontrado y si hay mas de uno se coloca la leyenda "y otros."
        boolean isPieza = documento.getCarpeta().getTipoPieza() == null;
        String actor, demandado;
        if (isPieza) {
            actor = getNombrePersonaByIdAndParte(documento.getCarpeta().getId(), "Actor");
            demandado = getNombrePersonaByIdAndParte(documento.getCarpeta().getId(), "Demandado");
        } else {
            actor = getNombrePersonaByIdAndParte(documento.getCarpeta().getCarpetaPadre().getId(), "Actor");
            demandado = getNombrePersonaByIdAndParte(documento.getCarpeta().getCarpetaPadre().getId(), "Demandado");

        }

        String procedencia = getExhortoPromocion(documento);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("juzgado", documento.getCarpeta().getJuzgado().getNombre());
        parameters.put("expediente", expendienteYear[0]);
        parameters.put("year", expendienteYear[1]);
        parameters.put("documentoFolio", tipoDocumentoFolio(documento));
        parameters.put("actor", actor);
        parameters.put("demandado", demandado);
        parameters.put("codigoQR", expendienteYear[0]);
        parameters.put("logotipoHeder", "jasper/header.jpg");
        parameters.put("numeroExpediente", documento.getCarpeta().getExpediente());
        parameters.put("isExhorto", isExhorto); // es un Exhorto
        parameters.put("isApelacion", Objects.equals(documento.getTipoDocumento(), TipoDocumento.APELACION));
        parameters.put("procedencia", "<b>Procedencia: </b>" + procedencia);

        if (documento.getCarpeta().getTipoPieza() != null) {
            parameters.put("tipoPieza", documento.getCarpeta().getTipoPieza().getTipo());
        }

        isExhorto = false;

        return JasperFillManager.fillReport(
                caratula.getInputStream(),
                parameters,
                new JREmptyDataSource());
    }

    public String getNombrePersonaByIdAndParte(Integer id, String parte) {
        List<Rol> rol = List.of(Rol.PRINCIPAL);
        List<PersonaDocumentoRecord> personas = personaDocumentoRepository.findPersonaAndTipoParteByCarpetaId(id, parte,
                rol);
        PersonaDocumentoRecord persona = personas.get(0);

        if (persona == null) {
            return "";
        }

        String nombre = persona.nombre() != null ? persona.nombre() : "";
        String apellidoPaterno = persona.apellidoPaterno() != null ? persona.apellidoPaterno() : "";
        String apellidoMaterno = persona.apellidoMaterno() != null ? persona.apellidoMaterno() : "";

        String nombreFormateado = String.format("%s %s %s", nombre, apellidoPaterno, apellidoMaterno).trim();
        if (personas.size() > 1) {
            return String.format("%s y otros.", nombreFormateado);
        }

        return nombreFormateado;
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
                case "PIEZA" -> "PZ";
                default -> "";
            };

        }
        result = prefijo + "." + documento.getCarpeta().getFolio();
        return result;
    }

    public String getExhortoPromocion(Documento documento) {
        isExhorto = documento.getCarpeta().getTipoCarpeta() == TipoCarpeta.EXHORTO;
        return Boolean.TRUE.equals(isExhorto) ? documento.getData().getExhortoProcedencia() : "";
    }

}
