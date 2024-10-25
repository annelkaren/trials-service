package mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;

import java.io.Serializable;

@Entity
@Data
@Table(name = "TBL_DOCUMENTO_DETALLE")
public class DocumentoDetalle implements Serializable {
    @Id
    @SequenceGenerator(name = "idDocDetalle", sequenceName = "SEQ_DOC_DETALLE_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idDocDetalle")
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Size(max = 50)
    @Column(name = "S_RUTA")
    private String ruta;

    @JoinColumn(name = "FN_DOCUMENTO", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Documento documento;
}
