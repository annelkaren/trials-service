package mx.gob.pjpuebla.trials.core.documentos;

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

import java.io.Serializable;

@Entity
@EntityListeners(AuditListener.class)
@Data
@Table(name = "TBL_DOCUMENTOS")
public class Documento implements Serializable, Auditable {

    @Id
    @SequenceGenerator(name = "idDocumento", sequenceName = "SEQ_DOCUMENTOS_ID", allocationSize =  50)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idDocumento")
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Size(max = 15)
    @Column(name = "S_FOLIO", nullable = false)
    private String folio;

    @Size(max = 50)
    @Column(name = "S_RUTA", nullable = false)
    private String ruta;

//  LABORAL = primera face procesal
//  Recepción demanda = tradicional, civil, mercantil
//  Recepción documentos = Juicios orales
    @Size(max = 30)
    @Column(name = "S_ESTATUS_PROCESAL", nullable = false)
    private String status;

    @Enumerated
    @Column(name = "N_TIPO_DOCUMENTO", nullable = false)
    private TipoDocumento tipoDocumento;

    @JoinColumn(name = "FN_JUZGADO", referencedColumnName = "PN_ID", updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Juzgado juzgado;

    @JoinColumn(name = "FN_TIPO_JUICIO", referencedColumnName = "PN_ID", updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
//    tipo del juicio el que listo de mis materias
    private TipoJuicio tipoJuicio;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;


}
