package mx.gob.pjpuebla.trials.util;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Embeddable
public class Audit implements Serializable {

    @NotNull
    @PastOrPresent
    @Column(name = "T_FECHA_ALTA", updatable = false)
    private LocalDateTime fechaAlta;

    @NotNull
    @PastOrPresent
    @Column(name = "T_FECHA_EDITA")
    private LocalDateTime fechaEdita;

    @NotNull
    @Column(name = "S_USUARIO_ALTA", updatable = false)
    private String usuarioAlta;

    @NotNull
    @Column(name = "S_USUARIO_EDITA")
    private String usuarioEdita;

}