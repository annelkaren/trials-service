package mx.gob.pjpuebla.trials.core.paises;

import com.fasterxml.jackson.annotation.JsonAlias;

public class Spa {
    private String official;
    private  String common;

    public Spa(String brasil, String common) {
        this.official = brasil;
        this.common = common;
    }

    @JsonAlias("official")
    public String getOfficial() {
        return official;
    }

    @JsonAlias("common")
    public String getCommon() {
        return common;
    }

}
