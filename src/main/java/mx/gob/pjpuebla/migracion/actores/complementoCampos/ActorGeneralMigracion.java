package mx.gob.pjpuebla.migracion.actores.complementoCampos;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

//aplicable cuando es tipo de juicio de oralidad familiar.

@Entity
@Table(name = "actor_general")
@Data
public class ActorGeneralMigracion {
    
    @Id
    @Column(name = "id_actor")
    private Integer id;

    private String cuEntradas;

    @Column(name = "cu_actor")
    private String cuActor;

    private String nombre;

    @Column(name = "estado_civil")
    private String estadoCivil;

    private String edad;
    private String estudios;

    @Column(name = "leer_escribir")
    private String leerEscribir;

    private String nacionalidad;
    private String dedica;
    private String trabajo;

    @Column(name = "anio_nac")
    private String anioNac;

    private String domicilio;
    private String entidad;
    private String municipio;
    private String localidad;
    private String celular;
    private String correo;
    private String tipo;

    @Column(name = "tipo_juicio")
    private String tipoJuicio;

    @Column(name="id_ent")
    private String idEnt;

    @Column(name = "id_muni")
    private String idMuni;

    @Column(name = "id_loc")
    private String idLoc;
    
    private LocalDate fecha;
    private LocalDateTime hora;

}
