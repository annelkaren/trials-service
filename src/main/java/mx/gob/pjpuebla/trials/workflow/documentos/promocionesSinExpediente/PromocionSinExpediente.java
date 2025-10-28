package mx.gob.pjpuebla.trials.workflow.documentos.promocionesSinExpediente;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.enums.PromocionSinExpedienteEnum;
import mx.gob.pjpuebla.trials.util.enums.TipoPromocion;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;

@Entity
@EntityListeners(AuditListener.class)
@Data
@Table(name = "TBL_PROMOCIONES_PENDIENTES")
public class PromocionSinExpediente implements Serializable, Auditable {

    @Id
    @SequenceGenerator(name = "idPromocionSinExp", sequenceName = "SEQ_PROMOCION_PENDIENTE_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idPromocionSinExp")
    @Column(name = "PN_ID")
    private Integer id;

    @Max(Integer.MAX_VALUE)
    @Version
    @Column(name = "N_VERSION")
    private Integer version;

    @Size(max = 15)
    @Column(name = "S_FOLIO")
    private String folio;

    @Size(max = 50)
    @Column(name = "S_EXPEDIENTE")
    private String expediente;

    @JoinColumn(name = "FN_JUZGADO", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Juzgado juzgado;

    @JoinColumn(name = "FN_TIPO_JUICIO", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoJuicio tipoJuicio;

    @Column(name = "S_ANEXOS")
    private String anexos;

    @Column(name = "N_ESTATUS")
    @Enumerated
    private PromocionSinExpedienteEnum estado;

    @Column(name = "S_TIPO_REGISTRO")
    private String tipoRegistro;

    @Column(name = "N_TIPO_PROMOCION")
    private TipoPromocion tipoPromocion;

    @JoinColumn(name = "FN_CARPETA", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Carpeta carpeta;

    @JoinColumn(name = "FN_DOCUMENTO", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Documento documento;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;

}
