package mx.gob.pjpuebla.migracion.materias;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "materias")
public class MateriasMigracion {
    @Id
    private Integer idmateria;
    private String materia;
    private String codigo;
}
