package mx.gob.pjpuebla.trials.workflow.folios;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecuenciaService {

    private SecuenciaRepository secuenciaRepository;

    // tipo: tiene que ser E-exhorto, D-demanda, P-promocion.
    public String obtenerFolio(String tipo) {
        Long valNum;
        switch (tipo) {
            case "E":           // Case para exhorto
                valNum = secuenciaRepository.getNextValExhorto();
                break;
            case "D":           // Case para demanda
                valNum = secuenciaRepository.getNextValDemanda();
                break;
            case "P":           // Case para promocion
                valNum = secuenciaRepository.getNextValPromocion();
                break;
            default:
                throw new IllegalArgumentException("Tipo de folio no válido: " + tipo);
        }
        return tipo + valNum;
    }

}
