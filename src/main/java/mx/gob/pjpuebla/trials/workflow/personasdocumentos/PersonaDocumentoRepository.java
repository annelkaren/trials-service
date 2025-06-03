package mx.gob.pjpuebla.trials.workflow.personasdocumentos;

import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRecord;
import mx.gob.pjpuebla.trials.litigante.LitiganteExpedientesRecord;
import mx.gob.pjpuebla.trials.litigante.responselitigante.LibroGobiernoRecord;
import mx.gob.pjpuebla.trials.util.enums.Rol;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.ApelacionRecordResponse;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.PersonaDataRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.RelacionExpedientesRecord;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonaDocumentoRepository extends JpaRepository<PersonaDocumento, Integer> {

    List<PersonaDocumento> findByNombreAndApellidoPaternoAndApellidoMaternoAndPseudonimoAndTipoPartesId(String nombre, String apellidoPaterno, String apellidoMaterno, String pseudonimo, Integer tipoParte);

    @Query("""
            SELECT new mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRecord(
                pd.nombre,
                pd.apellidoPaterno,
                pd.apellidoMaterno,
                pd.pseudonimo,
                pd.tipoPersona,
                pd.curp,
                pd.domicilio,
                pd.celular,
                pd.correoElectronico,
                tp.nombre,
                tp.id,
                c.id
            )
            FROM PersonaDocumento pd
            JOIN pd.carpeta c
            JOIN pd.tipoPartes tp
            WHERE c.id = :carpetaId
            AND pd.rol IN :rol
            AND tp.nombre = :parte
            """)
    PersonaDocumentoRecord findPersonaAndTipoParteByCarpetaId(
            @Param("carpetaId") Integer carpetaId,
            @Param("parte") String parte,
            @Param("rol") List<Rol> rol);

            @Query("""
                SELECT new mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRecord(
                    pd.nombre,
                    pd.apellidoPaterno,
                    pd.apellidoMaterno,
                    pd.pseudonimo,
                    pd.tipoPersona,
                    pd.curp,
                    pd.domicilio,
                    pd.celular,
                    pd.correoElectronico,
                    tp.nombre,
                    tp.id,
                    c.id
                )
                FROM PersonaDocumento pd
                JOIN pd.carpeta c
                JOIN pd.tipoPartes tp
                WHERE c.id = :carpetaId
                AND pd.rol IN :rol
                AND tp.nombre = :parte
                """)
        List<PersonaDocumentoRecord> findPersonaAndTipoParteByCarpetaIdPenal(
                @Param("carpetaId") Integer carpetaId,
                @Param("parte") String parte,
                @Param("rol") List<Rol> rol);

    List<PersonaDocumento> findByNombreIgnoreCaseAndApellidoPaternoIgnoreCaseAndApellidoMaternoIgnoreCaseAndPseudonimoIgnoreCaseAndTipoPartesId(String nombre, String apellidoPaterno, String apellidoMaterno, String pseudonimo, Integer tipoParte);

    @Query("""
             SELECT new mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRecord(
                         pd.nombre,
                         pd.apellidoPaterno,
                         pd.apellidoMaterno,
                         pd.pseudonimo,
                         pd.tipoPersona,
                         pd.curp,
                         pd.domicilio,
                         pd.celular,
                         pd.correoElectronico,
                         tp.nombre,
                         tp.id,
                         c.id
             )
             FROM PersonaDocumento pd
             JOIN pd.carpeta c
             JOIN pd.tipoPartes tp
             WHERE c.id = :carpetaId
             AND pd.rol = :rol
            """)
    List<PersonaDocumentoRecord> findPersonasByCarpetaId(@Param("carpetaId") Integer carpetaId, @Param("rol") Rol rol);

    @Query("""
            SELECT new mx.gob.pjpuebla.trials.workflow.carpeta.records.ApelacionRecordResponse(
                pd.nombre,
                pd.apellidoPaterno,
                pd.apellidoMaterno,
                pd.pseudonimo,
                pd.tipoPersona,
                pd.rol,
                pd.carpeta.id,
                pd.tipoPartes.nombre,
                pd.tipoPartes.id
            )
            FROM PersonaDocumento pd
            WHERE pd.carpeta.id = :carpetaId
            """)
    List<ApelacionRecordResponse> findPersonaDocumentoByCarpetaId(Integer carpetaId);

    @Query("""
        SELECT new mx.gob.pjpuebla.trials.workflow.carpeta.records.RelacionExpedientesRecord(c.expediente, j.nombre, tj.nombre)
        FROM PersonaDocumento pd
        JOIN pd.carpeta c
        JOIN c.juzgado j
        JOIN c.tipoJuicio tj
        JOIN tj.materia m
        WHERE lower(pd.nombre) = lower(:nombreA)
        AND lower(pd.apellidoPaterno) = lower(:apellidoP)
        AND lower(pd.apellidoMaterno) = lower(:apellidoM)
        AND m.id = :materiaId
        """)
    List<RelacionExpedientesRecord> getAllExpedienteRelacionadosByPersonaId(@Param("nombreA") String nombreA, @Param("apellidoM") String apellidoM, @Param("apellidoP") String apellidoP, @Param("materiaId") Integer materiaId);


    @Query("""
            SELECT new mx.gob.pjpuebla.trials.workflow.carpeta.records.PersonaDataRecord(
                pd.id,
                pd.nombre,
                pd.apellidoPaterno,
                pd.apellidoMaterno,
                pd.tipoPartes.nombre,
                pd.rol,
                pd.pseudonimo
            )
            FROM PersonaDocumento pd
            WHERE pd.carpeta.id = :carpetaId
            """)
    List<PersonaDataRecord> findPersonaDocumentoDataByCarpetaId(Integer carpetaId);

    List<PersonaDocumento> findByCarpetaIdAndRolAndTipoPartesNombre(Integer id, Rol rol, String parte);

    List<PersonaDocumento> findByCarpetaId(Integer carpetaId);

    @Query("""
        SELECT new mx.gob.pjpuebla.trials.litigante.LitiganteExpedientesRecord(ca.id, ca.expediente,  ma.nombre,
         tj.nombre, '', '', juz.nombre, 0L, '')
        FROM PersonaDocumento pd
        JOIN pd.carpeta ca
        JOIN ca.juzgado juz
        JOIN ca.tipoJuicio tj
        JOIN tj.materia ma
        WHERE (lower(pd.correoElectronico) = :username
        OR lower(pd.correoNotificacion) = :username)
        AND tipoNotificacion = mx.gob.pjpuebla.trials.util.enums.TipoNotificacion.CORREO_ELECTRONICO
        AND (lower(juz.nombre) LIKE %:key% OR lower(ca.expediente) LIKE %:key%)
        GROUP BY (ca.id, ca.expediente, ma.nombre, tj.nombre, juz.nombre)
        """)
    Page<LitiganteExpedientesRecord> findByUsername(String username, String key, Pageable pageable);

    @Query("""
        SELECT new mx.gob.pjpuebla.trials.litigante.LitiganteExpedientesRecord(ca.id, CONCAT(ca.expediente, ' - ', juz.nombre),
        '', '', '', '', '', 0L, '')
        FROM PersonaDocumento pd
        JOIN pd.carpeta ca
        JOIN ca.juzgado juz
        WHERE (lower(pd.correoElectronico) = :username
        OR lower(pd.correoNotificacion) = :username)
        AND tipoNotificacion = mx.gob.pjpuebla.trials.util.enums.TipoNotificacion.CORREO_ELECTRONICO
        GROUP BY (ca.id, ca.expediente, juz.nombre)
        ORDER BY juz.nombre, ca.expediente
        """)
    List<LitiganteExpedientesRecord> findAllByUsername(String username);

    @Query("""
        SELECT CONCAT(pd.nombre, ' ', pd.apellidoPaterno, ' ', pd.apellidoMaterno)
        FROM PersonaDocumento pd
        JOIN pd.carpeta ca
        JOIN ca.tipoJuicio tj
        JOIN pd.tipoPartes tp
        WHERE pd.rol = mx.gob.pjpuebla.trials.util.enums.Rol.PRINCIPAL
        and ca.id = :carpetaId and tp.nombre = :tipoParte
        """)
    List<String> findTipoPartePrincipalByCarpetaId(Integer carpetaId, String tipoParte);

    @Query("""
            SELECT new mx.gob.pjpuebla.trials.litigante.responselitigante.LibroGobiernoRecord(
                ca.expediente,
                j.nombre,
                tj.nombre,
                tp.nombre
            )
            FROM PersonaDocumento pd
            JOIN pd.carpeta ca
            JOIN ca.tipoJuicio tj
            JOIN pd.tipoPartes tp
            JOIN ca.juzgado j
            WHERE LOWER(TRANSLATE(pd.nombre, 'áéíóúüñÁÉÍÓÚÜÑ', 'aeiounAEIOUN')) = LOWER(:nombre)
            AND LOWER(TRANSLATE(pd.apellidoPaterno, 'áéíóúüñÁÉÍÓÚÜÑ', 'aeiounAEIOUN')) = LOWER(:aPaterno)
            AND LOWER(TRANSLATE(pd.apellidoMaterno, 'áéíóúüñÁÉÍÓÚÜÑ', 'aeiounAEIOUN')) = LOWER(:aMaterno)
            ORDER BY ca.expediente
            """)
    Page<LibroGobiernoRecord> findByNombreCompleto(String nombre, String aPaterno, String aMaterno, Pageable pageable);

    @Query("""
            SELECT new mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRecord(
                pd.id,
                CONCAT(pd.nombre, ' ', COALESCE(pd.apellidoPaterno, ''), ' ', COALESCE(pd.apellidoMaterno || ' ', '')),
                tp.nombre
            )
            FROM PersonaDocumento pd
            JOIN pd.carpeta ca
            JOIN pd.tipoPartes tp
            WHERE ca.id = :carpetaId
            ORDER BY tp.nombre, pd.nombre
            """)
    List<TipoPartesRecord> findPartesByCarpetaId(Integer carpetaId);

}
