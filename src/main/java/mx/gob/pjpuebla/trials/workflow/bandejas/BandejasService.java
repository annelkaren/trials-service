package mx.gob.pjpuebla.trials.workflow.bandejas;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.entrada.BandejaEntradaFilter;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.entrada.BandejaEntradaResponse;

@RequiredArgsConstructor
@Slf4j
@Service
public class BandejasService {

     private final BandejaRepository bandejaRepo;

    // Estados por defecto de la bandeja (los de tu JPQL)
    private static final List<String> DEFAULT_ESTADOS_BANDEJA_ENTRADA = List.of("CAPTURA", "EDICION", "DEVUELTO_A_OFICIALIA");

    /**
     * Orquesta la consulta de la bandeja de entrada.
     * - Aplica filtros (record BandejaEntradaFilter)
     * - Aplica orden global según Pageable.getSort()
     * - Aplica paginación
     */
    public Page<BandejaEntradaResponse> listarBandejaEntrada(BandejaEntradaFilter filter, Pageable pageable) {
        return bandejaRepo.findBandejaEntradas(pageable, DEFAULT_ESTADOS_BANDEJA_ENTRADA, filter);
    }


}
