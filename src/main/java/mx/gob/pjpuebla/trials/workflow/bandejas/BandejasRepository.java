package mx.gob.pjpuebla.trials.workflow.bandejas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

import mx.gob.pjpuebla.trials.workflow.migracion.Migraciones;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.BandejaMigracionResponse;

public interface BandejasRepository extends JpaRepository<Migraciones, Integer> {

    @Query("""
                SELECT new mx.gob.pjpuebla.trials.workflow.bandejas.records.BandejaMigracionResponse(
                    m.id,
                    m.carpeta.expediente,
                    CASE m.estatus
                        WHEN mx.gob.pjpuebla.trials.util.enums.EstadoMigracion.EXPEDIENTE_MIGRADO THEN 'Expediente migrado'
                        WHEN mx.gob.pjpuebla.trials.util.enums.EstadoMigracion.DOCUMENTOS_MIGRADOS THEN 'Documentos del expediente migrados'
                        WHEN mx.gob.pjpuebla.trials.util.enums.EstadoMigracion.MIGRADO_COMPLETADO THEN 'Migración completada'
                        WHEN mx.gob.pjpuebla.trials.util.enums.EstadoMigracion.EXPEDIENTE_TURNADO THEN 'Expediente turnado'
                        WHEN mx.gob.pjpuebla.trials.util.enums.EstadoMigracion.NO_MIGRADO THEN 'Expediente no migrado'
                        ELSE ''
                    END,
                    COALESCE(UPPER(CONCAT(p.nombre, ' ', p.apellidoPaterno, ' ', COALESCE(p.apellidoMaterno, ''))), ''),
                    m.observaciones,
                    m.asignacionAnterior,
                    m.puestoAsignacionAnterior,
                    m.carpeta.id,
                    m.carpeta.concepto.nombre,
                    m.carpeta.concepto.dias
                )
                FROM Migraciones m
                LEFT JOIN Persona p ON p.usuario = m.audit.usuarioAlta
                WHERE m.carpeta.juzgado IN :juzgados
                AND (:expediente IS NULL OR m.carpeta.expediente LIKE %:expediente%)
                AND (:migradoPor IS NULL OR UPPER(CONCAT(p.nombre, ' ', p.apellidoPaterno, ' ', COALESCE(p.apellidoMaterno, ''))) LIKE %:migradoPor%)
                AND (:estado IS NULL OR m.estatus = :estado)
                AND (:key IS NULL OR (
                    m.carpeta.expediente LIKE %:key% OR
                    UPPER(CONCAT(p.nombre, ' ', p.apellidoPaterno, ' ', COALESCE(p.apellidoMaterno, ''))) LIKE %:key% OR
                    (CASE m.estatus
                        WHEN mx.gob.pjpuebla.trials.util.enums.EstadoMigracion.EXPEDIENTE_MIGRADO THEN 'Expediente migrado'
                        WHEN mx.gob.pjpuebla.trials.util.enums.EstadoMigracion.DOCUMENTOS_MIGRADOS THEN 'Documentos del expediente migrados'
                        WHEN mx.gob.pjpuebla.trials.util.enums.EstadoMigracion.MIGRADO_COMPLETADO THEN 'Migración completada'
                        WHEN mx.gob.pjpuebla.trials.util.enums.EstadoMigracion.EXPEDIENTE_TURNADO THEN 'Expediente turnado'
                        WHEN mx.gob.pjpuebla.trials.util.enums.EstadoMigracion.NO_MIGRADO THEN 'Expediente no migrado'
                        ELSE ''
                    END) LIKE %:key%
                ))
            """)
    Page<BandejaMigracionResponse> findByJuzgadoIn(
            @Param("juzgados") List<Juzgado> juzgados,
            @Param("key") String key,
            @Param("expediente") String expediente,
            @Param("migradoPor") String migradoPor,
            @Param("estado") mx.gob.pjpuebla.trials.util.enums.EstadoMigracion estado,
            Pageable pageable);

}
