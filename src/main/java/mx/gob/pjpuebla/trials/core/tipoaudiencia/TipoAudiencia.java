package mx.gob.pjpuebla.trials.core.tipoaudiencia;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;

import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.Auditable;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_TIPO_AUDIENCIA")
public class TipoAudiencia implements  Serializable, Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idTipoAudiencia")
    @SequenceGenerator(name = "idTipoAudiencia", sequenceName = "SEQ_TIPO_AUDIENCIA_ID", allocationSize = 1)
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

    @JoinColumn(name = "FN_MATERIA", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Materia materia;

    @JoinColumn(name = "FN_TIPO_SISTEMA", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoSistema tipoSistema;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;
}
