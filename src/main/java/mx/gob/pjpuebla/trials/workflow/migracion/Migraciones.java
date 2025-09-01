package mx.gob.pjpuebla.trials.workflow.migracion;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
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
import mx.gob.pjpuebla.trials.util.enums.EstadoMigracion;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.util.Auditable;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_MIGRACIONES")
public class Migraciones implements Serializable, Auditable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idMigracion")
    @SequenceGenerator(name = "idMigracion", sequenceName = "SEQ_MIGRACION_ID", allocationSize= 1)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Max(Integer.MAX_VALUE)
    @Version
    @Column(name = "N_VERSION")
    private Integer version;

    @Enumerated
    @Column(name = "N_ESTATUS_MIGRACION")
    private EstadoMigracion estatus;

    @Column(name = "S_OBSERVACIONES")
    private String observaciones;

    @Column(name = "S_ASIGNACION_ANTERIOR")
    private String asignacionAnterior;

    @Column(name = "S_PUESTO_ASIGNACION_ANTERIOR")
    private String puestoAsignacionAnterior;

    @JoinColumn(name = "FN_JUZGADO", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Juzgado juzgado;

    @JoinColumn(name = "FN_CARPETA", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Carpeta carpeta;

    //Persona a quien se va a turnar el expediente .
    @JoinColumn(name = "FN_PERSONA", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Persona personaTurnado;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;

}
