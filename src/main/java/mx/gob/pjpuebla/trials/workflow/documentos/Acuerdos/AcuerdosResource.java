package mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos.records.AcuerdoPromocionesRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos.records.AcuerdoRecord;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow")
@SecurityRequirement(name = "Keycloak")
public class AcuerdosResource {

    private final AcuerdosService acuerdosService;

    @PostMapping("/documentos/crearAcuerdo")
    public Documento crearAcuerdo(@RequestBody AcuerdoRecord acuerdo) {
        return acuerdosService.save(acuerdo);
    }

    @GetMapping("/documentos/obtenerPromociones/{carpetaId}")
    public List<AcuerdoPromocionesRecord> obtenerPromociones(@PathVariable Integer carpetaId){
        return acuerdosService.obtenerPromociones(carpetaId);
    }



}
