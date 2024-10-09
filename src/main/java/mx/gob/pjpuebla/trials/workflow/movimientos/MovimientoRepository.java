package mx.gob.pjpuebla.trials.workflow.movimientos;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Integer> {

    @Query
    ("""
        SELECT new mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoSalidaRecord(
        m.uuid, c.tipoCarpeta, c.folio, c.expediente, m.fecha, j.getNombre(), ''
        )
        FROM Movimiento m
        LEFT JOIN Carpeta c on c = m.carpeta
        LEFT JOIN Documento d on d = m.documento
        LEFT JOIN Juzgado j on j = m.juzgado
        WHERE m.uuid.toString() = :uuid
            """)
    List<MovimientoSalidaRecord> salidas(String uuid);
}
