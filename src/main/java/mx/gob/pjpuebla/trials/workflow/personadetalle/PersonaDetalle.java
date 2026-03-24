package mx.gob.pjpuebla.trials.workflow.personadetalle;

import java.io.Serializable;
import java.sql.Date;

import jakarta.persistence.*;
import lombok.Data;
import mx.gob.pjpuebla.trials.core.documentoidentificacion.DocumentoIdentificacion;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.escolaridades.Escolaridad;
import mx.gob.pjpuebla.trials.core.lenguasindigenas.LenguaIndigena;
import mx.gob.pjpuebla.trials.core.nacionalidades.Nacionalidad;
import mx.gob.pjpuebla.trials.core.paises.Pais;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;

@Data
@Entity
@Table(name = "TBL_PERSONA_DETALLE")
public class PersonaDetalle implements Serializable {
    
    @Id
    @SequenceGenerator(name = "idPersonaDetalle", sequenceName = "TRIALS.SEQ_PERSONADETALLE_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idPersonaDetalle")
    @Column(name = "PN_ID")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "FN_PERSONA_DOCUMENTO")
    private PersonaDocumento personaDocumento;

    @ManyToOne
    @JoinColumn(name = "FN_LENGUA_INDIGENA")
    private LenguaIndigena lenguaIndigena;

    @ManyToOne
    @JoinColumn(name = "FN_NACIONALIDAD")
    private Nacionalidad nacionalidad;

    @ManyToOne
    @JoinColumn(name = "FN_ESCOLARIDAD")  
    private Escolaridad escolaridad;

    @ManyToOne
    @JoinColumn(name = "FN_DOCUMENTO_IDENTIFICACION")
    private DocumentoIdentificacion documentoIdentificacion;

    @ManyToOne
    @JoinColumn(name = "FN_DOMICILIO")
    private Domicilio domicilio;

    @ManyToOne
    @JoinColumn(name = "FN_EN_REPRESENTACION_DE")
    private PersonaDocumento enRepresentacionDe;

    @Column(name = "S_SEXO")
    private String sexo;

    @Column(name = "D_FECHA_NACIMIENTO")
    @Temporal(TemporalType.DATE)
    private Date fechaNacimiento;

    @ManyToOne
    @JoinColumn(name = "FN_PAIS_NACIMIENTO")
    private Pais paisNacimiento;

    @Column(name = "S_MUNICIPIO_NACIMIENTO")
    private String municipioNacimiento;

    @Column(name = "S_ENTIDAD_NACIMIENTO")
    private String entidadNacimiento;

    @Column(name = "N_EDAD")
    private Integer edad;

    @Column(name = "N_POSICION_TRABAJO")
    private Integer posicionTrabajo;

    @Column(name = "S_TIPO_DOMICILIO")
    private String tipoDomicilio;

    @Column(name = "S_RFC", length = 13)
    private String rfc;

    @Column(name = "S_DISCAPACIDAD")
    private String discapacidad;

    @Column(name = "S_ESTADO_CIVIL")
    private String estadoCivil;

    @Column(name = "S_CONDICION_MIGRATORIA")
    private String condicionMigratoria;

    @Column(name = "B_HABLA_ESPANOL")
    private Boolean hablaEspanol;

    @Column(name = "S_LENGUA_EXTRANJERA")
    private String lenguaExtranjera;

    @Column(name = "S_GRUPO_VULNERABLE")
    private String grupoVulnerable;

    @Column(name = "B_SABE_LEER_ESCRIBIR")
    private Boolean sabeLeerEscribir;

    @Column(name = "S_PROFESION_OFICIO")
    private String profesionOficio;

    @Column(name = "S_INGRESO_MENSUAL_NETO")
    private String ingresoMensualNeto;

    @Column(name = "S_FRECUENCIA_INGRESO")
    private String frecuenciaIngreso;

    @Column(name = "S_LUGAR_TRABAJO")
    private String lugarTrabajo;

    @Column(name = "N_CANTIDAD_DEPENDIENTES_ECONOMICOS")  
    private Integer cantidadDependientesEconomicos;

    @Column(name = "S_DETALLE_DEPENDIENTES")
    private String detalleDependientes;

    @Column(name = "N_BIENES")
    private Integer bienes;

    @Column(name = "S_RELIGION")
    private String religion;

    @Column(name = "S_SENIAS_PARTICULARES")
    private String seniasParticulares;

    @Column(name = "S_GRUPO_ETNICO")
    private String grupoEtnico;

    @Column(name = "B_DATOS_PRIVADOS")
    private Boolean datosPrivados;

    @Column(name = "S_CEDULA", length = 13)
    private String cedula;

    @Column(name = "S_TIPO_DEFENSOR")  //ENUM
    private String tipoDefensor;

    @Column(name = "S_ADSCRIPCION")
    private String adscripcion;

    @Column(name = "B_RECIBE_PERCEPCIONES")
    private Boolean recibePercepciones;

    @Column(name = "B_HABLA_LENGUA_INDIGENA")
    private Boolean hablaLenguaIndigena;

    @Column(name = "B_PERTENECE_GRUPO_VULNERABLE")
    private Boolean perteneceGrupoVulnerable;

    @Column(name = "B_TIENE_BIENES")
    private Boolean tieneBienes;

    @Column(name = "B_TIENE_SENIAS_PARTICULARES")
    private Boolean tieneSeniasParticulares;

    @Column(name = "B_PERTENECE_GRUPO_ETNICO")
    private Boolean perteneceGrupoEtnico;

    @Column(name = "B_TIENE_RELIGION")
    private Boolean tieneReligion;

    @Column(name = "B_TIENE_DEPENDIENTES_ECONOMICOS")
    private Boolean tieneDependientes;

    @Column(name = "B_HABLA_LENGUA_EXTRANJERA")
    private Boolean hablaLenguaExtranjera;

}
