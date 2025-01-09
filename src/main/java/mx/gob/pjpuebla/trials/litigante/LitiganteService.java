package mx.gob.pjpuebla.trials.litigante;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class LitiganteService {

    private final AuditorAware<Jwt> auditorAware;
    private final PersonaDocumentoRepository personaDocumentoRepository;

    public Page<LitiganteExpedientesRecord> getExpedientesRelacionados(Pageable pageable) {
        Page<PersonaDocumento> page = personaDocumentoRepository.findByUsername(getLitiganteUsername(), pageable);

        List<LitiganteExpedientesRecord> list = page.stream()
                .map(pd ->
                        new LitiganteExpedientesRecord(
                                pd.getCarpeta().getExpediente(),
                                pd.getCarpeta().getTipoJuicio().getMateria().getNombre(),
                                pd.getCarpeta().getTipoJuicio().getNombre(),
                               "",
                                "",
                                pd.getCarpeta().getJuzgado().getNombre(),
                                0
                        ))
                .toList();

        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    private String getLitiganteUsername() {
        Jwt jwt = auditorAware.getCurrentAuditor().orElseThrow();
        return jwt.getClaims().get("preferred_username").toString();
    }
}
