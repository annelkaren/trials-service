package mx.gob.pjpuebla.trials.workflow.carpeta.carpetaetapas;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import mx.gob.pjpuebla.trials.core.etapaprocesal.EtapaProcesal;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "TBL_CARPETA_ETAPAS")
public class CarpetaEtapas implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idCarpetaEtapas")
    @SequenceGenerator(name = "idCarpetaEtapas", sequenceName = "SEQ_CARPETA_ETAPAS_ID", allocationSize = 1)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @JoinColumn(name = "FN_CARPETA_ID", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Carpeta carpeta;

    @JoinColumn(name = "FN_ETAPA_ID", referencedColumnName = "PN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private EtapaProcesal etapaProcesal;

    @Column(name = "T_FECHA_REGISTRO")
    private LocalDateTime fechaRegistro;

    @Size(max = 300)
    @Column(name = "S_OBSERVACIONES")
    private String observaciones;
}
