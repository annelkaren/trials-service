package mx.gob.pjpuebla.trials.core.nacionalidades;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NacionalidadService {

    private final NacionalidadRepository nacionalidadRepository;

    @Transactional(readOnly = true)
    public List<NacionalidadRecord> getAll(String key) {
        key = (key != null) ? key.toLowerCase() : "";
        List<Nacionalidad> results = nacionalidadRepository.findAllFilterByName(key);

        return results.stream()
                .map(n -> new NacionalidadRecord(
                        n.getId(),
                        StringUtils.capitalize(n.getName().toLowerCase())
                )).toList();
    }
}
