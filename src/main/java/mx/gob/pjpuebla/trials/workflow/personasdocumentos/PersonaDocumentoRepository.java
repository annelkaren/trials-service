package mx.gob.pjpuebla.trials.workflow.personasdocumentos;

import java.util.List;

import mx.gob.pjpuebla.trials.util.enums.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import mx.gob.pjpuebla.trials.util.enums.Estado;

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
                d.id
            )
            FROM PersonaDocumento pd
            JOIN pd.documento d
            JOIN pd.tipoPartes tp
            WHERE d.id = :documentoId
            AND d.estatus  IN :estado
            AND pd.rol IN :rol
            AND tp.nombre = :parte
            """)
    PersonaDocumentoRecord findDocumentoPersonaTipoParteByDocumentoId(
            @Param("documentoId") Integer documentoId,
            @Param("parte") String parte,
            @Param("estado") List<Estado> estados,
            @Param("rol") List<Rol> rol);
}
