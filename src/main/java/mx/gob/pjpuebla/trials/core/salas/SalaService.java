package mx.gob.pjpuebla.trials.core.salas;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.error.OptimisticLockingFailureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mx.gob.pjpuebla.trials.core.bloques.BloqueRecord;
import mx.gob.pjpuebla.trials.core.bloques.BloqueRepository;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.personas.PersonaRepository;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.util.Estado;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class SalaService {
    private static final Logger logger = LoggerFactory.getLogger(SalaService.class);
    private final SalaRepository salaRepository;
    private final JuzgadoRepository JuzgadoRepository;
    private final BloqueRepository BloqueRepository;
    private final PersonaRepository JuezRepository;

    @Transactional(readOnly = true)
    public Page<SalaRecord> getAll(Sala example, Pageable pageable) {
        
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Page<Sala> page = salaRepository.findAll(Example.of(example, exampleMatcher), pageable);

        List<SalaRecord> list = page.getContent().stream()
            .map(sala -> new SalaRecord(    
                                        sala.getId(),
                                        sala.getNombre(), 
                                        sala.getJuez().getNombre() + " " + sala.getJuez().getApellidoPaterno() + " " + sala.getJuez().getApellidoMaterno(),
                                        sala.getJuzgado().getNombre(),
                                        sala.getBloque().getHoraFinal() + " " + sala.getBloque().getHoraFinal(),
                                        sala.getEstado()))

            .toList();
        
        return  new PageImpl<>(list, pageable, page.getTotalElements());
       
    }

    @Transactional(readOnly = true)
    public SalaRecordResponse findById(Integer id) {
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        return salaRepository.findByIdAndEstadoIn(id, estados)
            .orElseThrow(() -> new NotFoundException("Sala no encontrada", "salaId"));
    }

    public Integer create(Sala sala) {
        
        sala.setJuez(JuezRepository.findById(sala.getJuez().getId()).orElse(null));
        sala.setBloque(BloqueRepository.findById(sala.getBloque().getId()).orElse(null));
        sala.setJuzgado(JuzgadoRepository.findById(sala.getJuzgado().getId()).orElse(null));
        sala.setNombre(getNameOfSala(sala));

        sala = salaRepository.save(sala);
        return sala.getId();
    }

    public Integer update(Sala sala) {
        try {
            
            sala.setJuez(JuezRepository.findById(sala.getJuez().getId()).orElse(null));
            sala.setBloque(BloqueRepository.findById(sala.getBloque().getId()).orElse(null));
            sala.setJuzgado(JuzgadoRepository.findById(sala.getJuzgado().getId()).orElse(null));
            
            sala = salaRepository.save(sala);
            return sala.getId();

        } catch (org.springframework.dao.OptimisticLockingFailureException ex) {
            throw new OptimisticLockingFailureException("Sala modificada por otro usuario", "salaId");
        }  
    }


    //Obtiene el consecutivo segun el juzgado.
    public String getNameOfSala(Sala sala){
        // Obtener el ID del juzgado
        int juzgadoId = sala.getJuzgado().getId();

        // Contar las salas asociadas al juzgado
        long count = salaRepository.countByJuzgadoId(juzgadoId);

        // Sumar 1 al conteo y convertirlo a String
        String nombre = String.valueOf(count + 1);
        return nombre;
    }





}
