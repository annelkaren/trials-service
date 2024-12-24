package mx.gob.pjpuebla.trials.workflow.anexos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.enums.EstadoAnexo;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;

import java.io.Serializable;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_ANEXOS")
public class Anexo implements Serializable, Auditable {

    @Id
    @SequenceGenerator(name = "idAnexo", sequenceName = "SEQ_ANEXOS_ID",  allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idAnexo")
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @NotBlank
    @Size(min = 3, max = 250)
    @Column(name = "S_NOMBRE", nullable = false)
    private String nombre;

    @JoinColumn(name = "FN_DOCUMENTO", referencedColumnName = "PN_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Documento documento;

    @Enumerated
    @Column(name = "N_ESTADO")
    private EstadoAnexo estado;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;
}

