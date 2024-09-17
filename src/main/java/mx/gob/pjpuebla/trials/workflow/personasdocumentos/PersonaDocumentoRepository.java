package mx.gob.pjpuebla.trials.workflow.personasdocumentos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonaDocumentoRepository extends JpaRepository<PersonaDocumento, Integer> {
    public List<PersonaDocumento> findByNombreIgnoreCaseAndApellidoPaternoIgnoreCaseAndApellidoMaternoIgnoreCaseAndPseudonimoIgnoreCaseAndTipoPartesId(String nombre, String apellidoPaterno, String apellidoMaterno, String pseudonimo, Integer tipoParte);
}
