package mx.gob.pjpuebla.trials.core.oficialias;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.sedes.SedeRecordResponse;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialia;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaRecord;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.enums.Estado;

import java.time.LocalDateTime;

public class OficialiaSetUp {

    private OficialiaSetUp() {
    }

    public static Oficialia createOficialia(TipoOficialia tipoOficialia, Sede sede) {
        Oficialia oficialia = new Oficialia()
                .setId(1)
                .setVersion(0)
                .setEstado(Estado.ACTIVE)
                .setTipoOficialia(tipoOficialia)
                .setNombre("Común")
                .setResponsable("Responsable")
                .setSede(sede);
        oficialia.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7", "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return oficialia;
    }

    public static OficialiaRecord createOficialiaRecord(Oficialia oficialia, TipoOficialiaRecord tipo, SedeRecordResponse sede) {
        return new OficialiaRecord(oficialia.getId(), oficialia.getVersion(), oficialia.getNombre(), oficialia.getResponsable(), Estado.ACTIVE, tipo, sede);
    }

    public static OficialiaRecordResponse createOficialiaRecordResponse(Oficialia oficialia) {
        return new OficialiaRecordResponse(oficialia.getId(), oficialia.getNombre());
    }

    public static OficialiaMateriaRecord CreateOficialiaMateriaRecord(Oficialia oficialia, Materia materia, Sede sede, TipoOficialia tipoOficialia, Juzgado juzgado) {
        return  new OficialiaMateriaRecord(oficialia.getId(), oficialia.getNombre(), oficialia.getEstado(), materia.getNombre(), materia.getId(), sede.getId(), tipoOficialia.getNombre(), tipoOficialia.getId(), juzgado.getNombre(), juzgado.getId() );
    }
}
