package mx.gob.pjpuebla.migracion.readers.usuario;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "usuario")
@Data
public class UsuarioMigracion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer clave;

    private Integer idusuario;
    private String correo;
    private String passwd;
    private LocalDate fec_reg;
    private String juz_reg;
    private String usu_reg;
    private String estatus;
    private String nombre;

}
