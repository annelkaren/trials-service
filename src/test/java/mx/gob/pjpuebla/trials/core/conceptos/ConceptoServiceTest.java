package mx.gob.pjpuebla.trials.core.conceptos;

import mx.gob.pjpuebla.trials.error.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ConceptoServiceTest {

    @Mock
    private ConceptoRepository mockConceptoRepository;

    @InjectMocks
    private ConceptoService conceptoService;

    private Concepto concepto;
    @BeforeEach
    public void setUp() {
        concepto = ConceptoSetUp.createConcepto();
        ConceptoSetUp.createConceptoRecordResponse();
    }

    @Test
    void getAll_returns_page_when_data_exists() {
        List<Concepto> listPage = Collections.singletonList(concepto);
        given(mockConceptoRepository.findAll(any(PageRequest.class)))
                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size()));

        Page<ConceptoRecordResponse> page = conceptoService.getAll(PageRequest.of(0, listPage.size()));

        assertThat(page.getContent())
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("id", concepto.getId())
                .hasFieldOrPropertyWithValue("nombre", concepto.getNombre())
                .hasFieldOrPropertyWithValue("dias", concepto.getDias())
                .hasFieldOrPropertyWithValue("tipoConcepto", concepto.getTipoConcepto())
                .hasFieldOrPropertyWithValue("estado", concepto.getEstado());
    }

    @Test
    void getById_returns_conceptoRecordResponse() {
        given(mockConceptoRepository.findById(concepto.getId()))
                .willReturn(Optional.of(concepto));

        ConceptoRecordResponse result = conceptoService.findById(concepto.getId());
        assertThat(result).isOfAnyClassIn(ConceptoRecordResponse.class)
                .hasFieldOrPropertyWithValue("id", concepto.getId())
                .hasFieldOrPropertyWithValue("nombre", concepto.getNombre())
                .hasFieldOrPropertyWithValue("dias", concepto.getDias())
                .hasFieldOrPropertyWithValue("tipoConcepto", concepto.getTipoConcepto())
                .hasFieldOrPropertyWithValue("estado", concepto.getEstado());
    }

    @Test
    void getById_returns_not_found() {
        Integer id = concepto.getId();
        given(mockConceptoRepository.findById(id))
                .willReturn(Optional.empty());

        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> conceptoService.findById(concepto.getId())
        );

        assertThat(assertThrows.getMessage()).contains("Concepto no encontrado");
    }
}
