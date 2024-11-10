package mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos.records.AcuerdoPromocionesRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos.records.AcuerdoRecord;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow")
@SecurityRequirement(name = "Keycloak")
public class AcuerdosResource {

    private final AcuerdosService acuerdosService;

    @PostMapping("/documentos/crearAcuerdo")
    public Integer crearAcuerdo(@RequestBody AcuerdoRecord acuerdo) {
        System.out.println("IMRPIIWNSO VARIABLES");

        System.out.println("carpeta id: " + acuerdo.carpetaId());
        System.out.println("carpeta id: " + acuerdo.documentoId());
        System.out.println("carpeta id: " + acuerdo.tipoAcuerdo());
        System.out.println("carpeta id: " + acuerdo.fechaResolucion());
        System.out.println("carpeta id: " + acuerdo.etapaProcesal());
        for (String rubro : acuerdo.rubros()) {
            System.out.println("Rubro 1: " + rubro);
        }

        for (AcuerdoPromocionesRecord promocion: acuerdo.promocionesRelacionadas()) {
            System.out.println("promocion: " + promocion.id());
        }

        System.out.println(acuerdo.tamanioPapel());
        System.out.println(acuerdo.textoEditor());
        
        
        return acuerdosService.save(acuerdo);
    }

    @GetMapping("/documentos/obtenerPromociones/{carpetaId}")
    public List<AcuerdoPromocionesRecord> obtenerPromociones(@PathVariable Integer carpetaId){
        return acuerdosService.obtenerPromociones(carpetaId);
    }



}
