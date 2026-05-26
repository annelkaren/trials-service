package mx.gob.pjpuebla.trials.util;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDateTime;

public class AuditListener {

    @SuppressWarnings("unchecked")
    private AuditorAware<Jwt> getAuditorAware() {
        return ApplicationContextHolder.getContext().getBean(AuditorAware.class);
    }

    @PrePersist
    public void setCreatedOn(Auditable auditable) {
        Audit audit = auditable.getAudit();

        if (audit == null) {
            audit = new Audit();
            auditable.setAudit(audit);
        }

        LocalDateTime now = LocalDateTime.now();
        audit.setFechaAlta(now);
        audit.setFechaEdita(now);

        Jwt jwt = getAuditorAware().getCurrentAuditor().orElseThrow();
        audit.setUsuarioAlta(jwt.getSubject());
        audit.setUsuarioEdita(jwt.getSubject());
    }

    @PreUpdate
    public void setUpdatedOn(Auditable auditable) {
        Audit audit = auditable.getAudit();

        if (audit == null) {
            audit = new Audit();
            auditable.setAudit(audit);
        }

        audit.setFechaEdita(LocalDateTime.now());

        Jwt jwt = getAuditorAware().getCurrentAuditor().orElseThrow();
        audit.setUsuarioEdita(jwt.getSubject());
    }
}