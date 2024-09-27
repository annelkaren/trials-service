package mx.gob.pjpuebla.trials.workflow.personasdocumentos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.util.enums.Rol;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartes;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;

import java.io.Serializable;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_PERSONAS_DOCUMENTOS")
public class PersonaDocumento implements Serializable, Auditable {

    @Id
    @SequenceGenerator(name = "idPersonasDocumentos", sequenceName = "SEQ_PERSONAS_DOCUMENTOS_ID" , allocationSize = 50)
    @GeneratedValue(strategy =  GenerationType.SEQUENCE, generator = "idPersonasDocumentos")
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @NotBlank
    @Size(min = 3, max = 50)
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

    @JoinColumn(name = "FN_CARPETA", referencedColumnName = "PN_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Carpeta carpeta;

    @JoinColumn(name = "FN_TIPO_PARTE", referencedColumnName = "PN_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoPartes tipoPartes;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;
}
