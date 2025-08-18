package mx.gob.pjpuebla.migracion.tipoPiezas;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import net.jcip.annotations.Immutable;

@Data
@Entity
@Immutable
@Table(name="tipos")
public class TipoPiezaMigracion {
    
    @Id
    @Column(name = "idtipo")
    private Integer idTipo;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "clave")
    private String clave;
}
