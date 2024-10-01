package mx.gob.pjpuebla.trials.workflow.carpeta;

import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoSetUp;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.CarpetaResponseRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CarpetaServiceTest {

    @Mock
    CarpetaRepository carpetaRepository;

    @Mock
    PersonaDocumentoRepository personaDocumentoRepository;

    @InjectMocks
    CarpetaService target;

    private Carpeta validCarpeta;
    private PersonaDocumentoRecord actor;
    private PersonaDocumentoRecord demandado;

    @BeforeEach
    public void setUp() {
        validCarpeta = CarpetaSetUp.create(TipoJuicioSetUp.createTipoJuicio(), JuzgadoSetUp.createJuzgado());
        actor = new PersonaDocumentoRecord("Juan", "Perez", "", null, "fisica", "Actor", 1, 200);
        demandado = new PersonaDocumentoRecord("Nauj", "Zerep", "", null, "fisica", "Demandado", 1, 200);
    }

    @Test
    void getCarpetaResponseByNumExpYearJuzgado_return_CarpetaResponseRecord() {
        given(carpetaRepository.findByExpedienteAndJuzgadoId(any(), any()))
                .willReturn(Optional.ofNullable(validCarpeta));
        given(personaDocumentoRepository.findPersonaAndTipoParteByCarpetaId(any(), eq("Actor"), any()))
                .willReturn(actor);
        given(personaDocumentoRepository.findPersonaAndTipoParteByCarpetaId(any(), eq("Demandado"), any()))
                .willReturn(demandado);

        CarpetaResponseRecord carpetaResponseRecord = target.getCarpetaResponseByNumExpYearJuzgado(any(), any());

        assertThat(carpetaResponseRecord)
                .isOfAnyClassIn(CarpetaResponseRecord.class)
                .hasFieldOrPropertyWithValue("idCarpeta", validCarpeta.getId())
                .hasFieldOrPropertyWithValue("actor", actor.nombre() + " " + actor.apellidoPaterno())
                .hasFieldOrPropertyWithValue("demandado", demandado.nombre() + " " + demandado.apellidoPaterno());
    }

    @Test
    void getCarpetaResponseByNumExpYearJuzgado_return_not_found() {
        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> {
                    target.getCarpetaResponseByNumExpYearJuzgado("1", 1);
                }
        );
        assertThat(assertThrows.getMessage()).contains("Carpeta no encontrada");
    }


}