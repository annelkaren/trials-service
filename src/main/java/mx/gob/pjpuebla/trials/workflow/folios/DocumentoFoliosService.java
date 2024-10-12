package mx.gob.pjpuebla.trials.workflow.folios;

import lombok.AllArgsConstructor;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.oficialias.Oficialia;
import mx.gob.pjpuebla.trials.core.oficialias.OficialiaRepository;
import mx.gob.pjpuebla.trials.core.personas.CentroTrabajoRecord;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.TipoCentroTrabajo;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DocumentoFoliosService {
    private DocumentoFolioRepository documentoFolioRepository;
    private JuzgadoRepository juzgadoRepository;
    private OficialiaRepository oficialiaRepository;

    public Integer getFolio(TipoDocumento tipoDocumento, Juzgado juzgado, Oficialia oficialia) {
        CentroTrabajoRecord centroTrabajo = getCentroTrabajoRecord(juzgado, oficialia);

        Optional<DocumentoFolios> documentoFolio = documentoFolioRepository
                .findByCentroTrabajoAndTipoDocumento(centroTrabajo, tipoDocumento);

        documentoFolio.map((f)->{
            Integer folio = f.getFolio()+1;
            documentoFolioRepository.updateFolio(f.getId(), folio);

            return folio;
        }).orElseGet(()->{
            DocumentoFolios nuevo = create(getDocumentoFolios(tipoDocumento, centroTrabajo));

            return nuevo.getFolio();
        });

        throw new NotFoundException("Folio","No se puede generar un número de Folio");
    }

    private static CentroTrabajoRecord getCentroTrabajoRecord(Juzgado juzgado, Oficialia oficialia) {
        Integer centroTrabajoId;
        TipoCentroTrabajo tipoCentroTrabajo;

        if (juzgado != null) {
            centroTrabajoId = juzgado.getId();
            tipoCentroTrabajo = TipoCentroTrabajo.JUZGADO;
        } else if (oficialia != null && oficialia.getTipoOficialia().getNombre().equals("COMÚN")) {
            centroTrabajoId = oficialia.getId();
            tipoCentroTrabajo = TipoCentroTrabajo.OFICIALIA_COMUN;
        } else {
            throw new NotFoundException("Centro de Trabajo", "No se puede generar el número de folio");
        }

        return new CentroTrabajoRecord(centroTrabajoId, "", tipoCentroTrabajo);
    }

    private static DocumentoFolios getDocumentoFolios(TipoDocumento tipoDocumento, CentroTrabajoRecord centroTrabajo){
        return new DocumentoFolios()
                .setFolio(1)
                .setTipoDocumento(tipoDocumento)
                .setCentroTrabajoId(centroTrabajo.id())
                .setTipoCentroTrabajo(centroTrabajo.tipo())
                .setYear(LocalDate.now().getYear());
    }

    public DocumentoFolios create(DocumentoFolios documentoFolios){
        return documentoFolioRepository.save(documentoFolios);
    }

}
