package mx.gob.pjpuebla.migracion.readers.movimientos;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import net.jcip.annotations.Immutable;

@Data
@Entity
@Immutable
@Table(name = "ubicaciones")
public class MovimientosMigracion {
     @Id
    @Column(name = "id_ubicaciones")
    private Integer id;

    @Column(name = "cu")
    private String cu;

    @Column(name = "id_puesto")
    private Integer idPuesto;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "hora")
    private String hora;

    @Column(name = "status")
    private String status;

    @Column(name = "estado")
    private String estado;

    @Column(name = "etapa")
    private String etapa;

    @Column(name = "entrego")
    private String entrego;

    @Column(name = "recibio")
    private String recibio;

    @Column(name = "puesto_entrego")
    private String puestoEntrego;

    @Column(name = "puesto_recibio")
    private String puestoRecibio;

    @Column(name = "libro")
    private String libro;

    @Column(name = "num_foja")
    private Integer numFoja;

    @Column(name = "obse")
    private String observaciones;

    @Column(name = "sentido")
    private String sentido;

    @Column(name = "digitalizado_acu")
    private String digitalizadoAcu;
}
