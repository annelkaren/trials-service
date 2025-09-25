package mx.gob.pjpuebla.migracion.readers.oficios;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "oficios")
@Data
public class OficiosMigracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "cu")
    private String cu;

    @Column(name = "oficio_juzgado")
    private Integer oficioJuzgado;

    @Column(name = "dependencia")
    private String dependencia;

    @Column(name = "asunto")
    private String asunto;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "amo")
    private String amo;

    @Column(name = "id_juzgado")
    private Integer idJuzgado;

    @Column(name = "oficio")
    private Integer oficio;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "expediente")
    private String expediente;

    @Column(name = "ruta")
    private String ruta;

    @Column(name = "ruta_ofi")
    private String rutaOfi;

    @Column(name = "nombre_ofi")
    private String nombreOfi;

    @Column(name = "estatus_ofi")
    private String estatusOfi;

    @Column(name = "fecha_entrega")
    private LocalDate fechaEntrega;

    @Column(name = "motivo")
    private String motivo;

    @Column(name = "se_entrego")
    private String seEntrego;

    @Column(name = "ruta_ofi_acuse")
    private String rutaOfiAcuse;

    @Column(name = "nombre_ofi_acuse")
    private String nombreOfiAcuse;

    @Column(name = "estatus_ofi_acuse")
    private String estatusOfiAcuse;

    @Column(name = "RevisarJA")
    private String revisarJa;

    @Column(name = "nume_medida")
    private String numeMedida;

    @Column(name = "fecha_RS")
    private LocalDate fechaRs;

    @Column(name = "hora_RS")
    private LocalTime horaRs;

    @Column(name = "ponencia")
    private Integer ponencia;
}