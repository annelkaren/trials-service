package mx.gob.pjpuebla.trials.core.salas;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.error.InvalidVersionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.bloques.Bloque;
import mx.gob.pjpuebla.trials.core.bloques.BloqueCitaItem;
import mx.gob.pjpuebla.trials.core.bloques.BloqueRepository;
import mx.gob.pjpuebla.trials.core.eventos.EventoService;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.personas.PersonaRepository;
import mx.gob.pjpuebla.trials.core.tipoaudiencia.TipoAudiencia;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartes;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;

import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.workflow.audiencias.Audiencia;
import mx.gob.pjpuebla.trials.workflow.audiencias.AudienciaRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;

import java.time.LocalDateTime;
import java.time.LocalDate;
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
    private final EventoService eventoService;
    private static final Integer TIEMPO_ESPERA_AUDIENCIA =  3;

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

    /***
     * 
     * @param juzgado Juzgado del Distrito
     * @param tipoAudiencia Tipo de Audiencia de Oralidad
     * @return SalaAudienciaRecord Primera sala disponible del Juzgado según la configuración del bloque
     */
    public SalaAudienciaRecord asignarSala(Juzgado juzgado, TipoAudiencia tipoAudiencia){
        LocalDate fecha = LocalDate.now().plusDays(TIEMPO_ESPERA_AUDIENCIA);
        LocalDateTime ultimaFechaAudiencia = audienciaRepository.getFechaUltimaAudiencia(juzgado, tipoAudiencia);
        List<Bloque> bloques = bloqueRepository.findBloquesSalasJuzgado(juzgado);

        if (ultimaFechaAudiencia!=null && ultimaFechaAudiencia.toLocalDate().isAfter(fecha)){
            fecha = ultimaFechaAudiencia.toLocalDate();
        }

        int max = 3;
        int intentos = 0;

        while(intentos <= max){

            if (eventoService.esDiaInHabil(fecha, juzgado, null)==Boolean.TRUE){
                fecha = eventoService.siguienteDiaHabil(fecha, juzgado, null);
            }

            for (Bloque bloque: bloques){
                List<BloqueCitaItem> citas = bloque.getData().getCitas();
    
                for (BloqueCitaItem cita: citas){
                    LocalDateTime fechaHoraAudiencia = LocalDateTime.of(fecha, cita.getHoraCitas());

                    Sala salaDisponible = this.findSalaDisponible(fechaHoraAudiencia, bloque, juzgado);
    
                    if (salaDisponible!=null){
                        return new SalaAudienciaRecord(salaDisponible.getId(), salaDisponible.getNombre(), 
                        salaDisponible.getJuez().getId(), salaDisponible.getJuez().getNombre(), juzgado.getNombre(), 
                        bloque.getId(), fechaHoraAudiencia);
                    }
                }
            }

            fecha = fecha.plusDays(1);
            intentos++;
        }

        throw new NotFoundException("No existe una sala disponible", "Sala");
    }

    public Sala findSalaDisponible(LocalDateTime fechaAudiencia, Bloque bloque, Juzgado juzgado){
        Optional<Sala> salaDisponible = salaRepository.findSalaDisponible(fechaAudiencia, bloque, juzgado)
            .stream().findFirst();

        return salaDisponible.orElse(null);
    }

    public SalaAudienciaRecord asignarSalaConexidad(PersonaDocumentoRecord actor, PersonaDocumentoRecord demandado, TipoJuicio tipoJuicio, TipoAudiencia tipoAudiencia){
         List<Carpeta> carpetas = new ArrayList<>();
        
        TipoPartes actorParte = tipoPartesRepository.findByNombreAndTipoJuicioId("Actor", tipoJuicio.getId()).orElseThrow(() -> new NotFoundException("Tipo Parte no encontrado", actor.tipoParte()));
        TipoPartes demandadoParte = tipoPartesRepository.findByNombreAndTipoJuicioId("Demandado", tipoJuicio.getId()).orElseThrow(() -> new NotFoundException("Tipo Parte no encontrado", demandado.tipoParte()));

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
                return asignarAudiencia(audiencia.get().getSala(), tipoAudiencia);
            }
        }

        return null;
    }


    /***
     * 
     * @param sala Sala de Audiencia 
     * @param tipoAudiencia Tipo de Audiencia de Oralidad
     * @return SalaAudienciaRecord Primer horario disponible de la Sala
     */
    public SalaAudienciaRecord asignarAudiencia(Sala sala, TipoAudiencia tipoAudiencia){
        LocalDate fecha = LocalDate.now().plusDays(TIEMPO_ESPERA_AUDIENCIA);
        LocalDateTime ultimaFechaAudiencia = audienciaRepository.getFechaUltimaAudiencia(sala.getJuzgado(), tipoAudiencia);
        Bloque bloque = sala.getBloque();
        int max = 3;
        int intentos = 0;

        if (ultimaFechaAudiencia!=null && ultimaFechaAudiencia.toLocalDate().isAfter(fecha)){
            fecha = ultimaFechaAudiencia.toLocalDate();
        }
 
        List<BloqueCitaItem> citas = bloque.getData().getCitas();

        while(intentos<=max){

            if (eventoService.esDiaInHabil(fecha, sala.getJuzgado(), null)==Boolean.TRUE){
                fecha = eventoService.siguienteDiaHabil(fecha, sala.getJuzgado(), null);
            }

            for (BloqueCitaItem cita: citas){
                LocalDateTime fechaHoraAudiencia = LocalDateTime.of(fecha, cita.getHoraCitas());
    
                if (checkHoraDisponible(fechaHoraAudiencia, sala)==Boolean.TRUE){
                    return new SalaAudienciaRecord(sala.getId(), sala.getNombre(),
                    sala.getJuez().getId(), sala.getJuez().getNombre(), sala.getJuzgado().getNombre(),
                    bloque.getId(), fechaHoraAudiencia);
                }
            }

            fecha = fecha.plusDays(1);
            intentos++;
        }

        throw new NotFoundException("No hay Horario disponible", "Sala");    
        
    }

    public Boolean checkHoraDisponible(LocalDateTime fechaAudiencia, Sala sala){
        Optional<Sala> salaDisponible = salaRepository.checkHoraDisponible(fechaAudiencia, sala);

        if (salaDisponible.isPresent())
            return Boolean.TRUE;

        return Boolean.FALSE;
    }

}
