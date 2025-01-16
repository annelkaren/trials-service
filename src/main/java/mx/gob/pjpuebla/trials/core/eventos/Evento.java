package mx.gob.pjpuebla.trials.core.eventos;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.oficialias.Oficialia;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.enums.Estado;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_EVENTOS")
public class Evento implements  Serializable, Auditable{

    @Id
    @SequenceGenerator(name = "idEvento", sequenceName = "SEQ_EVENTOS_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idEvento")
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Version
    @Column(name = "N_VERSION")
    private Integer version;

    @Column(name = "S_DESCRIPCION")
    private String descripcion;

    @NotNull
    @Column(name = "T_DIA_INICIO")
    private LocalDate diaInicio;

    @NotNull
    @Column(name = "T_DIA_FIN")
    private LocalDate diaFin;

    @JsonIgnore
    @JoinColumn(name = "FN_OFICIALIA", referencedColumnName = "PN_ID")
    @OneToOne
    private Oficialia oficialia;

    @JsonIgnore
    @JoinColumn(name = "FN_JUZGADO", referencedColumnName = "PN_ID")
    @OneToOne
    private Juzgado juzgado;

    @Enumerated
    @Column(name = "n_estado")
    private Estado estado;

    @JsonIgnore
    @Accessors(chain = false)
    @Embedded
    private Audit audit;
}