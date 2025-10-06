package mx.gob.pjpuebla.trials.workflow.bandejas;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.entrada.BandejaEntradaFilter;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.entrada.BandejaEntradaResponse;

@RequiredArgsConstructor
@Slf4j
@Service
public class BandejasService {

    private final BandejaRepository bandejaRepo;
    private final PersonaService personaService;

    // Estados por defecto de la bandeja (los de tu JPQL)
    private static final List<String> DEFAULT_ESTADOS_BANDEJA_ENTRADA = List.of("CAPTURA", "EDICION", "DEVUELTO_A_OFICIALIA");
    private static final List<String> DEFAULT_ESTADOS_BANDEJA_SALIDA = List.of("SALIDA");

    /**
     * Orquesta la consulta de la bandeja de entrada.
     * - Aplica filtros (record BandejaEntradaFilter)
     * - Aplica orden global según Pageable.getSort()
     * - Aplica paginación
     */
    public Page<BandejaEntradaResponse> listarBandejaEntrada(BandejaEntradaFilter filter, Pageable pageable) {
        Persona currentUser = personaService.getAuditor();
        Integer juzgadoId = getJuzgadoId(currentUser);
        Integer oficialiaId = getOficialiaId(currentUser);
        return bandejaRepo.findBandejaEntradas(pageable, DEFAULT_ESTADOS_BANDEJA_ENTRADA, filter, juzgadoId, oficialiaId);
    }

        /**
     * Orquesta la consulta de la bandeja de entrada.
     * - Aplica filtros (record BandejaEntradaFilter)
     * - Aplica orden global según Pageable.getSort()
     * - Aplica paginación
     */
    public Page<BandejaEntradaResponse> listarBandejaSalida(BandejaEntradaFilter filter, Pageable pageable) {
        Persona currentUser = personaService.getAuditor();
        Integer juzgadoId = getJuzgadoId(currentUser);
        Integer oficialiaId = getOficialiaId(currentUser);
        return bandejaRepo.findBandejaSalida(pageable, DEFAULT_ESTADOS_BANDEJA_SALIDA, filter, juzgadoId, oficialiaId);
    }

    private Integer getJuzgadoId(Persona currentUser) {
        return (currentUser.getJuzgado() != null) ? currentUser.getJuzgado().getId() : null;
    }

    private Integer getOficialiaId(Persona currentUser) {
        return (currentUser.getOficialia() != null) ? currentUser.getOficialia().getId() : null;
    }

}
