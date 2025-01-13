package mx.gob.pjpuebla.trials.core.juzgados;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.InstanciaJuzgado;
import mx.gob.pjpuebla.trials.workflow.folios.JuzgadoFolios;

import java.io.Serializable;
import java.util.List;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_JUZGADOS")
public class Juzgado implements Serializable, Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idJuzgado")
    @SequenceGenerator(name = "idJuzgado", sequenceName = "SEQ_JUZGADOS_ID", allocationSize = 1)
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

    @NotNull
    @Enumerated
    @Column(name = "N_ESTADO", nullable = false)
    private Estado estado;

    @NotNull
    @Enumerated
    @Column(name = "N_INSTANCIA", nullable = false)
    private InstanciaJuzgado instanciaJuzgado;

    @PositiveOrZero
    @Max(Integer.MAX_VALUE)
    @Column(name = "N_MAX_ASIGNACIONES")
    private Integer maxAsignacionesRonda;

    @PositiveOrZero
    @JsonIgnore
    @Column(name = "N_CONTADOR_ASIGNACIONES")
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

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.DETACH}, fetch = FetchType.LAZY)
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
    @NotNull
    @Size(min = 1, max = 50)
    private List<TipoJuicio> tipoJuicios;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "juzgado", fetch = FetchType.LAZY)
    private List<JuzgadoFolios> juzgadoFolios;

    @Size(max = 50)
    @Column(name = "S_NOMENCLATURA")
    private String nomenclatura;
}

