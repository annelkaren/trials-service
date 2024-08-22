package mx.gob.pjpuebla.trials.core.tipopartes;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;

import java.io.Serializable;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_TIPO_PARTES")
public class TipoPartes implements Serializable, Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idTipoParte")
    @SequenceGenerator(name = "idTipoParte", sequenceName = "SEQ_TIPO_PARTES_ID", allocationSize = 50)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Version
    @Column(name = "N_VERSION")
    private Integer version;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "S_NOMBRE")
    private String nombre;

    @Pattern(regexp = "[AID]")
    @Column(name = "S_ESTADO", nullable = false)
    private String estado;

    @NotNull
    @Column(name = "FN_MATERIA")
    private Integer materia;

    @Embedded
    private Audit audit;
}
