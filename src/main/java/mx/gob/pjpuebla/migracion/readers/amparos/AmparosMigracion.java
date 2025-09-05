package mx.gob.pjpuebla.migracion.readers.amparos;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "amparo")
@Data
public class AmparosMigracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 12)
    private String cu;

    @Column(length = 8)
    private String hora;

    private LocalDate fecha;

    @Column(name = "num_of", length = 50)
    private String numOf;

    @Column(length = 14)
    private String quejoso;

    @Column(length = 1)
    private String tipo;

    @Column(name = "juz_dis", length = 50)
    private String juzDis;

    @Column(length = 15)
    private String contra;

    @Column(name = "fec_in")
    private LocalDate fechaInicio;

    @Column(name = "fec_con")
    private LocalDate fechaConclusion;

    @Column(length = 2)
    private String concede;

    @Column(name = "clave_am", length = 16)
    private String claveAm;

    @Column(name = "año")
    private Integer anio;

    @Column(length = 4)
    private String ampa;

    @Column(length = 4)
    private String expediente;

    private Integer clave;

    @Column(name = "quejoso_1", length = 50)
    private String quejoso1;

    @Column(name = "num_amparo", length = 10)
    private String numAmparo;

    @Column(length = 15)
    private String revision;

    @Column(length = 15)
    private String impugna;

    @Column(length = 1)
    private String estatus;
}