package mx.gob.pjpuebla.trials.core.religiones;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import mx.gob.pjpuebla.trials.util.AuditListener;

import java.io.Serializable;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_RELIGIONES")
public class Religiones implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idReligiones")
    @SequenceGenerator(name = "idReligiones", sequenceName = "SEQ_RELIGIONES_ID")
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @NotBlank
    @Size(min = 3, max = 150)
    @Column(name = "S_NOMBRE", nullable = false)
    private String nombre;

}
