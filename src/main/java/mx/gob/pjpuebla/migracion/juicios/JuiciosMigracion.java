package mx.gob.pjpuebla.migracion.juicios;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "juicios")
public class JuiciosMigracion {

    @Id
    @Column(name = "idjuicio", length = 45)
    private String idJuicio;

    @Column(name = "descrip", length = 200, nullable = false)
    private String descripcion;

    @Column(name = "status", length = 1)
    private String status;

    @Column(name = "materia", length = 1)
    private String materia;
}