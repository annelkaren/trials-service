package mx.gob.pjpuebla.trials.workflow.personasdocumentos;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartes;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.enums.Rol;
import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;

import java.io.Serializable;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_PERSONAS_DOCUMENTOS")
public class PersonaDocumento implements Serializable, Auditable {

    @Id
    @SequenceGenerator(name = "idPersonasDocumentos", sequenceName = "SEQ_PERSONAS_DOCUMENTOS_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idPersonasDocumentos")
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;


    @Column(name = "S_NOMBRES", nullable = false)
    private String nombre;

    @Column(name = "S_APELLIDO_PATERNO")
    private String apellidoPaterno;

    @Column(name = "S_APELLIDO_MATERNO")
    private String apellidoMaterno;

    @Column(name = "S_PSEUDONIMO")
    private String pseudonimo;

    @Column(name = "S_TIPO_PERSONA")
    private String tipoPersona;

    @Enumerated
    @Column(name = "N_ROL")
    private Rol rol;

    @Column(name = "S_INE")
    private String ine;

    @Column(name = "S_CURP")
    private String curp;

    @Email
    @Column(name = "S_EMAIL")
    private String correoElectronico;

    @Column(name = "S_CELULAR")
    private String celular;

    @JoinColumn(name = "FN_CARPETA", referencedColumnName = "PN_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Carpeta carpeta;

    @Column(name = "S_DOMICILIO")
    private String domicilio;

    @JoinColumn(name = "FN_TIPO_PARTE", referencedColumnName = "PN_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoPartes tipoPartes;

    @Enumerated
    @Column(name = "N_TIPO_NOTIFICACION")
    private TipoNotificacion tipoNotificacion;

    @Column(name = "S_CORREO_NOTIFICACION")
    private String correoNotificacion;

    @JoinColumn(name = "FN_DOMICILIO", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Domicilio fnDomicilio;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;
} 
