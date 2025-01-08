package mx.gob.pjpuebla.trials.core.tipoprueba;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class TipoPruebasService {
    private final TipoPruebasRepository tipoPruebasRepository;
    private final CarpetaRepository carpetaRepository;

    public Optional<TipoPruebas> obtenerTipoPruebasPorNombre(String nombre) {
        return tipoPruebasRepository.findByNombre(nombre);
    }  
    
    public List<TipoPruebas> getTipoPruebasByTipoJuicioId(Integer carpetaId) {
        Carpeta carpeta = carpetaRepository.findById(carpetaId)
            .orElseThrow(() -> new EntityNotFoundException("Carpeta no encontrada con ID: " + carpetaId));

        TipoJuicio tipoJuicio =carpeta.getTipoJuicio();
        return tipoPruebasRepository.findByTipoJuicio(tipoJuicio);
    }
}
