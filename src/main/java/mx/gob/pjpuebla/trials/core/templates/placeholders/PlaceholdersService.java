package mx.gob.pjpuebla.trials.core.templates.placeholders;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class PlaceholdersService {

    private final  PlaceholdersRepository placeholdersRepository;

    public List<Placeholders> getPlaceholderList(){
        return placeholdersRepository.findAll();
    }
}
