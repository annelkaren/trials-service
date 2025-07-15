package mx.gob.pjpuebla.migracion.juzgados;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;


import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JuzgadosMigracionService {
    private final JuzgadosMigracionRepository juzgadosRepository;

    // ... (método buscarPorCodigo) ...

    /**
     * Busca todos los juzgados de forma paginada y los convierte a una página de
     * DTOs.
     * 
     * @param pageable Contiene la información de paginación (número de página,
     *                 tamaño, etc.).
     * @return Una página (Page) de DTOs de Juzgado.
     */
    @Transactional(readOnly = true)
    public List<JuzgadosMigracionRecord> buscarTodos() {
        // 1. Llama al repositorio para obtener una página de entidades 'Juzgado'.
        List<JuzgadosMigracion> paginaDeJuzgados = juzgadosRepository.findAll();

        // 2. Convierte (mapea) cada 'Juzgado' de la página a su 'JuzgadoRespuestaDTO'.
        return paginaDeJuzgados.stream().map(juzgado -> new JuzgadosMigracionRecord(
                juzgado.getIdJuzgado(),
                juzgado.getDescripcion(),
                juzgado.getCodigo()))
                .toList();
    }

}