package mx.gob.pjpuebla.migracion.readers.domicilio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import net.jcip.annotations.Immutable;

@Data
@Table(name = "domicilio")
@Entity
@Immutable
public class DomicilioMigracion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dom")
    private Integer idDom;

    private String cu;

    @Column(name = "cu_actor")
    private String cuActor;

    private Integer cp;
    private String estado;
    private String municipio;
    private String ciudad;
    private String colonia;
    private String calle;
    private String numex;
    private String numin;
    private Float latitud;
    private Float longitud;
    private String estatus; 

}
