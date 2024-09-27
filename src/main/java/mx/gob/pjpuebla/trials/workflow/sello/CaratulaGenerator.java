package mx.gob.pjpuebla.trials.workflow.sello;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.util.enums.Rol;
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
public class CaratulaGenerator {

    private final PersonaDocumentoRepository personaDocumentoRepository;
    private final DocumentoRepository documentoRepository;
    private final JuzgadoRepository juzgadoRepository;
    @Value("classpath:jasper/CaratulaReport.jasper")
    private Resource caratula;

    public byte[] exportToPdf(Integer id) throws JRException, IOException {
        Documento documento = documentoRepository.findById(id).orElseThrow();
        return JasperExportManager.exportReportToPdf(getReport(documento));
    }

    private JasperPrint getReport(Documento documento) throws IOException, JRException {
        String[] expendienteYear = getNoExpendienteYear(documento.getCarpeta().getExpediente());
        String actor = getNombrePersonaByIdAndParte(documento.getCarpeta().getId(), "Actor");
        String demandado = getNombrePersonaByIdAndParte(documento.getCarpeta().getId(), "Demandado");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("juzgado", documento.getCarpeta().getJuzgado().getNombre());
        parameters.put("expediente", expendienteYear[0]);
        parameters.put("year", expendienteYear[1]);
        parameters.put("documentoFolio", tipoDocumentoFolio(documento));
        parameters.put("actor", actor);
        parameters.put("demandado", demandado);
        parameters.put("codigoQR", expendienteYear[0]);//TODO. Eliminar si no es requerido en el reporte
        parameters.put("logotipoHeder", "src/main/resources/jasper/header.jpg");
        parameters.put("numeroExpediente", documento.getCarpeta().getExpediente());

        return JasperFillManager.fillReport(
                caratula.getInputStream(),
                parameters,
                new JREmptyDataSource());
    }

    private String[] getNoExpendienteYear(String expediente) {
        return expediente.split("/");
    }

    private String getNombrePersonaByIdAndParte(Integer id, String parte) {
        List<Rol> rol = Arrays.asList(Rol.PRINCIPAL);
        PersonaDocumentoRecord persona = personaDocumentoRepository.findPersonaAndTipoParteByCarpetaId(id, parte, rol);
        String apellidoMaterno = persona.apellidoPaterno() != null ? persona.apellidoMaterno() : "";
        return String.format("%s %s %s", persona.nombre(), persona.apellidoPaterno(), apellidoMaterno);
    }

    private String tipoDocumentoFolio(Documento documento){
        int tipoDocumentoOrdinal = documento.getCarpeta().getTipoCarpeta().ordinal();
        return tipoDocumentoOrdinal + "-" + documento.getCarpeta().getFolio();
    }
}
