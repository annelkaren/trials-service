package mx.gob.pjpuebla.trials.workflow.documentos;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.enums.EstadoDocumento;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;
import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.util.Date;

@Entity
@EntityListeners(AuditListener.class)
@Data
@Table(name = "TBL_DOCUMENTOS")
public class Documento implements Serializable, Auditable {

    @Id
    @SequenceGenerator(name = "idDocumento", sequenceName = "SEQ_DOCUMENTOS_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idDocumento")
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Max(Integer.MAX_VALUE)
    @Version
    @Column(name = "N_VERSION")
    private Integer version;

    @Enumerated
    @Column(name = "N_TIPO_DOCUMENTO")
    private TipoDocumento tipoDocumento;

    @Type(JsonBinaryType.class)
    @Column(name = "J_DATA", columnDefinition = "json")
    private DocumentoData data;

    @Size(max = 50)
    @Column(name = "S_RUTA")
    private String ruta;

    @Size(max = 250)
    @Column(name = "S_MOTIVO_EDITA")
    private String motivoEdita;

    @JoinColumn(name = "FN_CARPETA", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Carpeta carpeta;

    @JoinColumn(name = "FN_PERSONA", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Persona persona;

    @Column(name = "T_FECHA_ASIGNACION")
    private Date fechaAsignacion;

    @Column(name = "N_ESTADO")
    private EstadoDocumento estatus;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;

}
