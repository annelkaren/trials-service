package mx.gob.pjpuebla.trials.core.templates.placeholders;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;

import java.io.Serializable;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_PLACEHOLDERS")
public class Placeholders implements Serializable, Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "placeholdersSeq")
    @SequenceGenerator(name = "placeholdersSeq", sequenceName = "SEQ_PLACEHOLDERS_ID", allocationSize = 1)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @NotNull
    @Column(name = "S_NOMBRE")
    private String nombre;

    @NotNull
    @Column(name = "S_VALOR")
    private String contenido;

    @JsonIgnore
    @Accessors(chain = false)
    @Embedded
    private Audit audit;
}
