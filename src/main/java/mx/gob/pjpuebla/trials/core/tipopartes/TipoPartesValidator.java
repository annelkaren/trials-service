package mx.gob.pjpuebla.trials.core.tipopartes;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class TipoPartesValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {

        return TipoPartes.class.equals(clazz);

    }

    @Override
    public void validate(Object target, Errors errors) {

        TipoPartes tipoPartes = (TipoPartes) target;

        ValidationUtils.rejectIfEmpty(errors, "nombre", "NotEmpty");
        if (tipoPartes.getNombre().length() < 5 || tipoPartes.getNombre().length() > 50) {
            errors.rejectValue("nombre", "Size.tipoPartesForm.snombre");
        }

    }
}
