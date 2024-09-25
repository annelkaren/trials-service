package mx.gob.pjpuebla.trials.workflow.documentos;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import org.hibernate.annotations.Type;

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

    @Max(Integer.MAX_VALUE)
    @Version
    @Column(name = "N_VERSION")
    private Integer version;

    @NotNull
    @Enumerated
    @Column(name = "N_TIPO_DOCUMENTO", nullable = false)
    private TipoDocumento tipoDocumento;

    @Column(name = "J_DATA", columnDefinition = "jsonb")
    private String data; //TODO. crear objeto para representar json

    @JoinColumn(name = "FN_DOCUMENTO", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Documento documento;

    @JoinColumn(name = "FN_CARPETA", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Carpeta carpeta;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;

}
