package mx.gob.pjpuebla.trials.workflow.personasdocumentos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartes;

@Repository
public interface PersonaDocumentoRepository extends JpaRepository<PersonaDocumento, Integer> {
    public List<PersonaDocumento> findByNombreAndApellidoPaternoAndApellidoMaternoAndPseudonimoAndTipoPartes(String nombre, String apellidoPaterno, String apellidoMaterno, String pseudonimo, TipoPartes tipoParte);
}
