package mx.gob.pjpuebla.trials.core.estados;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.util.List;

@Data
public class EstadosDTO {

    private List<EstadoDTO> datos;

}

class EstadoDTO {

    private String id;
    private String name;
    private String abbreviation;

    @JsonAlias("cvegeo")
    public String getId() {
        return id;
    }

    @JsonAlias("nom_agee")
    public String getName() {
        return name;
    }

    @JsonAlias("nom_abrev")
    public String getAbbreviation() {
        return abbreviation;
    }
}
