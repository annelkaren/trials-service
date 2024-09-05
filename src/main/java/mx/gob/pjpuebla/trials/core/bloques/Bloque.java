package mx.gob.pjpuebla.trials.core.bloques;

import java.io.Serializable;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.Estado;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_BLOQUE")
public class Bloque implements Serializable, Auditable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idBloque")
    @SequenceGenerator(name = "idBloque", sequenceName = "SEQ_BLOQUE_ID", allocationSize = 1)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @NotNull
    @Column(name = "T_HORA_INICIAL")
    private LocalTime HoraInicial;

    @NotNull
    @Column(name = "T_HORA_FINAL")
    private LocalTime HoraFinal;
    
    @Enumerated
    @Column(name = "N_ESTADO", nullable = false)
    private Estado estado;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;
}
