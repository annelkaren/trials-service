package mx.gob.pjpuebla.trials.core.organismos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;


import java.util.*;

import static mx.gob.pjpuebla.trials.core.organismos.OrganismoSetup.createOrganismo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrganismoServiceTest {


    @Mock
    public OrganismoRepository organismoRepository;

    @InjectMocks
    public  OrganismoService target;

    private Organismo validOrganismo;

    @BeforeEach
    public void setUp() {
        validOrganismo = createOrganismo();
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

}