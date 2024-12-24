package mx.gob.pjpuebla.trials.core.lenguasindigenas;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "TBL_LENGUAS_INDIGENAS")
public class LenguaIndigena implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idLenguasIndigenas")
    @SequenceGenerator(name = "idLenguasIndigenas", sequenceName = "SEQ_LENGUAS_INDIGENAS_ID", allocationSize = 1)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Column(name = "S_CLAVE", nullable = false)
    private Integer key;

    @Column(name = "S_NOMBRE", nullable = false)
    private String name;
}
