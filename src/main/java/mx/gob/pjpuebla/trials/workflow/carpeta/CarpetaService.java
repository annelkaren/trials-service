package mx.gob.pjpuebla.trials.workflow.carpeta;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.Rol;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.ApelacionRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.ApelacionRecordResponse;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.ApelacionPersonaRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.CarpetaResponseRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoService;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class CarpetaService {

    private final CarpetaRepository carpetaRepository;
    private final JuzgadoRepository juzgadoRepository;
    private final PersonaDocumentoRepository personaDocumentoRepository;
    private final TipoPartesRepository tipoPartesRepository;
    private final DocumentoService documentoService;
    private final DocumentoRepository documentoRepository;
    private final AnexoRepository anexoRepository;
    private final TipoJuicioRepository tipoJuicioRepository;

    public CarpetaResponseRecord getCarpetaResponseByNumExpYearJuzgado(String expediente, Integer juzgadoId) {
        Carpeta carpeta = carpetaRepository.findByExpedienteAndJuzgadoId(expediente, juzgadoId).orElseThrow(() -> new NotFoundException("Carpeta no encontrada", expediente + " - " + juzgadoId));
        String actor = getNombrePersonaByIdAndParte(carpeta.getId(), "Actor");
        String demandado = getNombrePersonaByIdAndParte(carpeta.getId(), "Demandado");
        return new CarpetaResponseRecord(carpeta.getId(), actor, demandado);
    }

    protected String getNombrePersonaByIdAndParte(Integer id, String parte) {
        List<Rol> rol = List.of(Rol.PRINCIPAL);
        PersonaDocumentoRecord persona = personaDocumentoRepository.findPersonaAndTipoParteByCarpetaId(id, parte, rol);

        String nombre = persona.nombre() != null ? persona.nombre() : "";
        String apellidoPaterno = persona.apellidoPaterno() != null ? persona.apellidoPaterno() : "";
        String apellidoMaterno = persona.apellidoMaterno() != null ? persona.apellidoMaterno() : "";

        return String.format("%s %s %s", nombre, apellidoPaterno, apellidoMaterno).trim();
    }

    @Transactional(readOnly = true)
    public List<ApelacionRecordResponse> getPersonasDocumentoByCarpetaId(Integer carpetaId) {
        return carpetaRepository.findPersonaDocumentoByCarpetaId(carpetaId);
    }

    public DocumentoRecord createApelacion(ApelacionRecord apelacionRecord) {
        Documento documento = new Documento();
        Carpeta carpeta = new Carpeta();

        Carpeta carpetaPadre = carpetaRepository.findById(apelacionRecord.carpetaId())
                .orElseThrow(() -> new NotFoundException("Carpeta no encontrada", "carpetaId"));

        carpeta.setTipoJuicio(tipoJuicioRepository.findById(carpetaPadre.getTipoJuicio().getId())
                .orElseThrow(() -> new NotFoundException("Tipo Juicio no encontrado", "tipoJuicioId")));

        // TODO.ASIGNAR JUZGADO CORRECTAMENTE
        carpeta.setJuzgado(juzgadoRepository.findById(51).get());
        carpeta.setFolio("1");


        carpeta.setExpediente(documentoService.generateNumExpediente(carpeta.getJuzgado(), TipoCarpeta.APELACION));
        carpeta.setTipoCarpeta(TipoCarpeta.APELACION);
        carpeta.setEstatus(EstadoCarpeta.CAPTURA);
        carpeta.setSelloEstatus(SelloEstatus.VALIDO);
        carpeta = carpetaRepository.save(carpeta);
        documento.setCarpeta(carpeta);
        DocumentoData data = new DocumentoData();
        data.setApelacionOtroActorNombre(apelacionRecord.otroNombreActor());
        data.setApelacionOtroDemandadoNombre(apelacionRecord.otroNombreDemandado());
        data.setApelacionAntecedenteCarpeta(apelacionRecord.carpetaId().toString());
        documento.setData(data);
        documento = documentoRepository.save(documento);

        for (Anexo anexo : apelacionRecord.anexos()) {
            Anexo entity = new Anexo();
            entity.setNombre(anexo.getNombre());
            entity.setDocumento(documento);
            anexoRepository.save(entity);
        }

        for (ApelacionPersonaRecord persona : apelacionRecord.apelacionPersonaRecords()) {
            PersonaDocumento entity = new PersonaDocumento();
            entity.setNombre(persona.nombre());
            entity.setApellidoPaterno(persona.apellidoPaterno());
            entity.setApellidoMaterno(persona.apellidoMaterno());
            entity.setPseudonimo(persona.pseudonimo());
            entity.setTipoPersona(persona.tipoPersona());
            entity.setRol(Rol.SECUNDARIO);
            entity.setTipoPartes(tipoPartesRepository.findById(persona.tipoPartes())
                    .orElseThrow(() -> new EntityNotFoundException("TipoPartes no encontrado")));
            entity.setCarpeta(carpeta);
            personaDocumentoRepository.save(entity);
        }

        return new DocumentoRecord(documento.getId(), carpeta.getFolio(), documento.getCarpeta().getTipoCarpeta());
    }


}
