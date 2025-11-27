package mx.gob.pjpuebla.migracion.readers.ubicaciones;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.migracion.readers.movimientos.MovimientosMigracionRecord;

/**
 * Servicio para consultar la última ubicación/movimiento de un expediente en la
 * base secundaria (migración).
 * <p>
 * Usa un {@link NamedParameterJdbcTemplate} apuntando a la fuente de datos
 * secundaria para ejecutar
 * una consulta parametrizada por CU (clave única del expediente) sobre una
 * tabla de ubicaciones,
 * devolviendo el último registro activo (status = 'A') por orden del
 * identificador.
 * </p>
 *
 * <h3>Notas de implementación</h3>
 * <ul>
 * <li><b>Seguridad SQL:</b> el nombre de la tabla se concatena en la sentencia.
 * Debe validarse/whitelistearse antes de invocar este método.</li>
 * <li><b>Tipado de fecha:</b> se mapea {@code fecha} a {@link LocalDate} y
 * {@code hora} a {@link String}. Considera {@code LocalTime} si la columna es
 * de tipo hora.</li>
 * <li><b>Resultado vacío:</b> si no hay filas, se retorna {@code null}.
 * Considera usar {@code Optional} en una futura refactorización.</li>
 * </ul>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UbicacionesReader {

    /**
     * Plantilla JDBC configurada para usar la base secundaria (migración).
     */
    @Qualifier("secondaryNamedParameterJdbcTemplate")
    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * Obtiene el último movimiento (registro más reciente por
     * {@code id_ubicaciones}) de la tabla de ubicaciones
     * para el expediente identificado por el CU dado.
     *
     * @param cu       Clave única del expediente a consultar. No debe ser
     *                 {@code null} ni vacío.
     * @param tablaUbi Nombre calificado de la tabla de ubicaciones (p. ej.
     *                 {@code acuerdos.ubicaciones} o similar).
     *                 <b>Debe estar previamente validado</b> para evitar inyección
     *                 mediante identificadores.
     *
     * @return Un {@link MovimientosMigracionRecord} con el último movimiento
     *         activo; {@code null} si no hay coincidencias.
     */
    public MovimientosMigracionRecord buscarUltimoMovimiento(String cu, String tablaUbi) {
        String sql = "SELECT " +
                "  u.id_ubicaciones, u.cu, u.fecha,  u.status, u.estado, " +
                "  u.entrego, u.recibio, u.puesto_entrego, u.puesto_recibio," +
                "  p.nombre " +
                "FROM " + tablaUbi + " u " +
                "JOIN acuerdos.puestos p ON u.id_puesto = p.id_puesto " +
                "WHERE u.cu = :cu AND u.status = 'A' " + 
                "ORDER BY u.id_ubicaciones DESC " + 
                "LIMIT 1";

        try {
            return jdbcTemplate.queryForObject(
                    sql,
                    Map.of("cu", cu),
                    (rs, rowNum) -> new MovimientosMigracionRecord(
                            rs.getInt("id_ubicaciones"),
                            rs.getString("cu"),
                            rs.getObject("fecha", LocalDate.class),
                            rs.getString("status"),
                            rs.getString("estado"),
                            rs.getString("entrego"),
                            rs.getString("recibio"),
                            rs.getString("puesto_entrego"),
                            rs.getString("puesto_recibio"),
                            rs.getString("nombre")));
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            log.error("No fue posible encontrar la tabla", e);
            return null;
        }
    }

    public List<MovimientosMigracionRecord> buscarPiezasByCu(String cu, String tablaUbi) {
        String sql ="SELECT u.id_ubicaciones, u.cu, u.fecha, u.status, u.estado, " +
        "u.entrego, u.recibio, u.puesto_entrego, u.puesto_recibio, " +
        "p.nombre " +
        "FROM " + tablaUbi + " u " +
        "JOIN acuerdos.puestos p ON u.id_puesto = p.id_puesto " +
        "JOIN ( " +
            "SELECT cu, MAX(id_ubicaciones) AS max_id " +
            "FROM " + tablaUbi + " " +
            "WHERE /*CAST(*/cu/* AS CHAR)*/ LIKE CONCAT(:cu, '%') " + 
            "AND /*CAST(*/cu/* AS CHAR)*/ <> :cu " +
            "AND status = 'A' " +
            "GROUP BY cu " +
            ") t ON t.cu = u.cu AND t.max_id = u.id_ubicaciones " +
        "ORDER BY u.id_ubicaciones DESC";

        try {
            return jdbcTemplate.query(
                    sql,
                    Map.of("cu", cu),
                    (rs, rowNum) -> new MovimientosMigracionRecord(
                            rs.getInt("id_ubicaciones"),
                            rs.getString("cu"),
                            rs.getObject("fecha", LocalDate.class),
                            rs.getString("status"),
                            rs.getString("estado"),
                            rs.getString("entrego"),
                            rs.getString("recibio"),
                            rs.getString("puesto_entrego"),
                            rs.getString("puesto_recibio"),
                            rs.getString("nombre")));
        } catch (Exception e) {
            log.error("Error en la consulta", e);
            return List.of();
        }
    }

}
