package mx.gob.pjpuebla.trials.core.tipopieza;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;

import java.io.Serializable;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_TIPO_PIEZAS")
public class TipoPieza implements Serializable, Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idTipoPieza")
    @SequenceGenerator(name = "idTipoPieza", sequenceName = "SEQ_TIPO_PIEZA_ID")
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Version
    @Column(name = "N_VERSION")
    private Integer version;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "S_TIPO")
    private String tipo;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "S_CLAVE")
    private String clave;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;
}
