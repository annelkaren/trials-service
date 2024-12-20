package mx.gob.pjpuebla.trials.core.materialpericial;


import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "TBL_MATERIAL_PERICIAL")
public class MaterialPericial implements Serializable {

    @Id
    @SequenceGenerator(name = "idMaterialPericial", sequenceName = "SEQ_MATERIAL_PERICIAL_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idMaterialPericial")
    @Column(name = "PN_ID")
    private Integer id;

    @Column(name = "S_NOMBRE")
    private String nombre;
}
