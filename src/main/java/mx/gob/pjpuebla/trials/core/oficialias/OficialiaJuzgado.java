package mx.gob.pjpuebla.trials.core.oficialias;

import jakarta.persistence.*;
import lombok.Data;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;

import java.io.Serializable;

@Data
@Entity
@Table(name = "TBL_OFICIALIAS_JUZGADOS")
public class OficialiaJuzgado implements Serializable {

    @Id
    @Column(name = "FN_OFICIALIA")
    private Integer oficialiaId;

    @Id
    @Column(name = "FN_JUZGADO")
    private Integer juzgadoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FN_OFICIALIA_ID", referencedColumnName = "PN_ID", insertable = false, updatable = false)
    private Oficialia oficialia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FN_JUZGADO_ID", referencedColumnName = "PN_ID", insertable = false, updatable = false)
    private Juzgado juzgado;
}
