package mx.gob.pjpuebla.trials.core.instituciones;

import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoRecord;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRecord;
import mx.gob.pjpuebla.trials.util.enums.Estado;

public class InstitucionSetUp {

    private InstitucionSetUp() {

    }

    public static Institucion createInstitucion(Estado estado) {

        return new Institucion()
                .setId(1)
                .setNombre("institución prueba")
                .setTelefono("1234567894")
                .setExtension("12341")
                .setVersion(0)
                .setEstado(estado);
    }

    public static InstitucionRecord createInstitucionRecord() {
        return new InstitucionRecord(1, "institución prueba", "domicilio 1", "1234121212", "Externa");
    }

    public static InstitucionRecordResponse createInstitucionRecordResponse() {
        Domicilio dom = DomicilioSetUp.createDomicilio();
        DomicilioRecord domRecord = new DomicilioRecord(dom.getId(), dom.getCalle(), dom.getExterior(),
                dom.getInterior(), dom.getEstadoRepublica(), dom.getMunicipio(), dom.getLocalidad(), dom.getColonia(),
                dom.getCodigoPostal(), dom.getReferencia());

       // Distrito dis = DistritoSetUp.createDistrito();
       // DistritoRecord disRecord = new DistritoRecord(dis.getId(), dis.getNombre());

        return new InstitucionRecordResponse(
                1,
                0,
                "institución prueba",
                Estado.ACTIVE,
                "1234562323",
                "1212",
                "Externa",
                domRecord);
    }
}
