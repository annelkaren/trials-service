package mx.gob.pjpuebla.migracion.juicios;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JuiciosMigracionService {

    private final JuiciosMigracionRepository juiciosMigracionRepository;

        /**
     * Busca los datos del juicio asociado al ID recibido.
     *
     * @param idJuicio ID del juicio
     * @return Entidad `JuiciosMigracion` o null si no se encuentra
     */
    public JuiciosMigracion buscarJuicio(String idJuicio){
        Optional<JuiciosMigracion> juicios =  juiciosMigracionRepository.findById(idJuicio);
        if(juicios.isPresent()){
            return juicios.get();
        }
        return null;
    }
}
