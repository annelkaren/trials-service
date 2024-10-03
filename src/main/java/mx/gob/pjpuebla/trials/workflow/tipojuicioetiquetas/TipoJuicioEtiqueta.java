package mx.gob.pjpuebla.trials.workflow.tipojuicioetiquetas;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;

import java.io.Serializable;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditListener.class)
@Table(name = "TBL_TIPOJUICIO_ETIQUETAS")
public class TipoJuicioEtiqueta implements Serializable, Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idTipojuicioEtiquetas")
    @SequenceGenerator(name = "idTipojuicioEtiquetas", sequenceName = "SEQ_TIPOJUICIO_ETIQUETAS_ID")
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @NotBlank
    @Size(min = 3, max = 100)
    @Column(name = "S_NOMBRE")
    private String nombre;

    @NotBlank
    @Size(min = 3, max = 100)
    @Column(name = "S_VALUE")
    private String value;

    @JoinColumn(name = "FN_TIPO_JUICIO", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoJuicio tipoJuicio;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;
}
