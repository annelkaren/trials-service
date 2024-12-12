package mx.gob.pjpuebla.trials.workflow.notificaciondetalle;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import mx.gob.pjpuebla.trials.workflow.notificaciones.Notificacion;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;

@Entity
@Data
@Table(name = "TBL_NOTIFICACIONES_DETALLE")
public class NotificacionesDetalles implements Serializable {

    @Id
    @SequenceGenerator(name = "idNotificacionesDetalles", sequenceName = "SEQ_NOTIFICACIONES_DETALLES_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idNotificacionesDetalles")
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @JoinColumn(name = "FN_NOTIFICACION", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Notificacion notificacion;

    @JoinColumn(name = "FN_PERSONA_DOCUMENTO", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private PersonaDocumento personaDocumento;
}
