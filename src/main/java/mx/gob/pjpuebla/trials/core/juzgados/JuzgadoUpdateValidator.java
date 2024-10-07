package mx.gob.pjpuebla.trials.core.juzgados;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class JuzgadoUpdateValidator implements Validator {

    private final JuzgadoRepository juzgadoRepository;


    @Override
    public boolean supports(@Nullable Class<?> clazz) {
        if (clazz != null)
            return Juzgado.class.isAssignableFrom(clazz);
        return false;
    }

    @Override
    public void validate(@Nullable Object target, @Nullable Errors errors) throws NullPointerException {
        if (target == null)
            return;
        Juzgado juzgado = (Juzgado) target;
        Juzgado entity = juzgadoRepository.findById(juzgado.getId()).orElseThrow(() -> new NotFoundException("Juzgado no encontrado", "juzgadoId"));
    }
}
