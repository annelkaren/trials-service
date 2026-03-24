package mx.gob.pjpuebla.migracion.readers.detalle;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

//Esta tabla es donde se inserrtan las promociones electronicas que los litigantes realizan
//En php, para posterior ser insertadas en detalles_prom una vez que se reciban en el juzgado.
@Entity
@Table(name = "detalles")
@Data
public class DetallesMigracion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String cu;
    private Integer clave;
    private Integer tipo;
    private LocalDate fecha;
    private String hora;
    private String anexos;
    private String promovente;
    private String status;
    private String actor;
    private String archivo;
    
    @Column(name = "tamanio_file")
    private String tamanioFile;
    
    private String hash;
    private String referencia;
    private String acuerdo;
    private String contenido;

    @Column(name = "fecha_rec")
    private LocalDate fechaRec;

    @Column(name = "hora_rec")
    private String horaRec;

    private String idjuzp; 

}
