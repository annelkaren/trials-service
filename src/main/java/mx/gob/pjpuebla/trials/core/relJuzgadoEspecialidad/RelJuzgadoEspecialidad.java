package mx.gob.pjpuebla.trials.core.relJuzgadoEspecialidad;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.gob.pjpuebla.trials.core.especialidadJuzgado.Especialidades;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.util.AuditListener;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditListener.class)
@Table(name = "TBL_REL_JUZGADO_ESPECIALIDAD")
public class RelJuzgadoEspecialidad {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idRelJuzgadoEspecialidad")
    @SequenceGenerator(name = "idRelJuzgadoEspecialidad", sequenceName = "SEQ_REL_JUZGADO_ESPECIALIDAD_ID", allocationSize = 1)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Column(name = "FN_JUZGADO", insertable = false, updatable = false)
    private Integer juzgado;

    @Column(name = "FN_ESPECIALIDAD", insertable = false, updatable = false)
    private Integer especialidad;

}
