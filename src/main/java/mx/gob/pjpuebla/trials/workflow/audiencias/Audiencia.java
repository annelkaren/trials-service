package mx.gob.pjpuebla.trials.workflow.audiencias;

import java.time.LocalDateTime;

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
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;

import mx.gob.pjpuebla.trials.core.bloques.Bloque;
import mx.gob.pjpuebla.trials.core.salas.Sala;
import mx.gob.pjpuebla.trials.core.tipoaudiencia.TipoAudiencia;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.enums.EstatusAudiencia;
import mx.gob.pjpuebla.trials.util.enums.Asistencia;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;

@Entity
@EntityListeners(AuditListener.class)
@Data
@Table(name = "TBL_AUDIENCIA")
public class Audiencia implements  Serializable, Auditable {

    @Id
    @SequenceGenerator(name = "idAudiencia", sequenceName = "SEQ_AUDIENCIAS_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idAsistencia")
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @NotNull
    @Column(name = "T_FECHA_AUDIENCIA")
    private LocalDateTime fechaAudiencia;

    @Enumerated
    @Column(name = "N_ASISTENCIA_ACTOR")
    private Asistencia asisteActor;

    @Enumerated
    @Column(name = "N_ASISTENCIA_DEMANDADO")
    private Asistencia asisteDemandano;

    @NotNull
    @JoinColumn(name = "FN_SALA", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Sala sala;

    @Column(name="T_INICIO")
    private LocalDateTime inicio;

    @Column(name="T_FIN")
    private LocalDateTime fin;

    @NotNull
    @Enumerated
    @Column(name="N_ESTATUS_AUDIENCIA")
    private EstatusAudiencia estatusAudiencia;

    @Column(name = "FN_TIPO")
    private TipoAudiencia tipoAudiencia;

    @Column(name = "FN_CARPETA")
    private Carpeta carpeta;
    
    @JoinColumn(name = "FN_BLOQUE", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Bloque bloque;

    @Enumerated
    @Column(name = "N_ESTADO")
    private Estado estado;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;
}
