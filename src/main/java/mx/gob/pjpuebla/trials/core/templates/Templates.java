package mx.gob.pjpuebla.trials.core.templates;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.enums.TipoTempletes;

import java.io.Serializable;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_TEMPLATES")
public class Templates implements Serializable, Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "templatesSeq")
    @SequenceGenerator(name = "templatesSeq", sequenceName = "SEQ_TEMPLATES_ID", allocationSize = 1)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @NotNull
    @Size(min = 3, max = 250)
    @Column(name = "S_NOMBRE")
    private String nombre;

    @NotNull
    @Column(name = "N_TIPO")
    private TipoTempletes tipo;

    @NotNull
    @Column(name = "S_CONTENIDO")
    private String contenido;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FN_JUZGADO", referencedColumnName = "PN_ID")
    private Juzgado juzgado;

    @JsonIgnore
    @Accessors(chain = false)
    @Embedded
    private Audit audit;
}
