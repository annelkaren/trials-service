package mx.gob.pjpuebla.trials.core.juzgados;

import jakarta.persistence.*;
import lombok.Data;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FN_TIPOJUICIO", referencedColumnName = "PN_ID", nullable = false, insertable = false, updatable = false)
    private TipoJuicio tipoJuicio;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FN_JUZGADO", referencedColumnName = "PN_ID", nullable = false, insertable = false, updatable = false)
    private Juzgado juzgado;

}
