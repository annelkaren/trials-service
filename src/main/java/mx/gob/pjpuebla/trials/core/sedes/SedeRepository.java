package mx.gob.pjpuebla.trials.core.sedes;

import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SedeRepository extends JpaRepository<Sede, Integer> {

    @Query("""
            SELECT 
            new mx.gob.pjpuebla.trials.core.sedes.SedeRecord(s.id, s.version, s.nombre, s.estado, s.tipo, s.telefono, s.extension,
            new mx.gob.pjpuebla.trials.core.distritos.DistritoRecord(dis.id, dis.nombre),
            new mx.gob.pjpuebla.trials.core.domicilios.DomicilioRecord(dom.id, dom.calle, dom.exterior, 
                dom.interior, dom.estadoRepublica, dom.municipio, dom.localidad, dom.colonia, dom.codigoPostal, dom.referencia))
            FROM Sede s 
            LEFT JOIN s.domicilio dom
            LEFT JOIN s.distrito dis
            WHERE s.id =:id AND s.estado IN :estados""")
    Optional<SedeRecord> findByIdAndEstadoIn(Integer id, List<Estado> estados);

    @Query("""
        SELECT new mx.gob.pjpuebla.trials.core.sedes.SedeDomiciliosRecord(
            s.id,  s.nombre, d.calle,d.interior, d.exterior, d.colonia, d.codigoPostal, d.municipio,d.estadoRepublica,
            d.referencia, d.localidad
        )
        FROM Sede s
        JOIN s.domicilio d
        """)
    Page<SedeDomiciliosRecord> findSedesDomiciliosByJuzgadoId(Pageable pageable);

}
