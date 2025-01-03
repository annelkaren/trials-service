package mx.gob.pjpuebla.trials.core.tipoprueba;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.util.AuditListener;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_TIPO_PRUEBAS")
@JsonIgnoreProperties({"tipoJuicio", "pruebaPadre"})
public class TipoPruebas {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TIPO_PRUEBAS_ID")
    @SequenceGenerator(name = "seq_tipo_pruebas_id", sequenceName = "TRIALS.SEQ_TIPO_PRUEBAS_ID", allocationSize = 1)
    @Column(name = "PN_ID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FN_TIPO_JUICIO", referencedColumnName = "PN_ID")
    private TipoJuicio tipoJuicio;

    @Column(name = "S_NOMBRE")
    private String nombre;

}
