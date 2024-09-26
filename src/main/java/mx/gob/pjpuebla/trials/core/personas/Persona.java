package mx.gob.pjpuebla.trials.core.personas;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.escolaridades.Escolaridad;
import mx.gob.pjpuebla.trials.core.estadocivil.EstadoCivil;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.oficialias.Oficialia;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.Sexo;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_PERSONAS")
public class Persona implements Serializable, Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idPersona")
    @SequenceGenerator(name = "idPersona", sequenceName = "SEQ_PERSONAS_ID", allocationSize = 50)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Long id;

    @Max(Integer.MAX_VALUE)
    @Version
    @Column(name = "N_VERSION")
    private Integer version;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(name = "S_NOMBRES")
    private String nombre;

    @NotNull
    @Size(min = 3, max = 40)
    @Column(name = "S_APELLIDO_PATERNO")
    private String apellidoPaterno;

    @Column(name = "S_APELLIDO_MATERNO")
    private String apellidoMaterno;

    @NotNull
    @Pattern(regexp = "^[A-Z][AEIOU][A-Z][A-Z]\\d{6}[HM][A-Z]{2}[A-Z]{3}[A-Z0-9]{2}$")
    @Column(name = "S_CURP")
    private String curp;

    @Column(name = "S_RFC")
    private String rfc;

    @Past
    @Column(name = "T_FECHA_NACIMIENTO")
    private LocalDate fechaNacimiento;

    @Enumerated
    @Column(name = "N_SEXO", nullable = false)
    private Sexo sexo;

    @NotNull
    @Column(name = "S_OCUPACION")
    private String ocupacion;

    @NotNull
    @Email
    @Column(name = "S_CORREO_ELECTRONICO")
    private String correoElectronico;

    @Column(name = "S_TELEFONO")
    private String telefono;

    @Pattern(regexp = "^\\d{10}$")
    @Column(name = "S_CELULAR")
    private String celular;

    @Enumerated
    @Column(name = "N_ESTADO", nullable = false)
    private Estado estado;

    @JoinColumn(name = "FN_DOMICILIO", referencedColumnName = "PN_ID")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Domicilio domicilio;

    @JoinColumn(name = "FN_ESCOLARIDAD", referencedColumnName = "PN_ID")
    @OneToOne(fetch = FetchType.LAZY)
    private Escolaridad escolaridad;

    @JoinColumn(name = "FN_ESTADO_CIVIL", referencedColumnName = "PN_ID")
    @OneToOne(fetch = FetchType.LAZY)
    private EstadoCivil estadoCivil;

    @JoinColumn(name = "FN_JUZGADO", referencedColumnName = "PN_ID")
    @OneToOne(fetch = FetchType.LAZY)
    private Juzgado juzgado;

    @JoinColumn(name = "FN_OFICIALIA", referencedColumnName = "PN_ID")
    @OneToOne(fetch = FetchType.LAZY)
    private Oficialia oficialia;

    @Column(name = "S_USUARIO")
    private String usuario;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;
}
