package mx.gob.pjpuebla.trials.core.personas;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
                            dom.interior, dom.estadoRepublica, dom.municipio, dom.localidad, dom.colonia, dom.codigoPostal, dom.referencia, dom.ciudad),
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
                            dom.interior, dom.estadoRepublica, dom.municipio, dom.localidad, dom.colonia, dom.codigoPostal, dom.referencia, dom.ciudad),
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

        Optional<Persona> findByJuzgadoAndRolPrincipal(Juzgado j, String rolPrincipal);

        Optional<Persona> findByUsuarioAndJuzgadoIdAndEstadoIn(String usuario, Integer juzgadoId, List<Estado> estados);

        @Query("""
                SELECT new  mx.gob.pjpuebla.trials.core.personas.PersonaRecordResponse(
                        p.id,
                        CONCAT(p.nombre, ' ', p.apellidoPaterno, ' ', coalesce(p.apellidoMaterno, ''), ' '),
                        p.correoElectronico,
                        p.celular,
                        p.juzgado.nombre,
                        'ACTIVO',
                        p.rolPrincipal
                )
                FROM Persona p
                WHERE p.usuario IN (:usuario) AND p.juzgado.id = :juzgadoId AND p.estado IN :estados
                """)
        List<PersonaRecordResponse> getSecretariosOfJuzgado(List<String> usuario, Integer juzgadoId, List<Estado> estados);

        @Query("""
                        SELECT new mx.gob.pjpuebla.trials.core.personas.JuezRecord(
                            p.id,
                            CONCAT(p.nombre, ' ', p.apellidoPaterno, ' ', coalesce(p.apellidoMaterno, ''), ' ')
                        )
                        FROM Persona p
                        LEFT JOIN p.juzgado j
                        WHERE p.usuario IN (:usuarios) AND j.id = :juzgadoId AND p.estado IN :estados
                        """)
        List<JuezRecord> findByUsuarioInAndJuzgadoIdAndEstadoIn(
                        List<String> usuarios,
                        Integer juzgadoId,
                        List<Estado> estados);

        @Query("""
                        SELECT p FROM Persona p
                        LEFT JOIN p.juzgado juz
                        LEFT JOIN juz.materia ma
                        WHERE ma.nombre IN (:materias) AND p.usuario = :usuario
                        AND p.estado = mx.gob.pjpuebla.trials.util.enums.Estado.ACTIVE
                        """)
        Optional<Persona> findByUsuarioUUID(List<String> materias, String usuario);

        @Query("""
                            SELECT p FROM Persona p
                            LEFT JOIN p.oficialia o
                            LEFT JOIN p.juzgado j
                            WHERE (
                                :searchTerm IS NULL OR
                                LOWER(TRANSLATE(p.nombre, 'áéíóúüñÁÉÍÓÚÜÑ', 'aeiounAEIOUN')) LIKE LOWER(TRANSLATE(CONCAT('%', :searchTerm, '%'), 'áéíóúüñÁÉÍÓÚÜÑ', 'aeiounAEIOUN')) OR
                                LOWER(TRANSLATE(p.apellidoPaterno, 'áéíóúüñÁÉÍÓÚÜÑ', 'aeiounAEIOUN')) LIKE LOWER(TRANSLATE(CONCAT('%', :searchTerm, '%'), 'áéíóúüñÁÉÍÓÚÜÑ', 'aeiounAEIOUN')) OR
                                LOWER(TRANSLATE(p.apellidoMaterno, 'áéíóúüñÁÉÍÓÚÜÑ', 'aeiounAEIOUN')) LIKE LOWER(TRANSLATE(CONCAT('%', :searchTerm, '%'), 'áéíóúüñÁÉÍÓÚÜÑ', 'aeiounAEIOUN')) OR
                                LOWER(p.correoElectronico) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
                                LOWER(TRANSLATE(j.nombre, 'áéíóúüñÁÉÍÓÚÜÑ', 'aeiounAEIOUN')) LIKE LOWER(TRANSLATE(CONCAT('%', :searchTerm, '%'), 'áéíóúüñÁÉÍÓÚÜÑ', 'aeiounAEIOUN')) OR
                                LOWER(TRANSLATE(o.nombre, 'áéíóúüñÁÉÍÓÚÜÑ', 'aeiounAEIOUN')) LIKE LOWER(TRANSLATE(CONCAT('%', :searchTerm, '%'), 'áéíóúüñÁÉÍÓÚÜÑ', 'aeiounAEIOUN')) OR
                                LOWER(TRANSLATE(p.celular, 'áéíóúüñÁÉÍÓÚÜÑ', 'aeiounAEIOUN')) LIKE LOWER(TRANSLATE(CONCAT('%', :searchTerm, '%'), 'áéíóúüñÁÉÍÓÚÜÑ', 'aeiounAEIOUN'))
                            )
                             AND (
                            (:adminSistema = true) OR
                            (:oficialiaId IS NOT NULL AND p.oficialia.id = :oficialiaId) OR
                            (:juzgadoId IS NOT NULL AND p.juzgado.id = :juzgadoId) OR
                            (:adminSistema = false AND :oficialiaId IS NULL AND :juzgadoId IS NULL)
                            )
                        """)
        Page<Persona> findByCentroTrabajoAndSearch(
                        @Param("searchTerm") String searchTerm,
                        @Param("oficialiaId") Integer oficialiaId,
                        @Param("juzgadoId") Integer juzgadoId,
                        @Param("adminSistema") Boolean adminSistema,
                        Pageable pageable);

        List<Persona> findByJuzgadoId(Integer juzgadoId);

        Persona findByCorreoElectronico(String correo);

        Optional<Persona> findByUsuarioAndJuzgado(String usuario, Juzgado juzgado);
}
