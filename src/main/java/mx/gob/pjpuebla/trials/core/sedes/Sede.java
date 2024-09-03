package mx.gob.pjpuebla.trials.core.sedes;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.util.*;

import java.io.Serializable;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_SEDES")
public class Sede implements Serializable, Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idSede")
    @SequenceGenerator(name = "idSede", sequenceName = "SEQ_SEDES_ID", allocationSize = 50)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Max(Integer.MAX_VALUE)
    @Version
    @Column(name = "N_VERSION")
    private Integer version;

    @NotBlank
    @Size(min = 3, max = 250)
    @Column(name = "S_NOMBRE", nullable = false)
    private String nombre;

    @Size(max = 20)
    @Column(name = "S_TELEFONO")
    private String telefono;

    @Size(max = 6)
    @Column(name = "S_EXTENSION")
    private String extension;

    @Enumerated(EnumType.STRING)
    @Column(name = "S_TIPO", nullable = false)
    private Tipo tipo;

    @Enumerated
    @Column(name = "N_ESTADO", nullable = false)
    private Estado estado;

    @JoinColumn(name = "FN_DOMICILIO", referencedColumnName = "PN_ID", nullable = false)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Domicilio domicilio;

    @JoinColumn(name = "FN_DISTRITO", referencedColumnName = "PN_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Distrito distrito;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;
}
