package mx.gob.pjpuebla.migracion.readers.entradas;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.migracion.readers.actores.ActoresMigracion;
import mx.gob.pjpuebla.migracion.readers.actores.ActoresMigracionReader;
import mx.gob.pjpuebla.migracion.readers.detallesProm.DetallesProm;
import mx.gob.pjpuebla.migracion.readers.detallesProm.DetallesPromReader;
import mx.gob.pjpuebla.migracion.readers.juicios.JuicioResponseRecord;
import mx.gob.pjpuebla.migracion.readers.juicios.JuiciosMigracionReader;
import mx.gob.pjpuebla.migracion.readers.juzgados.JuzgadosMigracionReader;
import mx.gob.pjpuebla.migracion.readers.juzgados.JuzgadosMigracionRegistroRecord;
import mx.gob.pjpuebla.migracion.readers.movimientos.MovimientosMigracionRecord;
import mx.gob.pjpuebla.migracion.readers.ubicaciones.UbicacionesReader;
import mx.gob.pjpuebla.migracion.utils.UtilsMigracion;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.error.ConstraintViolationException;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntradasMigracionReader {

    private final EntradasMigracionRepository entradasMigracionRepository;
    private final JuzgadosMigracionReader juzgadosMigracionService;
    private final JuiciosMigracionReader juiciosMigracionService;
    private final ActoresMigracionReader actoresMigracionService;
    private final DetallesPromReader detallesPromService;

    private final UtilsMigracion utilsMigracion;
    private final UbicacionesReader ubicacionesService;

    // service de sistema actual:
    private final CarpetaRepository carpetaRepository;
    private final AnexoRepository anexoRepository;

    /**
     * Busca las entradas migradas por expediente, año y juzgado.
     * A cada entrada le anexa sus ubicaciones dinámicas, el juzgado correspondiente
     * y el juicio asociado.
     *
     * @param expediente    Número de expediente
     * @param amo           Año del expediente
     * @param juzgadoCodigo Código del juzgado
     * @return Lista de registros de entrada completos
     */
    public EntradasMigracionRecord buscarPorFiltros(String expediente, Integer amo, String juzgadoCodigo) {

        // Buscar entrada:
        EntradasMigracion entrada = buscarEntradasPorFiltros(expediente, amo, juzgadoCodigo);

        // Buscar juzgado:
        JuzgadosMigracionRegistroRecord juzgado = juzgadosMigracionService.findByCodigo(juzgadoCodigo);

        // Definir tabla ubicacion:
        String tablaUbi = juzgado != null ? juzgado.tablaUbicacion() : null;

        // Se obtienen las ubicaciones dependiendo del CU y de la tabla dinámica según
        // juzgado
        MovimientosMigracionRecord ubicaciones = ubicacionesService.buscarUltimoMovimiento(entrada.getCu(), tablaUbi);

        // Se obtiene el juicio asociado al campo `juicio` de la entrada
        JuicioResponseRecord juicio = juiciosMigracionService.buscarJuicio(entrada.getJuicio());

        // Se obtienen los actores:
        List<ActoresMigracion> actores = actoresMigracionService.buscarPorClave(entrada.getCu());

        // Se obtienen los detalles de la promocion si es que existen
        List<DetallesProm> detallesProm = detallesPromService.buscarPorCu(entrada.getCu());

        // Se ensambla el registro final
        return new EntradasMigracionRecord(entrada, juzgado, ubicaciones, juicio, actores, detallesProm,
                entrada.getEstadoMigracion());
    }

    public EntradasMigracion buscarEntradasPorFiltros(String expediente, Integer amo, String juzgadoCodigo) {
        
        return entradasMigracionRepository
                .findTopByExpedienteNormalizado(expediente, amo, juzgadoCodigo, "A")
                .orElseThrow(() -> {
                    String clave = expediente + "-" + amo + "-" + juzgadoCodigo;
                    log.error("No se encontraron resultados para la clave {}", clave);
                    return new NotFoundException("No se encontraron resultados", clave);
                });
    }

    public Optional<EntradasMigracion> buscarEntradasPorFiltrosProm(String expediente, Integer amo,
            String juzgadoCodigo) {
        return entradasMigracionRepository.findTopByExpedienteAndAmoAndJuzgadoAndStatusOrderByIdDesc(expediente, amo, juzgadoCodigo, "A");

    }

    /**
     * Busca una {@link Carpeta} por su número de expediente y juzgado.
     * <p>
     * No lanza excepciones: si no existe, devuelve {@link Optional#empty()}.
     * </p>
     *
     * @param expediente Número de expediente normalizado (por ejemplo, "123/2025").
     *                   No nulo ni vacío.
     * @param juzgado    Juzgado propietario del expediente. No nulo.
     * @return {@link Optional} con la carpeta si existe; vacío en caso contrario.
     * @throws IllegalArgumentException si los parámetros son inválidos.
     */
    @Transactional(readOnly = true)
    private Optional<Carpeta> findCarpeta(String expediente, Juzgado juzgado) {
        String exp = utilsMigracion.normalizeExpediente(expediente);
        utilsMigracion.requireNonNullJuzgado(juzgado);
        return carpetaRepository.findByExpedienteAndJuzgado(exp, juzgado);
    }

    /**
     * Obtiene una {@link Carpeta} por expediente y juzgado, fallando si no existe.
     * <p>
     * Útil en flujos donde la carpeta es obligatoria y se desea fallar temprano.
     * </p>
     *
     * @param expediente Número de expediente normalizado (por ejemplo, "123/2025").
     *                   No nulo ni vacío.
     * @param juzgado    Juzgado propietario del expediente. No nulo.
     * @return La carpeta encontrada (nunca {@code null}).
     * @throws IllegalArgumentException si los parámetros son inválidos.
     * @throws NotFoundException        si no existe una carpeta con ese expediente
     *                                  en ese juzgado.
     */
    @Transactional(readOnly = true)
    private Carpeta requireCarpeta(String expediente, Juzgado juzgado) {
        return findCarpeta(expediente, juzgado)
                .orElseThrow(() -> new NotFoundException(
                        "Carpeta (expediente) no encontrada para el juzgado indicado.",
                        expediente));
    }

    /**
     * Verifica que NO exista una {@link Carpeta} con el expediente y juzgado dados.
     * <p>
     * Útil antes de crear/insertar: si ya existe, lanza
     * {@link ConstraintViolationException}.
     * </p>
     *
     * @param expediente Número de expediente normalizado (por ejemplo, "123/2025").
     *                   No nulo ni vacío.
     * @param juzgado    Juzgado propietario del expediente. No nulo.
     * @throws IllegalArgumentException     si los parámetros son inválidos.
     * @throws ConstraintViolationException si ya existe una carpeta con ese
     *                                      expediente en ese juzgado.
     */
    @Transactional(readOnly = true)
    public void assertExpedienteDisponible(String expediente, Juzgado juzgado) {
        String exp = utilsMigracion.normalizeExpediente(expediente);
        utilsMigracion.requireNonNullJuzgado(juzgado);

        // Si tienes existsByExpedienteAndJuzgado en el repo, úsalo para eficiencia.
        Optional<Carpeta> existente = carpetaRepository.findByExpedienteAndJuzgado(exp, juzgado);
        if (existente.isPresent()) {
            Carpeta c = existente.get();
            throw new ConstraintViolationException(
                    "El expediente ya se encuentra en el sistema",
                    c.getId() != null ? c.getId().toString() : exp);
        }
    }

    @Transactional
    private List<Anexo> crearAnexosMigracion(String anexos, Documento documento) {
        if (documento == null) {
            throw new IllegalArgumentException("El documento es obligatorio.");
        }
        if (anexos == null || anexos.isBlank()) {
            return List.of();
        }

        // Divide por coma ignorando espacios, elimina vacíos y duplicados, mapea a
        // entidad
        List<Anexo> toSave = Pattern.compile("\\s*,\\s*")
                .splitAsStream(anexos)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .map(nombre -> new Anexo()
                        .setNombre(nombre)
                        .setDocumento(documento))
                .toList();

        return toSave.isEmpty() ? List.of() : anexoRepository.saveAll(toSave);
    }

}
