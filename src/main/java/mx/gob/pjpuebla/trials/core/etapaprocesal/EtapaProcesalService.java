package mx.gob.pjpuebla.trials.core.etapaprocesal;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.etapaprocesal.record.EtapaProcesalRecord;
import mx.gob.pjpuebla.trials.core.etapaprocesal.record.ListEtapaProcesalRecord;
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
   public List<ListEtapaProcesalRecord> getEtapaProcesal(Integer idTipoJuicio, Integer idProcedimiento) {
      TipoJuicio tipoJuicio = tipoJuicioRepository.getMateriaAndTipoSistemaById(idTipoJuicio)
              .orElseThrow(() -> new NotFoundException("Tipo de Juicio no encontrado", "tipoJuicioId"));

      List<EtapaProcesalRecord> listAmbos = etapaProcesalRepository.getListEtapaProcesalByTipoJuicioAndProcedimiento(
              tipoJuicio.getMateria().getId(),
              (idProcedimiento == null || idProcedimiento == 0) ? null : idProcedimiento
      );

      List<EtapaProcesalRecord> listFiltrada = listAmbos.stream()
              .filter(etapa -> etapa.idtipoSistema() == null)
              .toList();

      List<EtapaProcesalRecord> resultList = listFiltrada.isEmpty()
              ? listAmbos.stream()
              .filter(etapa -> tipoJuicio.getTipoSistema().getId().equals(etapa.idtipoSistema()))
              .toList()
              : listFiltrada;

      return resultList.stream()
              .map(etapa -> new ListEtapaProcesalRecord(etapa.id(), etapa.etapaProcesal()))
              .toList();
   }


}
