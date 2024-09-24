package mx.gob.pjpuebla.trials.core.domicilio;

import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioService;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DomicilioServiceTest {

    @Mock
    DomicilioRepository mockDomicilioRepository;

    @InjectMocks
    DomicilioService domicilioService;

    private Domicilio domicilio;

    @BeforeEach
    public void setUp() {
        domicilio = DomicilioSetUp.createDomicilio();
    }

    @Test
    void getById_return_domicilio() {
        given(mockDomicilioRepository.findById(domicilio.getId()))
                .willReturn(Optional.ofNullable(domicilio));

        Domicilio entity = domicilioService.findById(domicilio.getId());
        assertThat(entity).isOfAnyClassIn(Domicilio.class)
                .hasFieldOrPropertyWithValue("id", domicilio.getId())
                .hasFieldOrPropertyWithValue("calle", domicilio.getCalle());
    }

    @Test
    void getById_return_not_found() {
        given(mockDomicilioRepository.findById(domicilio.getId()))
                .willReturn(Optional.empty());
        long domicilioId = domicilio.getId();
        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> {
                    domicilioService.findById(domicilioId);
                }
        );

        assertThat(assertThrows.getMessage()).contains("Domicilio no encontrado");
    }

    @Test
    void save() {
        given(mockDomicilioRepository.save(domicilio))
                .willReturn(domicilio);

        Domicilio entity = domicilioService.save(domicilio);
        assertThat(entity).isOfAnyClassIn(Domicilio.class)
                .hasFieldOrPropertyWithValue("id", domicilio.getId())
                .hasFieldOrPropertyWithValue("calle", domicilio.getCalle());
    }
}
