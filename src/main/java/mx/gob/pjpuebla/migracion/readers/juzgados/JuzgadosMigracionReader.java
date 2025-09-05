package mx.gob.pjpuebla.migracion.readers.juzgados;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoService;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.error.NotFoundException;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JuzgadosMigracionReader {

    private final JuzgadosMigracionRepository juzgadosRepository;
    private final PersonaService personaService;
    private final JuzgadoService juzgadoService;

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

        // 1. obtiene persona logueada:
        Persona persona = personaService.getAuditor();
        String claveJuzgado = persona.getJuzgado().getClaveJuzgado();
        System.out.println("LA CLAVE DEL JUZGADO ES: " + claveJuzgado);

        if (claveJuzgado == null || claveJuzgado.isBlank() || claveJuzgado.isEmpty()) {
            throw new NotFoundException("Clave de juzgado no encontrada", "claveJuzgado");
        }

        // 2. Llama al repositorio para obtener una página de entidades 'Juzgado'.
        Optional<JuzgadosMigracion> juzgadosOptional = juzgadosRepository.findByCodigo(claveJuzgado);

        if (!juzgadosOptional.isPresent()) {
            throw new NotFoundException("Clave de juzgado no encontrada", "claveJuzgado");
        }

        List<JuzgadosMigracion> juzgados = List.of(juzgadosOptional.get());

        // 3. Convierte (mapea) cada 'Juzgado' de la página a su 'JuzgadoRespuestaDTO'.
        return juzgados.stream().map(juzgado -> new JuzgadosMigracionRecord(
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
    public JuzgadosMigracion requireByCodigo(String codigo) {
        Optional<JuzgadosMigracion> juzgado = juzgadosRepository.findByCodigo(codigo);

        if (juzgado.isPresent()) {
            return juzgado.get();
        }

        return null;
    }

    /**
     * Obtiene un {@link Juzgado} a partir de su clave única.
     * <p>
     * Si el parámetro es {@code null}, vacío o no existe en el sistema actual, se
     * lanza una excepción
     * {@link NotFoundException}. De esta manera, se garantiza que el resultado
     * nunca será {@code null}.
     * </p>
     *
     * <h3>Ventajas de este enfoque</h3>
     * <ul>
     * <li>Evita código repetido de validación en los consumidores.</li>
     * <li>Falla temprano en caso de datos inválidos (clave nula o
     * inexistente).</li>
     * <li>El nombre del método comunica claramente que se obtendra el Juzgado es
     * obligatorio.</li>
     * </ul>
     *
     * @param claveJuzgado Clave del juzgado a buscar. No debe ser {@code null} ni
     *                     vacío.
     * @return El {@link Juzgado} correspondiente.
     * @throws IllegalArgumentException si la clave es {@code null} o vacía.
     * @throws NotFoundException        si no existe un juzgado con esa clave.
     */
    public Juzgado getJuzgadoFromSistema(String claveJuzgado) {
        if (claveJuzgado == null || claveJuzgado.isBlank()) {
            throw new IllegalArgumentException("La clave del juzgado no puede ser nula ni vacía.");
        }

        Juzgado juzgado = juzgadoService.findByClaveJuzgado(claveJuzgado);
        if (juzgado == null) {
            throw new NotFoundException(
                    "Juzgado no encontrado, revise que esté dada de alta su clave.",
                    claveJuzgado);
        }
        return juzgado;
    }

}