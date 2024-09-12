package mx.gob.pjpuebla.trials.core.oficialias;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialia;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.enums.Estado;

import java.io.Serializable;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_OFICIALIAS")
public class Oficialia implements Serializable, Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idOficialia")
    @SequenceGenerator(name = "idOficialia", sequenceName = "SEQ_OFICIALIAS_ID", allocationSize = 50)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Max(Integer.MAX_VALUE)
    @Version
    @Column(name = "N_VERSION")
    private Integer version;

    @Enumerated
    @Column(name = "N_ESTADO", nullable = false)
    private Estado estado;

    @JoinColumn(name = "FN_TIPO", referencedColumnName = "PN_ID", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private TipoOficialia tipo;

    @NotNull
    @Column(name = "S_NOMBRE", nullable = false)
    private String nombre;

    @NotNull
    @Column(name = "S_RESPONSABLE", nullable = false)
    private String responsable;

    @JoinColumn(name = "FN_SEDE", referencedColumnName = "PN_ID", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private Sede sede;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;

}
