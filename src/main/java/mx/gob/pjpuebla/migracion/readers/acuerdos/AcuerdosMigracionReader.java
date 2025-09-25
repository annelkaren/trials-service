package mx.gob.pjpuebla.migracion.readers.acuerdos;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AcuerdosMigracionReader {
    
    private final AcuerdosMigracionRepository acuerdosMigracionRepository;
    
    /**
     * Busca todos los acuerdos migrados asociados a una clave única de entrada
     * (CU).
     *
     * @param cuEntradas CU relacionado con la entrada del expediente
     * @return Lista de acuerdos relacionados con el CU; vacía si no se encuentran
     */
    public List<AcuerdosMigracion> buscarAcuerdosPorCu(String cuEntradas){
        return acuerdosMigracionRepository.findByCuEntradasAndEstatus(cuEntradas,"A");
    }

    /**
     * Busca todas las sentencias migradas asociados a una clave única de entrada
     * (CU).
     *
     * @param cuEntradas CU relacionado con la entrada del expediente
     * @return Lista de acuerdos relacionados con el CU; vacía si no se encuentran
     */
    public List<AcuerdosMigracion> buscarSentenciasPorCu(String cuEntradas){
        return acuerdosMigracionRepository.findByCuEntradasAndSentenciaInAndEstatus(cuEntradas, List.of("A","C","D","I"), "A");
    }
    

}
