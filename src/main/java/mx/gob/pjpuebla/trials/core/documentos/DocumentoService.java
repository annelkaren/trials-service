package mx.gob.pjpuebla.trials.core.documentos;


import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.anexos.Anexo;
import mx.gob.pjpuebla.trials.core.anexos.AnexoRecord;
import mx.gob.pjpuebla.trials.core.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.personasdocumentos.DocumentoPersonaRecord;
import mx.gob.pjpuebla.trials.core.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.core.personasdocumentos.PersonaDocumentoRepository;
import mx.gob.pjpuebla.trials.core.personasdocumentos.PersonaRecord;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.Estado;
import mx.gob.pjpuebla.trials.util.TipoDocumento;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class DocumentoService {

    private final DocumentoRepository documentoRepository ;
    private final JuzgadoRepository juzgadoRepository;
    private final TipoJuicioRepository tipoJuicioRepository;

    private final AnexoRepository anexoRepository;
    private final PersonaDocumentoRepository personaDocumentoRepository;
    private final TipoPartesRepository tipoPartesRepository;

    public boolean create(DocumentoPersonaRecord documentoPersona) {
        Documento documento = new Documento();
        Anexo anexo = new Anexo();

        documento.setJuzgado(null);
        documento.setTipoJuicio(tipoJuicioRepository.findById(documento.getTipoJuicio().getId())
                .orElseThrow(() -> new NoSuchElementException("Tipo Juicio no encontrado")));
        documentoRepository.save(documento);


        PersonaRecord actor = documentoPersona.persona().get(0);
        PersonaRecord demandado = documentoPersona.persona().get(1);
        PersonaDocumento personaActor = new PersonaDocumento();
        personaActor.setNombre(actor.nombre());
        personaActor.setApellidoMaterno(actor.apellidoMaterno());
        personaActor.setApellidoPaterno(actor.apelidoPaterno());
        personaActor.setPseudonimo(actor.pseudonimo());
        personaActor.setTipoPersona(actor.tipoPersona());

        personaActor.setDocumento(documento);
        personaActor.setTipoPartes(tipoPartesRepository.findById(personaActor.getTipoPartes().getId())
                .orElseThrow(() -> new NoSuchElementException("Tipo parte del actor no encontrada")));

        PersonaDocumento personaDemandado = new PersonaDocumento();
        personaDemandado.setNombre(demandado.nombre());
        personaDemandado.setApellidoMaterno(demandado.apellidoMaterno());
        personaDemandado.setApellidoPaterno(demandado.apelidoPaterno());
        personaDemandado.setPseudonimo(demandado.pseudonimo());
        personaDemandado.setTipoPersona(demandado.tipoPersona());
        personaDemandado.setTipoPartes(tipoPartesRepository.findById(personaDemandado.getTipoPartes().getId())
                .orElseThrow(() -> new NoSuchElementException("Tipo parte del demandado no encontrada")));


        List<PersonaDocumento> personaDocumentos = Arrays.asList(personaActor, personaDemandado);
        personaDocumentoRepository.saveAll(personaDocumentos);

        List<AnexoRecord> anexoRecords = documentoPersona.anexo();
        List<Anexo> anexos = anexoRecords.stream()
                        .map(anexoRecord ->
                                {
                                   anexo.setDocumento(documento);
                                   anexo.setNombre(anexoRecord.nombre());
                                   return  anexo;
                                }).collect()




        anexo.setDocumento(documento);

        return true;

    }




}

