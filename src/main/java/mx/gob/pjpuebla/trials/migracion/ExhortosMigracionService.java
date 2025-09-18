package mx.gob.pjpuebla.trials.migracion;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.migracion.readers.exhortoCapital.ExhortoCapitalMigracionRecordSave;
import mx.gob.pjpuebla.migracion.readers.exhortoForaneo.ExhortoForaneoMigracionRecordSave;
import mx.gob.pjpuebla.migracion.readers.exhortoForaneo.ExhortoSalidaMigracionRecordSave;
import mx.gob.pjpuebla.migracion.readers.exhortoForaneo.ExhortoSalidaMigracionRecordSave2;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.carpetadetalle.CarpetaDetalle;
import mx.gob.pjpuebla.trials.workflow.carpeta.carpetadetalle.CarpetaDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;

@Service
@RequiredArgsConstructor
public class ExhortosMigracionService {

    private final CarpetaRepository carpetaRepository;
    private final TipoJuicioRepository tipoJuicioRepository;
    private final JuzgadoRepository juzgadoRepository;
    private final DocumentoRepository documentoRepository;
    private final AnexoRepository anexoRepository;
    private final CarpetaDetalleRepository carpetaDetalleRepository;

    public void createExhortoCapital(ExhortoCapitalMigracionRecordSave exhortoRecord) {
        TipoJuicio tipoJuicio = tipoJuicioRepository.findByNombreIgnoreCase("EXHORTO")
                .orElseThrow(() -> new NotFoundException(
                        "Tipo de juicio no encontrado con nombre: Exhorto",
                        "EXHORTO"));

        Juzgado juzgadoExhortos = juzgadoRepository.findByNombreIgnoreCaseAndContaining("EXHORTOS")
                .orElseThrow(() -> new NotFoundException(
                        "Juzgado con nombre : Exhorto, no ha sido encontrado",
                        "EXHORTO"));

        Carpeta exhorto = new Carpeta()
                .setEstatus(EstadoCarpeta.MIGRADO)
                .setFolio(exhortoRecord.folio())
                .setTipoCarpeta(TipoCarpeta.EXHORTO)
                .setTipoJuicio(tipoJuicio)
                .setJuzgado(juzgadoExhortos)
                .setExpediente(exhortoRecord.expediente())
                .setSelloEstatus(SelloEstatus.VALIDO)
                .setFechaAsignacion(exhortoRecord.fechaAsignacion().atStartOfDay());

        exhorto = carpetaRepository.save(exhorto);

        DocumentoData data = new DocumentoData()
                .setExhortoObservaciones(exhortoRecord.observaciones())
                .setExhortoProcedencia(exhortoRecord.procedencia());

        Documento documento = new Documento()
                .setData(data)
                .setCarpeta(exhorto)
                .setTipoDocumento(TipoDocumento.EXHORTO)
                .setFechaAsignacion(exhortoRecord.fechaAsignacion().atStartOfDay());

        documento = documentoRepository.save(documento);
        addAnexos(exhortoRecord.anexos(), documento);
        carpetaDetalleRepository.save(new CarpetaDetalle().setCarpeta(exhorto));
    }

    //asociado a un expediente.
    public void createExhortoSalida(ExhortoSalidaMigracionRecordSave2 exhortoRecord){
        DocumentoData data = new DocumentoData()
            .setTramite(exhortoRecord.tramite())
            .setDestino(exhortoRecord.destino())
            .setExhortoObservaciones(exhortoRecord.observaciones())
            .setFechaDevolucion(exhortoRecord.fechaDevolucion());

        Documento documento = new Documento()
            .setData(data)
            .setCarpeta(exhortoRecord.carpeta())
            .setFolio(exhortoRecord.folio())
            .setTipoDocumento(TipoDocumento.EXHORTO_SALIDA);
        
        documento = documentoRepository.save(documento);

        //TODO: ver como guardar documento digitalizado si aplica 
    }


    private void addAnexos(List<String> anexos, Documento documento) {
        if (anexos != null && !anexos.isEmpty()) {
            for (String anexo : anexos) {
                Anexo entity = new Anexo();
                entity.setNombre(anexo);
                entity.setDocumento(documento);
                anexoRepository.save(entity);
            }
        }
    }

}
