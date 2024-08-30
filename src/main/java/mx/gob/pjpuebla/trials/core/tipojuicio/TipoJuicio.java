package mx.gob.pjpuebla.trials.core.tipojuicio;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.Estado;

import java.io.Serializable;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_TIPO_JUICIO")
public class TipoJuicio implements Serializable, Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idTipoJuicio")
    @SequenceGenerator(name = "idTipoJuicio", sequenceName = "SEQ_TIPO_JUICIO_ID", allocationSize = 1)
    @Column(name = "PN_ID", insertable = false, updatable = false)
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

    @JoinColumn(name = "FN_TIPO_SISTEMA", referencedColumnName = "PN_ID", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private TipoSistema tipoSistema;

    @JoinColumn(name = "FN_MATERIA", referencedColumnName = "PN_ID", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Materia materia;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;

}
