package mx.gob.pjpuebla.trials.core.domicilios;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;

import java.io.Serializable;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_DOMICILIOS")
public class Domicilio implements Serializable, Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idDomicilio")
    @SequenceGenerator(name = "idDomicilio", sequenceName = "SEQ_DOMICILIOS_ID", allocationSize = 50)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Long id;

    @Min(1)
    @Max(Integer.MAX_VALUE)
    @Version
    @Column(name = "N_VERSION")
    private Integer version;

    @Pattern(regexp = "A|I|D")
    @Column(name = "S_ESTADO", nullable = false)
    private String estado;

    @NotBlank
    @Size(min = 3, max = 250)
    @Column(name = "S_CALLE", nullable = false)
    private String calle;

    @Column(name = "S_INTERIOR")
    private String interior;

    @Column(name = "S_EXTERIOR")
    private String exterior;

    @Size(min = 3, max = 250)
    @Column(name = "S_COLONIA")
    private String colonia;

    @Size(min = 5, max = 5)
    @Column(name = "S_CODIGO_POSTAL", nullable = false)
    private String codigoPostal;

    @NotBlank
    @Size(min = 3, max = 250)
    @Column(name = "S_MUNICIPIO", nullable = false)
    private String municipio;

    @NotBlank
    @Size(min = 3, max = 250)
    @Column(name = "S_ESTADO_REPUBLICA", nullable = false)
    private String estadoRepublica;

    @Embedded
    private Audit audit;

}