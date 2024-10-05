package mx.gob.pjpuebla.trials.workflow.personasdocumentos;

import mx.gob.pjpuebla.trials.util.enums.Rol;
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
}
