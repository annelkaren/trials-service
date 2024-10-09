package mx.gob.pjpuebla.trials.workflow.carpeta;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_CARPETAS")
public class Carpeta implements Serializable, Auditable {

    @Id
    @SequenceGenerator(name = "idCarpeta", sequenceName = "SEQ_CARPETAS_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idCarpeta")
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Max(Integer.MAX_VALUE)
    @Version
    @Column(name = "N_VERSION")
    private Integer version;

    @Size(max = 15)
    @Column(name = "S_FOLIO", nullable = false)
    private String folio;

    @Size(max = 20)
    @Column(name = "S_EXPEDIENTE", nullable = false)
    private String expediente;

    @Enumerated
    @Column(name = "N_IMPRESION_SELLO", nullable = false)
    private SelloEstatus selloEstatus;

    @Enumerated
    @Column(name = "N_ESTADO", nullable = false)
    private EstadoCarpeta estatus;

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

    @Accessors(chain = false)
    @Embedded
    private Audit audit;
}
