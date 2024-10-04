package mx.gob.pjpuebla.trials.core.eventos;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
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
    @SequenceGenerator(name = "idEvento", sequenceName = "SEQ_EVENTOS_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idEvento")
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Version
    @Column(name = "N_VERSION")
    private Integer version;

    @Column(name = "s_descripcion")
    private String descripcion;

    @NotNull
    @Column(name = "T_DIA_INICIO")
    private LocalDate diaInicio;

    @NotNull
    @Column(name = "T_DIA_FIN")
    private LocalDate diaFin;

    @JoinColumn(name = "fn_oficialia", referencedColumnName = "PN_ID")
    private Oficialia oficialia;

    @JoinColumn(name = "fn_juzgado", referencedColumnName = "PN_ID")
    private Juzgado juzgado;

    @Enumerated
    @Column(name = "n_estado")
    private Estado estado;

    @JsonIgnore
    @Accessors(chain = false)
    @Embedded
    private Audit audit;
}