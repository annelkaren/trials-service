package mx.gob.pjpuebla.trials.core.bloques;

import java.io.Serializable;
import java.time.LocalTime;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.enums.Estado;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_BLOQUES")
public class Bloque implements Serializable, Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idBloque")
    @SequenceGenerator(name = "idBloque", sequenceName = "SEQ_BLOQUE_ID", allocationSize = 1)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @NotNull
    @Column(name = "T_HORA_INICIAL")
    private LocalTime horaInicial;

    @NotNull
    @Column(name = "T_HORA_FINAL")
    private LocalTime horaFinal;

    @Enumerated
    @Column(name = "N_ESTADO", nullable = false)
    private Estado estado;
    
    @Type(JsonBinaryType.class)
    @Column(name = "J_DATA")
    private BloqueData data;

    @JsonIgnore
    @Accessors(chain = false)
    @Embedded
    private Audit audit;
}
