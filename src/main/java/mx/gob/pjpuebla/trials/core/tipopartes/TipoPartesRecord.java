package mx.gob.pjpuebla.trials.core.tipopartes;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TipoPartesRecord(
        Integer id,
        String nombre,
        String tipoJuicio
) implements Serializable {

    public TipoPartesRecord hideNames() {
        StringBuilder newName = new StringBuilder();
        String[] names = nombre().split(" ");
        for (String name : names) {

            if (name.length() < 3) {
                continue;
            }

            String name1 = name.substring(0, 2);
            StringBuilder wildcard = new StringBuilder();
            for (int i = 2; i < name.length(); i++) {
                wildcard.append("*");
                if(i == name.length() -1){
                    wildcard.append(" ");
                }
            }
            newName.append(name1).append(wildcard);
        }
        return new TipoPartesRecord(id(), newName.toString(), tipoJuicio());
    }
}
