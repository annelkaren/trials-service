package mx.gob.pjpuebla.trials.workflow.listaestrados;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import mx.gob.pjpuebla.trials.core.personas.Persona;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "TBL_LISTA_ESTRADOS")
public class ListaEstrado implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idListaEstrado")
    @SequenceGenerator(name = "idListaEstrado", sequenceName = "SEQ_LISTA_ESTRADOS_ID")
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @NotNull
    @PastOrPresent
    @Column(name = "T_FECHA_ALTA", updatable = false)
    private LocalDateTime fechaAlta;

    @Column(name = "T_FECHA_VENCIMIENTO", updatable = false)
    private Date fechaVencimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FN_PERSONA")
    private Persona persona;

}
