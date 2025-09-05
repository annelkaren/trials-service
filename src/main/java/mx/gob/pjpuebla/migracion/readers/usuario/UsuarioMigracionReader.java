package mx.gob.pjpuebla.migracion.readers.usuario;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioMigracionReader {
    private final UsuarioMigracionRepository usuarioMigracionRepository;

    public UsuarioMigracion findUsuarioMigracionByIdUsuarioAnEstado(Integer IdUsuario, String estado){
        Optional<UsuarioMigracion> usuarioMigracionOptional = usuarioMigracionRepository.findByIdUsuarioAndEstatus(IdUsuario, estado);

        if(usuarioMigracionOptional.isPresent()){
            return usuarioMigracionOptional.get();
        }

        return null;
    }

}
