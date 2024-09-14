package mx.gob.pjpuebla.trials.workflow.documentos;

import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRecord;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.workflow.folios.SecuenciaRepositoryCustom;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Integer>, SecuenciaRepositoryCustom {

    /*
    @Query("""
              SELECT
                 new mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRecord(
                        t.id, t.nombre, t.tipoJuicio
                        new mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRecord(
                               dp.id, dp.nombre, pd.apellidoPaterno, pd.apellidoMaterno
                        ),
                        new mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRecord(
                                d.id
                        ),
              )
              FROM Documento d
                   LEFT JOIN p.tipopartes t 
                   LEFT JOIN p.personasdocumentos dp   
                        """)
    Optional<PersonaDocumentoRecord> findByIdAndParte(Integer id, List<Estado> estados);

    */
}
