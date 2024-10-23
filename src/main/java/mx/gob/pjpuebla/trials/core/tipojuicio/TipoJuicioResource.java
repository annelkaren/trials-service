package mx.gob.pjpuebla.trials.core.tipojuicio;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/tipojuicio")
@SecurityRequirement(name = "Keycloak")
public class TipoJuicioResource {

    private final TipoJuicioService tipoJuicioService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<TipoJuicioRecord> getAll(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(value = "tipoJuicioNombre", required = false) String tipoJuicioNombre,
            @RequestParam(value = "tipoSistemaNombre", required = false) String tipoSistemaNombre,
            @RequestParam(value = "materiaNombre", required = false) String materiaNombre
    ) {

        return tipoJuicioService.getAllActiveByCentroTrabajo(pageable);

    }

    @GetMapping(value = "/oralidad", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TipoJuicioDemandasRecord> getTiposJuiciosOralidad() {
        return tipoJuicioService.getAllTipoJuicios();
    }
    

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TipoJuicioRecord getById(@PathVariable Integer id) {
        return tipoJuicioService.findById(id);
    }

}
