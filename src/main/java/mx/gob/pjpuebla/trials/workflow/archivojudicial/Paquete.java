package mx.gob.pjpuebla.trials.workflow.archivojudicial;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.util.enums.carpeta.Urgente;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "TBL_PAQUETES")
public class Paquete implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idPaquete")
    @SequenceGenerator(name = "idPaquete", sequenceName = "SEQ_PAQUETE_ID", allocationSize = 1)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Column(name = "N_PAQUETE_ID")
    private Integer paqueteId;

    @Enumerated
    @Column(name = "N_URGENTE", nullable = false)
    private Urgente urgente;

    @NotNull
    @PastOrPresent
    @Column(name = "T_FECHA_ENVIO")
    private LocalDateTime fechaEnvio;

    @NotNull
    @Column(name = "T_FECHA_TERMINO")
    private LocalDate fechaTermino;

    @JoinColumn(name = "S_USUARIO_ALTA", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Persona usuarioAlta;

}
