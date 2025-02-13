package mx.gob.pjpuebla.trials.workflow.documentos.bandejaEnvios;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.bandejaEnvios.records.BandejaEnvioRecordResponse;
import mx.gob.pjpuebla.trials.workflow.documentos.bandejaEnvios.records.BandejaEnviosRecord;

import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Transactional
@RequiredArgsConstructor
@Service
public class BandejaEnviosService {
    
    private final DocumentoRepository documentoRepository;


    @Transactional(readOnly = true)
    public Page<BandejaEnviosRecord> getAllBandejaEnviados(String key, Pageable pageable){
        key = (key != null) ? key.toLowerCase() : "";

        return documentoRepository.findAllOficiosBandejaSalida(pageable, key);
    }

    public BandejaEnvioRecordResponse actualizarEstatusOficio(Integer estatus, Integer documentoId){
        
        Documento documento = documentoRepository.findById(documentoId)
            .orElseThrow(() -> new NotFoundException("Documento no encontrado", "documentoId: " + documentoId.toString()));
        
        EstadoCarpeta estadoCarpeta = EstadoCarpeta.values()[estatus];
        
        documento.setEstatus(estadoCarpeta);
        documentoRepository.save(documento);
        
        return new BandejaEnvioRecordResponse(documentoId, "Estatus actualizado correctamente");
    
    }

}
