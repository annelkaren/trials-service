package mx.gob.pjpuebla.trials.workflow.listaestrados;

import mx.gob.pjpuebla.trials.workflow.notificaciones.NotificacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ListaEstradoServiceTest {

    @Mock
    ListaEstradoRepository listaEstradoRepository;
    @Mock
    NotificacionRepository notificacionRepository;
    @InjectMocks
    ListaEstradoService target;

    private ListaEstrado listaEstrado;

    @BeforeEach
    public void setUp() {
        listaEstrado = ListaEstradoSetUp.createLisEstrado();
    }

    @Test
    void testFindAllByListaEstradoIdWithNullSearchQuery() {
        when(listaEstradoRepository.findAllListaEstradoIdAndSearch(any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(listaEstrado)));

        when(notificacionRepository.countNotificacionesByListaEstradoId(any())).thenReturn(5L);

        Page<ListaEstradoRecord> result = target.findAllByListaEstradoId(1, null, PageRequest.of(0, 20));

        assertNotNull(result);

        verify(listaEstradoRepository).findAllListaEstradoIdAndSearch("", 1, PageRequest.of(0, 20));

        verify(notificacionRepository).countNotificacionesByListaEstradoId(listaEstrado.getId());
    }

    @Test
    void testFindAllByListaEstradoIdWithEmptySearchQuery() {
        when(listaEstradoRepository.findAllListaEstradoIdAndSearch(any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(listaEstrado)));

        when(notificacionRepository.countNotificacionesByListaEstradoId(any())).thenReturn(5L);

        Page<ListaEstradoRecord> result = target.findAllByListaEstradoId(1, "", PageRequest.of(0, 20));

        assertNotNull(result);

        verify(listaEstradoRepository).findAllListaEstradoIdAndSearch("", 1, PageRequest.of(0, 20));

        verify(notificacionRepository).countNotificacionesByListaEstradoId(listaEstrado.getId());
    }
}