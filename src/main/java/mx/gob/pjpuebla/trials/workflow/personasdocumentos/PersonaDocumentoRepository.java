package mx.gob.pjpuebla.trials.workflow.personasdocumentos;

import java.util.List;

import mx.gob.pjpuebla.trials.util.enums.Rol;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoAnexoRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;

@Repository
public interface PersonaDocumentoRepository extends JpaRepository<PersonaDocumento, Integer> {
    public List<PersonaDocumento> findByNombreAndApellidoPaternoAndApellidoMaternoAndPseudonimoAndTipoPartesId(String nombre, String apellidoPaterno, String apellidoMaterno, String pseudonimo, Integer tipoParte);

    @Query("""
            SELECT new mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRecord(
                pd.nombre,
                pd.apellidoPaterno,
                pd.apellidoMaterno,
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
    PersonaDocumentoRecord findDocumentoPersonaTipoParteByCarpetaId(
            @Param("carpetaId") Integer carpetaId,
            @Param("parte") String parte,
            @Param("rol") List<Rol> rol);

    public List<PersonaDocumento> findByNombreIgnoreCaseAndApellidoPaternoIgnoreCaseAndApellidoMaternoIgnoreCaseAndPseudonimoIgnoreCaseAndTipoPartesId(String nombre, String apellidoPaterno, String apellidoMaterno, String pseudonimo, Integer tipoParte);

//    @Query("""
//    SELECT new mx.gob.pjpuebla.trials.workflow.documentos.DocumentoAnexoRecord(
//        pd.id,
//        pd.nombre,
//        pd.apellidoPaterno,
//        pd.apellidoMaterno,
//        pd.pseudonimo,
//        pd.tipoPersona,
//        tp.nombre,
//        tp.id
//    )
//    FROM PersonaDocumento pd
//    JOIN pd.carpeta c
//    JOIN pd.tipoPartes tp
//    WHERE c.id = :carpetaId
//   """)
//    List<DocumentoAnexoRecord> findDocumentoAnexoByCarpetaId(@Param("carpetaId") Integer carpetaId);

    @Query("""
    SELECT a.nombre
    FROM Anexo a
    WHERE a.documento.id = :documentoId
""")
    List<String> findNombresAnexosByDocumentoId(@Param("documentoId") Integer documentoId);
}
