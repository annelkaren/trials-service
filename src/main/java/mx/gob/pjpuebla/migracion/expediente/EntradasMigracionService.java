package mx.gob.pjpuebla.migracion.expediente;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.migracion.actores.ActoresMigracion;
import mx.gob.pjpuebla.migracion.actores.ActoresMigracionService;
import mx.gob.pjpuebla.migracion.acuerdos.AcuerdosMigracion;
import mx.gob.pjpuebla.migracion.acuerdos.AcuerdosMigracionService;
import mx.gob.pjpuebla.migracion.amparos.AmparoMigracionService;
import mx.gob.pjpuebla.migracion.amparos.AmparosMigracion;
import mx.gob.pjpuebla.migracion.detallesProm.DetallesProm;
import mx.gob.pjpuebla.migracion.detallesProm.DetallesPromService;
import mx.gob.pjpuebla.migracion.exhortoCapital.ExhortosCapitalMigracion;
import mx.gob.pjpuebla.migracion.exhortoCapital.ExhortosCapitalMigracionService;
import mx.gob.pjpuebla.migracion.exhortoForaneo.ExhortoForaneoMigracion;
import mx.gob.pjpuebla.migracion.exhortoForaneo.ExhortoForaneoMigracionService;
import mx.gob.pjpuebla.migracion.juicios.JuiciosMigracion;
import mx.gob.pjpuebla.migracion.juicios.JuiciosMigracionService;
import mx.gob.pjpuebla.migracion.juzgados.JuzgadosMigracion;
import mx.gob.pjpuebla.migracion.juzgados.JuzgadosMigracionService;
import mx.gob.pjpuebla.migracion.movimientos.MovimientosMigracionRecord;
import mx.gob.pjpuebla.migracion.ocomun.Ocomun;
import mx.gob.pjpuebla.migracion.ocomun.OcomunRepository;
import mx.gob.pjpuebla.migracion.ocomun.OcomunService;
import mx.gob.pjpuebla.migracion.oficios.OficiosMigracion;
import mx.gob.pjpuebla.migracion.oficios.OficiosMigracionService;
import mx.gob.pjpuebla.trials.core.conceptos.Concepto;
import mx.gob.pjpuebla.trials.core.conceptos.ConceptoRepository;
import mx.gob.pjpuebla.trials.core.conceptos.ConceptoService;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoService;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaService;
import mx.gob.pjpuebla.trials.core.oficialias.Oficialia;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioService;
import mx.gob.pjpuebla.trials.error.ApiResponseFactory;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.EstadoMigracion;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaService;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoService;
import mx.gob.pjpuebla.trials.workflow.migracion.Migraciones;
import mx.gob.pjpuebla.trials.workflow.migracion.MigracionesService;

@Service
@RequiredArgsConstructor
public class EntradasMigracionService {

    // Plantilla JDBC configurada para usar la base secundaria (migración)
    @Qualifier("secondaryNamedParameterJdbcTemplate")
    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final AcuerdosMigracionService acuerdosMigracionService;
    private final EntradasMigracionRepository entradasMigracionRepository;
    private final JuzgadosMigracionService juzgadosMigracionService;
    private final JuiciosMigracionService juiciosMigracionService;
    private final AmparoMigracionService amparoMigracionService;
    private final OficiosMigracionService oficiosMigracionService;
    private final ActoresMigracionService actoresMigracionService;
    private final DetallesPromService detallesPromService;
    private final ExhortoForaneoMigracionService exhortoForaneoMigracionService;
    private final ExhortosCapitalMigracionService exhortoCapitalMigracionService;
    private final OcomunService ocomunService;

    // service de sistema actual:
    private final JuzgadoService juzgadoService;
    private final CarpetaService carpetaService;
    private final CarpetaRepository carpetaRepository;
    private final TipoJuicioRepository tipoJuicioRepository;
    private final DocumentoService documentoService;
    private final MateriaService materiaService;
    private final TipoJuicioService tipoJuicioService;
    private final ConceptoService conceptoService;
    private final ConceptoRepository conceptoRepository;
    private final MigracionesService migracionesService;

