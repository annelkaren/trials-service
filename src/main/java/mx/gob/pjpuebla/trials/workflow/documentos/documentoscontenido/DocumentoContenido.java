package mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido;
import jakarta.persistence.*;
import lombok.Data;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import java.io.Serializable;
@Entity
@Data
@Table(name = "TBL_DOCUMENTO_CONTENIDO")
public class DocumentoContenido implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idDocContenido")
    @SequenceGenerator(name = "idDocContenido", sequenceName = "SEQ_DOC_CONTENIDO_ID",  allocationSize = 1)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Column(name = "S_TEXTO")
    private String texto;

    @Column(name="C_TAMANIOPAPEL")
    private Character tamanioPapel;

    @Column(name = "C_OFICIOPUBLICADO")
    private Character oficioPublicado;

    @JoinColumn(name = "FN_DOCUMENTO", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Documento documento;
}