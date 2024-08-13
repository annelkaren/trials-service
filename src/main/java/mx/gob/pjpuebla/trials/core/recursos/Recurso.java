package mx.gob.pjpuebla.trials.core.recursos;

import lombok.Data;

import java.util.Set;

@Data
public class Recurso {

    private String id;

    private String name;

    private Set<String> uris;

    private String scopes;

    private String type;

    private String displayName;
}
