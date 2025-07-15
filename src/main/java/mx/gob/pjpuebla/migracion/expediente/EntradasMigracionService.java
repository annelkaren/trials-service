package mx.gob.pjpuebla.migracion.expediente;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.migracion.juicios.JuiciosMigracion;
import mx.gob.pjpuebla.migracion.juzgados.JuzgadosMigracion;
import mx.gob.pjpuebla.migracion.movimientos.MovimientosMigracionRecord;

@Service
@RequiredArgsConstructor
public class EntradasMigracionService {

    // Plantilla JDBC configurada para usar la base secundaria (migración)
    @Qualifier("secondaryNamedParameterJdbcTemplate")
    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * Busca las entradas migradas por expediente, año y juzgado.
     * A cada entrada le anexa sus ubicaciones dinámicas, el juzgado correspondiente y el juicio asociado.
     *
     * @param expediente Número de expediente
     * @param amo        Año del expediente
     * @param juzgadoCodigo Código del juzgado
     * @return Lista de registros de entrada completos
     */
    public List<EntradasMigracionRecord> buscarPorFiltros(String expediente, Integer amo, String juzgadoCodigo) {
        List<EntradasMigracion> entradas = buscarEntradas(expediente, amo, juzgadoCodigo);
        JuzgadosMigracion juzgado = buscarJuzgado(juzgadoCodigo);
        String tablaUbi = juzgado != null ? juzgado.getTablaUbicacion() : null;

        List<EntradasMigracionRecord> resultado = new ArrayList<>();

        for (EntradasMigracion entrada : entradas) {
            // Se obtienen las ubicaciones dependiendo del CU y de la tabla dinámica según juzgado
            List<MovimientosMigracionRecord> ubicaciones = buscarUbicaciones(entrada.getCu(), tablaUbi);

            // Se obtiene el juicio asociado al campo `juicio` de la entrada
            JuiciosMigracion juicio = buscarJuicio(entrada.getJuicio());

            // Se ensambla el registro final compuesto por la entrada + ubicaciones + juicio + juzgado
            resultado.add(new EntradasMigracionRecord(entrada, juzgado, ubicaciones, juicio));
        }

        return resultado;
    }

    /**
     * Consulta la tabla `entradas` por filtros principales.
     *
     * @param expediente Número de expediente
     * @param amo        Año
     * @param juzgadoCodigo Código del juzgado
     * @return Lista de entidades `EntradasMigracion`
     */
    private List<EntradasMigracion> buscarEntradas(String expediente, Integer amo, String juzgadoCodigo) {
        String sql = """
            SELECT * FROM entradas
            WHERE expediente = :expediente AND amo = :amo AND juzgado = :juzgado
        """;

        Map<String, Object> params = Map.of(
                "expediente", expediente,
                "amo", amo,
                "juzgado", juzgadoCodigo
        );

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            EntradasMigracion entrada = new EntradasMigracion();
            entrada.setId(rs.getInt("id"));
            entrada.setJuzgado(rs.getString("juzgado"));
            entrada.setExpediente(rs.getString("expediente"));
            entrada.setAmo(rs.getInt("amo"));
            entrada.setCu(rs.getString("cu"));
            entrada.setJuicio(rs.getString("juicio"));
            entrada.setTipo(rs.getString("tipo"));
            entrada.setDocumentos(rs.getString("documentos"));
            entrada.setCantidad(rs.getBigDecimal("cantidad"));
            entrada.setFecha(rs.getObject("fecha", LocalDate.class));
            entrada.setHora(rs.getString("hora"));
            entrada.setProcedenci(rs.getString("procedenci"));
            entrada.setAsunto(rs.getString("asunto"));
            entrada.setFechaEnvi(rs.getObject("fecha_envi", LocalDate.class));
            entrada.setLegajo(rs.getString("legajo"));
            entrada.setOficio(rs.getString("oficio"));
            entrada.setEtapa(rs.getString("etapa"));
            entrada.setTipMo(rs.getString("tip_mo"));
            entrada.setStatus(rs.getString("status"));
            entrada.setTipoAccion(rs.getString("tipo_accion"));
            entrada.setTipoDivorcio(rs.getString("tipoDivorcio"));
            entrada.setMateria(rs.getString("materia"));
            return entrada;
        });
    }

    /**
     * Busca el juzgado por su código.
     *
     * @param codigo Código único del juzgado
     * @return Entidad `JuzgadosMigracion` si existe; null si no se encuentra
     */
    private JuzgadosMigracion buscarJuzgado(String codigo) {
        String sql = "SELECT * FROM juzgados WHERE codigo = :codigo";

        try {
            return jdbcTemplate.queryForObject(sql, Map.of("codigo", codigo), (rs, rowNum) -> {
                JuzgadosMigracion j = new JuzgadosMigracion();
                j.setIdJuzgado(rs.getInt("id_juzgado"));
                j.setDescripcion(rs.getString("descrip"));
                j.setTablaUbicacion(rs.getString("tabla_ubi"));
                j.setCodigo(rs.getString("codigo"));
                return j;
            });
        } catch (EmptyResultDataAccessException e) {
            return null; // Si no se encuentra el juzgado, se maneja como nulo
        }
    }

    /**
     * Busca las ubicaciones asociadas a un CU en una tabla dinámica.
     *
     * @param cu        Código único del expediente (CU)
     * @param tablaUbi  Nombre de la tabla dinámica según el juzgado
     * @return Lista de movimientos (ubicaciones) encontrados
     */
    private List<MovimientosMigracionRecord> buscarUbicaciones(String cu, String tablaUbi) {
        if (tablaUbi == null || tablaUbi.isBlank()) return List.of(); // No hay ubicaciones si no hay tabla

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
                rs.getString("digitalizado_acu")
        ));
    }

    /**
     * Busca los datos del juicio asociado al ID recibido.
     *
     * @param idJuicio ID del juicio
     * @return Entidad `JuiciosMigracion` o null si no se encuentra
     */
    private JuiciosMigracion buscarJuicio(String idJuicio) {
        String sql = "SELECT * FROM juicios WHERE idjuicio = :idJuicio";

        try {
            return jdbcTemplate.queryForObject(sql, Map.of("idJuicio", idJuicio), (rs, rowNum) -> {
                JuiciosMigracion j = new JuiciosMigracion();
                j.setDescripcion(rs.getString("descrip"));
                j.setIdJuicio(rs.getString("idjuicio"));
                j.setMateria(rs.getString("materia"));
                j.setStatus(rs.getString("status"));
                return j;
            });
        } catch (EmptyResultDataAccessException e) {
            return null; // No se encontró el juicio
        }
    }
}
