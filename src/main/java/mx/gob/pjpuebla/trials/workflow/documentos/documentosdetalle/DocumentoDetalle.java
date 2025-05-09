package mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.util.enums.EstadoAcuse;
import mx.gob.pjpuebla.trials.util.enums.EstadoEnvio;
import mx.gob.pjpuebla.trials.util.enums.TipoResolucion;
import mx.gob.pjpuebla.trials.util.enums.TipoSentencia;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "TBL_DOCUMENTO_DETALLE")
public class DocumentoDetalle implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idDocDetalle")
    @SequenceGenerator(name = "idDocDetalle", sequenceName = "SEQ_DOC_DETALLE_ID", allocationSize = 1)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Size(max = 50)
    @Column(name = "S_RUTA")
    private String ruta;

    @Size(max = 150)
    @Column(name = "S_ASUNTO")
    private String asunto;

    @Column(name = "T_FECHA_EMISION")
    private LocalDate fechaEmision;

    @Column(name = "T_FECHA_ENTREGA")
    private LocalDate fechaEntrega;

    @Column(name = "N_ESTADO")
    private EstadoAcuse estado;

    @Size(max = 150)
    @Column(name = "S_COMENTARIO")
    private String comentario;

    @Column(name= "S_TIPO_ACUERDO")
    private String tipoAcuerdo;

    @Column(name="T_FECHA_RESOLUCION")
    private LocalDate fechaResolucion;

    @Column(name="S_ETAPA_PROCESAL")
    private String etapaProcesal;

    @Column(name="N_TIPO_SENTENCIA")
    private TipoSentencia tipoSentencia;

    @Column(name = "N_TIPO_RESOLUCION")
    private TipoResolucion tipoResolucion;

    @Size(max=600)
    @Column(name = "S_EXTRACTO_SENTENCIA")
    private String extractoSentencia;

    @Size(max = 150)
    @Column(name = "S_RESUMEN")
    private String resumen;

    @Column(name = "T_FECHA_PUBLICACION")
    private LocalDate fechaPublicacion;

    @Column(name = "N_ESTADO_ENVIO")
    private EstadoEnvio estadoEnvio;

    @JoinColumn(name = "FN_PERSONA", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Persona persona;

    @JoinColumn(name = "FN_DOCUMENTO", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Documento documento;
}
