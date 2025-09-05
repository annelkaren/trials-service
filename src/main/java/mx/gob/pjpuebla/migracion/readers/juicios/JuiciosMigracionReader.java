package mx.gob.pjpuebla.migracion.readers.juicios;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;

@Service
@RequiredArgsConstructor
public class JuiciosMigracionReader {

    private final JuiciosMigracionRepository juiciosMigracionRepository;
    private final TipoJuicioRepository tipoJuicioRepository;

    /**
     * Busca los datos del juicio asociado al ID recibido.
     *
     * @param idJuicio ID del juicio
     * @return Entidad `JuiciosMigracion` o null si no se encuentra
     */
    public JuiciosMigracion buscarJuicio(String idJuicio) {
        Optional<JuiciosMigracion> juicios = juiciosMigracionRepository.findById(idJuicio);
        if (juicios.isPresent()) {
            return juicios.get();
        }
        return null;
    }

    public TipoJuicio crearTipoJuicio(Materia materia, String nombre) {
        TipoJuicio tipoJuicio = new TipoJuicio()
                .setEstado(Estado.INACTIVE)
                .setMateria(materia)
                .setNombre(nombre)
                .setTipoCausa(null)
                .setTipoJuicioPadreOral(null)
                .setTipoJuicioPadreTrad(null);

        return tipoJuicioRepository.save(tipoJuicio);
    }

}
