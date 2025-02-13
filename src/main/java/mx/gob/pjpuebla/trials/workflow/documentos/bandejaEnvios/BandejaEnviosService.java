package mx.gob.pjpuebla.trials.workflow.documentos.bandejaEnvios;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.bandejaEnvios.records.BandejaEnviosRecord;

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

}
