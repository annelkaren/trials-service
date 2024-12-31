package mx.gob.pjpuebla.trials.workflow.comentariosasistentes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;

import java.io.Serializable;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_COMENTARIOS_ASISTENTES")
public class ComentariosAsistentes implements Serializable, Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idComentariosAsistentes")
    @SequenceGenerator(name = "idComentariosAsistentes", sequenceName = "SEQ_COMENTARIOSASISTENTES_ID", allocationSize = 1)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @JsonIgnore
    @JoinColumn(name = "FN_PERSONA_DOCUMENTO", referencedColumnName = "PN_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private PersonaDocumento personaDocumento;

    @Size(max = 500)
    @Column(name = "S_COMENTARIO")
    private String comentario;

    @JsonIgnore
    @Accessors(chain = false)
    @Embedded
    private Audit audit;
}
