package mx.gob.pjpuebla.migracion.readers.exhortoCapital;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "exhortos_capital")
@Data
public class ExhortosCapitalMigracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ec")
    private Integer idEc;

    @Column(name = "exhorto", nullable = false)
    private String exhorto;

    @Column(name = "numero", nullable = false)
    private String numero;

    @Column(name = "amo", nullable = false)
    private Integer amo;

    @Column(name = "juzgadoOr", nullable = false)
    private String juzgado;

    @Column(name = "destino")
    private String destino;

    @Column(name = "tramite")
    private String tramite;

    @Column(name = "fechaEn")
    private LocalDate fechaEn;

    @Column(name = "fechaDe")
    private LocalDate fechaDe;

    @Column(name = "obse", length = 500)
    private String obse;

    @Column(name = "descrip")
    private String descrip;

    @Column(name = "fecha_re")
    private LocalDate fechaRe;

    @Column(name = "hora_re")
    private LocalTime horaRe;

    @Column(name = "personal", nullable = false)
    private String personal;
}