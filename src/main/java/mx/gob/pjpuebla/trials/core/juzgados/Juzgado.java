package mx.gob.pjpuebla.trials.core.juzgados;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;

import java.io.Serializable;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_JUZGADOS")
public class Juzgado implements Serializable, Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idJuzgado")
    @SequenceGenerator(name = "idJuzgado", sequenceName = "SEQ_JUZGADOS_ID", allocationSize = 50)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Max(Integer.MAX_VALUE)
    @Version
    @Column(name = "N_VERSION")
    private Integer version;

    @Embedded
    private Audit audit;

    @NotBlank
    @Size(min = 3, max = 250)
    @Column(name = "S_NOMBRE")
    private String nombre;

    @Pattern(regexp = "A|I|D")
    @Column(name = "S_ESTADO", nullable = false)
    private String estado;

    @Column(name = "FN_MATERIA")
    private Integer materia;

    @Column(name = "FN_DOMICILIO")
    private Long domicilio;

    @Column(name = "FN_DISTRITO")
    private Integer distrito;

}

