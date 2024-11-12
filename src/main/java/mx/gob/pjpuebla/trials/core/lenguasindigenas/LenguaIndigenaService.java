package mx.gob.pjpuebla.trials.core.lenguasindigenas;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LenguaIndigenaService {

    private final LenguaIndigenaRepository lenguaIndigenaRepository;

    @Transactional(readOnly = true)
    public List<LenguaIndigenaRecord> getAll(String key) {
        key = (key != null) ? key.toLowerCase() : "";
        List<LenguaIndigena> results = lenguaIndigenaRepository.findAllFilterByName(key);

        return results.stream()
                .map(n -> new LenguaIndigenaRecord(
                        n.getId(),
                        StringUtils.capitalize(n.getName().toLowerCase())
                )).toList();
    }
}
