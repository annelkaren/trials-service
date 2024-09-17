package mx.gob.pjpuebla.trials.workflow.documentos;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.TipoDocumento;
import mx.gob.pjpuebla.trials.util.enums.EstadoDocumento;
import mx.gob.pjpuebla.trials.util.enums.Rol;

import java.io.Serializable;

@Entity
@EntityListeners(AuditListener.class)
@Data
@Table(name = "TBL_DOCUMENTOS")
public class Documento implements Serializable, Auditable {

    @Id
    @SequenceGenerator(name = "idDocumento", sequenceName = "SEQ_DOCUMENTOS_ID", allocationSize = 50)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idDocumento")
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Size(max = 15)
    @Column(name = "S_FOLIO", nullable = false)
    private String folio;

    @Size(max = 20)
    @Column(name = "S_EXPEDIENTE", nullable = false)
    private String expediente;

    @Size(max = 150)
    @Column(name = "S_RUTA")
    private String ruta;

    @Size(max = 30)
    @Column(name = "S_ESTATUS_PROCESAL", nullable = false)
    private String estatusProcesal;

    @Enumerated
    @Column(name = "N_TIPO_DOCUMENTO", nullable = false)
    private TipoDocumento tipoDocumento;

    @Enumerated
    @Column(name = "N_ESTADO_DOCUMENTO", nullable = false)
    private EstadoDocumento estatus;

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
