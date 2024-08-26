package mx.gob.pjpuebla.trials.core.oficialias;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;

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

    @Pattern(regexp = "[AID]")
    @Column(name = "S_ESTADO", nullable = false)
    private String estado;

    @Column(name = "S_TIPO", nullable = false)
    private String tipo;

    @NotNull
    @Column(name = "S_NOMBRE", nullable = false)
    private String nombre;

    @NotNull
    @Column(name = "FN_DOMICILIO", nullable = false)
    private Integer domicilio;

    @NotNull
    @Column(name = "S_RESPONSABLE", nullable = false)
    private String responsable;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;

}
