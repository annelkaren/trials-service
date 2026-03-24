package mx.gob.pjpuebla.migracion.readers.conceptos.familiar;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "doc12")
@Data
public class ConceptosMatFamiliarMigracion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    private String clave;
    private String dias;
    private String estatus;
}
