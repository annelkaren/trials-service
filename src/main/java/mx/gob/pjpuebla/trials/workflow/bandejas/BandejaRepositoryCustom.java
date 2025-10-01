package mx.gob.pjpuebla.trials.workflow.bandejas;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.annotation.Nullable;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.entrada.BandejaEntradaFilter;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.entrada.BandejaEntradaResponse;

public interface BandejaRepositoryCustom {

    Page<BandejaEntradaResponse> findBandejaEntradas(
            Pageable pageable,
            List<String> estados,
            @Nullable BandejaEntradaFilter filtro
    );
}