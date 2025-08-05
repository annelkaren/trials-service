package mx.gob.pjpuebla.migracion.expediente;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.migracion.actores.ActoresMigracion;
import mx.gob.pjpuebla.migracion.actores.ActoresMigracionService;
import mx.gob.pjpuebla.migracion.acuerdos.AcuerdosMigracion;
import mx.gob.pjpuebla.migracion.acuerdos.AcuerdosMigracionService;
import mx.gob.pjpuebla.migracion.amparos.AmparoMigracionService;
import mx.gob.pjpuebla.migracion.amparos.AmparosMigracion;
import mx.gob.pjpuebla.migracion.detallesProm.DetallesProm;
import mx.gob.pjpuebla.migracion.detallesProm.DetallesPromService;
import mx.gob.pjpuebla.migracion.exhortoCapital.ExhortosCapitalMigracion;
import mx.gob.pjpuebla.migracion.exhortoCapital.ExhortosCapitalMigracionService;
import mx.gob.pjpuebla.migracion.exhortoForaneo.ExhortoForaneoMigracion;
import mx.gob.pjpuebla.migracion.exhortoForaneo.ExhortoForaneoMigracionService;
import mx.gob.pjpuebla.migracion.juicios.JuiciosMigracion;
import mx.gob.pjpuebla.migracion.juicios.JuiciosMigracionService;
import mx.gob.pjpuebla.migracion.juzgados.JuzgadosMigracion;
import mx.gob.pjpuebla.migracion.juzgados.JuzgadosMigracionService;
import mx.gob.pjpuebla.migracion.movimientos.MovimientosMigracionRecord;
import mx.gob.pjpuebla.migracion.oficios.OficiosMigracion;
import mx.gob.pjpuebla.migracion.oficios.OficiosMigracionService;

@Service
@RequiredArgsConstructor
public class EntradasMigracionService {

    // Plantilla JDBC configurada para usar la base secundaria (migración)
    @Qualifier("secondaryNamedParameterJdbcTemplate")
    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final AcuerdosMigracionService acuerdosMigracionService;
    private final EntradasMigracionRepository entradasMigracionRepository;
    private final JuzgadosMigracionService juzgadosMigracionService;
    private final JuiciosMigracionService juiciosMigracionService;
    private final AmparoMigracionService amparoMigracionService;
    private final OficiosMigracionService oficiosMigracionService;
    private final ActoresMigracionService actoresMigracionService;
    private final DetallesPromService detallesPromService;
    private final ExhortoForaneoMigracionService exhortoForaneoMigracionService;
    private final ExhortosCapitalMigracionService exhortoCapitalMigracionService;

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
    public List<EntradasMigracionRecord> buscarPorFiltros(String expediente, Integer amo, String juzgadoCodigo) {
        List<EntradasMigracion> entradas = buscarEntradas(expediente, amo, juzgadoCodigo);
        JuzgadosMigracion juzgado = juzgadosMigracionService.buscarByCodigo(juzgadoCodigo);
        String tablaUbi = juzgado != null ? juzgado.getTablaUbicacion() : null;

        List<EntradasMigracionRecord> resultado = new ArrayList<>();

        for (EntradasMigracion entrada : entradas) {
            // Se obtienen las ubicaciones dependiendo del CU y de la tabla dinámica según
            // juzgado
            List<MovimientosMigracionRecord> ubicaciones = buscarUbicaciones(entrada.getCu(), tablaUbi);

            // Se obtiene el juicio asociado al campo `juicio` de la entrada
            JuiciosMigracion juicio = juiciosMigracionService.buscarJuicio(entrada.getJuicio());

            // Se obtienen los acuerdos: 
            List<AcuerdosMigracion> acuerdos = acuerdosMigracionService.buscarAcuerdosPorCu(entrada.getCu());

            // se obtienen sentencias:
            List<AcuerdosMigracion> sentencias = acuerdosMigracionService.buscarSentenciasPorCu(entrada.getCu());

            //Se obtienen amparos: 
            List<AmparosMigracion> amparos = amparoMigracionService.buscarPorCu(entrada.getCu());

            //Se obtienen oficios:
            List<OficiosMigracion> oficios = oficiosMigracionService.buscarPorCu(entrada.getCu());

            //Se obtienen los actores:
            List<ActoresMigracion> actores = actoresMigracionService.buscarPorClave(entrada.getCu());

            //Se obtienen los detalles de la promocion si es que existen
            List<DetallesProm> detallesProm = detallesPromService.buscarPorCu(entrada.getCu());

            //Se obtienen los exhortos foraneos:
            List<ExhortoForaneoMigracion> exhortoForaneoMigracion = exhortoForaneoMigracionService.buscarPorJuzgadoOr(juzgado.getCodigo());

            //se obtienen los exhortos capital
            List<ExhortosCapitalMigracion> exortoCapitalMigracion = exhortoCapitalMigracionService.buscarPorJuzgadoOr(juzgado.getCodigo());

            // Se ensambla el registro final
            resultado.add(new EntradasMigracionRecord(
                entrada, juzgado, ubicaciones, 
                juicio, acuerdos, amparos, 
                oficios, actores, detallesProm,
                exhortoForaneoMigracion, exortoCapitalMigracion,
                sentencias));
        }

        return resultado;
    }

    /**
     * Consulta la tabla `entradas` por filtros principales.
     *
     * @param expediente    Número de expediente
     * @param amo           Año
     * @param juzgadoCodigo Código del juzgado
     * @return Lista de entidades `EntradasMigracion`
     */
    private List<EntradasMigracion> buscarEntradas(String expediente, Integer amo, String juzgadoCodigo) {
        return entradasMigracionRepository.buscarPorExpedienteAmoYJuzgado(expediente, amo, juzgadoCodigo);
    }

    /**
     * Busca las ubicaciones asociadas a un CU en una tabla dinámica.
     *
     * @param cu       Código único del expediente (CU)
     * @param tablaUbi Nombre de la tabla dinámica según el juzgado
     * @return Lista de movimientos (ubicaciones) encontrados
     */
    private List<MovimientosMigracionRecord> buscarUbicaciones(String cu, String tablaUbi) {
        if (tablaUbi == null || tablaUbi.isBlank())
            return List.of(); // No hay ubicaciones si no hay tabla

        String sql = "SELECT * FROM " + tablaUbi + " WHERE cu = :cu";
        return jdbcTemplate.query(sql, Map.of("cu", cu), (rs, rowNum) -> new MovimientosMigracionRecord(
                rs.getInt("id_ubicaciones"),
                rs.getString("cu"),
                rs.getObject("id_puesto", Integer.class),
                rs.getObject("fecha", LocalDate.class),
                rs.getString("hora"),
                rs.getString("status"),
                rs.getString("estado"),
                rs.getString("etapa"),
                rs.getString("entrego"),
                rs.getString("recibio"),
                rs.getString("puesto_entrego"),
                rs.getString("puesto_recibio"),
                rs.getString("libro"),
                rs.getObject("num_foja", Integer.class),
                rs.getString("obse"),
                rs.getString("sentido"),
                rs.getString("digitalizado_acu")));
    }
}
