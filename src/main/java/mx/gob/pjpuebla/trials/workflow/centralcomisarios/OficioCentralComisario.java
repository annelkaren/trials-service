package mx.gob.pjpuebla.trials.workflow.centralcomisarios;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Max;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.enums.EstadoCentralComisario;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;

@Entity
@EntityListeners(AuditListener.class)
@Data
@Table(name = "TBL_OFICIOS_CENTRAL_COMISARIO")
public class OficioCentralComisario implements Serializable, Auditable {

    @Id
    @SequenceGenerator(name = "idOficioCentralComisario", sequenceName = "SEQ_OFICIOS_CENTRAL_COMISARIO_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idOficioCentralComisario")
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Max(Integer.MAX_VALUE)
    @Version
    @Column(name = "N_VERSION")
    private Integer version;

    @JoinColumn(name = "FN_DOCUMENTO", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Documento documento;

    @JoinColumn(name = "FN_PERSONA_ENVIA", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Persona personaEnvia;

    @JoinColumn(name = "FN_PERSONA_RECIBE", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Persona personaRecibe;

    @JoinColumn(name = "FN_COMISARIO", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Persona comisario;

    @JoinColumn(name = "FN_PERSONA_DEVUELVE", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Persona personaDevuelve;

    @JoinColumn(name = "FN_CENTRAL_COMISARIOS", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Juzgado centralComisarios;

    @Column(name = "N_ESTADO")
    private EstadoCentralComisario estado;

    @Column(name = "T_FECHA_ENVIO")
    private LocalDateTime fechaEnvio;

    @Column(name = "T_FECHA_RECEPCION")
    private LocalDateTime fechaRecepcion;

    @Column(name = "T_FECHA_NOTIFICACION")
    private LocalDateTime fechaNotificacion;

    @Column(name = "T_FECHA_DEVOLUCION")
    private LocalDateTime fechaDevolucion;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;

}