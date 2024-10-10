package mx.gob.pjpuebla.trials.workflow.movimientos;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;

import java.util.List;
import java.util.UUID;

import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Transactional
@RequiredArgsConstructor
@Service
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;

    public List<MovimientoSalidaRecord> getMovimientosSalida(String uuid){

        UUID uuidMov = UUID.fromString(uuid);
        return movimientoRepository.salidas(uuidMov, EstadoCarpeta.TURNADO);
    }
    
    public Movimiento createMovimento(Carpeta carpeta, Documento documento, Persona persona, String motivo) {
        Movimiento movimiento = new Movimiento()
                .setCarpeta(carpeta)
                .setDocumento(documento)
                .setFechaAsignacion(LocalDateTime.now())
                .setMotivo(motivo)
                .setPersona(persona)
                .setOficialia(persona.getOficialia())
                .setJuzgado(persona.getJuzgado());
        movimiento = this.movimientoRepository.save(movimiento);
        return movimiento;
    }
}
