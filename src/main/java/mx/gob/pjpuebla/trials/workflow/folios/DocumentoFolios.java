package mx.gob.pjpuebla.trials.workflow.folios;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import mx.gob.pjpuebla.trials.util.enums.TipoCentroTrabajo;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;

@Data
@Entity
@Table(name="TBL_DOCUMENTOS_FOLIOS")
public class DocumentoFolios {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idDocumentoFolios")
    @SequenceGenerator(name = "idDocumentoFolios", sequenceName = "SEQ_DOCUMENTOS_FOLIOS_ID", allocationSize = 1)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Enumerated
    @Column(name = "n_tipo_documento")
    private TipoDocumento tipoDocumento;

    @NotNull
    @Column(name = "n_centro_trabajo_id")
    private Integer centroTrabajoId;

    @Enumerated
    @NotNull
    @Column(name = "n_tipo_centro_trabajo")
    private TipoCentroTrabajo tipoCentroTrabajo;

    @NotNull
    @Column(name = "n_folio")
    private Integer folio;

    @NotNull
    @Column(name = "n_year")
    private Integer year;
}
