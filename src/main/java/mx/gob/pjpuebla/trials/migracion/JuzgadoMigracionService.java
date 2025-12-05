package mx.gob.pjpuebla.trials.migracion;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoService;
import mx.gob.pjpuebla.trials.error.NotFoundException;

@Service
@RequiredArgsConstructor
public class JuzgadoMigracionService {

    private final JuzgadoService juzgadoService;

    public Juzgado requireJuzgadoActual(String clave) {
        return juzgadoService.findByClaveJuzgado(clave).orElseThrow(
                () -> new NotFoundException("Juzgado no encontrado en sistema actual", clave));
    }

}
