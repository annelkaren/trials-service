package mx.gob.pjpuebla.trials.core.instituciones;

import java.io.Serializable;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.enums.Estado;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_INSTITUCIONES")
public class Institucion implements Serializable, Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idInstitucion")
    @SequenceGenerator(name = "idInstitucion", sequenceName = "SEQ_INSTITUCIONES_ID", allocationSize = 1)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Max(Integer.MAX_VALUE)
    @Version
    @Column(name = "N_VERSION")
    private Integer version;

    @Column(name = "S_NOMBRE")
    private String nombre;

    @Size(max = 20)
    @Column(name = "S_TELEFONO")
    private String telefono;

    @Size(max = 6)
    @Column(name = "S_EXTENSION")
    private String extension;

    @Enumerated
    @Column(name = "N_ESTADO", nullable = false)
    private Estado estado;

    @JoinColumn(name = "FN_DOMICILIO", referencedColumnName = "PN_ID")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Domicilio domicilio;

    @JoinColumn(name = "FN_DISTRITO", referencedColumnName = "PN_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Distrito distrito;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;

}
