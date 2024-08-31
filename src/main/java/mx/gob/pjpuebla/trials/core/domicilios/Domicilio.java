package mx.gob.pjpuebla.trials.core.domicilios;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
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

    @NotBlank
    @Size(min = 3, max = 250)
    @Column(name = "S_CALLE", nullable = false)
    private String calle;

    @Size(max = 20)
    @Column(name = "S_INTERIOR")
    private String interior;

    @Size(min = 1, max = 20)
    @Column(name = "S_EXTERIOR", nullable = false)
    private String exterior;

    @Size(max = 250)
    @Column(name = "S_COLONIA")
    private String colonia;

    @Size(max = 250)
    @Column(name = "S_LOCALIDAD")
    private String localidad;

    @Size(max = 5)
    @Column(name = "S_CODIGO_POSTAL")
    private String codigoPostal;

    @NotBlank
    @Size(min = 3, max = 250)
    @Column(name = "S_MUNICIPIO", nullable = false)
    private String municipio;

    @NotBlank
    @Size(min = 3, max = 250)
    @Column(name = "S_ESTADO_REPUBLICA", nullable = false)
    private String estadoRepublica;

    @Size(max = 250)
    @Column(name = "S_REFERENCIA")
    private String referencia;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;

}