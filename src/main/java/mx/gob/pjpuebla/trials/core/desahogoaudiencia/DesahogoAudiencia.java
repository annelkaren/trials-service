package mx.gob.pjpuebla.trials.core.desahogoaudiencia;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import mx.gob.pjpuebla.trials.util.AuditListener;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_DESAHOGO_AUDIENCIA")
public class DesahogoAudiencia {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idDesahogoAudiencia")
    @SequenceGenerator(name = "idDesahogoAudiencia", sequenceName = "SEQ_DESAHOGO_AUD_ID", allocationSize = 1)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @NotBlank
    @Size(min = 3, max = 10)
    @Column(name = "S_KEY", nullable = false)
    private String key;

    @NotBlank
    @Size(min = 3, max = 250)
    @Column(name = "S_NOMBRE", nullable = false)
    private String nombre;

}
