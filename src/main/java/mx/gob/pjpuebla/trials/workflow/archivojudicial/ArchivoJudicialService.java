package mx.gob.pjpuebla.trials.workflow.archivojudicial;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.bandejas.BandejaRepository;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.entrada.BandejaEntradaFilter;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.entrada.BandejaEntradaResponse;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class ArchivoJudicialService {

    private final DocumentoRepository documentoRepository;
    private final BandejaRepository bandejaRepository;
    private final CarpetaRepository carpetaRepository;
    private final MovimientoService movimientoService;
    private final PersonaService personaService;

    public Page<ArchivoJudicialRecord> getAll(Pageable pageable) {
        return documentoRepository.findArchivoJudicialList(pageable)
                .map(p -> new ArchivoJudicialRecord(
                        p.getId(),
                        p.getJuzgado(),
                        p.getTipo(),
                        obtenerEtiquetaTipo(p.getTipo(), p.getTipoId()),
                        p.getExpediente(),
                        p.getFechaAlta(),
                        p.getAnexos(),
                        p.getPaqueteId()
                ));
    }

    public Page<RecibidosRecord> getRecibidos(Pageable pageable) {
        return documentoRepository.findArchivoJudicialRecibidos(pageable)
                .map(p -> new RecibidosRecord(
                        p.getId(),
                        p.getJuzgado(),
                        p.getExpediente(),
                        p.getActorPrincipal(),
                        p.getDemandadoPrincipal(),
                        obtenerEtiquetaTipo(p.getTipo(), p.getTipoId()),
                        p.getFechaRecepcion(),
                        p.getPaqueteId()
                ));
    }

    public Page<SolicitudesRecord> getSolicitudes(Pageable pageable) {
        return documentoRepository.findArchivoJudicialSolicitudes(pageable)
                .map(p -> new SolicitudesRecord(
                        p.getId(),
                        p.getJuzgado(),
                        p.getExpediente(),
                        p.getActorPrincipal(),
                        p.getDemandadoPrincipal(),
                        obtenerEtiquetaTipo(p.getTipo(), p.getTipoId()),
                        p.getFechaSolicitud(),
                        p.getUrgente(),
                        calcularDiasRestantes(p.getFechaSolicitud())
                ));
    }

    public static String calcularDiasRestantes(LocalDateTime fechaSolicitud) {
        if (fechaSolicitud == null) {
            return "N/A";
        }

        LocalDateTime hoy = LocalDateTime.now();
        long dias = ChronoUnit.DAYS.between(hoy.toLocalDate(), fechaSolicitud.toLocalDate());

        return String.valueOf(Math.max(dias, 0));
    }

    public Page<BandejaEntradaResponse> getHistorico(BandejaEntradaFilter filter, Pageable pageable) {
        return bandejaRepository.findArchivoJudicialHistorial(pageable, List.of("ARCHIVO_JUDICIAL", "ARCHIVO_JUDICIAL_RECIBIDO", "ARCHIVO_JUDICIAL_SOLICIT"), filter);
    }

    public String recibirExpedientes(List<RecibirExpedienteRecord> list) {
        Persona auditor = personaService.getAuditor();
        Integer counter = 0;
        for (RecibirExpedienteRecord record : list) {
            if (record.tipo().toUpperCase().contains("CARPETA")) {
                Carpeta carpeta = carpetaRepository.findById(record.id()).orElse(null);
                if (carpeta != null) {
                    counter += 1;
                    carpeta.setEstatus(EstadoCarpeta.ARCHIVO_JUDICIAL_RECIBIDO);
                    carpetaRepository.save(carpeta);
                    movimientoService.createMovimento(carpeta, null, auditor, null, EstadoCarpeta.ARCHIVO_JUDICIAL_RECIBIDO.name());
                }
            }
            if (record.tipo().toUpperCase().contains("DOCUMENTO")) {
                Documento documento = documentoRepository.findById(record.id()).orElse(null);
                if (documento != null) {
                    counter += 1;
                    documento.setEstatus(EstadoCarpeta.ARCHIVO_JUDICIAL_RECIBIDO);
                    documentoRepository.save(documento);
                    movimientoService.createMovimento(null, documento, auditor, null, EstadoCarpeta.ARCHIVO_JUDICIAL_RECIBIDO.name());
                }
            }
        }
        return "Recibido(s) " + counter + " expediente(s) de " + list.size() + " seleccionado(s).";
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
