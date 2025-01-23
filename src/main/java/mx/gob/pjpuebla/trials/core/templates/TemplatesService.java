package mx.gob.pjpuebla.trials.core.templates;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.templates.placeholders.Placeholders;
import mx.gob.pjpuebla.trials.core.templates.placeholders.PlaceholdersService;
import mx.gob.pjpuebla.trials.error.InvalidVersionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class TemplatesService {

    private final TemplatesRepository templatesRepository;
    private final PersonaService personaService;
    private final PlaceholdersService placeholdersService;

    public List<Templates> getTempletesByJuzgado(){
        return this.templatesRepository.findByJuzgadoId(personaService.getAuditor().getJuzgado().getId());
    }

    public  List<TempletesPlaceholderRecord> getTempletesAndPlaceholder(){
        List<Templates> templates = getTempletesByJuzgado();
        List<Placeholders> placeholders = placeholdersService.getPlaceholderList();

        List<TempletesPlaceholderRecord> records = new ArrayList<>();

        for (Templates template : templates) {
            records.add(new TempletesPlaceholderRecord(
                    template.getNombre(),
                    template.getContenido(),
                    true
            ));
        }

        for (Placeholders placeholder : placeholders) {
            records.add(new TempletesPlaceholderRecord(
                    placeholder.getNombre(),
                     placeholder.getContenido(),
                    false
            ));
        }

        return records;
    }

    public TempleteRecordResponse create(Templates templates) {
        templates.setJuzgado(personaService.getAuditor().getJuzgado());
        templates = templatesRepository.save(templates);
        return new TempleteRecordResponse(templates.getNombre());
    }

    public TempleteRecordResponse update(Templates templates) {
        try {
            templatesRepository.save(templates);
            return new TempleteRecordResponse(templates.getNombre());
        } catch (org.springframework.dao.OptimisticLockingFailureException ex) {
            throw new InvalidVersionException(Templates.class.getSimpleName());
        }
    }

    public void delete(Integer id) {
        templatesRepository.deleteById(id);
    }


}
