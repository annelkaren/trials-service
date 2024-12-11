package mx.gob.pjpuebla.trials.core.acuerdorubros;

import jakarta.persistence.*;
import lombok.Data;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.util.AuditListener;

import java.io.Serializable;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_ACUERDO_RUBROS")
public class AcuerdoRubros implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idAcuerdoRubros")
    @SequenceGenerator(name="idAcuerdoRubros", sequenceName = "SEQ_ACUERDO_RUBROS_ID")
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Column(name = "S_NOMBRE")
    String nombre;

    @JoinColumn(name = "FN_MATERIA", referencedColumnName = "PN_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Materia materia;

    @JoinColumn(name = "FN_TIPO_SISTEMA", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoSistema tipoSistema;
}