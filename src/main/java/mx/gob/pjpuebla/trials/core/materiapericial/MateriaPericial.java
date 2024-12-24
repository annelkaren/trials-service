package mx.gob.pjpuebla.trials.core.materiapericial;


import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "TBL_MATERIA_PERICIAL")
public class MateriaPericial implements Serializable {

    @Id
    @SequenceGenerator(name = "idMateriaPericial", sequenceName = "SEQ_MATERIA_PERICIAL_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idMateriaPericial")
    @Column(name = "PN_ID")
    private Integer id;

    @Column(name = "S_NOMBRE")
    private String nombre;
}
