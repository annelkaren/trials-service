package mx.gob.pjpuebla.trials.workflow.documentos.promocionesSinExpediente;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoService;
import mx.gob.pjpuebla.trials.error.ApiResponse;
import mx.gob.pjpuebla.trials.error.ApiResponseFactory;
import mx.gob.pjpuebla.trials.util.enums.PromocionSinExpedienteEnum;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoService;
import mx.gob.pjpuebla.trials.workflow.documentos.promocionesSinExpediente.records.PromocionSinExpedienteFiltrosRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.promocionesSinExpediente.records.PromocionSinExpedientePageRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.promocionesSinExpediente.records.PromocionSinExpedienteRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.promocionesSinExpediente.records.PromocionSinExpedienteSaveRecord;

@RequiredArgsConstructor
@Service
@Transactional
public class PromocionSinExpedienteService {
    
    private final DocumentoService documentoService;
    private final JuzgadoService juzgadoService;
    private final PromocionSinExpedienteRepository promocionSinExpedienteRepository;

    @Transactional
    public Page<PromocionSinExpedientePageRecord> getAll(
        PromocionSinExpedienteFiltrosRecord filtros,
        Pageable pageable) {

        return promocionSinExpedienteRepository.getAll(pageable);
    }

    @Transactional
    public ApiResponse<PromocionSinExpedienteSaveRecord> save(PromocionSinExpedienteRecord promocion){
        
        
        Juzgado juzgado = juzgadoService.findJuzgadoById(promocion.juzgadoId());
        PromocionSinExpediente promocionSinExp = new PromocionSinExpediente()
        .setFolio(documentoService.getFolio("P"))
        .setExpediente(promocion.expediente() + "/" + promocion.year())
        .setJuzgado(juzgado)
        .setTipoJuicio(null)
        .setAnexos(String.join(", ", promocion.anexos()))
        .setEstado(PromocionSinExpedienteEnum.REGISTRADO)
        .setTipoRegistro(null)
        .setTipoPromocion(promocion.tipoPromocion());

        promocionSinExp = promocionSinExpedienteRepository.save(promocionSinExp);
        PromocionSinExpedienteSaveRecord saveRecord = new PromocionSinExpedienteSaveRecord(
            promocionSinExp.getId(),
            "La promoción ha sido registrada exitosamente"
        );
        return ApiResponseFactory.success("La promoción ha sido registrada exitosamente", saveRecord);
    }
    
    public PromocionSinExpediente findById(Integer id){
        return promocionSinExpedienteRepository.findById(id).orElse(null);
    }
}
