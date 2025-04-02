package mx.gob.pjpuebla.trials.core.juzgados;

import mx.gob.pjpuebla.trials.core.catalogos.sedes.Sede;
import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.sedes.SedeSetUp;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaSetUp;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.util.List;
import java.util.Optional;

import static mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp.createTipoJuicio;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class JuzgadoUpdateValidatorTest {

    @InjectMocks
    JuzgadoUpdateValidator juzgadoUpdateValidator;

    @Mock
    JuzgadoRepository juzgadoRepository;

    @Test
    void validate() {
        Materia materia = MateriaSetUp.createMateria();
        Distrito distrito = DistritoSetUp.createDistrito();
        Domicilio domicilio = DomicilioSetUp.createDomicilio();
        Sede sede = SedeSetUp.createSede();
        sede.setDistrito(distrito);
        sede.setDomicilio(domicilio);
        TipoSistema tipoSistema = TipoSistemaSetUp.createTipoSistema();
        TipoJuicio tipoJuicio = createTipoJuicio(tipoSistema, materia);
        Juzgado juzgado = JuzgadoSetUp.createJuzgado(materia, sede)
                .setTipoJuicios(List.of(tipoJuicio));

        given(juzgadoRepository.findById(juzgado.getId()))
                .willReturn(Optional.empty());

        Errors errors = new BeanPropertyBindingResult(juzgado, "juzgado");

        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> juzgadoUpdateValidator.validate(juzgado, errors)
        );

        assertThat(assertThrows.getMessage()).contains("Juzgado no encontrado");

    }
}