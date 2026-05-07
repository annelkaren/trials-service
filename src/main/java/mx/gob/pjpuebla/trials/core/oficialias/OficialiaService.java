package mx.gob.pjpuebla.trials.core.oficialias;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRepository;
import mx.gob.pjpuebla.trials.core.oficialias.records.OficialiaRecord;
import mx.gob.pjpuebla.trials.core.oficialias.records.OficialiaRecordResponse;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.sedes.SedeRepository;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialia;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaRepository;
import mx.gob.pjpuebla.trials.error.ConflictException;
import mx.gob.pjpuebla.trials.error.InvalidVersionException;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.CarpetaCatalogoRecord;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class OficialiaService {

    private final OficialiaRepository oficialiaRepository;
    private final SedeRepository sedeRepository;
    private final TipoOficialiaRepository tipoOficialiaRepository;
    private final MateriaRepository materiaRepository;
    private final JuzgadoRepository juzgadoRepository;

    @Transactional(readOnly = true)
    public Page<OficialiaMateriaRecord> getAllByOficialiaMateria(String key, String nombre, String materia, String tipo,
            String juzgado, Estado estatus, Pageable pageable) {

        key = key != null ? key.toLowerCase() : "";
        nombre = nombre != null ? nombre.toLowerCase() : "";
        materia = materia != null ? materia.toLowerCase() : "";
        tipo = tipo != null ? tipo.toLowerCase() : "";
        juzgado = juzgado != null ? juzgado.toLowerCase() : "";

        List<Estado> estados = estatus == null ? List.of(Estado.ACTIVE, Estado.INACTIVE) : List.of(estatus);

        Page<Oficialia> oficialias = oficialiaRepository.findAllActive(key, nombre, materia, tipo, juzgado, estados,
                pageable);

        return new PageImpl<>(oficialias.stream().map(o -> new OficialiaMateriaRecord(
                o.getId(),
                o.getNombre(),
                o.getEstado(),
                String.join(", ",
                        o.getMaterias().stream().map(m -> StringUtils.capitalize(m.getNombre().toLowerCase()))
                                .toList()),
                o.getMaterias().stream().map(m -> m.getId()).toArray(),
                o.getSede().getId(),
                o.getTipoOficialia().getNombre(),
                o.getTipoOficialia().getId(),
                listarJuzgados(o.getJuzgados()),
                null,
                o.getTiposDocumentos() != null && !o.getTiposDocumentos().isBlank()
                        ? Arrays.stream(o.getTiposDocumentos().split(","))
                                .map(String::trim)
                                .map(clave -> {
                                    if (clave.equals("PROMOCION")) {
                                        TipoDocumento tipoDocumento = TipoDocumento.valueOf(clave);
                                        return new CarpetaCatalogoRecord(clave,
                                                tipoDocumento.getEtiqueta());
                                    } else {
                                        TipoCarpeta tipoCarpeta = TipoCarpeta.valueOf(clave);
                                        return new CarpetaCatalogoRecord(clave,
                                                tipoCarpeta.getEtiqueta());
                                    }

                                })
                                .collect(Collectors.toList())
                        : null))
                .toList(), pageable, oficialias.getTotalElements());
    }

    @Transactional(readOnly = true)
    public OficialiaRecord findById(Integer id) {
        Oficialia o = oficialiaRepository.findByIdAndEstadoIn(id, Arrays.asList(Estado.INACTIVE, Estado.ACTIVE))
                .orElseThrow(() -> new NotFoundException("Oficialia no encontrada", "oficialiaId: " + id));

        List<mx.gob.pjpuebla.trials.core.materias.MateriaRecord> materias = o.getMaterias() != null ? 
            o.getMaterias().stream().map(m -> new mx.gob.pjpuebla.trials.core.materias.MateriaRecord(m.getId(), m.getNombre())).collect(Collectors.toList()) : null;

        return new OficialiaRecord(
            o.getId(), o.getVersion(), o.getNombre(), o.getResponsable(), o.getEstado(),
            new mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaRecord(o.getTipoOficialia().getId(), o.getTipoOficialia().getNombre()),
            new mx.gob.pjpuebla.trials.core.sedes.records.SedeRecordResponse(o.getSede().getId(), o.getSede().getNombre(), o.getSede().getEstado()),
            o.getTiposDocumentos() != null && !o.getTiposDocumentos().isBlank()
                ? Arrays.stream(o.getTiposDocumentos().split(","))
                    .map(String::trim)
                    .map(clave -> {
                        if (clave.equals("PROMOCION")) {
                            return new CarpetaCatalogoRecord(clave, TipoDocumento.valueOf(clave).getEtiqueta());
                        } else {
                            return new CarpetaCatalogoRecord(clave, TipoCarpeta.valueOf(clave).getEtiqueta());
                        }
                    }).collect(Collectors.toList())
                : null,
            materias
        );
    }

    public OficialiaRecordResponse create(Oficialia oficialia) {

        if (oficialiaRepository.findByNombreIgnoreCase(oficialia.getNombre()).isPresent()) {
            throw new ConflictException("No pueden existir 2 oficialias con el mismo nombre");
        }

        if (oficialia.getSede() != null && oficialia.getSede().getId() != null) {
            Sede sede = sedeRepository.findById(oficialia.getSede().getId())
                    .orElseThrow(() -> new NotFoundException("Sede no encontrada", "sedeId"));
            oficialia.setSede(sede);
        }

        if (oficialia.getTipoOficialia() != null && oficialia.getTipoOficialia().getId() != null) {
            TipoOficialia tipoOficialia = tipoOficialiaRepository.findById(oficialia.getTipoOficialia().getId())
                    .orElseThrow(() -> new NotFoundException("Tipo Oficialia no encontrada", "tipoOficialiaId"));
            oficialia.setTipoOficialia(tipoOficialia);
        }

        if (oficialia.getTipoOficialia() != null) {
            validateJuzgadosByTipoOficialia(oficialia, oficialia.getTipoOficialia());
        } else {
            throw new IllegalArgumentException("El tipo de oficialía no puede ser nulo.");
        }

        if (oficialia.getJuzgados() != null && !oficialia.getJuzgados().isEmpty()) {
            List<Juzgado> juzgados = juzgadoRepository.findAllById(
                    oficialia.getJuzgados().stream().map(Juzgado::getId).toList());
            oficialia.setJuzgados(juzgados);
        }

        if (oficialia.getMaterias() != null) {
            List<Integer> mIds = oficialia.getMaterias().stream()
                    .map(Materia::getId)
                    .toList();

            List<Materia> materias = materiaRepository.findAllById(mIds);
            oficialia.setMaterias(materias);
        }

        oficialia = oficialiaRepository.save(oficialia);
        return new OficialiaRecordResponse(oficialia.getId(), oficialia.getNombre());
    }

    public OficialiaRecordResponse update(Oficialia oficialia) {
        Optional<Oficialia> test = oficialiaRepository.findByNombreIgnoreCase(oficialia.getNombre());

        if (test.isPresent() && !Objects.equals(test.get().getId(), oficialia.getId())) {
            throw new ConflictException("No pueden existir 2 oficialias con el mismo nombre");
        }
        try {
            Oficialia existingOficialia = oficialiaRepository.findById(oficialia.getId())
                    .orElseThrow(() -> new NotFoundException("Oficialia no encontrada", "oficialiaId"));
            existingOficialia.setNombre(oficialia.getNombre());
            existingOficialia.setEstado(oficialia.getEstado());

            existingOficialia.setSede(sedeRepository.findById(oficialia.getSede().getId())
                    .orElseThrow(() -> new NotFoundException("Sede no encontrada", "sedeId")));

            existingOficialia.setTipoOficialia(tipoOficialiaRepository.findById(oficialia.getTipoOficialia().getId())
                    .orElseThrow(() -> new NotFoundException("Tipo Oficialia no encontrada", "tipoOficialiaId")));

            // Validación y actualización de juzgados
            if (oficialia.getJuzgados() != null && !oficialia.getJuzgados().isEmpty()) {
                List<Juzgado> juzgados = juzgadoRepository.findAllById(
                        oficialia.getJuzgados().stream().map(Juzgado::getId).toList());

                if (existingOficialia.getTipoOficialia().getNombre().equalsIgnoreCase("Mayor")) {
                    if (juzgados.size() != 1) {
                        throw new InvalidVersionException(
                                "Una oficialía de tipo Mayor debe tener exactamente 1 juzgado.");
                    }
                } else if (existingOficialia.getTipoOficialia().getNombre().equalsIgnoreCase("Común")) {
                    if (juzgados.size() < 2) {
                        throw new InvalidVersionException(
                                "Una oficialía de tipo Común debe tener al menos 2 juzgados.");
                    }
                }

                existingOficialia.setJuzgados(juzgados);
            } else {
                existingOficialia.setJuzgados(existingOficialia.getJuzgados());
            }

            if (oficialia.getMaterias() != null) {
                List<Integer> mIds = oficialia.getMaterias().stream()
                        .map(Materia::getId).toList();

                List<Materia> materias = materiaRepository.findAllById(mIds);
                existingOficialia.setMaterias(materias);
            }

            existingOficialia.setTiposDocumentos(oficialia.getTiposDocumentos());

            oficialiaRepository.save(existingOficialia);

            return new OficialiaRecordResponse(existingOficialia.getId(), existingOficialia.getNombre());
        } catch (OptimisticLockingFailureException ex) {
            throw new InvalidVersionException(Oficialia.class.getSimpleName());
        }
    }

    public void delete(Integer id) {
        oficialiaRepository.deleteById(id);
    }

    private String listarJuzgados(List<Juzgado> juzgados) {
        if (juzgados.isEmpty())
            return "";

        int num = juzgados.size();
        String mensaje = num > 1 ? String.format(" y %d más", num - 1) : "";
        return juzgados.stream().findFirst().get().getNombre() + mensaje;
    }

    private void validateJuzgadosByTipoOficialia(Oficialia oficialia, TipoOficialia tipoOficialia) {
        List<Juzgado> juzgados = oficialia.getJuzgados();

        if (tipoOficialia.getNombre().equalsIgnoreCase("Común")) {
            if (juzgados == null || juzgados.size() < 2) {
                throw new IllegalArgumentException("Las oficialías de tipo Común deben tener al menos dos juzgados.");
            }
        } else if (tipoOficialia.getNombre().equalsIgnoreCase("Mayor")) {
            if (juzgados == null || juzgados.size() != 1) {
                throw new IllegalArgumentException("Las oficialías de tipo Mayor deben tener exactamente un juzgado.");
            }
        }
    }

}
