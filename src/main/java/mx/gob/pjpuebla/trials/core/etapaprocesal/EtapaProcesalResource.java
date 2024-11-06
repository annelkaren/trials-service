package mx.gob.pjpuebla.trials.core.etapaprocesal;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.etapaprocesal.record.ListEtapaProcesalRecord;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/etapaprocesal")
@SecurityRequirement(name = "Keycloak")
public class EtapaProcesalResource {

    private final EtapaProcesalService etapaProcesalService ;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ListEtapaProcesalRecord>  getAll(
            @RequestParam(value = "IdTipoJuicio", required = false) Integer idTipoJuicio,
             @RequestParam(value = "IdProcedimiento", required = false) Integer idProcedimiento
    ){
        return etapaProcesalService.getEtapaProcesal( idTipoJuicio, idProcedimiento);
    }
}
