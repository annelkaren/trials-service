package mx.gob.pjpuebla.trials.core.oficialias;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.distritos.DistritoRecord;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRecord;
import mx.gob.pjpuebla.trials.core.sedes.SedeRecord;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaRecord;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.Response;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class OficialiaService {

    private final OficialiaRepository oficialiaRepository;

    @Transactional(readOnly = true)
    public Page<OficialiaRecord> getAllActive(Pageable pageable, Oficialia example) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("estado", ExampleMatcher.GenericPropertyMatchers.ignoreCase());

        Page<Oficialia> page = oficialiaRepository.findAll(Example.of(example.setEstado(Estado.ACTIVE)), pageable);
        List<OficialiaRecord> list = page.getContent().stream()
                .map(m -> new OficialiaRecord(m.getId(), m.getVersion(), m.getEstado(),new TipoOficialiaRecord(m.getTipo().getId(), m.getTipo().getNombre()),m.getNombre(), m.getDomicilio(), m.getResponsable(), new SedeRecord(m.getSede().getId(), m.getSede().getVersion(), m.getSede().getNombre(), m.getSede().getEstado(), m.getSede().getTipo(), m.getSede().getTelefono(), m.getSede().getExtension(), new DistritoRecord(m.getSede().getId(), m.getSede().getNombre()), new DomicilioRecord(m.getDomicilio().longValue(), m.getDomicilio().toString(), m.getDomicilio().toString(), m.getDomicilio().toString(), m.getDomicilio().toString(), m.getDomicilio().toString(), m.getDomicilio().toString(), m.getDomicilio().toString(), m.getDomicilio().toString(), m.getDomicilio().toString()))))
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    public Response findById(Integer id) {
        Response response = new Response();
        try {
            response.setMessage("La solicitud se ha completado satisfactoriamente.");
            response.setData(oficialiaRepository.findById(id).orElse(null));  // tipo de dato: Juzgado
        } catch (Exception ex) {
            response.setMessage("Excepción. Error al obtener Oficialia");
        }
        return response;
    }

    public Response create(Oficialia oficialia, BindingResult bindingResult) {
        Response response = new Response();
        OficialiaValidator validator = new OficialiaValidator();
        try {
            validator.validate(oficialia, bindingResult);
            if (bindingResult.hasErrors()) {
                response.setMessage("Error al guardar el registro por validacion: " + bindingResult.toString());
            } else {
                this.oficialiaRepository.save(oficialia);
                response.setMessage("Tipo Parte fue guardado con el UUID: " + oficialia.getId());
            }
        } catch (Exception ex) {
            response.setMessage("Excepción. Error al guardar el registro.");
        }
        return response;
    }

    public Response update(Oficialia oficialia) {
        Response response = new Response();
        try {
            this.oficialiaRepository.save(oficialia);
            response.setMessage("Oficializa actualizado.");
        } catch (Exception ex) {
            response.setMessage("Excepción. Error al actualizar Oficialia.");
        }
        return response;
    }

    public Response delete(Integer id) {
        Response response = new Response();
        try {
            this.oficialiaRepository.deleteById(id);
            response.setMessage("Oficialia eliminado.");
        } catch (Exception ex) {
            response.setMessage("Excepción. Error al eliminar Oficialia.");
        }
        return response;
    }

}