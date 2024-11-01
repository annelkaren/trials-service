package mx.gob.pjpuebla.trials.core.rubros;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.core.procedimientos.Procedimiento;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.enums.Estado;

import java.io.Serializable;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_RUBROS", schema = "TRIALS")
public class Rubro implements Serializable, Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idRubro")
    @SequenceGenerator(name = "idRubro", sequenceName = "SEQ_RUBROS_ID", allocationSize = 1)
    @Column(name = "PN_ID", nullable = false)
    private Integer id;

    @Max(Integer.MAX_VALUE)
    @Version
    @Column(name = "N_VERSION")
    private Integer version;

    @NotBlank
    @Size(min = 3, max = 250)
    @Column(name = "S_NOMBRE", nullable = false)
    private String nombre;

    @Enumerated
    @Column(name = "N_ESTADO", nullable = false)
    private Estado estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FN_PROCEDIMIENTO", referencedColumnName = "PN_ID", foreignKey = @ForeignKey(name = "fk_procedimiento"))
    private Procedimiento procedimiento;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;
}
