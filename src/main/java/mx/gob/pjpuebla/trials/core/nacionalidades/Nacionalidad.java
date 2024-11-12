package mx.gob.pjpuebla.trials.core.nacionalidades;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "TBL_NACIONALIDADES")
public class Nacionalidad implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idNacionalidad")
    @SequenceGenerator(name = "idNacionalidad", sequenceName = "SEQ_NACIONALIDADES_ID")
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Column(name = "S_CLAVE", nullable = false)
    private Integer key;

    @Column(name = "S_NOMBRE", nullable = false)
    private String name;

    @Column(name = "S_ABREVIATURA")
    private String abbreviation;
}
