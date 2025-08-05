package mx.gob.pjpuebla.migracion.juzgados;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import mx.gob.pjpuebla.migracion.materias.MateriasMigracion;

import org.hibernate.annotations.Immutable;

@Data 
@Entity
@Immutable
@Table(name = "juzgados") 
public class JuzgadosMigracion {

    @Id
    @Column(name = "id_juzgado")
    private Integer idJuzgado;

    @Column(name = "descrip", insertable = false, updatable = false)
    private String descripcion;

    @Column(name = "codigo", insertable = false, updatable = false)
    private String codigo;

    @Column(name = "idjuzgadoext", insertable = false, updatable = false)
    private String idJuzgadoExterno;

    @Column(name = "tabla_ubi", insertable = false, updatable = false)
    private String tablaUbicacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materia", referencedColumnName = "codigo", insertable = false, updatable = false)
    private MateriasMigracion materiaObj;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materia_real", referencedColumnName = "codigo", insertable = false, updatable = false)
    private MateriasMigracion materiaRealObj;

    @Column(name = "distrito", insertable = false, updatable = false)
    private String distrito;

    @Column(name = "cita", insertable = false, updatable = false)
    private String cita;

    @Column(name = "instancia", insertable = false, updatable = false)
    private String instancia;

    @Column(name = "max_oficios", insertable = false, updatable = false)
    private Integer maxOficios;

    @Column(name = "max_exhortos_en", insertable = false, updatable = false)
    private Integer maxExhortosEn;

    @Column(name = "max_exhortos_sa", insertable = false, updatable = false)
    private Integer maxExhortosSa;

    @Column(name = "max_alta_apel", insertable = false, updatable = false)
    private Integer maxAltaApelacion;

    @Column(name = "max_despacho", insertable = false, updatable = false)
    private Integer maxDespacho;

    @Column(name = "max_expediente", insertable = false, updatable = false)
    private Integer maxExpediente;

    @Column(name = "libros", insertable = false, updatable = false)
    private String libros;

    @Column(name = "email", insertable = false, updatable = false)
    private String email;

    @Column(name = "email2", insertable = false, updatable = false)
    private String email2;

    @Column(name = "zona", insertable = false, updatable = false)
    private String zona;

    @Column(name = "filtro", insertable = false, updatable = false)
    private String filtro;

    @Column(name = "estatus_ma", insertable = false, updatable = false)
    private String estatusMa;

    @Column(name = "Oficios_juez", insertable = false, updatable = false)
    private String oficiosJuez;

    @Column(name = "abrev", insertable = false, updatable = false)
    private String abreviatura;

    @Column(name = "exhorto_filtro", insertable = false, updatable = false)
    private String exhortoFiltro;

    @Column(name = "zb", insertable = false, updatable = false)
    private String zb;

    @Column(name = "secgj_juz", insertable = false, updatable = false)
    private String secgjJuz;
}