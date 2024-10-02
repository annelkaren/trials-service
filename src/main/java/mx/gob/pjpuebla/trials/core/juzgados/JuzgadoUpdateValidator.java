package mx.gob.pjpuebla.trials.core.juzgados;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class JuzgadoUpdateValidator implements Validator {

    private final JuzgadoRepository juzgadoRepository;


    @Override
    public boolean supports(Class<?> clazz) {
        return Juzgado.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Juzgado juzgado = (Juzgado) target;
        Juzgado entity = juzgadoRepository.findById(juzgado.getId()).orElseThrow(() -> new NotFoundException("Juzgado no encontrado", "juzgadoId"));
    }
}
