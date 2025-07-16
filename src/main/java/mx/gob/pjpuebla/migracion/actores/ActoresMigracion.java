package mx.gob.pjpuebla.migracion.actores;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "actores")
@Data
public class ActoresMigracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "clave")
    private String clave;

    @Column(name = "clave_act")
    private String claveAct;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "representa")
    private String representa;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "estatus")
    private String estatus;

    @Column(name = "tipo_persona")
    private String tipoPersona;

    @Column(name = "razon_social")
    private String razonSocial;

    @Column(name = "rfc")
    private String rfc;

    @Column(name = "sexo")
    private String sexo;

    @Column(name = "curp")
    private String curp;

    @Column(name = "tipo_notificacion")
    private String tipoNotificacion;

    @Column(name = "repre_sino")
    private String repreSino;

    @Column(name = "bene")
    private String bene;

    @Column(name = "dias")
    private String dias;

    @Column(name = "emplazado")
    private String emplazado;

    @Column(name = "dias_mas_emplazado")
    private LocalDate diasMasEmplazado;

    @Column(name = "motivo")
    private String motivo;

    @Column(name = "cujus")
    private String cujus;

    @Column(name = "reconven")
    private String reconven;
}