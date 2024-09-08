package mx.gob.pjpuebla.trials.core.personasdocumentos;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import mx.gob.pjpuebla.trials.core.documentos.Documento;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartes;
import mx.gob.pjpuebla.trials.util.AuditListener;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_PERSONAS_DOCUMENTOS")
public class PersonaDocumento {

    @Id
    @SequenceGenerator(name = "idPersonasDocumentos", sequenceName = "SEQ_PERSONAS_DOCUMENTOS_ID" , allocationSize = 50)
    @GeneratedValue(strategy =  GenerationType.SEQUENCE, generator = "idPersonasDocumentos")
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(name = "S_NOMBRES", nullable = false)
    private String nombre;

    @NotBlank
    @Size(min = 3, max = 40)
    @Column(name = "S_APELLIDO_PATERNO", nullable = false)
    private String apellidoPaterno;

    @NotBlank
    @Column(name = "S_APELLIDO_MATERNO")
    private String apellidoMaterno;

    @Column(name = "S_PSEUDONIMO")
    private String pseudonimo;

    @Column(name = "S_TIPO_PERSONA")
    private String tipoPersona;

    @JoinColumn(name = "FN_DOCUMENTO", referencedColumnName = "PN_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Documento documento;

    @JoinColumn(name = "FN_TIPO_PARTE", referencedColumnName = "PN_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoPartes tipoPartes;


}
