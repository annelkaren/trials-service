package mx.gob.pjpuebla.trials.workflow.archivojudicial;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.bandejas.BandejaRepository;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.entrada.BandejaEntradaFilter;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.entrada.BandejaEntradaResponse;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class ArchivoJudicialService {

    private final DocumentoRepository documentoRepository;
    private final MovimientoRepository movimientoRepository;
    private final BandejaRepository bandejaRepo;

    public Page<ArchivoJudicialRecord> getAll(Pageable pageable) {
        return documentoRepository.findArchivoJudicialList(pageable)
                .map(p -> new ArchivoJudicialRecord(
                        p.getId(),
                        p.getJuzgado(),
                        p.getTipo(),
                        obtenerEtiquetaTipo(p.getTipo(), p.getTipoId()),
                        p.getExpediente(),
                        p.getFechaAlta(),
                        p.getAnexos()
                ));
    }

    public Page<BandejaEntradaResponse> getHistorico(BandejaEntradaFilter filter, Pageable pageable) {
        return bandejaRepo.findArchivoJudicialHistorial(pageable, List.of("ARCHIVO_JUDICIAL"), filter);
    }

    private String obtenerEtiquetaTipo(String tipo, Integer tipoId) {
        if (tipoId == null) {
            return null;
        }
        return switch (tipo) {
            case "DOCUMENTO" -> obtenerEtiquetaTipoDocumento(tipoId);
            case "CARPETA" -> obtenerEtiquetaTipoCarpeta(tipoId);
            default -> throw new IllegalArgumentException("Tipo desconocido: " + tipo);
        };
    }

    private String obtenerEtiquetaTipoDocumento(Integer tipoId) {
        TipoDocumento[] values = TipoDocumento.values();

        if (tipoId < 0 || tipoId >= values.length) {
            throw new IllegalArgumentException(
                    "TipoDocumento ordinal inválido: " + tipoId
            );
        }

        return values[tipoId].getEtiqueta();
    }

    private String obtenerEtiquetaTipoCarpeta(Integer tipoId) {
        if (tipoId == 0) {
            return "Principal";
        }

        TipoCarpeta[] values = TipoCarpeta.values();

        if (tipoId < 0 || tipoId >= values.length) {
            throw new IllegalArgumentException(
                    "TipoCarpeta ordinal inválido: " + tipoId
            );
        }

        return values[tipoId].getEtiqueta();
    }
}
