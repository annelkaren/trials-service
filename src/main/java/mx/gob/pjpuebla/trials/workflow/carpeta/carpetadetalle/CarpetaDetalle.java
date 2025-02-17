package mx.gob.pjpuebla.trials.workflow.carpeta.carpetadetalle;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.util.enums.PresentacionImputado;
import mx.gob.pjpuebla.trials.util.enums.SolicitudAudiencia;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "TBL_CARPETA_DETALLE")
public class CarpetaDetalle implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idCarpetaDetalle")
    @SequenceGenerator(name = "idCarpetaDetalle", sequenceName = "SEQ_CARPETA_DETALLE_ID", allocationSize = 1)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @JoinColumn(name = "FN_CARPETA", referencedColumnName = "PN_ID")
    @OneToOne(fetch = FetchType.LAZY)
    private Carpeta carpeta;

    @Column(name = "T_FECHA_ADMISION")
    private LocalDateTime fechaAdmision;

    @Column(name = "T_FECHA_DESECHADO")
    private LocalDateTime fechaDesechado;

    @Size(max = 150)
    @Column(name = "S_ASUNTO")
    private String asunto;

    @Size(max = 300)
    @Column(name = "S_OBSERVACIONES")
    private String observaciones;

    @Size(max = 150)
    @Column(name = "S_PROMOVENTE")
    private String promovente;

    @Size(max = 50)
    @Column(name = "S_NUMERO_CARPETA_INVESTIGACION")
    private String numeroCarpetaInvestigacion;

    @Size(max = 50)
    @Column(name = "S_NUMERO_OFICIO")
    private String numeroOficio;

    @Size(max = 150)
    @Column(name = "S_LUGAR_HECHO")
    private String lugarHecho;

    @Column(name = "T_FECHA_HECHO")
    private LocalDate fechaHecho;

    @Column(name = "N_CANTIDAD_PRINCIPAL")
    private Integer cantidadPrincipal;

    @Size(max = 150)
    @Column(name = "S_MONEDA")
    private String moneda;

    @Column(name = "N_HIJOS")
    private Integer numeroHijos;

    @Column(name = "N_HIJOS_MENORES_EDAD")
    private Integer numeroHijosMenoresEdad;

    @Size(max = 50)
    @Column(name = "S_NUMERO_ACTA_MATRIMONIO")
    private String actaMatrimonio;

    @Size(max = 150)
    @Column(name = "S_LUGAR_ACTA_MATRIMONIO")
    private String lugarRegistroMatrimonio;

    @Size(max = 150)
    @Column(name = "S_ENTIDAD")
    private String entidad;

    @Size(max = 150)
    @Column(name = "S_MUNICIPIO")
    private String municipio;

    @Size(max = 150)
    @Column(name = "S_LOCALIDAD")
    private String localidad;

    @Column(name = "T_FECHA_REGISTRO")
    private LocalDateTime fechaRegistro;

    @Column(name = "T_HORA_REGISTRO")
    private LocalTime horaFormal;

    @Column(name = "T_HORA_MATERIAL")
    private LocalTime horaMaterial;

    @Size(max = 150)
    @Column(name = "S_LUGAR_DISPOSICION")
    private String lugarDisposicion;

    @Column(name = "N_PRESENTACION_IMPUTADO")
    private PresentacionImputado presentacionImputado;

    @Column(name = "N_SOLICITUD_AUDIENCIA")
    private SolicitudAudiencia solicitudAudiencia;

    @Column(name = "T_FECHA_PRESENTACION_IMPUTADO")
    private LocalDate fechaPresentacionImputado;

    @Column(name = "S_CUJUS")
    private String cujus;

    @JoinColumn(name = "FN_TIPO_JUICIO", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoJuicio tipoJuicio;
}
