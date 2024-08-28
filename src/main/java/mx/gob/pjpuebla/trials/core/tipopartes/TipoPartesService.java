package mx.gob.pjpuebla.trials.core.tipopartes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRecord;
import mx.gob.pjpuebla.trials.util.Estado;
import mx.gob.pjpuebla.trials.util.Response;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TipoPartesService {

    private final TipoPartesRepository tipoPartesRepository;

    public Page<TipoPartesRecord> getAll(Pageable pageable, TipoPartes tipoPartes) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("estado", ExampleMatcher.GenericPropertyMatchers.ignoreCase());

        Page<TipoPartes> page = tipoPartesRepository.findAll(pageable);
        List<TipoPartesRecord> list = page.getContent().stream()
                .map(m -> new TipoPartesRecord(m.getId(), m.getNombre()))
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    public Response findById(Integer id) {
        Response response = new Response();
        try {
            response.setMessage("La solicitud se ha completado satisfactoriamente.");
            response.setData(tipoPartesRepository.findById(id).orElse(null));
        } catch (Exception ex) {
            log.error("findById ", ex);
            response.setMessage("Excepción. Error al obtener tipo partes id");
        }
        return response;
    }

    public Response findByMateriaId(Integer materiaId) {
        Response response = new Response();
        try {
            response.setMessage("La solicitud se ha completado satisfactoriamente.");
            response.setData(tipoPartesRepository.findByMateriaId(materiaId));
        } catch (Exception ex) {
            log.error("findByMateriaId ", ex);
            response.setMessage("Excepción. Error al obtener tipo partes por materia id");
        }
        return response;
    }
}