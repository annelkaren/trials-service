package mx.gob.pjpuebla.trials.core.menu;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "TBL_MENUS")
public class Menu implements Serializable {

    @Id
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Size(max = 100)
    @Column(name = "S_NOMBRE", nullable = false)
    private String nombre;

    @Size(max = 100)
    @Column(name = "S_ROL", nullable = false)
    private String roles;

    @Size(max = 100)
    @Column(name = "S_LINK", nullable = false)
    private String link;

    @Column(name = "N_ORDER")
    private Integer order;

    @Column(name = "FN_PARENT")
    private Integer parent;
}
