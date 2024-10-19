package mx.gob.pjpuebla.trials.workflow.movimientos;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Integer> {

    @Query
            ("""
                    SELECT new mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoSalidaRecord(
                    m.uuid, c.tipoCarpeta, c.folio, c.expediente, m.fechaAsignacion, j.nombre, d.data, d.folio
                    )
                    FROM Movimiento m
                    LEFT JOIN Carpeta c on c = m.carpeta and c.estatus = :estadoCarpeta
                    LEFT JOIN Documento d on d = m.documento
                    LEFT JOIN Juzgado j on j = m.juzgado
                    WHERE m.uuid = :uuid
                    """)
    List<MovimientoSalidaRecord> getSalidas(UUID uuid, EstadoCarpeta estadoCarpeta);

    @Query("SELECT m FROM Movimiento m "
            + "LEFT JOIN m.carpeta c "
            + "LEFT JOIN c.juzgado jc "
            + "LEFT JOIN m.documento d "
            + "LEFT JOIN d.carpeta cd "
            + "LEFT JOIN cd.juzgado jcd "
            + "JOIN FETCH m.persona p "
            + "LEFT JOIN m.juzgado j "
            + "LEFT JOIN m.oficialia o "
            + "WHERE (c.estatus IN :estado OR d.estatus IN :estado) AND "
            + "m.fechaAsignacion = (SELECT MAX(mov.fechaAsignacion) FROM Movimiento mov WHERE (mov.carpeta.id = c.id "
            + "OR mov.documento.id = d.id) AND mov.motivo IN (:motivos)) "
            + "AND (jc.id = :juzgadoId OR jcd.id = :juzgadoId) "
            + "AND (LOWER(c.folio) LIKE %:key% OR LOWER(c.expediente) LIKE %:key% "
            + "OR LOWER(d.folio) LIKE %:key% "
            + "OR LOWER(cd.folio) LIKE %:key% OR LOWER(cd.expediente) LIKE %:key% "
            + "OR LOWER(p.nombre) LIKE %:key% OR LOWER(p.apellidoPaterno) LIKE %:key% "
            + "OR LOWER(j.nombre) LIKE %:key% OR LOWER(o.nombre) LIKE %:key%) "
    )
    Page<Movimiento> getAllBandejaRecepcion(Pageable pageable, Integer juzgadoId, List<EstadoCarpeta> estado, String key, List<String> motivos);
}
