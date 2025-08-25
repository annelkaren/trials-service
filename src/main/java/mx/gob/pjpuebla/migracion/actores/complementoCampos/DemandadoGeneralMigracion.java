package mx.gob.pjpuebla.migracion.actores.complementoCampos;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "dem_general")
@Data
public class DemandadoGeneralMigracion {
    
    @Id
    @Column(name = "id_dem")
    private Integer id;

    private String cuEntradas;

    @Column(name = "cu_dem")
    private String cuDem;

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

    @Column(name = "lugar_trabajo")
    private String lugarTrabajo;

    @Column(name = "dom_lab")
    private String domLab;

    private String rfc;
    private String curp;
    private String nss;

    @Column(name="id_ent")
    private String idEnt;

    @Column(name = "id_muni")
    private String idMuni;

    @Column(name = "id_loc")
    private String idLoc;

    private LocalDate fecha;
    private LocalDateTime hora;
}
