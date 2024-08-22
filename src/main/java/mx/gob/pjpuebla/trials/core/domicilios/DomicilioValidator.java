package mx.gob.pjpuebla.trials.core.domicilios;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class DomicilioValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {

        return Domicilio.class.equals(clazz);

    }

    @Override
    public void validate(Object target, Errors errors) {

        Domicilio domicilio = (Domicilio) target;

        ValidationUtils.rejectIfEmpty(errors, "calle", "NotEmpty");
        if (domicilio.getCalle().length() < 20 || domicilio.getCalle().length() > 250) {

            errors.rejectValue("scalle", "Size.domicilioForm.scalle");

        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "interior", "NotEmpty");
        if (domicilio.getInterior().isEmpty() || domicilio.getInterior().length() > 20) {

            errors.rejectValue("interior", "Size.domicilioForm.interior");

        }

        ValidationUtils.rejectIfEmpty(errors, "exterior", "NotEmpty");
        if (domicilio.getExterior().isEmpty() || domicilio.getExterior().length() > 20) {

            errors.rejectValue("exterior", "Size.domicilioForm.exterior");

        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "colonia", "NotEmpty");
        if (domicilio.getColonia().length() < 10 || domicilio.getColonia().length() > 250) {

            errors.rejectValue("colonia", "Size.domicilioForm.colonia");

        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "municipio", "NotEmpty");
        if (domicilio.getMunicipio().length() < 5 || domicilio.getMunicipio().length() > 250) {

            errors.rejectValue("municipio", "Size.domicilioForm.municipio");

        }

        ValidationUtils.rejectIfEmpty(errors, "estado", "NotEmpty");
        if (domicilio.getEstado().length() < 5 || domicilio.getEstado().length() > 250) {

            errors.rejectValue("estado", "Size.domicilioForm.estado");

        }

    }

}