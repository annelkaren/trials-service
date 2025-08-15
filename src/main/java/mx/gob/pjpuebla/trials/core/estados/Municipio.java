package mx.gob.pjpuebla.trials.core.estados;

import com.fasterxml.jackson.annotation.JsonAlias;

public class Municipio {

    private String id;
    private String stateId;
    private String name;

    @JsonAlias("cve_agem")
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
