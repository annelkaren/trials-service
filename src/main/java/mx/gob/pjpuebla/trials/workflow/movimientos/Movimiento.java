package mx.gob.pjpuebla.trials.workflow.movimientos;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.oficialias.Oficialia;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "TBL_MOVIMIENTOS")
public class Movimiento implements Serializable {
    @Id
    @SequenceGenerator(name = "idMovimientos", sequenceName = "SEQ_MOVIMIENTOS_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idMovimientos")
    @Column(name =  "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @JoinColumn(name = "FN_CARPETA", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Carpeta carpeta;

    @JoinColumn(name = "FN_DOCUMENTO", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Documento documento;

    @Column(name = "T_FECHA_ASIGNACION")
    private LocalDateTime fechaAsignacion;

    @JoinColumn(name = "FN_PERSONA", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Persona persona;

    @JoinColumn(name = "FN_PERSONA_DESTINO", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Persona destino;

    @Column(name = "S_MOTIVO")
    private String motivo;

    @Column(name = "S_CONCEPTO")
    private String concepto;

    @JoinColumn(name = "FN_OFICIALIA", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Oficialia oficialia;

    @JoinColumn(name = "FN_JUZGADO", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Juzgado juzgado;

    @Column(name = "S_ESTADO")
    private String estado;

    @Column(name = "S_UUID")
    private UUID uuid;

    @Size(max = 300)
    @Column(name = "S_OBSERVACIONES")
    private String observaciones;

    @Size(max = 60)
    @Column(name = "S_RECOMENDACIONES")
    private String recomendaciones;
}
