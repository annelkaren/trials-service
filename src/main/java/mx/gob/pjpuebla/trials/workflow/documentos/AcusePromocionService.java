package mx.gob.pjpuebla.trials.workflow.documentos;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaRepository;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoRepository;

import java.util.HashMap;
import java.util.Map;

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
    

    public byte[] exportToPdf(String tipo, Integer documentoId){

        Documento promocion = documentoRepository.findById(documentoId).orElseThrow(
            () -> new NotFoundException("Documento no encontrado", "documentoId"));

        Carpeta expediente = promocion.getCarpeta();
        Persona litigante = personaRepository.findByUsuario(promocion.getAudit().getUsuarioAlta());
        Boolean isEnvio = tipo.equals("envio");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("p_juzgado", expediente.getJuzgado().getNombre());
        parameters.put("p_expediente", expediente.getExpediente());
        parameters.put("p_folio", promocion.getFolio());
        parameters.put("p_fecha_envio", promocion.getAudit().getFechaAlta().toString());
        parameters.put("p_hora_envio", promocion.getAudit().getFechaAlta().toLocalTime());
        parameters.put("p_promovente", litigante.getCorreoElectronico());
        parameters.put("p_tipo_promocion", "");
        parameters.put("p_is_envio", isEnvio);
        parameters.put("p_fecha_recepcion", );
        parameters.put("p_hora_recepcion", );
        parameters.put("p_puesto_recibe", );
        parameters.put("p_recibe", );

    }

}
