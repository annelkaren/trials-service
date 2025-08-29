package mx.gob.pjpuebla.migracion.juzgados;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.error.NotFoundException;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.micrometer.core.instrument.util.StringEscapeUtils;

@Service
@RequiredArgsConstructor
public class JuzgadosMigracionService {

    private final JuzgadosMigracionRepository juzgadosRepository;
    private final PersonaService personaService;

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
        
        if(claveJuzgado == null || claveJuzgado.isBlank() || claveJuzgado.isEmpty()){
            throw new NotFoundException("Clave de juzgado no encontrada", "claveJuzgado");
        }

        // 2. Llama al repositorio para obtener una página de entidades 'Juzgado'.
        Optional<JuzgadosMigracion> juzgadosOptional = juzgadosRepository.findByCodigo(claveJuzgado);
        
        if(!juzgadosOptional.isPresent()){
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
    public JuzgadosMigracion buscarByCodigo(String codigo) {
        Optional<JuzgadosMigracion> juzgado = juzgadosRepository.findByCodigo(codigo);

        if (juzgado.isPresent()) {
            return juzgado.get();
        }

        return null;
    }

}