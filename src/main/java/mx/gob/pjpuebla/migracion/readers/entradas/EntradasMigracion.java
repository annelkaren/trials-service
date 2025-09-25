package mx.gob.pjpuebla.migracion.readers.entradas;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;
import net.jcip.annotations.Immutable;

@Data
@Entity
@Immutable
@Table(name = "entradas")
public class EntradasMigracion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String juzgado;

    private String expediente;

    private Integer amo; // amo significa año en varias entidades de la base de datos de mysql.

    private LocalDate fecha;

    private String juicio;

    private String tipo;

    @Lob
    private String documentos;

    private BigDecimal cantidad;

    private String hora;

    private String procedenci;

    private String asunto;

    private String cu;

    @Column(name = "fecha_envi")
    private LocalDate fechaEnvi;

    private String legajo;

    private String oficio;

    private String etapa;

    private String status;

    @Column(name = "tipo_accion")
    private String tipoAccion;

    @Column(name = "tipoDivorcio")
    private String tipoDivorcio;

    private String materia;
}
