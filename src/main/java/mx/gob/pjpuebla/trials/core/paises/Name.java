package mx.gob.pjpuebla.trials.core.paises;

import com.fasterxml.jackson.annotation.JsonAlias;

public class Name {
        private final String common;
        private final String official;

    public  Name(String common, String official ){
        this.common = common;
        this.official = official;
    }
    @JsonAlias("common")
    public String getCommon() {
        return common;
    }

    @JsonAlias("official")
    public String getOfficial() {
        return official;
    }
}


