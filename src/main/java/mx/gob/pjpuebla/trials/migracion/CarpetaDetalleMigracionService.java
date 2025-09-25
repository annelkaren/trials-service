package mx.gob.pjpuebla.trials.migracion;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.carpetadetalle.CarpetaDetalle;
import mx.gob.pjpuebla.trials.workflow.carpeta.carpetadetalle.CarpetaDetalleRepository;

@Service
@RequiredArgsConstructor
public class CarpetaDetalleMigracionService {
    
    private final CarpetaDetalleRepository carpetaDetalleRepository;

    @Transactional
    public CarpetaDetalle createCarpetaDetalle(Carpeta c){
        return carpetaDetalleRepository.save(new CarpetaDetalle().setCarpeta(c));
    }

}
