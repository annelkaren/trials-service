package mx.gob.pjpuebla.trials.core.configuraciones;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;

@Data
@EntityListeners(AuditListener.class)
@Entity
@Table(name = "TBL_CONFIGURACIONES")
public class Configuraciones {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="idConfiguracion")
    @SequenceGenerator(name = "idConfiguracion", sequenceName = "SEQ_CONFIGURACION_ID", allocationSize = 1)
    @Column(name = "pn_id", insertable = false, updatable = false)
    private Integer id;

    @Column(name = "s_propiedad")
    private String propiedad;

    @Column(name = "s_valor")
    private String valor;

    @JsonIgnore
    @Accessors(chain = false)
    @Embedded
    private Audit audit;


}
