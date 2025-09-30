package mx.gob.pjpuebla.trials.workflow.bandejas;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.entrada.BandejaEntradaResponse;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoService;

@RequiredArgsConstructor
@Slf4j
@Service
public class BandejasService {

    private final MovimientoService movimientoService;
    //bandeja entrada:
    public Page<BandejaEntradaResponse> listarBandejaEntrada(Pageable paggeable){
        return movimientoService.getBandejaEntrada(paggeable);
    }

}
