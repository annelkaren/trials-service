package mx.gob.pjpuebla.trials.workflow.asistenciaaudiencia;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.documentoidentificacion.DocumentoIdentificacion;
import mx.gob.pjpuebla.trials.core.documentoidentificacion.DocumentoIdentificacionRepository;
import mx.gob.pjpuebla.trials.util.enums.Asistencia;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.asistenciaaudiencia.records.RegistrarAsistenciaAudienciaRecord;
import mx.gob.pjpuebla.trials.workflow.audiencias.Audiencia;
import mx.gob.pjpuebla.trials.workflow.audiencias.AudienciaRepository;
import mx.gob.pjpuebla.trials.workflow.audiencias.record.AudienciasResponseRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.DigitalizacionService;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class AsistenciaAudienciaService {

    private static final String AUDIENCIA_NOT_FOUND = "Audiencia no encontrada";
    private static final String PERSONA_DOCUMENTO_NOT_FOUND = "Persona documento no encontrada";
    private static final String DOCUMENTO_IDENTIFICACION_NOT_FOUND = "Documento identificación no encontrado";
    private static final String DOCUMENTO_NOT_FOUND = "Documento no encontrado";
    private final AsistenciaAudienciaRepository asistenciaAudienciaRepository;
    private final AudienciaRepository audienciaRepository;
    private final PersonaDocumentoRepository personaDocumentoRepository;
    private final DocumentoIdentificacionRepository documentoIdentificacionRepository;
    private final DocumentoRepository documentoRepository;
    private final DigitalizacionService digitalizacionService;

    public Page<AsistenciaAudienciaResponse> getAll(Pageable pageable) {

        Page<AsistenciaAudiencia> page = asistenciaAudienciaRepository.findAll(pageable);

        List<AsistenciaAudienciaResponse> responses = page.getContent().stream()
                .map(asistenciaAudiencia -> new AsistenciaAudienciaResponse(
                        asistenciaAudiencia.getId(),
                        asistenciaAudiencia.getPersonaDocumento().getId(),
                        new AudienciasResponseRecord(asistenciaAudiencia.getAudiencia().getId(), asistenciaAudiencia.getAudiencia().getEstatusAudiencia()),
                        asistenciaAudiencia.getAsistencia(),
                        asistenciaAudiencia.getDocumentoIdentificacion().getName(),
                        asistenciaAudiencia.getUrlDocumento()
                ))
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, page.getTotalElements());
    }

    public AsistenciaAudiencia registrarAsistencia (RegistrarAsistenciaAudienciaRecord asistenciaAudienciaRecord, MultipartFile multipartFile) {

        Documento documento = new Documento();

        PersonaDocumento personaDocumento = personaDocumentoRepository.findById(asistenciaAudienciaRecord.idPersonaDocumento())
                .orElseThrow(() -> new IllegalArgumentException(PERSONA_DOCUMENTO_NOT_FOUND));

        Audiencia audiencia = audienciaRepository.findById(asistenciaAudienciaRecord.idAudiencia())
                .orElseThrow(() -> new IllegalArgumentException(AUDIENCIA_NOT_FOUND));

        DocumentoIdentificacion documentoIdentificacion = documentoIdentificacionRepository.findById(asistenciaAudienciaRecord.idDocumentoIdentificacion())
                .orElseThrow(() -> new IllegalArgumentException(DOCUMENTO_IDENTIFICACION_NOT_FOUND));

        documento.setCarpeta(audiencia.getCarpeta());
        documento.setFechaAsignacion(null);
        documento.setPersona(null);
        documento.setTipoDocumento(TipoDocumento.DOCUMENTO_IDENTIFICACION);
        documentoRepository.save(documento);

        digitalizacionService.setAudienciaId(asistenciaAudienciaRecord.idAudiencia());
        digitalizacionService.guardarArchivo(multipartFile, documento.getId());

        Documento documentoRuta = documentoRepository.findById(documento.getId())
                .orElseThrow(() -> new IllegalArgumentException(DOCUMENTO_NOT_FOUND));

        AsistenciaAudiencia asistenciaAudiencia = new AsistenciaAudiencia()
                .setPersonaDocumento(personaDocumento)
                .setAudiencia(audiencia)
                .setDocumentoIdentificacion(documentoIdentificacion)
                .setUrlDocumento(documentoRuta.getRuta())
                .setAsistencia(Asistencia.SI);

        return asistenciaAudienciaRepository.save(asistenciaAudiencia);

    }

}
