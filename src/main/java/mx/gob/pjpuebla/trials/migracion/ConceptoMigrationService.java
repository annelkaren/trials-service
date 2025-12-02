package mx.gob.pjpuebla.trials.migracion;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.conceptos.Concepto;
import mx.gob.pjpuebla.trials.core.conceptos.ConceptoRepository;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaService;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.util.enums.Estado;

@Service
@RequiredArgsConstructor
public class ConceptoMigrationService {

    private final ConceptoRepository conceptoRepository;
    private final TipoJuicioRepository tipoJuicioRepository;
    private final MateriaService materiaService; // para resolver Materia por nombre


    // Todo: asignar tipo de sistema al tipo juicio.
    /**
     * Busca o crea un TipoJuicio para migración (crea sin activar flags especiales de negocio,
     * los defaults especiales van en los *MigrationService* cuando aplique).
     */
    @Transactional
    public TipoJuicio findOrCreateTipoJuicioForMigration(String materiaNombre, String descripcionLegacy, TipoSistema tipoSistema) {
        
        // 1) Resolver Materia 
        Materia materia = Optional.ofNullable(materiaService.findByNombre(materiaNombre))
                .orElseThrow(() -> new IllegalArgumentException("Materia no encontrada: " + materiaNombre));

       
        // 2) Buscar tipoJuicio por nombre
        Optional<TipoJuicio> existente = tipoJuicioRepository.findByNombre(descripcionLegacy);
        if (existente.isPresent()) return existente.get();

        // 3) Crear si no existe
        TipoJuicio nuevo = new TipoJuicio()
                .setNombre(descripcionLegacy)
                .setMateria(materia)
                .setEstado(Estado.INACTIVE)
                .setTipoCausa(null)
                .setTipoSistema(tipoSistema)
                .setTipoJuicioPadreOral(null)
                .setTipoJuicioPadreTrad(null);

        return tipoJuicioRepository.save(nuevo);
    }

    /**
     * Busca o crea un Concepto atado a un TipoJuicio en base al último movimiento (estado),
     * aplicando defaults de migración (estado INACTIVE) y cálculo de días por tablas legacy.
     */
    @Transactional
    public Concepto findOrCreateByUltimoMovimiento(TipoJuicio tipoJuicio, String nombre, Integer dias) {
    

        Optional<Concepto> existente = conceptoRepository.findByNombreAndTipoJuicio(nombre, tipoJuicio);
        if (existente.isPresent()) return existente.get();

        Concepto concepto = new Concepto()
                .setVersion(0)
                .setNombre(nombre)
                .setDias(dias)
                .setEstado(Estado.INACTIVE) 
                .setTipoJuicio(tipoJuicio)
                .setRoles(null);

        return conceptoRepository.save(concepto);
    }

}
