package mx.gob.pjpuebla.trials.core.oficialiamateria;


import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.oficialias.Oficialia;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_OFICIALIAS_MATERIAS")
public class OficialiaMateria {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idOficialiaMateria")
    @SequenceGenerator(name = "idOficialiaMateria", sequenceName = "SEQ_OFICIALIAS_MATERIAS_ID", allocationSize = 50)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @JoinColumn(name = "FN_OFICIALIA", referencedColumnName = "PN_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Oficialia oficialia;

    @Column(name = "S_OFICIALIA", nullable = false)
    private String nombre;

    @JoinColumn(name = "FN_MATERIA", referencedColumnName = "PN_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Materia materia;

    @Column(name = "S_MATERIA", nullable = false)
    private String nombreMateria;

    @Column(name = "S_TIPO_OFICIALIA", nullable = false)
    private String tipo;

    @Column(name = "S_JUZGADO", nullable = false)
    private String nombreJuzgado;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;

}
