package mx.gob.pjpuebla.trials.util;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.time.LocalDateTime;

@Component
public class AuditListener {

    @Autowired
    private AuditorAware<Jwt> auditorAware;

    public AuditListener() {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
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

        Jwt jwt = auditorAware.getCurrentAuditor().orElseThrow();
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

        Jwt jwt = auditorAware.getCurrentAuditor().orElseThrow();
        audit.setUsuarioEdita(jwt.getSubject());
    }
}