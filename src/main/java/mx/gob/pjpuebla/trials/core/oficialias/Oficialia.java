package mx.gob.pjpuebla.trials.core.oficialias;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialia;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.enums.Estado;

import java.io.Serializable;
import java.util.List;

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

    @Enumerated
    @Column(name = "N_ESTADO", nullable = false)
    private Estado estado;

    @JoinColumn(name = "FN_TIPO", referencedColumnName = "PN_ID", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private TipoOficialia tipoOficialia;

    @NotNull
    @Column(name = "S_NOMBRE", nullable = false)
    private String nombre;

    @NotNull
    @Column(name = "S_RESPONSABLE", nullable = false)
    private String responsable;

    @JoinColumn(name = "FN_SEDE", referencedColumnName = "PN_ID", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private Sede sede;

    @JoinColumn(name = "FN_JUZGADO", referencedColumnName = "PN_ID", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private Juzgado juzgado;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.DETACH}, fetch = FetchType.EAGER)
    @JoinTable(name = "TBL_OFICIALIAS_MATERIAS",
            joinColumns = {
                    @JoinColumn(name = "FN_OFICIALIA", referencedColumnName = "PN_ID")
            }, inverseJoinColumns = {
            @JoinColumn(name = "FN_MATERIA", referencedColumnName = "PN_ID")
    }, uniqueConstraints = @UniqueConstraint(columnNames = {
            "FN_OFICIALIA",
            "FN_MATERIA"
    }))
    @OrderBy("id")
    private List<Materia> materia;

}
