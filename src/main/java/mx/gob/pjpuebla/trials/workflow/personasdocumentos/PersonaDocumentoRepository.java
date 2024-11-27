package mx.gob.pjpuebla.trials.workflow.personasdocumentos;

import mx.gob.pjpuebla.trials.util.enums.Rol;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.ApelacionRecordResponse;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.PersonaDataRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.RelacionExpedientesRecord;
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
                pd.rol
            )
            FROM PersonaDocumento pd
            WHERE pd.carpeta.id = :carpetaId
            """)
    List<PersonaDataRecord> findPersonaDocumentoDataByCarpetaId(Integer carpetaId);

    List<PersonaDocumento> findByCarpetaIdAndRolAndTipoPartesNombre(Integer id, Rol rol, String parte);
}
