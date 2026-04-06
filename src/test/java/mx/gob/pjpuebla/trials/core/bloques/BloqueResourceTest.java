package mx.gob.pjpuebla.trials.core.bloques;

import jakarta.ws.rs.core.MediaType;
import mx.gob.pjpuebla.trials.core.utils.resource.ResourceUtilTest;
import mx.gob.pjpuebla.trials.error.InvalidVersionException;
import mx.gob.pjpuebla.trials.error.NotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BloqueResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class BloqueResourceTest {

	@MockBean
	private BloqueService mockBloqueService;

	@Autowired
	private MockMvc mockMvc;

	private BloqueRecordResponse bloqueRecordResponse;

	@BeforeEach
	void setUp() {
		bloqueRecordResponse = BloqueSetUp.createBloqueRecordResponse();
	}

	@Test
	void getAll_success() throws Exception {
		given(mockBloqueService.getAll(anyString(), any(Pageable.class)))
				.willReturn(new PageImpl<>(Collections.singletonList(bloqueRecordResponse)));

		mockMvc.perform(
				get("/api/core/bloques")
						.param("nombre", "B")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void getById_success() throws Exception {
		given(mockBloqueService.findById(anyInt()))
				.willReturn(bloqueRecordResponse);

		mockMvc.perform(
				get("/api/core/bloques/1")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void getById_not_found() throws Exception {
		given(mockBloqueService.findById(anyInt()))
				.willThrow(NotFoundException.class);

		mockMvc.perform(
				get("/api/core/bloques/0")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	void getById_invalid() throws Exception {
		given(mockBloqueService.findById(anyInt()))
				.willThrow(MethodArgumentTypeMismatchException.class);

		mockMvc.perform(
				get("/api/core/bloques/X")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	void create_success() throws Exception {
		given(mockBloqueService.create(any(Bloque.class)))
				.willReturn(bloqueRecordResponse);

		mockMvc.perform(
				post("/api/core/bloques")
						.content(ResourceUtilTest.asJsonString(BloqueSetUp.createBloque()))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void update_success() throws Exception {
		given(mockBloqueService.update(any(Bloque.class)))
				.willReturn(bloqueRecordResponse);

		mockMvc.perform(
				put("/api/core/bloques")
						.content(ResourceUtilTest.asJsonString(BloqueSetUp.createBloque()))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void update_error() throws Exception {
		given(mockBloqueService.update(any(Bloque.class)))
				.willThrow(InvalidVersionException.class);

		mockMvc.perform(
				put("/api/core/bloques")
						.content(ResourceUtilTest.asJsonString(BloqueSetUp.createBloque()))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	void delete_success() throws Exception {
		mockMvc.perform(
				delete("/api/core/bloques/1")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

}