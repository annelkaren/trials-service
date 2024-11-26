package mx.gob.pjpuebla.trials.core.paises;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.util.*;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.Tipo;

import java.io.Serializable;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_PAISES")
public class Pais  implements Serializable {


    @Id
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @NotBlank
    @Size(min = 3, max = 2)
    @Column(name = "S_CCA2", nullable = false)
    private String cca2;

    @NotBlank
    @Size(min = 3, max = 250)
    @Column(name = "S_NOMBRE_OFICIAL", nullable = false)
    private String nombreOficial;


    @NotBlank
    @Size(min = 3, max = 250)
    @Column(name = "S_NOMBRE_COMUN", nullable = false)
    private String  nombreComun;
}
