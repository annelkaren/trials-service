package mx.gob.pjpuebla.trials.core.tiposistema;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.enums.Estado;

import java.io.Serializable;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_TIPO_SISTEMA")
public class TipoSistema implements Serializable, Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idTipoSistema")
    @SequenceGenerator(name = "idTipoSistema", sequenceName = "SEQ_TIPO_SISTEMA_ID", allocationSize = 1)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Max(Integer.MAX_VALUE)
    @Version
    @Column(name = "N_VERSION")
    private Integer version;

    @NotBlank
    @Size(min = 3, max = 100)
    @Column(name = "S_NOMBRE")
    private String nombre;

    @Enumerated
    @Column(name = "N_ESTADO", nullable = false)
    private Estado estado;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;

}