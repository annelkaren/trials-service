package mx.gob.pjpuebla.trials.migracion;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.migracion.readers.entradas.EntradasMigracion;
import mx.gob.pjpuebla.migracion.readers.ocomun.Ocomun;
import mx.gob.pjpuebla.trials.core.conceptos.Concepto;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.error.ConstraintViolationException;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.migracion.policies.CreationPolicy;
import mx.gob.pjpuebla.trials.migracion.policies.MigracionDefaults;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;

@Service
@RequiredArgsConstructor
public class CarpetaMigrationService {

    private final CarpetaRepository carpetaRepository;
    private final MigracionDefaults defaults;
    private final JuzgadoLookup juzgadoLookup; // helper local para requireJuzgadoActual

    /** Helper chico para reusar lógica de buscar Juzgado por clave del sistema actual. */
    @Service
    @RequiredArgsConstructor
    public static class JuzgadoLookup {
        private final mx.gob.pjpuebla.trials.core.juzgados.JuzgadoService juzgadoService;

        public Juzgado requireJuzgadoActual(String clave) {
            var j = juzgadoService.findByClaveJuzgado(clave);
            if (j == null) throw new NotFoundException("Juzgado no encontrado en sistema actual", clave);
            return j;
        }
    }

    @Transactional(readOnly = true)
    public void assertExpedienteDisponible(String expedienteCompleto, Juzgado juzgado) {
        Optional<Carpeta> existente = carpetaRepository.findByExpedienteAndJuzgado(expedienteCompleto, juzgado);
        if (existente.isPresent()) {
            var c = existente.get();
            throw new ConstraintViolationException(
                "El expediente ya se encuentra en el sistema",
                c.getId() != null ? c.getId().toString() : expedienteCompleto
            );
        }
    }

    @Transactional
    public Carpeta createFromLegacy(EntradasMigracion entrada,
                                    Ocomun ocomun,
                                    Juzgado juzgado,
                                    TipoJuicio tipoJuicio,
                                    Concepto concepto) {
        return createFromLegacy(entrada, ocomun, juzgado, tipoJuicio, concepto, CreationPolicy.MIGRATION);
    }

    @Transactional
    public Carpeta createFromLegacy(EntradasMigracion entrada,
                                    Ocomun ocomun,
                                    Juzgado juzgado,
                                    TipoJuicio tipoJuicio,
                                    Concepto concepto,
                                    CreationPolicy policy) {

        if (entrada == null || juzgado == null || tipoJuicio == null || concepto == null) {
            throw new IllegalArgumentException("Parámetros obligatorios nulos en createFromLegacy");
        }

        var carpeta = new Carpeta()
            .setVersion(0)
            .setFolio(ocomun != null && ocomun.getFolio() != null
                        ? ocomun.getFolio().toString()
                        : UUID.randomUUID().toString())
            .setExpediente(entrada.getExpediente() + "/" + entrada.getAmo())
            .setJuzgado(juzgado)
            .setTipoJuicio(tipoJuicio)
            .setConcepto(concepto)
            .setFechaAsignacion(defaults.now())
            .setPersona(null)
            .setCarpetaPadre(null)
            .setDeterminacionJurisdiccional(null)
            .setSentencia(null)
            .setTipoPieza(null)
            .setHoras(null)
            .setPrioridad(null);

        // Política:
        if (policy == CreationPolicy.MIGRATION) {
            carpeta.setSelloEstatus(defaults.defaultSelloCarpeta())
                   .setEstatus(defaults.defaultEstadoCarpeta())
                   .setTipoCarpeta(TipoCarpeta.DEMANDA)
                   .setMigrado(defaults.defaultFlagMigrado())
                   .setCu(entrada.getCu());
        } else {
            // DEFAULT: si s requiere aplicar otra politica
        }

        return carpetaRepository.save(carpeta);
    }

    public Juzgado requireJuzgadoActual(String clave) {
        return juzgadoLookup.requireJuzgadoActual(clave);
    }
}
