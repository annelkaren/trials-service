package mx.gob.pjpuebla.trials.workflow.listaestrados;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.workflow.notificaciones.NotificacionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class ListaEstradoService {

    private final ListaEstradoRepository listaEstradoRepository;
    private final NotificacionRepository notificacionRepository;

    @Transactional(readOnly = true)
    public Page<ListaEstradoRecord> findAllByListaEstradoId(Integer listaEstrado, String searchQuery, Pageable pageable) {

        Page<ListaEstrado> listaEstradoPage = listaEstradoRepository.findAllListaEstradoIdAndSearch(
                searchQuery == null ? "" : searchQuery,
                listaEstrado,
                pageable);

        return listaEstradoPage.map(le -> {
            // Inicializar la relación antes de serializar
            if (le.getPersona() != null) {
                le.getPersona().getDomicilio();  // Forzamos la inicialización del domicilio si es necesario
            }

            long noNotificaciones = notificacionRepository.countNotificacionesByListaEstradoId(le.getId());

            String nombreCompleto =
                    (le.getPersona().getNombre() != null ? le.getPersona().getNombre() : "") +
                            (le.getPersona().getApellidoMaterno() != null ? " " + le.getPersona().getApellidoMaterno() : "") +
                            (le.getPersona().getApellidoPaterno() != null ? " " + le.getPersona().getApellidoPaterno() : "");

            return new ListaEstradoRecord(
                    le.getId(),
                    le.getFechaAlta().toString(),
                    (int) noNotificaciones,
                    nombreCompleto
            );
        });
    }



}
