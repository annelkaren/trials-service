package mx.gob.pjpuebla.migracion.readers.usuario;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioMigracionReader {
    private final UsuarioMigracionRepository usuarioMigracionRepository;

    public UsuarioMigracion findUsuarioMigracionByIdUsuarioAnEstado(Integer IdUsuario){
         return usuarioMigracionRepository.findByIdusuarioAndEstatus(IdUsuario, "A").orElse(null);
    }

}
