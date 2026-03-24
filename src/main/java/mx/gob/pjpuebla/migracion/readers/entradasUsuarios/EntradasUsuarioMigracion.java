package mx.gob.pjpuebla.migracion.readers.entradasUsuarios;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "entradasusuario")
public class EntradasUsuarioMigracion {
      
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer clave;

    private Integer identradasUsuario;
    private Integer idusuario;
    private String cuEntradas;
    private String tipo;
    private String otorgo;
    private String tipoparte;

    @Column(name = "clave_act")
    private String claveActor;
    private String estatus;

}