    /**
     * Busca las entradas migradas por expediente, año y juzgado.
     * A cada entrada le anexa sus ubicaciones dinámicas, el juzgado correspondiente
     * y el juicio asociado.
     *
     * @param expediente    Número de expediente
     * @param amo           Año del expediente
     * @param juzgadoCodigo Código del juzgado
     * @return Lista de registros de entrada completos
     */
    public List<EntradasMigracionRecord> buscarPorFiltros(String expediente, Integer amo, String juzgadoCodigo) {
        List<EntradasMigracion> entradas = buscarEntradas(expediente, amo, juzgadoCodigo);
        JuzgadosMigracion juzgado = juzgadosMigracionService.buscarByCodigo(juzgadoCodigo);
        String tablaUbi = juzgado != null ? juzgado.getTablaUbicacion() : null;

        List<EntradasMigracionRecord> resultado = new ArrayList<>();

        for (EntradasMigracion entrada : entradas) {
            // Se obtienen las ubicaciones dependiendo del CU y de la tabla dinámica según
            // juzgado
            List<MovimientosMigracionRecord> ubicaciones = buscarUbicaciones(entrada.getCu(), tablaUbi);

            // Se obtiene el juicio asociado al campo `juicio` de la entrada
            JuiciosMigracion juicio = juiciosMigracionService.buscarJuicio(entrada.getJuicio());

            // Se obtienen los acuerdos:
            List<AcuerdosMigracion> acuerdos = acuerdosMigracionService.buscarAcuerdosPorCu(entrada.getCu());

            // se obtienen sentencias:
            List<AcuerdosMigracion> sentencias = acuerdosMigracionService.buscarSentenciasPorCu(entrada.getCu());

            // Se obtienen amparos:
            List<AmparosMigracion> amparos = amparoMigracionService.buscarPorCu(entrada.getCu());

            // Se obtienen oficios:
            List<OficiosMigracion> oficios = oficiosMigracionService.buscarPorCu(entrada.getCu());

            // Se obtienen los actores:
            List<ActoresMigracion> actores = actoresMigracionService.buscarPorClave(entrada.getCu());

            // Se obtienen los detalles de la promocion si es que existen
            List<DetallesProm> detallesProm = detallesPromService.buscarPorCu(entrada.getCu());

            // Se obtienen los exhortos foraneos:
            List<ExhortoForaneoMigracion> exhortoForaneoMigracion = exhortoForaneoMigracionService
                    .buscarPorJuzgadoOr(juzgado.getCodigo());

            // se obtienen los exhortos capital
            List<ExhortosCapitalMigracion> exortoCapitalMigracion = exhortoCapitalMigracionService
                    .buscarPorJuzgadoOr(juzgado.getCodigo());

            // Se ensambla el registro final
            resultado.add(new EntradasMigracionRecord(
                    entrada, juzgado, ubicaciones,
                    juicio, acuerdos, amparos,
                    oficios, actores, detallesProm,
                    exhortoForaneoMigracion, exortoCapitalMigracion,
                    sentencias));
        }

        return resultado;
    }

    /**
     * Consulta la tabla `entradas` por filtros principales.
     *
     * @param expediente    Número de expediente
     * @param amo           Año
     * @param juzgadoCodigo Código del juzgado
     * @return Lista de entidades `EntradasMigracion`
     */
    private List<EntradasMigracion> buscarEntradas(String expediente, Integer amo, String juzgadoCodigo) {
        return entradasMigracionRepository.buscarPorExpedienteAmoYJuzgado(expediente, amo, juzgadoCodigo);
    }

    /**
     * Busca las ubicaciones asociadas a un CU en una tabla dinámica.
     *
     * @param cu       Código único del expediente (CU)
     * @param tablaUbi Nombre de la tabla dinámica según el juzgado
     * @return Lista de movimientos (ubicaciones) encontrados
     */
    private List<MovimientosMigracionRecord> buscarUbicaciones(String cu, String tablaUbi) {
        if (tablaUbi == null || tablaUbi.isBlank())
            return List.of(); // No hay ubicaciones si no hay tabla

        String sql = "SELECT * FROM " + tablaUbi + " WHERE cu = :cu";
        return jdbcTemplate.query(sql, Map.of("cu", cu), (rs, rowNum) -> new MovimientosMigracionRecord(
                rs.getInt("id_ubicaciones"),
                rs.getString("cu"),
                rs.getObject("id_puesto", Integer.class),
                rs.getObject("fecha", LocalDate.class),
                rs.getString("hora"),
                rs.getString("status"),
                rs.getString("estado"),
                rs.getString("etapa"),
                rs.getString("entrego"),
                rs.getString("recibio"),
                rs.getString("puesto_entrego"),
                rs.getString("puesto_recibio"),
                rs.getString("libro"),
                rs.getObject("num_foja", Integer.class),
                rs.getString("obse"),
                rs.getString("sentido"),
                rs.getString("digitalizado_acu"),
                ""));
    }

