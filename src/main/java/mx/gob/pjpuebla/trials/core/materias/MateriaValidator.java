package mx.gob.pjpuebla.trials.core.materias;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class MateriaValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {

        return Materia.class.equals(clazz);

    }

    @Override
    public void validate(Object target, Errors errors) {

        Materia materia = (Materia) target;

        ValidationUtils.rejectIfEmpty(errors, "nombre", "NotEmpty");
        if (materia.getNombre().length() > 101) {
            errors.rejectValue("nombre", "Size.materiaForm.nombre");
        }

    }


}
