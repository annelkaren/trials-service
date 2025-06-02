package mx.gob.pjpuebla.trials.core.tipopartes;

import com.fasterxml.jackson.annotation.JsonInclude;
import mx.gob.pjpuebla.trials.core.personas.PersonaRecord;
import mx.gob.pjpuebla.trials.core.roles.RoleRecord;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TipoPartesRecord(
        Integer id,
        String nombre,
        String tipoJuicio
) implements Serializable {

    public TipoPartesRecord hideNames() {
        String newName = "";
        String[] names = nombre().split(" ");
        for (String name : names) {
            String name1 = name.substring(0, 2);
            String wildcard = "";
            for (int i = 2; i < name.length(); i++) {
                wildcard += "*";
                if(i == name.length() -1){
                    wildcard += " ";
                }
            }
            newName += name1 + wildcard;
        }
        return new TipoPartesRecord(id(), newName, tipoJuicio());
    }
}
