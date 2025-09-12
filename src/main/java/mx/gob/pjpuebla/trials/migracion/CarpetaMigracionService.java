package mx.gob.pjpuebla.trials.migracion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.migracion.acl.mapper.RubrosMapper;
import mx.gob.pjpuebla.migracion.readers.acuerdos.AcuerdosMigracionReader;
import mx.gob.pjpuebla.migracion.readers.detallesProm.DetallesPromReader;
import mx.gob.pjpuebla.migracion.readers.entradas.EntradasMigracion;
import mx.gob.pjpuebla.migracion.readers.movimientos.MovimientosMigracionRecord;
import mx.gob.pjpuebla.migracion.readers.ocomun.Ocomun;
import mx.gob.pjpuebla.migracion.readers.tipoPiezas.TipoPiezaMigracion;
import mx.gob.pjpuebla.migracion.readers.tipoPiezas.TipoPiezaMigracionRead;
import mx.gob.pjpuebla.trials.core.conceptos.Concepto;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipopieza.TipoPieza;
import mx.gob.pjpuebla.trials.core.tipopieza.TipoPiezaRepository;
import mx.gob.pjpuebla.trials.error.ConstraintViolationException;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.migracion.policies.CreationPolicy;
import mx.gob.pjpuebla.trials.migracion.policies.MigracionDefaults;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;

@Service
@RequiredArgsConstructor
public class CarpetaMigracionService {

    private final CarpetaRepository carpetaRepository;
    private final MigracionDefaults defaults;
    private final JuzgadoLookup juzgadoLookup; // helper local para requireJuzgadoActual
    private final TipoPiezaRepository tipoPiezaRepository;
    private final DocumentoRepository documentoRepository;
    private final RubrosMapper rubrosMapper;
    private final DocumentoMigracionService documentoMig;
    private final TipoPiezaMigracionRead tipoPiezaMigracionRead;

    // Readers:
    private final AcuerdosMigracionReader acuerdosReader;
    private final DetallesPromReader detallesReader;

    /**
     * Helper chico para reusar lógica de buscar Juzgado por clave del sistema
     * actual.
     */
    @Service
    @RequiredArgsConstructor
    public static class JuzgadoLookup {
        private final mx.gob.pjpuebla.trials.core.juzgados.JuzgadoService juzgadoService;

        public Juzgado requireJuzgadoActual(String clave) {
            var j = juzgadoService.findByClaveJuzgado(clave);
            if (j == null)
                throw new NotFoundException("Juzgado no encontrado en sistema actual", clave);
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
                    c.getId() != null ? c.getId().toString() : expedienteCompleto);
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
        }

        return carpetaRepository.save(carpeta);
    }

    public Juzgado requireJuzgadoActual(String clave) {
        return juzgadoLookup.requireJuzgadoActual(clave);
    }

    // Creación de piezas a expediente:
    public void createPiezaConDocumentos(Carpeta carpeta, List<MovimientosMigracionRecord> piezas){

        piezas.forEach(pieza -> {
            String tipoPiezaString = tipoPieza(pieza.cu());
            TipoPieza tipoPieza = findTipoPieza(tipoPiezaString.substring(0,2));
           
            Concepto concepto = null;
            String expediente = carpeta.getExpediente() + "/" + tipoPieza(pieza.cu());

            Carpeta piezaNew = new Carpeta()
                .setFolio(documentoRepository.getNextValPieza().toString())
                .setExpediente(expediente)
                .setCarpetaPadre(carpeta)
                .setFechaAsignacion(pieza.fecha().atStartOfDay())
                .setTipoCarpeta(TipoCarpeta.PIEZA)
                .setSelloEstatus(SelloEstatus.VALIDO)
                .setEstatus(pieza.estado() == "ANEXO AL EXPEDIENTE" ? EstadoCarpeta.INTEGRADO : EstadoCarpeta.ASIGNADO)
                .setJuzgado(carpeta.getJuzgado())
                .setTipoJuicio(carpeta.getTipoJuicio())
                .setTipoPieza(tipoPieza)
                .setConcepto(concepto)
                .setCu(pieza.cu());

            piezaNew = carpetaRepository.save(piezaNew);

            //Creamos documentos:
            createDocumentos(piezaNew);
                
        });
    }

    // gettipoPieza:
    public String tipoPieza(String cu) {
        if (cu == null) {
            throw new IllegalArgumentException("cu no puede ser null");
        }
        cu = cu.trim();

        int len = cu.length();
        if (len != 16) {
            throw new IllegalArgumentException(
                    "cu debe tener exactamente 16 caracteres; recibido " + len + ": '" + cu + "'");
        }

        // (Opcional) Validación de formato: 12 dígitos + 2 letras + 2 dígitos
        // Ajusta el regex si tu sufijo puede variar.
        if (!cu.matches("\\d{12}[A-Za-z]{2}\\d{2}")) {
            throw new IllegalArgumentException(
                    "Formato de pieza (cu) inválido. Se esperaba 12 dígitos, 2 letras, 2 dígitos.");
        }

        // Si ya validaste longitud==16, es equivalente usar (12,16) o solo (12)
        return cu.substring(12); // "AC01" por ejemplo
    }

    // Creación de documentos de pieza:

    private void createDocumentos(Carpeta pieza) {
        // obtenemos los acuerdos, sentencias, promociones de la pieza:
        var acuerdos = acuerdosReader.buscarAcuerdosPorCu(pieza.getCu());
        var sentencias = acuerdosReader.buscarSentenciasPorCu(pieza.getCu());
        var promos = detallesReader.buscarPorCu(pieza.getCu());

        documentoMig.createAcuerdosFromLegacy(acuerdos, pieza, rubrosMapper);
        documentoMig.createSentenciasFromLegacy(sentencias, pieza);
        documentoMig.createPromocionesFromLegacy(promos, pieza);

    }

    private TipoPieza findTipoPieza(String clave){

        //Intentamos buscar la tipo pieza en el sistema de java
        Optional<TipoPieza> tipoPiezaJava = tipoPiezaRepository.findByClave(clave);

        //verificamos si es que existe la regresamos.
        if(tipoPiezaJava.isPresent()){
            return tipoPiezaJava.get();
        }

        //En caso que no exista consultamos si existe en los tipos de piezas registrados en secgj php
        //Si no encuentra registro de la clave de la pieza en php lanza una excepción ya que no se podria determinar ni el tipo ni la clave para registrarla.
        TipoPiezaMigracion tipoPiezaMigracion = tipoPiezaMigracionRead.findByClave(clave);

        //en caso de que si se encuentre se registra en java como inactiva y se retorna:

        return tipoPiezaRepository.save(new TipoPieza()
            .setClave(clave)
            .setTipo(tipoPiezaMigracion.getTipo())
            .setVersion(0)
            .setEstado(Estado.INACTIVE));
        

    }
}
