package mx.gob.pjpuebla.trials.statistics.reports;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final CarpetaRepository carpetaRepository;

    public List<ReporteDatesRecord> getDatesByMateria() {
        List<ReporteDatesRecord> response = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        List<ReporteDateRecord> list = reporteRepository.getAllKeys();
        for (ReporteDateRecord record : list) {
            ExtraData data = mapper.convertValue(record.extraData(), ExtraData.class);
            if (data.getMaterias() != null && !data.getMaterias().isEmpty()) {
                if (data.getJuiciosExcluidos() == null || data.getJuiciosExcluidos().isEmpty()) {
                    data.setJuiciosExcluidos(Arrays.asList(0));
                }
                ReporteDatesRecord result = new ReporteDatesRecord(record.reporte(), carpetaRepository.getDatesByMateria(data.getMaterias(), data.getJuiciosExcluidos()));
                response.add(result);
            } else if (data.getTipoJuicios() != null && !data.getTipoJuicios().isEmpty()) {
                ReporteDatesRecord result = new ReporteDatesRecord(record.reporte(), carpetaRepository.getDatesByTipoJuicio(data.getTipoJuicios()));
                response.add(result);
            }
        }
        return response;
    }
}
