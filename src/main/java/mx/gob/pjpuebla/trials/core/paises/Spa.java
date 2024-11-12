package mx.gob.pjpuebla.trials.core.paises;

import com.fasterxml.jackson.annotation.JsonAlias;

public class Spa {
    private final String official;
    private final String common;

    public Spa(String official, String common) {
        this.official = official;
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
