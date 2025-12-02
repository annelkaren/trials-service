package mx.gob.pjpuebla.trials.migracion;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.migracion.readers.movimientos.MovimientosMigracionRecord;
import mx.gob.pjpuebla.trials.core.conceptos.Concepto;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipopieza.TipoPieza;
import mx.gob.pjpuebla.trials.core.tipopieza.TipoPiezaRepository;
import mx.gob.pjpuebla.trials.error.ConstraintViolationException;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.Migrado;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarpetaMigracionService {

    private final CarpetaRepository carpetaRepository;
    private final JuzgadoMigracionService juzgadoMigracionService;
    private final TipoPiezaRepository tipoPiezaRepository;
    private final DocumentoRepository documentoRepository;

    @Transactional(readOnly = true)
    public void assertExpedienteDisponible(String expedienteCompleto, Juzgado juzgado) {
        Optional<Carpeta> existente = carpetaRepository.findByExpedienteAndJuzgado(expedienteCompleto, juzgado);
        if (existente.isPresent()) {
            throw new ConstraintViolationException(
                    "El expediente ya se encuentra en el sistema", expedienteCompleto);
        }
    }

    @Transactional
    @SuppressWarnings("ConstantConditions")
    public Carpeta crearCarpeta(String expedienteCompleto,
            String folio,
            String cu,
            @NotNull Juzgado juzgado,
            @NotNull TipoJuicio tipoJuicio,
            @NotNull Concepto concepto) {

        Carpeta carpeta = new Carpeta()
                .setVersion(0)
                .setFolio(folio)
                .setExpediente(expedienteCompleto)
                .setJuzgado(juzgado)
                .setTipoJuicio(tipoJuicio)
                .setConcepto(concepto)
                .setFechaAsignacion(LocalDateTime.now())
                .setPersona(null)
                .setCarpetaPadre(null)
                .setDeterminacionJurisdiccional(null)
                .setSentencia(null)
                .setTipoPieza(null)
                .setHoras(null)
                .setPrioridad(null)
                .setSelloEstatus(SelloEstatus.VALIDO)
                .setEstatus(EstadoCarpeta.MIGRADO)
                .setTipoCarpeta(TipoCarpeta.DEMANDA)
                .setMigrado(Migrado.SI)
                .setCu(cu);

        return carpetaRepository.save(carpeta);
    }

    // Creación de piezas a expediente:
    public List<Carpeta> createPiezas(Carpeta carpeta, List<MovimientosMigracionRecord> piezas,
            String tipoPiezaCompleta) {
        List<Carpeta> piezasCreadas = new ArrayList<>();

        piezas.forEach(pieza -> {
            TipoPieza tipoPieza = findTipoPieza(tipoPiezaCompleta.substring(0, 2), pieza.tipoPieza());
            String expediente = carpeta.getExpediente() + "/" + tipoPiezaCompleta;

            Carpeta piezaNew = new Carpeta()
                    .setFolio(documentoRepository.getNextValPieza().toString())
                    .setExpediente(expediente)
                    .setCarpetaPadre(carpeta)
                    .setFechaAsignacion(pieza.fecha().atStartOfDay())
                    .setTipoCarpeta(TipoCarpeta.PIEZA)
                    .setSelloEstatus(SelloEstatus.VALIDO)
                    .setEstatus(pieza.estado().equalsIgnoreCase("ANEXO AL EXPEDIENTE") ? EstadoCarpeta.INTEGRADO
                            : EstadoCarpeta.ASIGNADO)
                    .setJuzgado(carpeta.getJuzgado())
                    .setTipoJuicio(carpeta.getTipoJuicio())
                    .setTipoPieza(tipoPieza)
                    .setCu(pieza.cu());

            piezaNew = carpetaRepository.save(piezaNew);
            piezasCreadas.add(piezaNew);

        });

        return piezasCreadas;
    }

    // Creación de documentos de pieza:

    private TipoPieza findTipoPieza(String clave, String tipo) {

        // Intentamos buscar la tipo pieza en el sistema de java
        Optional<TipoPieza> tipoPiezaJava = tipoPiezaRepository.findByClave(clave);

        // verificamos si es que existe la regresamos.
        if (tipoPiezaJava.isPresent()) {
            return tipoPiezaJava.get();
        }

        return crearTipoPieza(clave, tipo);
    }

    public Carpeta findByExpYearAndClaveJuzgado(String expediente, Integer year, String claveJuzgado) {
        String expedienteCompleto = expediente + "/" + year;
        Juzgado juzgado = juzgadoMigracionService.requireJuzgadoActual(claveJuzgado);

        Optional<Carpeta> carpeta = carpetaRepository.findByExpedienteAndJuzgado(expedienteCompleto, juzgado);

        if (carpeta.isPresent()) {
            return carpeta.get();
        } else {
            throw new NotFoundException("No se encontró la carpeta con los datos proporcionados",
                    expedienteCompleto + " - " + claveJuzgado);
        }
    }

    public TipoPieza crearTipoPieza(String clave, String tipo) {
        TipoPieza tipoPieza = new TipoPieza()
                .setClave(clave)
                .setTipo(tipo)
                .setVersion(0)
                .setEstado(Estado.INACTIVE);

        return tipoPiezaRepository.save(tipoPieza);

    }

}
