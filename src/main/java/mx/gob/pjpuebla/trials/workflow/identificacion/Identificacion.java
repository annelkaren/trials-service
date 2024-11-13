package mx.gob.pjpuebla.trials.workflow.identificacion;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_DOCUMENTOS_IDENTIFICACION")
public class Identificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idIdentificacion")
    @SequenceGenerator(name = "idIdentificacion", sequenceName = "SEQ_DOCUMENTOS_IDENTIFICACION_ID")
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Version
    @Column(name = "N_VERSION")
    private Integer version;

    @NotNull
    @Size(min = 1, max = 250)
    @Column(name = "S_IDENTIFICACION")
    private String name;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;

}
