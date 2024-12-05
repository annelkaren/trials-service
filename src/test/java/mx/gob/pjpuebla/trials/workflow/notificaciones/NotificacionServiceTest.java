package mx.gob.pjpuebla.trials.workflow.notificaciones;

import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaSetUp;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @InjectMocks
    private NotificacionService notificacionService;

    private Carpeta carpeta;
    private Notificacion notificacion;

    @BeforeEach
    public void setUp(){
        carpeta = CarpetaSetUp.create();
        notificacion =  NotificacionSetUp.createNotificacion();
    }


    @Test
    void getNotificacionPorTipoEstrado() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<Notificacion> notificacionPage = new PageImpl<>(List.of(notificacion), pageable, 1);
        when(notificacionRepository.getNotificacionByTipo(TipoNotificacion.ESTRADO, pageable))
                .thenReturn(notificacionPage);
        Page<NotificacionRecord> result = notificacionService.getAllNotificaciones("ESTRADO", pageable);

        assertEquals(1, result.getTotalElements());
        NotificacionRecord notificacionrecord = result.getContent().get(0);
        assertEquals(notificacion.getConcepto(), notificacionrecord.concepto());
        assertEquals(notificacion.getNotas(), notificacionrecord.notas());
        assertEquals(notificacion.getTipoNotificacion(), notificacionrecord.tipo());
    }

    @Test
    void getInvalid() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<NotificacionRecord> result = notificacionService.getAllNotificaciones("invalido", pageable);

        assertEquals(0, result.getTotalElements());
    }

    @Test
    void getAll() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<Notificacion> notificacionPage = new PageImpl<>(List.of(notificacion), pageable, 1);
        when(notificacionRepository.getNotificacionByTipo(TipoNotificacion.ESTRADO, pageable))
                .thenReturn(notificacionPage);

        Page<NotificacionRecord> result = notificacionService.getAllNotificaciones(null, pageable);
        assertEquals(1, result.getTotalElements());
    }


}
