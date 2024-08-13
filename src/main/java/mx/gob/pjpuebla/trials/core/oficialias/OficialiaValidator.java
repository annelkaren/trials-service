package mx.gob.pjpuebla.trials.core.oficialias;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class OficialiaValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {

        return Oficialia.class.equals(clazz);

    }

    @Override
    public void validate(Object target, Errors errors) {

        Oficialia oficialia = (Oficialia) target;

        ValidationUtils.rejectIfEmpty(errors, "nombre", "NotEmpty");
        if (oficialia.getNombre().length() < 5 || oficialia.getNombre().length() > 50) {
            errors.rejectValue("nombre", "Size.oficialiaForm.snombre");
        }

    }
}