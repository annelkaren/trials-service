package mx.gob.pjpuebla.trials.workflow.solicitudesProrrogas;

import java.io.Serializable;
import java.time.LocalDate;

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
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.enums.EstadoProrroga;
import mx.gob.pjpuebla.trials.workflow.movimientos.Movimiento;


@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_SOLICITUDES_PRORROGAS")
public class SolicitudesProrrogas implements Serializable, Auditable {
    
    @Id
    @SequenceGenerator(name="idSolicitudProrroga", sequenceName="SEQ_SOLICITUDES_PRORROGAS", allocationSize= 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idSolicitudProrroga")
    @Column(name = "PN_ID", insertable = false, updatable = false)
    public Integer id;

    @Max(Integer.MAX_VALUE)
    @Version
    @Column(name = "N_VERSION")
    private Integer version;

    @Size(max=250)
    @Column(name = "S_motivo_prorroga")
    private String motivoProrroga;

    @Enumerated
    @Column(name = "N_ESTADO", nullable = false)
    private EstadoProrroga estado;
    
    @Column(name = "T_FECHA_PRORROGA")
    private LocalDate fechaProrroga;

    @Column(name = "T_FECHA_AUTORIZADA")
    private LocalDate fechaAutorizada;

    @JoinColumn(name = "FN_MOVIMIENTO", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Movimiento movimiento;
    
    @Accessors(chain = false)
    @Embedded
    private Audit audit;

}
