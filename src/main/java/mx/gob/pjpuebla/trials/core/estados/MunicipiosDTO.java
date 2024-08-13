package mx.gob.pjpuebla.trials.core.estados;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.util.List;

@Data
public class MunicipiosDTO {

    private List<MunicipioDTO> datos;
}

class MunicipioDTO {

    private String id;
    private String stateId;
    private String name;

    @JsonAlias("cvegeo")
    public String getId() {
        return id;
    }

    @JsonAlias("cve_agee")
    public String getStateId() {
        return stateId;
    }

    @JsonAlias("nom_agem")
    public String getName() {
        return name;
    }
}