package mx.gob.pjpuebla.trials.core.salas;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.error.InvalidVersionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.bloques.BloqueRepository;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.personas.PersonaRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartes;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.workflow.audiencias.Audiencia;
import mx.gob.pjpuebla.trials.workflow.audiencias.AudienciaRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoItemRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class SalaService {

    private final SalaRepository salaRepository;
    private final JuzgadoRepository juzgadoRepository;
    private final BloqueRepository bloqueRepository;
    private final PersonaRepository juezRepository;
    private final AudienciaRepository audienciaRepository;
    private final TipoPartesRepository tipoPartesRepository;
    private final PersonaDocumentoRepository personaDocumentoRepository;

    @Transactional(readOnly = true)
    public Page<SalaRecord> getAll(Sala example, Pageable pageable) {

        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Page<Sala> page = salaRepository.findAll(Example.of(example, exampleMatcher), pageable);

        List<SalaRecord> list = page.getContent().stream()
                .map(sala -> new SalaRecord(
                        sala.getId(),
                        sala.getNombre(),
                        sala.getJuez().getNombre() + " " + sala.getJuez().getApellidoPaterno() + " "
                                + sala.getJuez().getApellidoMaterno(),
                        sala.getJuzgado().getNombre(),
                        new mx.gob.pjpuebla.trials.core.bloques.BloqueRecord(sala.getBloque().getId(),
                                sala.getBloque().getHoraInicial(), sala.getBloque().getHoraFinal()),
                        sala.getEstado()))

                .toList();

        return new PageImpl<>(list, pageable, page.getTotalElements());

    }

    @Transactional(readOnly = true)
    public SalaRecordResponse findById(Integer id) {
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        return salaRepository.findByIdAndEstadoIn(id, estados)
                .orElseThrow(() -> new NotFoundException("Sala no encontrada", "salaId"));
    }

    public Integer create(Sala sala) {

        sala.setJuez(juezRepository.findById(sala.getJuez().getId()).orElse(null));
        sala.setBloque(bloqueRepository.findById(sala.getBloque().getId()).orElse(null));
        sala.setJuzgado(juzgadoRepository.findById(sala.getJuzgado().getId()).orElse(null));
        sala.setNombre(getNameOfSala(sala));

        sala = salaRepository.save(sala);
        return sala.getId();
    }

    public Integer update(Sala sala) {
        try {

            sala.setJuez(juezRepository.findById(sala.getJuez().getId()).orElse(null));
            sala.setBloque(bloqueRepository.findById(sala.getBloque().getId()).orElse(null));
            sala.setJuzgado(juzgadoRepository.findById(sala.getJuzgado().getId()).orElse(null));

            sala = salaRepository.save(sala);
            return sala.getId();

        } catch (org.springframework.dao.OptimisticLockingFailureException ex) {
            throw new InvalidVersionException(Sala.class.getSimpleName());
        }
    }

    public String getNameOfSala(Sala sala) {
        if (sala.getJuzgado() != null && sala.getJuzgado().getId() != null) {
            int juzgadoId = sala.getJuzgado().getId();

            long count = salaRepository.countByJuzgadoId(juzgadoId);

            return String.valueOf(count + 1);
        } else {

            return "1";
        }
    }

    public SalaRecord findSalaDisponible(LocalDateTime fechaAudiencia, Juzgado juzgado){

        Optional<SalaRecord> salaDisponible = salaRepository.findSalaDisponible(fechaAudiencia, juzgado).stream().findFirst();

        while (salaDisponible.isEmpty()){
            LocalDateTime siguienteFecha = fechaAudiencia.plusDays(1);

            if (siguienteFecha.getDayOfWeek()==DayOfWeek.SATURDAY){
                siguienteFecha = siguienteFecha.plusDays(2);
            }else if(siguienteFecha.getDayOfWeek()==DayOfWeek.SUNDAY){
                siguienteFecha = siguienteFecha.plusDays(1);
            }

            salaDisponible = salaRepository.findSalaDisponible(siguienteFecha, juzgado).stream().findFirst();
        }

        return salaDisponible.get();
    }

    public Sala findConexidadSala(PersonaDocumentoItemRecord actor, PersonaDocumentoItemRecord demandado, TipoJuicio tipoJuicio){
         List<Carpeta> carpetas = new ArrayList<>();
        
        TipoPartes actorParte = tipoPartesRepository.findByNombreAndTipoJuicioId("Actor", tipoJuicio.getId()).orElseThrow(() -> new NotFoundException("Tipo Parte no encontrado", actor.tipoParte().toString()));
        TipoPartes demandadoParte = tipoPartesRepository.findByNombreAndTipoJuicioId("Demandado", tipoJuicio.getId()).orElseThrow(() -> new NotFoundException("Tipo Parte no encontrado", demandado.tipoParte().toString()));

        List<PersonaDocumento> registrosActor = personaDocumentoRepository
                .findByNombreIgnoreCaseAndApellidoPaternoIgnoreCaseAndApellidoMaternoIgnoreCaseAndPseudonimoIgnoreCaseAndTipoPartesId(
                        actor.nombre(), actor.apellidoPaterno(), actor.apellidoMaterno(), actor.pseudonimo(), actorParte.getId());

        List<PersonaDocumento> registrosDemandado = personaDocumentoRepository
                .findByNombreIgnoreCaseAndApellidoPaternoIgnoreCaseAndApellidoMaternoIgnoreCaseAndPseudonimoIgnoreCaseAndTipoPartesId(
                        demandado.nombre(), demandado.apellidoPaterno(), demandado.apellidoMaterno(), demandado.pseudonimo(), demandadoParte.getId());

        if (registrosActor.isEmpty() || registrosDemandado.isEmpty()) {
            return null;
        }

        for (PersonaDocumento tmp : registrosActor) {
            carpetas.add(tmp.getCarpeta());
        }

        for (PersonaDocumento tmp : registrosDemandado) {
            Optional<Audiencia> audiencia;

            if (!carpetas.contains(tmp.getCarpeta()))
                continue;

            audiencia = audienciaRepository.findByCarpeta(tmp.getCarpeta());

            if (audiencia.isPresent()){
                return audiencia.get().getSala();
            }
        }
        return null;
    }

}
