package mx.gob.pjpuebla.trials.workflow.movimientos;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Integer> {

    @Query
    ("""
        SELECT new mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoSalidaRecord(
        m.uuid, c.tipoCarpeta, c.folio, c.expediente, m.fechaAsignacion, j.nombre, d.data
        )
        FROM Movimiento m
        LEFT JOIN Carpeta c on c = m.carpeta and c.estatus = :estadoCarpeta
        LEFT JOIN Documento d on d = m.documento
        LEFT JOIN Juzgado j on j = m.juzgado
        WHERE m.uuid = :uuid
        """)
    List<MovimientoSalidaRecord> getSalidas(UUID uuid, EstadoCarpeta estadoCarpeta);
}
