package mx.gob.pjpuebla.trials.statistics.reports;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;

@Data
@Entity
@Table(name = "TBL_REPORTES")
public class Reporte {

    @Id
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Column(name = "S_CLAVE")
    private String key;

    @Column(name = "S_NOMBRE")
    private String name;

    @Column(name = "S_DESCRIPCION")
    private String description;

    @Column(name = "N_ORDEN")
    private Integer order;

    @Type(JsonBinaryType.class)
    @Column(name = "EXTRA_DATA", columnDefinition = "json")
    private ExtraData extraData;

}
