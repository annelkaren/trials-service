package mx.gob.pjpuebla.trials.core.salaPersona;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
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
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.salas.Sala;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.enums.Estado;

@Entity
@Data
@EntityListeners(AuditListener.class)
@Table(name = "TBL_SALAS_PERSONAS", schema = "trials")
public class SalaPersona implements Serializable, Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sala_personas_seq_gen")
    @SequenceGenerator(name = "sala_personas_seq_gen", sequenceName = "trials.sala_personas_seq", allocationSize = 1)
    @Column(name = "PN_ID", nullable = false)
    private Integer id;

    @Max(Integer.MAX_VALUE)
    @Version
    @Column(name = "N_VERSION")
    private Integer version;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FN_PERSONA", referencedColumnName = "PN_ID", nullable = false)
    private Persona persona;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FN_SALA", referencedColumnName = "PN_ID", nullable = false)
    private Sala sala;

    @Column(name = "S_ROL", length = 50)
    private String rol;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", length = 20, nullable = false)
    private Estado estado;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;

}
