package mx.gob.pjpuebla.trials.workflow.asistenciaaudiencia;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.core.documentoidentificacion.DocumentoIdentificacion;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.enums.Asistencia;
import mx.gob.pjpuebla.trials.workflow.audiencias.Audiencia;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;

import java.io.Serializable;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_ASISTENCIA_AUDIENCIA")
public class AsistenciaAudiencia implements Serializable, Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idAsistenciaAudiencia")
    @SequenceGenerator(name = "idAsistenciaAudiencia", sequenceName = "SEQ_ASISTENCIAAUDIENCIA_ID", allocationSize = 1)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @JoinColumn(name = "FN_PERSONA_DOCUMENTO", referencedColumnName = "PN_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private PersonaDocumento personaDocumento;

    @JoinColumn(name = "FN_AUDIENCIA", referencedColumnName = "PN_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Audiencia audiencia;

    @NotNull
    @Enumerated
    @Column(name = "N_ASISTENCIA")
    private Asistencia asistencia;

    @JoinColumn(name = "FN_DOCUMENTO_IDENTIFICACION", referencedColumnName = "PN_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private DocumentoIdentificacion documentoIdentificacion;

    @Column(name = "S_URL_DOCUMENTO")
    private String urlDocumento;

    @JsonIgnore
    @Accessors(chain = false)
    @Embedded
    private Audit audit;
}
