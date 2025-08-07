package mx.gob.pjpuebla.trials.statistics.reports;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ExtraData implements Serializable {

    private List<Integer> tipoJuicios;
    private List<Integer> materias;
    private List<Integer> juiciosExcluidos;
}
