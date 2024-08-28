package mx.gob.pjpuebla.trials.core.organismos;

import mx.gob.pjpuebla.trials.error.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;


import java.util.*;

import static mx.gob.pjpuebla.trials.core.organismos.OrganismoSetup.CreateOrganismo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrganismoServiceTest {


    @Mock
    public OrganismoRepository organismoRepository;

    @InjectMocks
    public  OrganismoService target;

    private Organismo validOrganismo;

    @BeforeEach
    public void setUp() {
        validOrganismo = CreateOrganismo();
    }

    @Test
    void getAll_return_list() {
        List<Organismo> listPage = Collections.singletonList(validOrganismo);
        Page<Organismo> page = new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size());

        given(organismoRepository.findAll(any(Example.class), any(PageRequest.class)))
                .willReturn(page);

        List<OrganismoRecord> resultList =  target.getAll(PageRequest.of(0, 1), validOrganismo);
        assertThat(resultList)
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("id", validOrganismo.getId())
                .hasFieldOrPropertyWithValue("nombre", validOrganismo.getNombre());
    }

    @Test
    void getAll_return_not_found() {
        when(organismoRepository.findAll(any(Example.class), any(Pageable.class)))
                .thenReturn(Page.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> target.getAll(PageRequest.of(0, 1), new Organismo())
        );
        assertThat(exception.getMessage()).contains("Organismos no encontrados");
    }

}