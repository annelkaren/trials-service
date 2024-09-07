package mx.gob.pjpuebla.trials.core.salas;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Max;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.core.bloques.Bloque;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaRecord;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.Estado;

@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "TBL_SALA")
public class Sala implements Serializable, Auditable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idSala")
    @SequenceGenerator(name = "idSala", sequenceName = "SEQ_SALA_ID", allocationSize = 1)
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id; 

    @Max(Integer.MAX_VALUE)
    @Version
    @Column(name = "N_VERSION")
    private Integer version;

    @Column(name = "S_NOMBRE")
    private String nombre;

    @Enumerated
    @Column(name = "N_ESTADO", nullable = false)
    private Estado estado;

    @JoinColumn(name = "FN_JUEZ_ID", referencedColumnName = "PN_ID")
    @OneToOne()
    private Persona juez; // esto debe de ser una llave foranea pero como no se tiene aun hacia que se referenciara se coloca momentaneamente como integer.

    @JoinColumn(name= "FN_BLOQUE_ID", referencedColumnName = "PN_ID")
    @OneToOne()
    private Bloque bloque;
    
    @JoinColumn(name = "FN_JUZGADO_ID", referencedColumnName = "PN_ID")
    @OneToOne()
    private Juzgado juzgado;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;
}
