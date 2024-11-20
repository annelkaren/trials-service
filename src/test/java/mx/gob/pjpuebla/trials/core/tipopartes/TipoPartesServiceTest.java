package mx.gob.pjpuebla.trials.core.tipopartes;

import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaSetUp;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TipoPartesServiceTest {

    @Mock
    TipoPartesRepository mockTipoPartesRepository;

    @Mock
    DocumentoRepository mockDocumentoRepository;

    @Mock
    CarpetaRepository mockCarpetaRepository;


    @InjectMocks
    TipoPartesService target;

    private TipoPartes validTipoPartes;

    @BeforeEach
    public void setUp() {
        validTipoPartes = TipoPartesSetUp.createTipoPartes();
        TipoSistema tipoSistema = TipoSistemaSetUp.createTipoSistema();
        Materia materia = MateriaSetUp.createMateria();
        TipoJuicio tipoJuicio = TipoJuicioSetUp.createTipoJuicio(tipoSistema, materia);
        validTipoPartes.setTipoJuicio(tipoJuicio);
    }

    @Test
    void getAll_return_page() {
        List<TipoPartes> listPage = Collections.singletonList(validTipoPartes);
        given(mockTipoPartesRepository.findAll(any(Example.class), any(PageRequest.class)))
                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size()));
        Page<TipoPartesRecord> page = target.getAll(PageRequest.of(1, listPage.size()), validTipoPartes);
        assertThat(page.getContent())
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("id", validTipoPartes.getId())
                .hasFieldOrPropertyWithValue("nombre", validTipoPartes.getNombre());
    }

    @Test
    void getById_return_tipoPartes() {
        given(mockTipoPartesRepository.findById(validTipoPartes.getId()))
                .willReturn(Optional.ofNullable(validTipoPartes));

        TipoPartesRecord mr = target.findById(validTipoPartes.getId());
        assertThat(mr).isOfAnyClassIn(TipoPartesRecord.class)
                .hasFieldOrPropertyWithValue("id", validTipoPartes.getId())
                .hasFieldOrPropertyWithValue("nombre", validTipoPartes.getNombre());
    }

    @Test
    void findByIdError() {
        int id = validTipoPartes.getId();
        given(mockTipoPartesRepository.findById(validTipoPartes.getId()))
                .willReturn(Optional.empty());

        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> target.findById(id)
        );

        assertThat(assertThrows.getMessage()).contains("TipoPartes no encontrada");
    }

    @Test
    void getByTipoJuicioId_return_tipoPartes() {
        given(mockTipoPartesRepository.findByTipoJuicioId(validTipoPartes.getTipoJuicio().getId()))
                .willReturn(Collections.singletonList(validTipoPartes));

        List<TipoPartesRecord> list = target.findByTipoJuicioId(validTipoPartes.getTipoJuicio().getId());
        assertThat(list).hasSize(1);
        assertThat(list.get(0))
                .hasFieldOrPropertyWithValue("id", validTipoPartes.getId())
                .hasFieldOrPropertyWithValue("nombre", validTipoPartes.getNombre())
                .hasFieldOrPropertyWithValue("tipoJuicio", validTipoPartes.getTipoJuicio().getNombre());
    }


    @Test
        void getTipoPartesByDocumentoId_success() {
                Documento documento = new Documento();
                documento.setId(1); 
                Carpeta carpeta = new Carpeta();
                TipoJuicio tipoJuicio = new TipoJuicio();
                tipoJuicio.setId(2); 
                tipoJuicio.setNombre("Civil (Oral)"); 
                carpeta.setTipoJuicio(tipoJuicio);
                documento.setCarpeta(carpeta);

                given(mockDocumentoRepository.findById(1)).willReturn(Optional.of(documento));

                TipoPartes tipoPartes = new TipoPartes();
                tipoPartes.setId(3);
                tipoPartes.setNombre("Actor - Abogado litigante");

                given(mockTipoPartesRepository.findByTipoJuicioId(2)).willReturn(Collections.singletonList(tipoPartes));

                List<TipoPartesRecord> result = target.getTipoPartesByDocumentoId(1);

                assertThat(result).hasSize(1);
                assertThat(result.get(0)).hasFieldOrPropertyWithValue("id", 3)
                                        .hasFieldOrPropertyWithValue("nombre", "Actor - Abogado litigante")
                                        .hasFieldOrPropertyWithValue("tipoJuicio", "Civil (Oral)");
        }

        @Test
        void getTipoPartesByDocumentoId_documentoNotFound() {
                given(mockDocumentoRepository.findById(1)).willReturn(Optional.empty());

                NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
                        target.getTipoPartesByDocumentoId(1); 
                });

                assertThat(thrown.getMessage()).contains("Documento no encontrado");
        }

        @Test
        void getTipoPartesByDocumentoId_carpetaNotFound() {
                Documento documento = new Documento();
                documento.setId(1); 
                documento.setCarpeta(null);
                
                given(mockDocumentoRepository.findById(1)).willReturn(Optional.of(documento));
                
                NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
                    target.getTipoPartesByDocumentoId(1);
                });
                
                assertThat(thrown.getMessage()).contains("Carpeta no encontrada");
        }

        @Test
        void getTipoPartesByDocumentoId_tipoJuicioNotFound() {
                Documento documento = new Documento();
                documento.setId(1); 
                Carpeta carpeta = new Carpeta();
                carpeta.setTipoJuicio(null); 
                documento.setCarpeta(carpeta);
                
                given(mockDocumentoRepository.findById(1)).willReturn(Optional.of(documento));
                
                NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
                    target.getTipoPartesByDocumentoId(1);
                });
                
                assertThat(thrown.getMessage()).contains("Tipo de juicio no encontrado");
            }

    @Test
    void getTiposPartesByCarpetaId_success() {
        Integer carpetaId = 1;
        Integer tipoJuicioId = 100;
        List<TipoPartes> tiposPartes = Arrays.asList(validTipoPartes);

        given(mockCarpetaRepository.findTipoJuicioIdByCarpetaId(carpetaId)).willReturn(tipoJuicioId);
        given(mockTipoPartesRepository.findByTipoJuicioId(tipoJuicioId)).willReturn(tiposPartes);

        List<TipoPartesRecord> result = target.getTiposPartesByCarpetaId(carpetaId);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).hasFieldOrPropertyWithValue("id", validTipoPartes.getId())
                .hasFieldOrPropertyWithValue("nombre", validTipoPartes.getNombre())
                .hasFieldOrPropertyWithValue("tipoJuicio", validTipoPartes.getTipoJuicio().getNombre());
    }
        
}
