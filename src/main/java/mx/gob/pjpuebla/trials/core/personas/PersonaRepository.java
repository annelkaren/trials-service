package mx.gob.pjpuebla.trials.core.personas;

import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {

    @Query("""
            SELECT
            new mx.gob.pjpuebla.trials.core.personas.PersonaRecord(p.id, p.version, p.nombre, p.apellidoPaterno,
                p.apellidoMaterno, p.curp, p.rfc, p.fechaNacimiento, p.correoElectronico, p.telefono,
                p.celular, p.sexo, p.ocupacion, p.estado, ec.id, e.id, j.id, o.id,
                new mx.gob.pjpuebla.trials.core.domicilios.DomicilioRecord(dom.id, dom.calle, dom.exterior,
                dom.interior, dom.estadoRepublica, dom.municipio, dom.localidad, dom.colonia, dom.codigoPostal, dom.referencia),
                p.usuario, null
            )
            FROM Persona p
            LEFT JOIN p.escolaridad e
            LEFT JOIN p.estadoCivil ec
            LEFT JOIN p.domicilio dom
            LEFT JOIN p.juzgado j
            LEFT JOIN p.oficialia o
            WHERE p.id =:id AND p.estado IN :estados""")
    Optional<PersonaRecord> findByIdAndEstadoIn(Long id, List<Estado> estados);

    @Query("""
            SELECT
            new mx.gob.pjpuebla.trials.core.personas.PersonaRecord(p.id, p.version, p.nombre, p.apellidoPaterno,
                p.apellidoMaterno, p.curp, p.rfc, p.fechaNacimiento, p.correoElectronico, p.telefono,
                p.celular, p.sexo, p.ocupacion, p.estado, ec.id, e.id, j.id, o.id,
                new mx.gob.pjpuebla.trials.core.domicilios.DomicilioRecord(dom.id, dom.calle, dom.exterior,
                dom.interior, dom.estadoRepublica, dom.municipio, dom.localidad, dom.colonia, dom.codigoPostal, dom.referencia),
                p.usuario, null
            )
            FROM Persona p
            LEFT JOIN p.escolaridad e
            LEFT JOIN p.estadoCivil ec
            LEFT JOIN p.domicilio dom
            LEFT JOIN p.juzgado j
            LEFT JOIN p.oficialia o
            WHERE p.curp =:curp""")
    Optional<PersonaRecord> findByCurp(String curp);

    Optional<Persona> findByUsuario(String usuario);

    Optional<Persona> findByUsuarioAndJuzgadoIdAndEstadoIn(String usuario, Integer juzgadoId, List<Estado> estados);

    @Query("""
            SELECT p FROM Persona p
            WHERE CASE
                WHEN p.juzgado IS NULL AND p.oficialia IS NULL THEN 1
                WHEN :oficialiaId IS NOT NULL AND p.oficialia.id = :oficialiaId THEN 1
                WHEN :juzgadoId IS NOT NULL AND p.juzgado.id = :juzgadoId THEN 1
                ELSE 0
            END = 1
            """)
    Page<Persona> findByCentroTrabajo(Integer oficialiaId, Integer juzgadoId, Pageable pageable);

    Page<Persona> findByJuzgadoId(Integer juzgadoId, Pageable pageable);
}
