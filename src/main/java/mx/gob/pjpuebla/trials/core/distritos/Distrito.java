package mx.gob.pjpuebla.trials.core.distritos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.enums.Estado;

import java.io.Serializable;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_DISTRITOS")
public class Distrito implements Serializable, Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idDistrito")
    @SequenceGenerator(name = "idDistrito", sequenceName = "SEQ_DISTRITOS_ID", allocationSize = 50)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Version
    @Column(name = "N_VERSION")
    private Integer version;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "S_NOMBRE")
    private String nombre;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "S_REGION")
    private String region;

    @Enumerated
    @Column(name = "N_ESTADO", nullable = false)
    private Estado estado;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;
}