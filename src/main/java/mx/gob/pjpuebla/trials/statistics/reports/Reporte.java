package mx.gob.pjpuebla.trials.statistics.reports;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;

@Data
@Entity
@IdClass(ReporteId.class)
@Table(name = "TBL_REPORTES")
public class Reporte {

    @Id
    @Column(name = "PN_FILA", insertable = false, updatable = false)
    private Integer row;

    @Id
    @Column(name = "PN_COLUMNA", insertable = false, updatable = false)
    private Integer column;

    @Column(name = "S_REPORTE")
    private String reportKey;

    @Type(JsonBinaryType.class)
    @Column(name = "STYLE_DATA", columnDefinition = "json")
    private StyleData styleData;

    @Type(JsonBinaryType.class)
    @Column(name = "EXTRA_DATA", columnDefinition = "json")
    private ExtraData extraData;
}
