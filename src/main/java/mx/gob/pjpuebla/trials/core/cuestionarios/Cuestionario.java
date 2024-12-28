package mx.gob.pjpuebla.trials.core.cuestionarios;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.enums.ListCuestionario;
import mx.gob.pjpuebla.trials.util.enums.TipoPregunta;

import java.io.Serializable;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_CUESTIONARIOS")
public class Cuestionario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idCuestionario")
    @SequenceGenerator(name = "idCuestionario", sequenceName = "SEQ_CUESTIONARIOS_ID", allocationSize = 1)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @NotBlank
    @Size(min = 3, max = 250)
    @Column(name = "S_PREGUNTAS")
    private String preguntas;

    @Max(Integer.MAX_VALUE)
    @Column(name = "N_LISTA")
    private ListCuestionario lista;

    @Max(Integer.MAX_VALUE)
    @Column(name = "N_TIPO")
    private TipoPregunta tipo;
}
