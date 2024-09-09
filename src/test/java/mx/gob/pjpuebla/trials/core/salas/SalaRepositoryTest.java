package mx.gob.pjpuebla.trials.core.salas;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import mx.gob.pjpuebla.trials.core.bloques.Bloque;
import mx.gob.pjpuebla.trials.core.bloques.BloqueRepository;
import mx.gob.pjpuebla.trials.core.bloques.BloqueSetUp;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoSetUp;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaRepository;
import mx.gob.pjpuebla.trials.core.personas.PersonaSetUp;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.sedes.SedeSetUp;
import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.Estado;

@DataJpaTest(properties = {
    "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class SalaRepositoryTest extends AuditConfigTest {
    
    @Autowired
    private JuzgadoRepository juzgadoRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private BloqueRepository bloqueRepository;

    @Autowired
    private SalaRepository salaRepository;

    @Test
    void findByIdAndEstadoActive(){
        Juzgado juzgado = juzgadoRepository.save(JuzgadoSetUp.createJuzgado(Estado.ACTIVE));
        Persona juez = personaRepository.save(PersonaSetUp.createPersona());
        Bloque bloque = bloqueRepository.save(BloqueSetUp.createBloque());
        
        Sala sala = SalaSetUp.createSala(Estado.ACTIVE);
        sala.setBloque(bloque);
        sala.setJuzgado(juzgado);
        sala.setJuez(juez);
        sala = salaRepository.save(sala);

        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        Optional<SalaRecordResponse> entity = salaRepository.findByIdAndEstadoIn(sala.getId(), estados);

        assertThat(entity).isPresent();
        assertThat(entity.get().estado()).isEqualTo(Estado.ACTIVE);
    }

    @Test
    void findByIdAndEstadoInactive(){
        Juzgado juzgado = juzgadoRepository.save(JuzgadoSetUp.createJuzgado(Estado.ACTIVE));
        Persona juez = personaRepository.save(PersonaSetUp.createPersona());
        Bloque bloque = bloqueRepository.save(BloqueSetUp.createBloque());
        
        Sala sala = SalaSetUp.createSala(Estado.INACTIVE);
        sala.setBloque(bloque);
        sala.setJuzgado(juzgado);
        sala.setJuez(juez);
        sala = salaRepository.save(sala);

        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        Optional<SalaRecordResponse> entity = salaRepository.findByIdAndEstadoIn(sala.getId(), estados);

        assertThat(entity).isPresent();
        assertThat(entity.get().estado()).isEqualTo(Estado.INACTIVE);
    }

    @Test
    void testCountByJuzgadoId() {
        Juzgado juzgado = juzgadoRepository.save(JuzgadoSetUp.createJuzgado(Estado.ACTIVE));
        Persona juez = personaRepository.save(PersonaSetUp.createPersona());
        Bloque bloque = bloqueRepository.save(BloqueSetUp.createBloque());

        Sala sala1 = SalaSetUp.createSala(Estado.ACTIVE);
        sala1.setJuzgado(juzgado);
        sala1.setJuez(juez);
        sala1.setBloque(bloque);
        salaRepository.save(sala1);

        Sala sala2 = SalaSetUp.createSala(Estado.ACTIVE);
        sala2.setJuzgado(juzgado);
        sala2.setJuez(juez);
        sala2.setBloque(bloque);
        salaRepository.save(sala2);

        long count = salaRepository.countByJuzgadoId(juzgado.getId());

        assertThat(count).isEqualTo(2);
    }

    @Test
    void testCountByJuzgadoId_noSalas() {
        Juzgado juzgado = juzgadoRepository.save(JuzgadoSetUp.createJuzgado(Estado.ACTIVE));

        long count = salaRepository.countByJuzgadoId(juzgado.getId());

        assertThat(count).isEqualTo(0); 
    }

}
