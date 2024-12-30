package mx.gob.pjpuebla.trials.core.derechoshumanos;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.util.enums.TipoDerechosHumanos;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class DerechosHumanosService {

    private final DerechosHumanosRepository derechosHumanosRepository;

    @Transactional(readOnly = true)
    public List<DerechosHumanosRecord> getListByTipo(String tipo) {
        TipoDerechosHumanos tipoDerecho = TipoDerechosHumanos.valueOf(tipo);

        List<DerechosHumanos> derechos = derechosHumanosRepository.findByTipoDerecho(tipoDerecho);

        return derechos.stream()
                .map(derechosHumanos -> new DerechosHumanosRecord(
                        derechosHumanos.getId(),
                        derechosHumanos.getNombre(),
                        derechosHumanos.getTipoDerecho()
                ))
                .toList();
    }


}
