package mx.gob.pjpuebla.trials.core.tipoprueba;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TipoPruebasService {
    private final TipoPruebasRepository tipoPruebasRepository;

    public Optional<TipoPruebas> obtenerTipoPruebasPorNombre(String nombre) {
        return tipoPruebasRepository.findByNombre(nombre);
    }   
}