    // Retorna el ultimo movimiento de la tabla ubicaciones perteneciente al juzgado
    // que atendio el expediente
    private MovimientosMigracionRecord buscarUltimoMovimiento(String cu, String tablaUbi) {
        String sql = "SELECT " +
                "  u.id_ubicaciones, u.cu, u.id_puesto, u.fecha, u.hora, u.status, u.estado, u.etapa, " +
                "  u.entrego, u.recibio, u.puesto_entrego, u.puesto_recibio, u.libro, u.num_foja, " +
                "  u.obse, u.sentido, u.digitalizado_acu, p.nombre " + // ← solo columnas que mapeas
                "FROM " + tablaUbi + " u " +
                "JOIN acuerdos.puestos p ON u.id_puesto = p.id_puesto " + // ← alias p
                "WHERE u.cu = :cu AND u.status = 'A' " + // ← califica columnas
                "ORDER BY u.id_ubicaciones DESC " + // ← usa la PK/último id real
                "LIMIT 1";

        try {
            return jdbcTemplate.queryForObject(
                    sql,
                    Map.of("cu", cu),
                    (rs, rowNum) -> new MovimientosMigracionRecord(
                            rs.getInt("id_ubicaciones"),
                            rs.getString("cu"),
                            rs.getObject("id_puesto", Integer.class),
                            rs.getObject("fecha", LocalDate.class),
                            rs.getString("hora"),
                            rs.getString("status"),
                            rs.getString("estado"),
                            rs.getString("etapa"),
                            rs.getString("entrego"),
                            rs.getString("recibio"),
                            rs.getString("puesto_entrego"),
                            rs.getString("puesto_recibio"),
                            rs.getString("libro"),
                            rs.getObject("num_foja", Integer.class),
                            rs.getString("obse"),
                            rs.getString("sentido"),
                            rs.getString("digitalizado_acu"),
                            rs.getString("nombre")));
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null; 
        }
    }

    public ResponseEntity<String> migrarExpediente(String expediente, Integer year, String claveJuzgado) {

        // Paso 1: validar que exista tanto el juzgado como la oficialia en el sistema
        // actual
        JuzgadosMigracion juzgadoMigracion = juzgadosMigracionService.buscarByCodigo(claveJuzgado);
        Juzgado juzgado = validaJuzgado(claveJuzgado);

        // Paso 2: traemos información del expediente desde mysql :
        List<EntradasMigracion> entradas = buscarEntradas(expediente, year, claveJuzgado);

        // Paso 3: Buscamos si ya existe el expediente en el sistema por el juzgado y
        // por el expediente.
        Carpeta carpetaExistente = carpetaService.getExpediente(expediente + "/" + year, juzgado);

        if (carpetaExistente != null) {
            ApiResponseFactory.error("El expediente ya se encuentra en el sistema.", "500");
        }

        // PASO 4: traer información de ocomun para ir llenando mi expediente:
        Ocomun oficiliaComunPhp = ocomunService.findByOcomun(entradas.get(0).getCu()); // Tomamos la primera
                                                                                       // coincidencia de entradas.
        //TODO: EVALUAR ESCENARIO DONDE NO HAY OFICIALIA COMUN

        // Paso 5: Se obtiene el juicio asociado al campo `juicio` de la entrada
        JuiciosMigracion juicioPhp = juiciosMigracionService.buscarJuicio(entradas.get(0).getJuicio());

        // Paso 6: se busca la materia de la entrada para vincularla con el tipoJuicio:
        String materiaString = mapMateria(juicioPhp.getMateria());
        Materia materia = materiaService.findByNombre(materiaString);

        // Paso 7 : se busca si existe el tipo de juicio en el sistema actual si no lo
        // crea desactivado:
        TipoJuicio tipoJuicio = tipoJuicioService.findByNombre(juicioPhp.getDescripcion());
        if (tipoJuicio == null) {
            tipoJuicio = crearTipoJuicio(materia, juicioPhp.getDescripcion());
        }

        // paso 8: se busca el concepto del ultimo turnado si no se encuentra lo crea
        MovimientosMigracionRecord ultimoMovimientoPhp = buscarUltimoMovimiento(entradas.get(0).getCu(),
                juzgadoMigracion.getTablaUbicacion());
        Concepto concepto = conceptoService.findByNombre(ultimoMovimientoPhp.estado());
        if (concepto == null) {
            concepto = crearConcepto(ultimoMovimientoPhp.estado(), tipoJuicio);
        }

        // Paso 9: crear la carpeta carpeta:
        Carpeta carpeta = crearCarpetaMigracion(entradas.get(0), oficiliaComunPhp, juzgado, tipoJuicio, concepto,
                null);

        // Paso 10: se crea el registro de migración
        Migraciones migracion = null;
        if (carpeta != null) {
            String observacionesMigracion = "Se ha migrado el expediente principal";
            migracion = migracionesService.createMigraciones(EstadoMigracion.EXPEDIENTE_MIGRADO, observacionesMigracion,
                    ultimoMovimientoPhp.recibio(), ultimoMovimientoPhp.puestoRecibioTBLPuesto(), juzgado, carpeta);
        }

        return ResponseEntity.ok("Expediente migrado correctamente.");
    }

