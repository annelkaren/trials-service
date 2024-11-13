package mx.gob.pjpuebla.trials.core.paises;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;

public class Translations {
    private final Spa spa;

    @JsonCreator
    public Translations(Spa spa) {
        this.spa = spa;
    }

    @JsonAlias("spa")
    public Spa getSpa() {
        return spa;
    }
}
