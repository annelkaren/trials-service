package mx.gob.pjpuebla.trials.workflow.documentos.anexosoficios;

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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;

import java.io.Serializable;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_ANEXOS_OFICIOS")
public class AnexoOficio implements Serializable, Auditable {

    @Id
    @SequenceGenerator(name = "idAnexoOficio", sequenceName = "SEQ_ANEXOS_OFICIOS_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idAnexoOficio")
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @NotBlank
    @Size(min = 3, max = 250)
    @Column(name = "S_NOMBRE_ARCHIVO", nullable = false)
    private String nombreArchivo;

    @JoinColumn(name = "FN_DOCUMENTO", referencedColumnName = "PN_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Documento documento;

    @Enumerated
    @Column(name = "N_ESTADO", nullable = false)
    private Estado estado;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;
}
