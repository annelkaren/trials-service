package mx.gob.pjpuebla.trials.workflow.notificaciones;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import mx.gob.pjpuebla.trials.workflow.listaestrados.ListaEstrado;
import mx.gob.pjpuebla.trials.util.enums.EstadoNotificacion;
import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;

import java.io.Serializable;

@Entity
@Data
@Table(name = "TBL_NOTIFICACIONES")
public class Notificacion implements Serializable {

    @Id
    @SequenceGenerator(name = "idNotificaciones", sequenceName = "SEQ_NOTIFICACIONES_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idNotificaciones")
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;


    @Size(max = 500)
    @Column(name = "S_NOTAS")
    private String notas;

    @NotNull
    @Enumerated
    @Column(name = "N_TIPO", nullable = false)
    private TipoNotificacion tipoNotificacion;

    @NotNull
    @Enumerated
    @Column(name = "N_ESTADO", nullable = false)
    private EstadoNotificacion estadoNotificacion;

    @JoinColumn(name = "FN_DOCUMENTO", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Documento documento;

    @JoinColumn(name = "FN_LISTA", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private ListaEstrado listaEstrado;
}
