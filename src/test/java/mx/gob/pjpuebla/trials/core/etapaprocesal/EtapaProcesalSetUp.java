package mx.gob.pjpuebla.trials.core.etapaprocesal;

import mx.gob.pjpuebla.trials.core.etapaprocesal.record.EtapaProcesalRecord;
import mx.gob.pjpuebla.trials.core.etapaprocesal.record.ListEtapaProcesalRecord;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.procedimientos.Procedimiento;
import mx.gob.pjpuebla.trials.core.rubros.Rubro;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaSetUp;
import mx.gob.pjpuebla.trials.util.Audit;

import java.time.LocalDateTime;

public class EtapaProcesalSetUp {

    private EtapaProcesalSetUp(){
    }

    public  static EtapaProcesal createEtapaProcesal(){
        EtapaProcesal etapaProcesal = new EtapaProcesal()
                .setId(1)
                .setNombre("Apelación / Amparo directo")
                .setVersion(0)
                .setRubro(new Rubro())
                .setMateria(MateriaSetUp.createMateria())
                .setProcedimiento(new Procedimiento())
                .setTipoJuicio(TipoJuicioSetUp.createTipoJuicio())
                .setTipoSistema(TipoSistemaSetUp.createTipoSistema());
                etapaProcesal.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7", "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return etapaProcesal;
    }

    public static EtapaProcesalRecord createEtapaProcesalRecord(){
        return new EtapaProcesalRecord(
                1,
                "Apelación / Amparo directo",
                10,
                "",
                null,
                200
        );
    }

    public static ListEtapaProcesalRecord createListEtapaProcesalRecord(){
        return new ListEtapaProcesalRecord(
                1,
                "Apelación / Amparo directo"
        );
    }
    public static EtapaProcesalRecord createEtapaProcesalRecordCase2(){
        return new EtapaProcesalRecord(
                1,
                "Apelación / Amparo directo",
                10,
                "",
                1,
                200
        );
    }
}
