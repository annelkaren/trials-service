package mx.gob.pjpuebla.trials.core.tipoacuerdo;


import jakarta.persistence.*;
import lombok.Data;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.util.AuditListener;

import java.io.Serializable;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_TIPO_ACUERDO")
public class TipoAcuerdo implements Serializable {

    @Id
    @SequenceGenerator(name="idTipoAcuerdo", sequenceName = "SEQ_TIPO_ACUERDO_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idTipoAcuerdo")
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
