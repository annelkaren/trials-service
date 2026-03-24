package mx.gob.pjpuebla.trials.core.domicilios;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.core.paises.Pais;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;

import java.io.Serializable;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_DOMICILIOS")
public class Domicilio implements Serializable, Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idDomicilio")
    @SequenceGenerator(name = "idDomicilio", sequenceName = "SEQ_DOMICILIOS_ID",  allocationSize = 1)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Long id;

    
    @Size(min = 3, max = 250)
    @Column(name = "S_CALLE")
    private String calle;

    @Size(max = 20)
    @Column(name = "S_INTERIOR")
    private String interior;

    @Size(min = 1, max = 20)
    @Column(name = "S_EXTERIOR", nullable = false)
    private String exterior;

    @Size(max = 250)
    @Column(name = "S_COLONIA")
    private String colonia;

    @Size(max = 250)
    @Column(name = "S_LOCALIDAD")
    private String localidad;

    @Size(max = 5)
    @Column(name = "S_CODIGO_POSTAL")
    private String codigoPostal;

    @NotBlank
    @Size(min = 3, max = 250)
    @Column(name = "S_MUNICIPIO", nullable = false)
        private String municipio;

    @Size(max = 10)
    @Column(name = "S_MUNICIPIO_ID")
    private String municipioId;

    @NotBlank
    @Size(min = 3, max = 250)
    @Column(name = "S_ESTADO_REPUBLICA", nullable = false)
    private String estadoRepublica;

    @Size(max = 250)
    @Column(name = "S_REFERENCIA")
    private String referencia;
    
    @ManyToOne
    @JoinColumn(name = "FN_PAIS_RESIDENCIA")
    private Pais paisResidencia;

    @Column(name = "S_LATITUD")
    private String latitud;

    @Column(name = "S_LONGITUD")
    private String longitud;

    @Column(name = "S_CIUDAD")
    private String ciudad;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;

    //TODO: CONSIDERAR QUE NO SIEMPRE ES UNACALLE.
    public String getLineaDomicilio() {
        StringBuilder sb = new StringBuilder();
        if (calle != null) {
            sb.append("Calle ").append(calle);
        }
        if (exterior != null) {
            sb.append(", No. ").append(exterior);
        }
        if (interior != null && !interior.isBlank()) {
            sb.append(", Interior ").append(interior);
        }
        if (colonia != null) {
            sb.append(", Col. ").append(colonia);
        }
        if (localidad != null) {
            sb.append(", ").append(localidad);
        }
        if (codigoPostal != null) {
            sb.append(", C.P. ").append(codigoPostal);
        }
        if (municipio != null) {
            sb.append(", ").append(municipio);
        }
        if (estadoRepublica != null) {
            sb.append(", ").append(estadoRepublica);
        }
        if (paisResidencia != null && paisResidencia.getNombreComun() != null) {
            sb.append(", ").append(paisResidencia.getNombreComun());
        }else if(paisResidencia != null && paisResidencia.getNombreOficial() != null){
            sb.append(", ").append(paisResidencia.getNombreOficial());
        }
        return sb.toString().trim();
    }

    public String getDireccionInstitucion() {
        StringBuilder sb = new StringBuilder();
        if (calle != null) {
            sb.append(calle).append(" ");
        }
        if (colonia != null) {
            sb.append(colonia).append(" ");
        }
        if (exterior != null && !exterior.isBlank()) {
            sb.append(exterior).append(" ");
        }
        if (interior != null && !interior.isBlank()) {
            sb.append("Int. ").append(interior).append(" ");
        }
        if (estadoRepublica != null) {
            sb.append(estadoRepublica).append(" ");
        }
        if (municipio != null) {
            sb.append(municipio).append(" ");
        }
        if (localidad != null) {
            sb.append(localidad).append(" ");
        }
        if (codigoPostal != null) {
            sb.append(codigoPostal).append(" ");
        }
        if (referencia != null) {
            sb.append("Ref: ").append(referencia);
        }
        return sb.toString().trim();
    }

}