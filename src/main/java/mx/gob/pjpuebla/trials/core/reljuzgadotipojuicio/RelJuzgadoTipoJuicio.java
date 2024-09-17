package mx.gob.pjpuebla.trials.core.reljuzgadotipojuicio;

import jakarta.persistence.*;
import lombok.Data;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;

import java.io.Serializable;

@Entity
@Data
@Table(name = "TBL_JUZGADO_TIPOJUICIO")
public class RelJuzgadoTipoJuicio implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FN_JUZGADO", nullable = false)
    private Juzgado juzgado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FN_TIPOJUICIO", nullable = false)
    private TipoJuicio tipoJuicio;
}