    // metodos de validación:
    private Juzgado validaJuzgado(String claveJuzgado) {

        Juzgado juzgado = juzgadoService.findByClaveJuzgado(claveJuzgado);

        if (juzgado == null) {
            ApiResponseFactory.error("EL juzgado no se encuentra registrado en el sistema", "500");
        }

        return juzgado;
    }

    // metodos de cración:
    private Carpeta crearCarpetaMigracion(EntradasMigracion entrada,
            Ocomun ocomun,
            Juzgado juzgado,
            TipoJuicio tipoJuicio,
            Concepto concepto,
            Migraciones migracion) {

        Carpeta carpeta = new Carpeta()
                .setVersion(0)
                .setFolio(ocomun.getFolio().toString()) // TODO: UID PARA LOS QUE NO TIENEN OFICIALIA.
                .setExpediente(ocomun.getExpediente())  // TODO: DESDE ENTRADAS
                .setSelloEstatus(SelloEstatus.VALIDO)
                .setEstatus(EstadoCarpeta.MIGRADO)
                .setTipoCarpeta(TipoCarpeta.DEMANDA) // TODO: EVALUAR DE DONDE VIENE SI ES JUZGADO ES DENABDAM SU ES JUZGADO PERO DE EXHORTO ES EXHOTHO SI ES SALA ES APELACION
                .setJuzgado(juzgado)
                .setTipoJuicio(tipoJuicio)
                .setPersona(null) //
                .setFechaAsignacion(LocalDateTime.now())
                .setCarpetaPadre(null)
                .setDeterminacionJurisdiccional(null)
                .setSentencia(null)
                .setTipoPieza(null)
                .setConcepto(concepto)
                .setHoras(null)
                .setPrioridad(null)
                .setMigracion(migracion)
                .setCu(documentoService.getCu(juzgado, ocomun.getExpediente())); //TODO: TRAER CU NO CALCULARLO.

        return carpetaRepository.save(carpeta);

    }

    private TipoJuicio crearTipoJuicio(Materia materia, String nombre) {
        TipoJuicio tipoJuicio = new TipoJuicio()
                .setEstado(Estado.INACTIVE)
                .setMateria(materia)
                .setNombre(nombre)
                .setTipoCausa(null)
                .setTipoJuicioPadreOral(null)
                .setTipoJuicioPadreTrad(null);

        return tipoJuicioRepository.save(tipoJuicio);

    }

    private Concepto crearConcepto(String nombre, TipoJuicio tipoJuicio) {
        Concepto concepto = new Concepto()
                .setVersion(0)
                .setNombre(nombre)
                .setDias(null)
                .setEstado(Estado.INACTIVE)
                .setTipoJuicio(tipoJuicio)
                .setRoles(null);

        return conceptoRepository.save(concepto);

    }

    // mapeos
    private String mapMateria(String m) {
        return switch (m) {
            case "P" -> "PENAL";
            case "L" -> "LABORAL";
            case "M" -> "MERCANTIL";
            case "F" -> "FAMILIAR";
            case "C" -> "CIVIL";
            case "E" -> "EXHORTO";
            case "J" -> "JUSTICIA PARA ADOLESCENTES";
            default -> "DESCONOCIDO";
        };
    }

}
