package mx.gob.pjpuebla.trials.workflow.folios;

import jakarta.persistence.*;
import lombok.Data;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;

import java.io.Serializable;
import java.time.Year;

@Data
@Entity
@Table(name = "TBL_JUZGADO_FOLIOS")
public class JuzgadoFolios implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idJuzgadoFolios")
    @SequenceGenerator(name = "idJuzgadoFolios", sequenceName = "SEQ_JUZGADO_FOLIOS_ID", allocationSize = 1)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @JoinColumn(name = "FN_JUZGADO_ID", referencedColumnName = "PN_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Juzgado juzgado;

    @Enumerated
    @Column(name = "N_TIPO_CARPETA")
    private TipoCarpeta tipoCarpeta;

    @Column(name = "N_VALUE")
    private Integer value;

    @Column(name = "N_YEAR")
    private Integer year;

    @PrePersist
    public void prePersist() {
        if (value == null) {
            value = 1;
        }
        if (year == null) {
            year = Year.now().getValue();
        }
    }

}
