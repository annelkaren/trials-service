package mx.gob.pjpuebla.trials.workflow.transferencias;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.enums.EstadoTransferencia;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name="TBL_TRANSFERENCIAS")
public class Transferencia {
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
    @Column(name = "FN_JUZGADO")
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

    @Embedded
    private Audit audit;

}
