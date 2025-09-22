package mx.gob.pjpuebla.migracion.readers.exhortoForaneo;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "exhorto_foraneo")
@Data
public class ExhortoForaneoMigracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ef")
    private Integer idEf;

    @Column(name = "exhorto", nullable = false)
    private String exhorto;

    @Column(name = "numero", nullable = false)
    private String numero;

    @Column(name = "amo", nullable = false)
    private Integer amo;

    @Column(name = "juzgadoOr", nullable = false)
    private String juzgado;

    @Column(name = "partes")
    private String partes;

    @Column(name = "tramite")
    private String tramite;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "obse", columnDefinition = "text")
    private String obse;

    @Column(name = "descrip", columnDefinition = "text")
    private String descrip;

    @Column(name = "fecha_re")
    private LocalDate fechaRe;

    @Column(name = "hora_re")
    private LocalTime horaRe;

    @Column(name = "personal", nullable = false)
    private String personal;

    @Column(name = "Fecha_re_1")
    private String fechaRe1;
}