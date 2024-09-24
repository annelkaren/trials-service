package mx.gob.pjpuebla.trials.core.instituciones;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoRepository;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.springframework.data.domain.Page;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"
})
class InstitucionRepositoryTest extends AuditConfigTest {

    @Autowired
    private InstitucionRepository institucionRepository;
    @Autowired
    private DomicilioRepository domicilioRepository;
    @Autowired
    private DistritoRepository distritoRepository;

    @Test
    void findByIdAndEstadoActive() {
        Distrito distrito = distritoRepository.save(DistritoSetUp.createDistrito());
        Domicilio domicilio = domicilioRepository.save(DomicilioSetUp.createDomicilio());

        Institucion institucion = InstitucionSetUp.createInstitucion(Estado.ACTIVE);
        institucion.setDomicilio(domicilio);
        institucion.setDistrito(distrito);
        institucion = institucionRepository.save(institucion);
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        Optional<InstitucionRecordResponse> entity = institucionRepository.findByIdAndEstadoIn(institucion.getId(),
                estados);
        assertThat(entity).isPresent();
        assertThat(entity.get().estado()).isEqualTo(Estado.ACTIVE);
    }

    @Test
    void findAllEstadoIn_return_page() {
      
        Institucion institucion = InstitucionSetUp.createInstitucion(Estado.ACTIVE);
        Domicilio domicilio = domicilioRepository.save(DomicilioSetUp.createDomicilio());
        Distrito distrito = distritoRepository.save(DistritoSetUp.createDistrito());

        institucion.setDomicilio(domicilio);
        institucion.setDistrito(distrito);
        institucion = institucionRepository.save(institucion); // Asegúrate de guardar la institución
    
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
    
        Page<InstitucionRecord> result = institucionRepository.findAllEstadoIn(estados, PageRequest.of(0, 1));
    
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).id()).isEqualTo(institucion.getId());
        assertThat(result.getContent().get(0).nombre()).isEqualTo(institucion.getNombre());
        assertThat(result.getContent().get(0).telefono()).isEqualTo(institucion.getTelefono());
    }
    

}
