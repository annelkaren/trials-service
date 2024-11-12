package mx.gob.pjpuebla.trials.core.paises;

import com.fasterxml.jackson.annotation.JsonAlias;

public class Pais {

    private Name name;
    private String codeAlpha2;
    private String codeNumeric;
    private Translations translations;

    public Pais(Name name, String codeAlpha2, String codeNumeric, Translations translations) {
        this.name = name;
        this.codeAlpha2 = codeAlpha2;
        this.codeNumeric = codeNumeric;
        this.translations = translations;
    }

    @JsonAlias("name")
    public Name getName() {
        return name;
    }

    @JsonAlias("cca2")
    public String getCodeAlpha2() {
        return codeAlpha2;
    }

    @JsonAlias("ccn3")
    public String getCodeNumeric() {
        return codeNumeric;
    }

    @JsonAlias("translations")
    public Translations getTranslations() {
        return translations;
    }


}