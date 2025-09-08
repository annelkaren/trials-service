package mx.gob.pjpuebla.migracion.readers.usuario;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioMigracionReader {
    private final UsuarioMigracionRepository usuarioMigracionRepository;

    public UsuarioMigracion findUsuarioMigracionByIdUsuarioAnEstado(Integer IdUsuario){
        Optional<UsuarioMigracion> usuarioMigracionOptional = usuarioMigracionRepository.findByIdusuarioAndEstatus(IdUsuario, "A");

        if(usuarioMigracionOptional.isPresent()){
            return usuarioMigracionOptional.get();
        }

        return null;
    }

}
