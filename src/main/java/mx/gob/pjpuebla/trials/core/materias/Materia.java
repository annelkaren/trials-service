package mx.gob.pjpuebla.trials.core.materias;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;

import java.io.Serializable;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_MATERIAS")
public class Materia implements Serializable, Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idMateria")
    @SequenceGenerator(name = "idMateria", sequenceName = "SEQ_MATERIAS_ID", allocationSize = 50)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Max(Integer.MAX_VALUE)
    @Version
    @Column(name = "N_VERSION")
    private Integer version;

    @Pattern(regexp = "A|I|D")
    @Column(name = "S_ESTADO", nullable = false)
    private String estado;

    @Embedded
    private Audit audit;

    @Column(name = "S_NOMBRE", nullable = false)
    private String nombre;


}
