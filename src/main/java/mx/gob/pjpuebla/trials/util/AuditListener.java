package mx.gob.pjpuebla.trials.util;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class AuditListener {

    private final AuditorAware<Jwt> auditorAware;

    @PrePersist
    public void setCreatedOn(Auditable auditable) {
        Audit audit = auditable.getAudit();
        if(audit == null) {
            audit = new Audit();
            auditable.setAudit(audit);
        }
        audit.setFechaAlta(LocalDateTime.now());
        audit.setFechaEdita(LocalDateTime.now());
        Jwt jwt = auditorAware.getCurrentAuditor().orElseThrow();
        audit.setUsuarioAlta(jwt.getSubject());
        audit.setUsuarioEdita(jwt.getSubject());
    }

    @PreUpdate
    public void setUpdatedOn(Auditable auditable) {
        Audit audit = auditable.getAudit();
        if(audit == null) {
            audit = new Audit();
            auditable.setAudit(audit);
        }
        audit.setFechaEdita(LocalDateTime.now());
        Jwt jwt = auditorAware.getCurrentAuditor().orElseThrow();
        audit.setUsuarioEdita(jwt.getSubject());
    }
}
