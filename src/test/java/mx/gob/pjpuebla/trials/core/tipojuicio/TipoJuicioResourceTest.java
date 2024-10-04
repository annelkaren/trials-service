package mx.gob.pjpuebla.trials.core.tipojuicio;

import org.springframework.http.MediaType;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TipoJuicioResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class TipoJuicioResourceTest {

    @MockBean
    private TipoJuicioService mockTipoJuicioService;

    @Autowired
    private MockMvc mockMvc;

    private TipoJuicioRecord validTipoJuicioRecord;

    @BeforeEach
    void setUp() {
        validTipoJuicioRecord = TipoJuicioSetUp.createTipoJuicioRecord();
    }

    @Test
    void getAllByNameAndActiveAndTipoSistemaNameAndMateriaName_success() throws Exception {
        given(mockTipoJuicioService.getAllActive(any(Pageable.class), any(TipoJuicio.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(validTipoJuicioRecord)));

        mockMvc.perform(
                get("/api/core/tipojuicio")
                        .param("tipoJuicioName", "PE")
                        .param("tipoSistemaNombre", "PE")
                        .param("tipoJuicioNombre", "PE")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }


    @ParameterizedTest
    @ValueSource(strings = {"tipoJuicioName", "materiaNombre", "tipoSistemaNombre"})
    void getAllByFieldAndActive_success(String field) throws Exception {
        given(mockTipoJuicioService.getAllActive(any(Pageable.class), any(TipoJuicio.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(validTipoJuicioRecord)));

        mockMvc.perform(
                get("/api/core/tipojuicio")
                        .param(field, "PE")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void getAllByMateriaAndActive_success() throws Exception {
        given(mockTipoJuicioService.getAllActive(any(Pageable.class), any(TipoJuicio.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(validTipoJuicioRecord)));

        mockMvc.perform(
                get("/api/core/tipojuicio")
                        .param("materiaNombre", "PE")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }


    @Test
    void getAllByTipoSistemaAndActive_success() throws Exception {
        given(mockTipoJuicioService.getAllActive(any(Pageable.class), any(TipoJuicio.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(validTipoJuicioRecord)));

        mockMvc.perform(
                get("/api/core/tipojuicio")
                        .param("tipoSistemaNombre", "PE")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void getById_success() throws Exception {
        given(mockTipoJuicioService.findById(anyInt()))
                .willReturn(validTipoJuicioRecord);

        mockMvc.perform(
                get("/api/core/tipojuicio/1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void getById_not_found() throws Exception {
        given(mockTipoJuicioService.findById(anyInt()))
                .willThrow(NotFoundException.class);

        mockMvc.perform(
                get("/api/core/tipojuicio/0")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    void getById_invalid() throws Exception {
        given(mockTipoJuicioService.findById(anyInt()))
                .willThrow(MethodArgumentTypeMismatchException.class);

        mockMvc.perform(
                get("/api/core/tipojuicio/A")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void getTiposJuiciosOralidad() throws Exception {
        
        // Creación de una lista de ejemplo de TipoJuicioDemandasRecord
        List<TipoJuicioDemandasRecord> tipoJuicios = Arrays.asList(
                new TipoJuicioDemandasRecord(1, "Familiar Oralidad (Alimentos)"),
                new TipoJuicioDemandasRecord(2, "Familiar Oralidad (Divorcio)")
        );
        // Simulación del comportamiento del servicio
        given(mockTipoJuicioService.getAllTipoJuicios()).willReturn(tipoJuicios);
        
                mockMvc.perform(
                get("/api/core/tipojuicio/oralidad")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
   
        }

}