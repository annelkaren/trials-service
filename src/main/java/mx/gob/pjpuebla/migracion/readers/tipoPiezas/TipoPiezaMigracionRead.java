package mx.gob.pjpuebla.migracion.readers.tipoPiezas;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.error.NotFoundException;

@Service
@RequiredArgsConstructor
public class TipoPiezaMigracionRead {

    private final TipoPiezaMigracionRepository tipoPiezaMigracionRepository;

    public TipoPiezaMigracion findByClave(String clave){
        Optional<TipoPiezaMigracion> tipoPiezaMigracion = tipoPiezaMigracionRepository.findByClave(clave);

        if(tipoPiezaMigracion.isPresent()){
            return tipoPiezaMigracion.get();
        }else{
            throw new NotFoundException("No fue posible determinar el tipo de pieza", clave);
        }
    }
}
