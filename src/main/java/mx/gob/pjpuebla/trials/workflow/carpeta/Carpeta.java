package mx.gob.pjpuebla.trials.workflow.carpeta;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.core.conceptos.Concepto;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.rubros.Rubro;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipopieza.TipoPieza;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.enums.*;
import mx.gob.pjpuebla.trials.util.enums.carpeta.CatalogoDeterminacionJurisdiccional;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_CARPETAS")
public class Carpeta implements Serializable, Auditable {

    @Id
    @SequenceGenerator(name = "idCarpeta", sequenceName = "SEQ_CARPETAS_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idCarpeta")
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Max(Integer.MAX_VALUE)
    @Version
    @Column(name = "N_VERSION")
    private Integer version;

    @Size(max = 50)
    @Column(name = "S_FOLIO", nullable = false)
    private String folio;

    @Size(max = 30)
    @Column(name = "S_EXPEDIENTE", nullable = false)
    private String expediente;

    @Enumerated
    @Column(name = "N_IMPRESION_SELLO", nullable = false)
    private SelloEstatus selloEstatus;

    @Enumerated
    @Column(name = "N_ESTADO", nullable = false)
    private EstadoCarpeta estatus;

    @Column(name = "N_HORAS")
    private Integer horas;

    @Enumerated
    @Column(name = "N_PRIORIDAD")
    private Prioridad prioridad;

    @JoinColumn(name = "FN_CONCEPTO", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Concepto concepto;

    @NotNull
    @Enumerated
    @Column(name = "N_TIPO_CARPETA", nullable = false)
    private TipoCarpeta tipoCarpeta;

    @JoinColumn(name = "FN_JUZGADO", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Juzgado juzgado;

    @JoinColumn(name = "FN_TIPO_JUICIO", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoJuicio tipoJuicio;

    @JoinColumn(name = "FN_PERSONA", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Persona persona;

    @Column(name = "T_FECHA_ASIGNACION")
    private LocalDateTime fechaAsignacion;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "TBL_CARPETAS_RUBROS",
            joinColumns = @JoinColumn(name = "FN_CARPETA", referencedColumnName = "PN_ID"),
            inverseJoinColumns = @JoinColumn(name = "FN_RUBRO", referencedColumnName = "PN_ID")
    )
    private Set<Rubro> rubros;

    @Enumerated
    @Column(name = "S_DETERMINACION_JURISDICCIONAL")
    private CatalogoDeterminacionJurisdiccional determinacionJurisdiccional;

    @Enumerated
    @Column(name = "N_SENTENCIA", nullable = false)
    private TipoSentencia sentencia;

    @Column(name = "S_CU")
    private String cu;

    @Column(name = "N_MIGRADO", nullable = false)
    @Enumerated
    private Migrado migrado = Migrado.NO;

    @JoinColumn(name = "FN_CARPETA_PADRE", referencedColumnName= "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Carpeta carpetaPadre;

    @JoinColumn(name = "FN_TIPO_PIEZA", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoPieza tipoPieza;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;
}
