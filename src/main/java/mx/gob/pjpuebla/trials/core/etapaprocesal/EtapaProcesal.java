package mx.gob.pjpuebla.trials.core.etapaprocesal;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.procedimientos.Procedimiento;
import mx.gob.pjpuebla.trials.core.rubros.Rubro;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import java.io.Serializable;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_ETAPA_PROCESAL")
public class EtapaProcesal implements Serializable, Auditable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idEtapaProcesal")
    @SequenceGenerator(name = "idEtapaProcesal", sequenceName = "SEQ_ETAPA_PROCESAL_ID")
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Version
    @Column(name = "N_VERSION")
    private Integer version;

    @NotBlank
    @Size(max = 250)
    @Column(name = "S_NOMBRE", nullable = false)
    private String nombre;

    @JoinColumn(name = "FN_TIPO_SISTEMA", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoSistema tipoSistema;

    @NotNull
    @JoinColumn(name = "FN_MATERIA", referencedColumnName = "PN_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Materia materia;

    @JoinColumn(name = "FN_TIPO_JUICIO", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoJuicio tipoJuicio;

    @JoinColumn(name = "FN_PROCEDIMIENTO", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Procedimiento procedimiento;

    @JoinColumn(name = "FN_RUBRO", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Rubro rubro;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;
}
