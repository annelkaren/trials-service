package mx.gob.pjpuebla.migracion.readers.detallesProm;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "detalles_prom")
@Data
public class DetallesProm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "cu")
    private String cu;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "actor")
    private String actor;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "hora")
    private String hora;

    @Column(name = "descrip")
    private String descrip;

    @Column(name = "anexos")
    private String anexos;

    @Column(name = "promovente")
    private String promovente;

    @Column(name = "status")
    private String status;

    @Column(name = "clave_ti")
    private String claveTi;

    @Column(name = "archivo")
    private String archivo;

    @Column(name = "acuerdo")
    private String acuerdo;

    @Column(name = "referencia")
    private Integer referencia;

    @Column(name = "atendida")
    private String atendida;
}