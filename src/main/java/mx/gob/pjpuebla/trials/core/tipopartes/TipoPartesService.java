package mx.gob.pjpuebla.trials.core.tipopartes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class TipoPartesService {

    private final TipoPartesRepository tipoPartesRepository;
    private final DocumentoRepository documentoRepository;

    public Page<TipoPartesRecord> getAll(Pageable pageable, TipoPartes example) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("estado", ExampleMatcher.GenericPropertyMatchers.ignoreCase());

        Page<TipoPartes> page = tipoPartesRepository.findAll(Example.of(example.setEstado(Estado.ACTIVE), exampleMatcher), pageable);
        List<TipoPartesRecord> list = page.getContent().stream()
                .map(m -> new TipoPartesRecord(m.getId(), m.getNombre(), m.getTipoJuicio().getNombre()))
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public TipoPartesRecord findById(Integer id) {
        TipoPartes tipoPartes = tipoPartesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("TipoPartes no encontrada", "id"));
        return new TipoPartesRecord(tipoPartes.getId(), tipoPartes.getNombre(), tipoPartes.getTipoJuicio().getNombre());
    }

    public List<TipoPartesRecord> findByTipoJuicioId(Integer materiaId) {
        List<TipoPartes> tipoPartes = tipoPartesRepository.findByTipoJuicioId(materiaId);
        return tipoPartes.stream().map(entity -> new TipoPartesRecord(entity.getId(), entity.getNombre(), entity.getTipoJuicio().getNombre())).toList();
    }

    public List<TipoPartesRecord> getTipoPartesByDocumentoId(Integer documentoId) {
        Documento documento = documentoRepository.findById(documentoId)
            .orElseThrow(() -> new NotFoundException("Documento no encontrado", documentoId.toString()));
    
        Carpeta carpeta = documento.getCarpeta();
        if (carpeta == null) {
            throw new NotFoundException("Carpeta no encontrada", "la carpeta es null");
        }
        
        TipoJuicio tipoJuicio = carpeta.getTipoJuicio();
        if (tipoJuicio == null) {
            throw new NotFoundException("Tipo de juicio no encontrado", "El tipo de juicio es null");
        }
    
        List<TipoPartes> tiposPartes = tipoPartesRepository.findByTipoJuicioId(tipoJuicio.getId());

        return tiposPartes.stream()
        .map(tipoParte -> new TipoPartesRecord(tipoParte.getId(), tipoParte.getNombre(), tipoJuicio.getNombre()))
        .collect(Collectors.toList());
    }
}