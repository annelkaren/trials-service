package mx.gob.pjpuebla.trials.core.religiones;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class ReligionesService {

    private final ReligionesRepository religionesRepository;

    @Transactional(readOnly = true)
    public List<ReligionesRecord> findAllByReligionesAutocomplete(String key) {
        return religionesRepository.findAllByidReligion(key);
    }
}
