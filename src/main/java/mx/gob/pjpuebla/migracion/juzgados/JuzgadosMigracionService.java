package mx.gob.pjpuebla.migracion.juzgados;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JuzgadosMigracionService {
    private final JuzgadosMigracionRepository juzgadosRepository;

    // ... (método buscarPorCodigo) ...

    /**
     * Busca todos los juzgados de forma de lista
     * 
     * @param pageable Contiene la información de paginación (número de página,
     *                 tamaño, etc.).
     * @return Un objeto mapeado de juzgados
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

    /**
     * Busca el juzgado por su código.
     *
     * @param codigo Código único del juzgado
     * @return Entidad `JuzgadosMigracion` si existe; null si no se encuentra
     */
    public JuzgadosMigracion buscarByCodigo(String codigo) {
        Optional<JuzgadosMigracion> juzgado = juzgadosRepository.findByCodigo(codigo);

        if (juzgado.isPresent()) {
            return juzgado.get();
        }

        return null;
    }

}