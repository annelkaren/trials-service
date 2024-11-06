package mx.gob.pjpuebla.trials.core.etapaprocesal;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.etapaprocesal.record.EtapaProcesalRecord;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EtapaProcesalService {

   private final  EtapaProcesalRepository etapaProcesalRepository;
   private final TipoJuicioRepository tipoJuicioRepository;

   @Transactional(readOnly = true)
   public List<EtapaProcesalRecord>  getEtapaProcesal(Integer idTipoJuicio, Integer idProcedimiento){
      TipoJuicio tipoJuicio = tipoJuicioRepository.getMateriaAndTipoSistemaById(idTipoJuicio)
              .orElseThrow(() -> new NotFoundException("Tipo de Juicio no encontrado", "tipoJuicioId"));

      return etapaProcesalRepository.getListEtapaProcesalByTipoJuicioAndProcedimiento(
              tipoJuicio.getMateria().getId(),
              tipoJuicio.getTipoSistema().getId(),
              (idProcedimiento == null || idProcedimiento == 0) ? null : idProcedimiento
      );

   }
}
