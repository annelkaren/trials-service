package mx.gob.pjpuebla.trials.core.juzgados;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.enums.Estado;

import java.io.Serializable;
import java.util.List;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_JUZGADOS")
public class Juzgado implements Serializable, Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idJuzgado")
    @SequenceGenerator(name = "idJuzgado", sequenceName = "SEQ_JUZGADOS_ID")
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Max(Integer.MAX_VALUE)
    @Version
    @Column(name = "N_VERSION")
    private Integer version;

    @NotBlank
    @Size(min = 3, max = 250)
    @Column(name = "S_NOMBRE")
    private String nombre;

    @Enumerated
    @Column(name = "N_ESTADO", nullable = false)
    private Estado estado;

    @Column(name = "N_MAX_ASIGNACIONES")
    private Integer maxAsignacionesRonda;

    @JsonIgnore
    @Column(name = "N_CONTADOR_ASIGNACIONes")
    private Integer contadorAsignaciones;

    @JoinColumn(name = "FN_MATERIA", referencedColumnName = "PN_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Materia materia;

    @JoinColumn(name = "FN_SEDE", referencedColumnName = "PN_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Sede sede;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;

    @JsonProperty
    public Integer getContadorAsignaciones() {
        return this.contadorAsignaciones;
    }

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.DETACH}, fetch = FetchType.EAGER)
    @JoinTable(name = "tbl_juzgado_tipojuicio",
            joinColumns = {
                    @JoinColumn(name = "fn_juzgado", referencedColumnName = "pn_id")
            }, inverseJoinColumns = {
            @JoinColumn(name = "fn_tipojuicio", referencedColumnName = "pn_id")
    }, uniqueConstraints = @UniqueConstraint(columnNames = {
            "fn_juzgado",
            "fn_tipojuicio"
    }))
    @OrderBy("id")
    private List<TipoJuicio> tipoJuicios;
}

