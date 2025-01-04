package mx.gob.pjpuebla.trials.workflow.audienciaspruebas;

import jakarta.persistence.*;
import lombok.Data;
import mx.gob.pjpuebla.trials.core.materiapericial.MateriaPericial;
import mx.gob.pjpuebla.trials.core.tipoprueba.TipoPruebas;
import mx.gob.pjpuebla.trials.util.enums.DesistimientoAdmision;
import mx.gob.pjpuebla.trials.workflow.audiencias.Audiencia;

import java.io.Serializable;

@Data
@Entity
@Table(name = "TBL_AUDIENCIAS_PRUEBAS", schema = "TRIALS")
public class AudienciaPruebas implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idAudienciasPruebas")
    @SequenceGenerator(name = "idAudienciasPruebas", sequenceName = "TRIALS.SEQ_AUDIENCIAS_PRUEBAS_ID", allocationSize = 1)
    @Column(name = "PN_ID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)  // Cambio a EAGER
    @JoinColumn(name = "FN_AUDIENCIA", nullable = false)
    private Audiencia audiencia;

    @ManyToOne(fetch = FetchType.EAGER)  // Cambio a EAGER
    @JoinColumn(name = "FN_TIPO_PRUEBA", nullable = false)
    private TipoPruebas tipoPrueba;

    @ManyToOne(fetch = FetchType.EAGER)  // Cambio a EAGER
    @JoinColumn(name = "FN_MATERIA_PERICIAL")
    private MateriaPericial materiaPericial;

    @Column(name = "S_NOMBRE_DECLARANTE", length = 60)
    private String nombreDeclarante;

    @Column(name = "S_DESCRIPCION_INSTRUMENTO", length = 150)
    private String descripcionInstrumento;

    @Column(name = "S_ABSOLVENTE", length = 60)
    private String absolvente;

    @Column(name = "N_DESISTIMIENTO_ADMISION", length = 60)
    private DesistimientoAdmision desistimientoAdmision;

    @Column(name = "S_DESCRIPCION_DOCUMENTO", length = 150)
    private String descripcionDocumento;

    @Column(name = "S_OBJETO", length = 70)
    private String objeto;

    @Column(name = "S_URL_DOCUMENTO", length = 255)
    private String urlDocumento;
}
