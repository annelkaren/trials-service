package mx.gob.pjpuebla.trials.core.tipopartes;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.Estado;

import java.io.Serializable;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_TIPO_PARTES")
public class TipoPartes implements Serializable, Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idTipoParte")
    @SequenceGenerator(name = "idTipoParte", sequenceName = "SEQ_TIPO_PARTES_ID", allocationSize = 50)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Version
    @Column(name = "N_VERSION")
    private Integer version;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "S_NOMBRE")
    private String nombre;

    @Enumerated
    @Column(name = "N_ESTADO", nullable = false)
    private Estado estado;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "FN_MATERIA")
    private Materia materia;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;
}