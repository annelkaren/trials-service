package mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;

import java.io.Serializable;

@Entity
@Data
@Table(name = "TBL_DOCUMENTO_CONTENIDO")
public class DocumentoContenido implements Serializable {
    @Id
    @SequenceGenerator(name = "idDocContenido", sequenceName = "SEQ_DOC_CONTENIDO_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idDocContenido")
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Size(max = 50)
    @Column(name = "S_TEXTO")
    private String texto;

    @JoinColumn(name = "FN_DOCUMENTO", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Documento documento;

}
