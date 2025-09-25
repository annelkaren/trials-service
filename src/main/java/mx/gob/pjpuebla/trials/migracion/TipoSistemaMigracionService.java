package mx.gob.pjpuebla.trials.migracion;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaRepository;

@Service
@RequiredArgsConstructor
public class TipoSistemaMigracionService {
    private final TipoSistemaRepository tipoSistemaRepository;

    public TipoSistema findTipoSistemaByNombre(String nombreTipoSistema){
        return tipoSistemaRepository.findByNombre(nombreTipoSistema).orElse(null);
    }

}
