package mx.gob.pjpuebla.trials.workflow.carpeta;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;

import java.io.Serializable;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_CARPETAS")
public class Carpeta implements Serializable, Auditable {

    @Id
    @SequenceGenerator(name = "idCarpeta", sequenceName = "SEQ_CARPETAS_ID", allocationSize = 50)
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

    @Size(max = 50)
    @Column(name = "S_RUTA")
    private String ruta;

    @Enumerated
    @Column(name = "N_IMPRESION_SELLO", nullable = false)
    private SelloEstatus selloEstatus;

    @Enumerated
    @Column(name = "N_ESTADO", nullable = false)
    private EstadoCarpeta estatus;

    @JoinColumn(name = "FN_JUZGADO", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Juzgado juzgado;

    @JoinColumn(name = "FN_TIPO_JUICIO", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoJuicio tipoJuicio;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;
}
