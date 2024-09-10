package mx.gob.pjpuebla.trials.core.sello;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Sello {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String juzgado;
    private String expediente;
    private String folio;
    private String fechaHora;
    private List<String> listanexos1;
    private String listanexos;
    private String nombreEntidad;
    private String capturista;
}
