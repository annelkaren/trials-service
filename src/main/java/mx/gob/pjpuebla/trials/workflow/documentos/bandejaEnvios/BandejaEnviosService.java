package mx.gob.pjpuebla.trials.workflow.documentos.bandejaEnvios;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.bandejaEnvios.records.BandejaEnvioRecordResponse;
import mx.gob.pjpuebla.trials.workflow.documentos.bandejaEnvios.records.BandejaEnviosRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalle;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@Transactional
@RequiredArgsConstructor
@Service
public class BandejaEnviosService {
    
    private final DocumentoRepository documentoRepository;


    @Transactional(readOnly = true)
    public Page<BandejaEnviosRecord> getAllBandejaEnviados(String key, Pageable pageable){
        key = (key != null) ? key.toLowerCase() : "";
                List<BandejaEnviosRecord> response = List.of();

                List<DocumentoDetalle> records = documentoRepository.findAllOficiosBandejaSalida(key);
                if(records.size() > 0){
                    response = records.stream()
                    .filter(doc -> doc.getDocumento().getData().getTipoOficio() != null && "Jurisdiccional".equals(doc.getDocumento().getData().getTipoOficio()))
                    .map(doc -> {
                        Documento documento = doc.getDocumento();
                        return new BandejaEnviosRecord(
                            documento.getId(),
                            documento.getFolio(),
                            documento.getInstitucion().getNombre(),
                            documento.getCarpeta().getJuzgado().getNombre(),
                            doc.getPersona() != null ?
                                doc.getPersona().getNombre() + " " + doc.getPersona().getApellidoPaterno() + " " + doc.getPersona().getApellidoMaterno()
                            : "Sin asignar",
                            documento.getEstatus().equals(EstadoCarpeta.CREADO) ? "CREADO" : doc.getEstadoEnvio().getEtiqueta()
                        );
                    })
                    .collect(Collectors.toList());
                }
               

        return new PageImpl<>(response, pageable, records.size());
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
