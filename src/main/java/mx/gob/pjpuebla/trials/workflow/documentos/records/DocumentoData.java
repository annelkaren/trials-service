package mx.gob.pjpuebla.trials.workflow.documentos.records;

import lombok.Data;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioDemandasRecord;
import mx.gob.pjpuebla.trials.util.enums.TipoPromocion;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
public class DocumentoData implements Serializable {

    private List<TipoJuicioDemandasRecord> tiposJuicios;
    private Integer tieneAbogado;
    private String nombreAbogado;
    private String cedulaAbogado;
    private String correoAbogado;
    private String domicilio;
    private TipoPromocion tipoPromocion;
    private String exhortoObservaciones;
    private String exhortoProcedencia;
    private String apelacionOtroActorNombre;
    private String apelacionOtroDemandadoNombre;
    private String apelacionAntecedenteCarpeta;
    private String tipoOficio;
    private LocalDate fechaEmision;
    private List<String> rubros;
}

