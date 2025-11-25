package mx.gob.pjpuebla.migracion.readers.tipoPromociones;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import net.jcip.annotations.Immutable;

@Table(name = "tipo_promociones")
@Entity
@Data
@Immutable
public class TipoPromocionesMigracion {
    
    @Id
    private Integer id;
    private String clave;
    private String nombre;
    private String materia;
    private Integer estatus;
}
