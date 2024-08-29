package mx.gob.pjpuebla.trials.core.organismos;


import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.Estado;

import java.time.LocalDateTime;
public class OrganismoSetup {

    private OrganismoSetup(){

    }

    public static Organismo createOrganismo(){
        Organismo organismo = new Organismo()
                .setId(1)
                .setNombre("CONSEJO DE LA JUDICATURA DEL PODER JUDICIAL DEL ESTADO DE PUEBLA")
                .setEstado(Estado.ACTIVE) // setEstado("A")
                .setVersion(1);
                organismo.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7", "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return organismo;
    }

    public static OrganismoRecord createEstadoCivilRecord(){
        return new OrganismoRecord(1, "CONSEJO DE LA JUDICATURA DEL PODER JUDICIAL DEL ESTADO DE PUEBLA");
    }
}
