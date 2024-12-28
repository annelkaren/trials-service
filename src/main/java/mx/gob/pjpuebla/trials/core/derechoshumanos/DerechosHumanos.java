package mx.gob.pjpuebla.trials.core.derechoshumanos;


import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import mx.gob.pjpuebla.trials.util.enums.TipoDerechosHumanos;

import java.io.Serializable;

@Data
@Entity
@Table(name = "TBL_DERECHOS")
public class DerechosHumanos implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idDerechosHumanos")
    @SequenceGenerator(name = "idDerechosHumanos", sequenceName = "SEQ_DERECHOS_ID", allocationSize = 1)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;    @NotBlank

    @Size(min = 3, max = 250)
    @Column(name = "S_NOMBRE")
    private String nombre;

    @Max(Integer.MAX_VALUE)
    @Column(name = "N_DERECHOS")
    private TipoDerechosHumanos tipoDerecho;

}
