package mx.gob.pjpuebla.trials.core.juzgados;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.sedes.SedeRepository;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.Estado;
import mx.gob.pjpuebla.trials.util.Response;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class JuzgadoService {

    private final JuzgadoRepository juzgadoRepository;
    private final SedeRepository sedeRepository;

    @Transactional(readOnly = true)
    public Page<JuzgadoRecordResponse> getAll(Juzgado example, Pageable pageable) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Page<Juzgado> page = juzgadoRepository.findAll(Example.of(example, exampleMatcher), pageable);

        List<JuzgadoRecordResponse> list = page.getContent().stream()
                .map(juzgado -> new JuzgadoRecordResponse(
                        juzgado.getId(), juzgado.getNombre(), juzgado.getEstado(),
                        juzgado.getMateria().getNombre()))
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public JuzgadoRecord findById(Integer id) {
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        return juzgadoRepository.findByIdAndEstadoIn(id, estados)
                .orElseThrow(() -> new NotFoundException("Juzgado no encontrado", "juzgadoId"));
    }

    public Response create(Juzgado juzgado) {
        Response response = new Response();
        try {
            juzgado = this.juzgadoRepository.save(juzgado);
            response.setMessage("El juzgado fue guardado con el id: " + juzgado.getId());
        } catch (Exception ex) {
            response.setMessage("Excepción. Error al guardar el registro.");
        }
        return response;
    }

    public Response update(Juzgado juzgado) {
        Response response = new Response();
        try {
            this.juzgadoRepository.save(juzgado);
            response.setMessage("Juzgado actualizado con id: " + juzgado.getId());
        } catch (OptimisticLockingFailureException ex) {
            response.setMessage("El registro fue actualizado o eliminado por otra transaccion");
        } catch (Exception ex) {
            response.setMessage("Error al actualizar el registro.");
        }
        return response;
    }

    public Response delete(Integer id) {
        Response response = new Response();
        try {
            this.juzgadoRepository.deleteById(id);
            response.setMessage("Juzgado eliminado con id: " + id);
        } catch (Exception ex) {
            response.setMessage("Excepción. Error al eliminar el juzgado.");
        }
        return response;
    }
}