package mx.gob.pjpuebla.trials.workflow.transferencias;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.enums.EstadoTransferencia;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name="TBL_TRANSFERENCIAS")
public class Transferencia implements Serializable, Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idTransferencia")
    @SequenceGenerator(name="idTransferencia", sequenceName = "SEQ_TRANSFERENCIA_ID")
    @Column(name =  "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @NotNull
    @Column(name = "FN_PERSONA_ENTREGA")
    private Long entregaId;

    @Column(name = "FN_PERSONA_RECIBE")
    private Long recibeId;

    @NotNull
    @Column(name = "FN_PERSONA_AUTORIZA")
    private Long autorizaId;

    @NotNull
    @JoinColumn(name = "FN_JUZGADO", referencedColumnName = "PN_ID")
    @OneToOne(fetch = FetchType.LAZY)
    private Juzgado juzgado;

    @Column(name = "T_FECHA_TRANSFERENCIA")
    private LocalDateTime fechaTransferencia;

    @Column(name = "S_OBSERVACIONES")
    private String observaciones;

    @NotNull
    @Column(name = "N_ESTATUS")
    private EstadoTransferencia estatus;

    @Column(name = "N_TOTAL_EXPEDIENTES")
    private Integer totalExpediente;

    @Column(name = "S_UUID_MOVIMIENTO")
    private UUID uuid;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;

}
