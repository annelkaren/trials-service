package mx.gob.pjpuebla.trials.core.personas;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;

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

    @Pattern(regexp = "A|I|D")
    @Column(name = "S_ESTADO", nullable = false)
    private String estado;

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

    @Column(name = "S_CURP")
    private String curp;

    @Column(name = "S_RFC")
    private String rfc;

    @Pattern(regexp = "H|M|X")
    @Column(name = "S_SEXO")
    private String sexo;

    @NotNull
    @Email
    @Column(name = "S_CORREO_ELECTRONICO")
    private String correoElectronico;

    @Pattern(regexp = "^\\d{10}$")
    @Column(name = "S_TELEFONO")
    private String telefono;

    @Pattern(regexp = "^\\d{10}$")
    @Column(name = "S_CELULAR")
    private String celular;

    @Past
    @Column(name = "T_FECHA_NACIMIENTO")
    private LocalDate fechaNacimiento;

    @Column(name = "FS_ESTADO_CIVIL")
    private String estadoCivil;

    @Column(name = "S_OCUPACION")
    private String ocupacion;

    @Column(name = "FS_ESCOLARIDAD")
    private String escolaridad;

    @Column(name = "S_MUNICIPIO_NACIMIENTO")
    private String municipioNacimiento;

    @Column(name = "S_ESTADO_NACIMIENTO")
    private String estadoNacimiento;

    @Column(name = "FN_DOMICILIO")
    private Long domicilio;

    @Embedded
    private Audit audit;

}
