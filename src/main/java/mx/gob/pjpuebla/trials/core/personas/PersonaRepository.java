package mx.gob.pjpuebla.trials.core.personas;

import mx.gob.pjpuebla.trials.util.enums.Estado;
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
                p.celular, p.sexo, p.ocupacion, p.estado, ec.id, e.id, j.id,
                new mx.gob.pjpuebla.trials.core.domicilios.DomicilioRecord(dom.id, dom.calle, dom.exterior, 
                dom.interior, dom.estadoRepublica, dom.municipio, dom.localidad, dom.colonia, dom.codigoPostal, dom.referencia),
                p.usuario, null
            )
            FROM Persona p 
            LEFT JOIN p.escolaridad e
            LEFT JOIN p.estadoCivil ec
            LEFT JOIN p.domicilio dom
            LEFT JOIN p.juzgado j
            WHERE p.id =:id AND p.estado IN :estados""")
    Optional<PersonaRecord> findByIdAndEstadoIn(Long id, List<Estado> estados);

    @Query("""
            SELECT 
            new mx.gob.pjpuebla.trials.core.personas.PersonaRecord(p.id, p.version, p.nombre, p.apellidoPaterno, 
                p.apellidoMaterno, p.curp, p.rfc, p.fechaNacimiento, p.correoElectronico, p.telefono,
                p.celular, p.sexo, p.ocupacion, p.estado, ec.id, e.id, j.id,
                new mx.gob.pjpuebla.trials.core.domicilios.DomicilioRecord(dom.id, dom.calle, dom.exterior, 
                dom.interior, dom.estadoRepublica, dom.municipio, dom.localidad, dom.colonia, dom.codigoPostal, dom.referencia),
                p.usuario, null
            )
            FROM Persona p 
            LEFT JOIN p.escolaridad e
            LEFT JOIN p.estadoCivil ec
            LEFT JOIN p.domicilio dom
            LEFT JOIN p.juzgado j
            WHERE p.curp =:curp""")
    Optional<PersonaRecord> findByCurp(String curp);

    Persona findByUsuario(String usuario);
}
