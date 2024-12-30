package mx.gob.pjpuebla.trials.core.cuestionarios;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.util.enums.ListCuestionario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CuestionarioService {

    private final CuestionarioRepository cuestionarioRepository;
    @Transactional(readOnly = true)
    public List<CuestionarioRecord> getListByLista(Integer lista) {
        if (lista == null || lista < 0 || lista >= ListCuestionario.values().length) {
            log.error("Índice fuera de rango: {}", lista);
            throw new IllegalArgumentException("Índice fuera de rango: " + lista);
        }

        ListCuestionario cuestionarioLista = ListCuestionario.values()[lista];
        List<Cuestionario> cuestionarios = cuestionarioRepository.findByLista(cuestionarioLista);

        assert cuestionarios != null;
        return cuestionarios.stream()
                .map(cuestionario -> new CuestionarioRecord(
                        cuestionario.getId(),
                        cuestionario.getPreguntas(),
                        cuestionario.getLista(),
                        cuestionario.getTipo()
                ))
                .toList();
    }

}
