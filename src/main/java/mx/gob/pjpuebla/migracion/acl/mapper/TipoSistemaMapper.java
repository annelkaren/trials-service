package mx.gob.pjpuebla.migracion.acl.mapper;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.migracion.TipoSistemaMigracionService;

@Slf4j
@Component
@RequiredArgsConstructor
public class TipoSistemaMapper {

    private final TipoSistemaMigracionService tipoSistemaMigracionService;

    public TipoSistema mapTipoSistema(String materiaCodigo, String juzgadoCodigo) {
        // SI el juzgado tiene una materia F(Familiar) y ademas el juzgado tiene clade
        // entre el 9000 y el 9900 es de un sistema oral:
        if (materiaCodigo.equals("F") &&
                (Integer.parseInt(juzgadoCodigo) >= 9000
                        && Integer.parseInt(juzgadoCodigo) < 9009)) {
            return tipoSistemaMigracionService.findTipoSistemaByNombre("Oral");
        }

        // Si el juzgado tiene una materia X (Mixta) es un sistema mixto.
        if (materiaCodigo.equals("X")) {
            return tipoSistemaMigracionService.findTipoSistemaByNombre("Mixto");
            
        }

        return tipoSistemaMigracionService.findTipoSistemaByNombre("Tradicional");
        
    }

}
