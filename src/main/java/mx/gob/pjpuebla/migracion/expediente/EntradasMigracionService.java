package mx.gob.pjpuebla.migracion.expediente;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.migracion.actores.ActoresMigracion;
import mx.gob.pjpuebla.migracion.actores.ActoresMigracionService;
import mx.gob.pjpuebla.migracion.actores.complementoCampos.ActorGeneralMigracion;
import mx.gob.pjpuebla.migracion.actores.complementoCampos.ActorGeneralMigracionRepository;
import mx.gob.pjpuebla.migracion.actores.complementoCampos.DemandadoGeneralMigracion;
import mx.gob.pjpuebla.migracion.actores.complementoCampos.DemandadoGeneralMigracionRepository;
import mx.gob.pjpuebla.migracion.acuerdos.AcuerdosMigracion;
import mx.gob.pjpuebla.migracion.acuerdos.AcuerdosMigracionService;
import mx.gob.pjpuebla.migracion.amparos.AmparoMigracionService;
import mx.gob.pjpuebla.migracion.conceptos.ConceptosMigracion;
import mx.gob.pjpuebla.migracion.conceptos.ConceptosMigracionService;
import mx.gob.pjpuebla.migracion.conceptos.familiar.ConceptosMatFamiliarMigracion;
import mx.gob.pjpuebla.migracion.conceptos.familiar.ConceptosMatFamiliarMigracionService;
import mx.gob.pjpuebla.migracion.detallesProm.DetallesProm;
import mx.gob.pjpuebla.migracion.detallesProm.DetallesPromService;
import mx.gob.pjpuebla.migracion.exhortoCapital.ExhortosCapitalMigracionService;
import mx.gob.pjpuebla.migracion.exhortoForaneo.ExhortoForaneoMigracionService;
import mx.gob.pjpuebla.migracion.juicios.JuiciosMigracion;
import mx.gob.pjpuebla.migracion.juicios.JuiciosMigracionService;
import mx.gob.pjpuebla.migracion.juzgados.JuzgadosMigracion;
import mx.gob.pjpuebla.migracion.juzgados.JuzgadosMigracionService;
import mx.gob.pjpuebla.migracion.movimientos.MovimientosMigracionRecord;
import mx.gob.pjpuebla.migracion.ocomun.Ocomun;

import mx.gob.pjpuebla.migracion.ocomun.OcomunService;
import mx.gob.pjpuebla.migracion.oficios.OficiosMigracionService;
import mx.gob.pjpuebla.trials.core.conceptos.Concepto;
import mx.gob.pjpuebla.trials.core.conceptos.ConceptoRepository;
import mx.gob.pjpuebla.trials.core.conceptos.ConceptoService;
import mx.gob.pjpuebla.trials.core.instituciones.Institucion;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoService;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaService;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioService;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartes;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;
import mx.gob.pjpuebla.trials.error.ConstraintViolationException;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.EstadoMigracion;
import mx.gob.pjpuebla.trials.util.enums.Migrado;
import mx.gob.pjpuebla.trials.util.enums.Rol;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;
import mx.gob.pjpuebla.trials.util.enums.TipoPromocion;
import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaService;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoService;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;
import mx.gob.pjpuebla.trials.workflow.migracion.Migraciones;
import mx.gob.pjpuebla.trials.workflow.migracion.MigracionesService;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;

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
    private final ActorGeneralMigracionRepository actorGeneralMigracionRepository;
    private final DemandadoGeneralMigracionRepository demandadoGeneralMigracionRepository;
    private final ConceptosMigracionService conceptosMigracionService;
    private final ConceptosMatFamiliarMigracionService conceptosMatFamiliarMigracionService;

    // service de sistema actual:
    private final JuzgadoService juzgadoService;
    private final CarpetaService carpetaService;
    private final CarpetaRepository carpetaRepository;
    private final TipoJuicioRepository tipoJuicioRepository;
    private final MateriaService materiaService;
    private final TipoJuicioService tipoJuicioService;
    private final ConceptoService conceptoService;
    private final ConceptoRepository conceptoRepository;
    private final MigracionesService migracionesService;
    private final DocumentoRepository documentoRepository;
    private final AnexoRepository anexoRepository;
    private final TipoPartesRepository tipoPartesRepository;
    private final PersonaDocumentoRepository personaDocumentoRepository;
    private final DocumentoService documentoService;

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
    public EntradasMigracionRecord buscarPorFiltros(String expediente, Integer amo, String juzgadoCodigo) {

        // Buscar entrada:
        EntradasMigracion entrada = buscarEntradas(expediente, amo, juzgadoCodigo);

        // Buscar juzgado:
        JuzgadosMigracion juzgado = juzgadosMigracionService.buscarByCodigo(juzgadoCodigo);

        // Definir tabla ubicacion:
        String tablaUbi = juzgado != null ? juzgado.getTablaUbicacion() : null;

        // Se obtienen las ubicaciones dependiendo del CU y de la tabla dinámica según
        // juzgado
        MovimientosMigracionRecord ubicaciones = buscarUltimoMovimiento(entrada.getCu(), tablaUbi);

        // Se obtiene el juicio asociado al campo `juicio` de la entrada
        JuiciosMigracion juicio = juiciosMigracionService.buscarJuicio(entrada.getJuicio());

        // Se obtienen los acuerdos:
         List<AcuerdosMigracion> acuerdos = acuerdosMigracionService.buscarAcuerdosPorCu(entrada.getCu());

        // se obtienen sentencias:
        // List<AcuerdosMigracion> sentencias =
        // acuerdosMigracionService.buscarSentenciasPorCu(entrada.getCu());

        // Se obtienen amparos:
        // List<AmparosMigracion> amparos =
        // amparoMigracionService.buscarPorCu(entrada.getCu());

        // Se obtienen oficios:
        // List<OficiosMigracion> oficios =
        // oficiosMigracionService.buscarPorCu(entrada.getCu());

        // Se obtienen los actores:
        List<ActoresMigracion> actores = actoresMigracionService.buscarPorClave(entrada.getCu());

        // Se obtienen los detalles de la promocion si es que existen
        List<DetallesProm> detallesProm = detallesPromService.buscarPorCu(entrada.getCu());

        // Se obtienen los exhortos foraneos:
        // List<ExhortoForaneoMigracion> exhortoForaneoMigracion =
        // exhortoForaneoMigracionService
        // .buscarPorJuzgadoOr(juzgado.getCodigo());

        // se obtienen los exhortos capital
        // List<ExhortosCapitalMigracion> exortoCapitalMigracion =
        // exhortoCapitalMigracionService
        // .buscarPorJuzgadoOr(juzgado.getCodigo());

        // Se ensambla el registro final
        return new EntradasMigracionRecord(entrada, juzgado, ubicaciones, juicio, actores, acuerdos);
    }

    /**
     * Consulta la tabla `entradas` por filtros principales.
     *
     * @param expediente    Número de expediente
     * @param amo           Año
     * @param juzgadoCodigo Código del juzgado
     * @return Lista de entidades `EntradasMigracion`
     */
    private EntradasMigracion buscarEntradas(String expediente, Integer amo, String juzgadoCodigo) {
        Optional<EntradasMigracion> entradasOptional = entradasMigracionRepository
                .findTopByExpedienteAndAmoAndJuzgadoAndStatusOrderByIdDesc(expediente, amo, juzgadoCodigo, "A");

        if (entradasOptional.isPresent()) {
            return entradasOptional.get();
        }

        throw new NotFoundException("No se encontro el expediente", expediente);
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

    @Transactional
    public ResponseEntity<String> migrarExpediente(String expediente, Integer year, String claveJuzgado) {

        // Paso 1: validar que exista tanto el juzgado como la oficialia en el sistema
        // actual
        JuzgadosMigracion juzgadoMigracion = juzgadosMigracionService.buscarByCodigo(claveJuzgado);
        Juzgado juzgado = validaJuzgado(claveJuzgado);
        if (juzgado == null) {
            throw new NotFoundException("Juzgado no encontrado, revise que este dada de alta su clave.", claveJuzgado);
        }

        // Paso 2: traemos información del expediente desde mysql :
        EntradasMigracion entrada = buscarEntradas(expediente, year, claveJuzgado);

        // Paso 3: Buscamos si ya existe el expediente en el sistema por el juzgado y
        Carpeta carpetaExistente = carpetaService.getExpediente(expediente + "/" + year, juzgado);

        if (carpetaExistente != null) {
            throw new ConstraintViolationException("El expediente ya se encuentra en el sistema",
                    carpetaExistente.getId().toString());
        }

        // PASO 4: traer información de ocomun para ir llenando mi expediente:
        Ocomun oficiliaComunPhp = ocomunService.findByOcomun(entrada.getCu()); // Tomamos la primera
                                                                               // coincidencia de entradas.
        // TODO: EVALUAR ESCENARIO DONDE NO HAY OFICIALIA COMUN

        // Paso 5: Se obtiene el juicio asociado al campo `juicio` de la entrada
        JuiciosMigracion juicioPhp = juiciosMigracionService.buscarJuicio(entrada.getJuicio());

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
        MovimientosMigracionRecord ultimoMovimientoPhp = buscarUltimoMovimiento(entrada.getCu(), juzgadoMigracion.getTablaUbicacion());
        String ultimoMovimientoText = ultimoMovimientoPhp.estado() != null ? ultimoMovimientoPhp.estado() : "Archivo";

        Integer diasConcepto = getDiasConceptoMigracion(tipoJuicio,ultimoMovimientoText);

        Concepto concepto = conceptoService.findByNombreAndTipoJuicio(ultimoMovimientoText, tipoJuicio);
        if (concepto == null) {
            concepto = crearConcepto(ultimoMovimientoText, tipoJuicio, diasConcepto);
        }

        // Paso 9: crear la carpeta carpeta:
        Carpeta carpeta = crearCarpetaMigracion(entrada, oficiliaComunPhp, juzgado, tipoJuicio, concepto);

        // paso 10: crear documento asociado a la carpeta, en este metodo se crea la
        // demanda inicial:
        Documento documento = crearDocumentoMigracion(null, carpeta, new DocumentoData(), oficiliaComunPhp, null, null,
                null, null);

        // paso 11: crear registro de los anexos:
        List<Anexo> anexos = crearAnexosMigracion(oficiliaComunPhp.getAnexos(), documento);

        // paso 12: buscar a las personas involucradas en la entrada: actores,
        // demandados o terceros involucrados:
        List<ActoresMigracion> personas = actoresMigracionService.buscarPorClave(entrada.getCu());

        // paso 13: crear registro de actores, demandados y terceros involucrados:
        List<PersonaDocumento> personaDocumentoActor = crearPersonaDocumento(personas, tipoJuicio, carpeta);

        //Migración de documentos:

        // paso 14 obtener promociones:
        List<DetallesProm> detallesProm = detallesPromService.buscarPorCu(entrada.getCu());
        createPromocionesMigracion(detallesProm, carpeta);
        //List<AcuerdosMigracion> acuerdos = acuerdosMigracionService.buscarAcuerdosPorCu(entrada.getCu());

        // Paso 15: se crea el registro de migración
        Migraciones migracion = null;
        if (carpeta != null) {
            String observacionesMigracion = "Se ha migrado el expediente principal";
            migracion = migracionesService.createMigraciones(EstadoMigracion.MIGRADO_COMPLETADO, observacionesMigracion,
                    ultimoMovimientoPhp.recibio(), ultimoMovimientoPhp.puestoRecibioTBLPuesto(), juzgado, carpeta);
        }

        return ResponseEntity.ok("Expediente migrado correctamente.");
    }

    // metodos de validación:
    private Juzgado validaJuzgado(String claveJuzgado) {

        Juzgado juzgado = juzgadoService.findByClaveJuzgado(claveJuzgado);

        return juzgado;
    }

    // metodos de cración:
    private Carpeta crearCarpetaMigracion(EntradasMigracion entrada,
            Ocomun ocomun,
            Juzgado juzgado,
            TipoJuicio tipoJuicio,
            Concepto concepto) {

        Carpeta carpeta = new Carpeta()
                .setVersion(0)
                .setFolio(ocomun != null ? ocomun.getFolio().toString() : UUID.randomUUID().toString())
                .setExpediente(entrada.getExpediente() + "/" + entrada.getAmo())
                .setSelloEstatus(SelloEstatus.VALIDO)
                .setEstatus(EstadoCarpeta.MIGRADO)
                .setTipoCarpeta(TipoCarpeta.DEMANDA) // TODO: EVALUAR DE DONDE VIENE SI ES JUZGADO ES DENABDAM SU ES
                                                     // JUZGADO PERO DE EXHORTO ES EXHOTHO SI ES SALA ES APELACION
                .setJuzgado(juzgado)
                .setTipoJuicio(tipoJuicio)
                .setPersona(null)
                .setFechaAsignacion(LocalDateTime.now())
                .setCarpetaPadre(null)
                .setDeterminacionJurisdiccional(null)
                .setSentencia(null)
                .setTipoPieza(null)
                .setConcepto(concepto)
                .setHoras(null)
                .setPrioridad(null)
                .setMigrado(Migrado.SI)
                .setCu(entrada.getCu());

        return carpetaRepository.save(carpeta);

    }

    private Documento crearDocumentoMigracion(TipoDocumento tipoDocumento, Carpeta carpeta,
            DocumentoData data, Ocomun ocomun, String folio, Concepto concepto, Institucion institucion,
            Documento documentoRelacionado) {

        Documento documento = new Documento()
                .setVersion(0)
                .setTipoDocumento(tipoDocumento)
                .setData(data)
                .setRuta(ocomun != null ? ocomun.getRutaDigitalizacion() : "")
                .setCarpeta(carpeta)
                .setPersona(null)
                .setFechaAsignacion(LocalDateTime.now())
                .setEstatus(EstadoCarpeta.MIGRADO)
                .setFolio(folio)
                .setConcepto(concepto)
                .setInstitucion(institucion)
                .setAcuerdoRespuesta(documentoRelacionado);

        return documentoRepository.save(documento);

    }

    @Transactional
    private List<Anexo> crearAnexosMigracion(String anexos, Documento documento) {
        if (documento == null) {
            throw new IllegalArgumentException("El documento es obligatorio.");
        }
        if (anexos == null || anexos.isBlank()) {
            return List.of();
        }

        // Divide por coma ignorando espacios, elimina vacíos y duplicados, mapea a
        // entidad
        List<Anexo> toSave = Pattern.compile("\\s*,\\s*")
                .splitAsStream(anexos)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .map(nombre -> new Anexo()
                        .setNombre(nombre)
                        .setDocumento(documento))
                .toList();

        return toSave.isEmpty() ? List.of() : anexoRepository.saveAll(toSave);
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

    private Concepto crearConcepto(String nombre, TipoJuicio tipoJuicio, Integer dias) {
        Concepto concepto = new Concepto()
                .setVersion(0)
                .setNombre(nombre)
                .setDias(dias)
                .setEstado(Estado.INACTIVE)
                .setTipoJuicio(tipoJuicio)
                .setRoles(null);

        return conceptoRepository.save(concepto);

    }

    private TipoPartes findOrCreateTipoPartes(TipoJuicio tipoJuicio, String nombreTipoParte) {
        Optional<TipoPartes> tipoParte = tipoPartesRepository.findByNombreAndTipoJuicioId(nombreTipoParte,
                tipoJuicio.getId());

        if (tipoParte.isPresent()) {
            return tipoParte.get();
        }

        TipoPartes tipoParteNew = new TipoPartes()
                .setEstado(Estado.INACTIVE)
                .setNombre(nombreTipoParte)
                .setTipoJuicio(tipoJuicio);

        return tipoPartesRepository.save(tipoParteNew);

    }

    private List<PersonaDocumento> crearPersonaDocumento(List<ActoresMigracion> personas, TipoJuicio tipoJuicio,
            Carpeta carpeta) {
        // Declaramos la lista de personasDocumentos la cual nos servira para guardar
        // todo:
        List<PersonaDocumento> personaDocumento = new ArrayList<>();

        personas.forEach(persona -> {
            // determinamos si la persona implicada es un actor, demandado:

            TipoPartes tipoPartes = findOrCreateTipoPartes(tipoJuicio, mapTipoPartesMigracion(persona.getTipo()));

            // campos que se llenan unicamente para tipo de juicio familiar y tipo de
            // sistema oralidad:
            String ine = null;
            String curp = null;
            String celular = null;
            String correoElectronico = null;
            String domicilio = null;

            // Configurar notificaciones:
            TipoNotificacion tipoNotificacion = mapTipoNotificacion(persona.getTipoNotificacion());
            String correoElectronicoNotificacion = null;

            PersonaDocumento personaObj = createPersonaDocumento(
                    persona.getNombre(),
                    mapTipoPersona(persona.getTipoPersona()),
                    Rol.PRINCIPAL,
                    carpeta,
                    tipoPartes,
                    ine,
                    curp,
                    celular,
                    correoElectronico,
                    domicilio,
                    tipoNotificacion,
                    correoElectronicoNotificacion);

            personaDocumento.add(personaObj);
        });

        return personaDocumentoRepository.saveAll(personaDocumento);

    }

    private PersonaDocumento createPersonaDocumento(String nombre, String tipoPersona, Rol rol,
            Carpeta carpeta, TipoPartes tipoPartes, String ine, String curp, String celular, String correoElectronico,
            String domicilio, TipoNotificacion tipoNotificacion, String correoNotificacion) {
        return new PersonaDocumento()
                .setNombre(nombre)
                .setTipoPersona(tipoPersona) // Moral o fisica
                .setRol(rol) // PRINCIPAL O SECUNDARIO
                .setCarpeta(carpeta)
                .setTipoPartes(tipoPartes)
                .setIne(ine)
                .setCurp(curp)
                .setCelular(celular)
                .setCorreoElectronico(correoElectronico)
                .setDomicilio(domicilio)
                .setTipoNotificacion(tipoNotificacion)
                .setCorreoNotificacion(correoNotificacion);

    }

    private List<Documento> createPromocionesMigracion(List<DetallesProm> detallesProm, Carpeta carpeta){
        List<Documento> promocionesCreadas = new ArrayList<>();

        detallesProm.forEach(promocion -> {
             // pasamos el tipo de promoción 1 escrito 2 oficio
             // Pasamos la descripción si es promocion electronica o una promocion .
            TipoPromocion tipoPromocion = mapTipoPromocion(promocion.getTipo(), promocion.getDescrip());
            
            //crear promoción: 
            Documento promocionCreada = documentoService.createPromocionMigracion(carpeta, tipoPromocion, promocion.getId().toString(), promocion.getArchivo());
            // Crear anexos: 
            createAnexosDePromociones(promocion.getAnexos(), promocionCreada);
                       
            
            promocionesCreadas.add(promocionCreada);
        });

        return promocionesCreadas;
    }

    private Integer getDiasConceptoMigracion(TipoJuicio tipoJuicio, String estado) {

        if (tipoJuicio.getMateria().getNombre() == "FAMILIAR" && tipoJuicio.getTipoSistema().getNombre() == "Oral") {
            ConceptosMatFamiliarMigracion concepto = conceptosMatFamiliarMigracionService.findConceptoMatFamiliarByClave(estado);
            return   concepto != null ? concepto.getDias() : 0;
        } else {
            ConceptosMigracion concepto = conceptosMigracionService.findConceptoByClave(estado);
            return concepto != null ? concepto.getDias() : 0;
        }
    }

    private ActorGeneralMigracion findByActorGeneralMigracion(String cuActor) {
        Optional<ActorGeneralMigracion> actorDatosGenerales = actorGeneralMigracionRepository.findBycuActor(cuActor);
        if (actorDatosGenerales.isPresent()) {
            return actorDatosGenerales.get();
        }

        return null;
    }

    private DemandadoGeneralMigracion findByDemandadoGeneralMigracion(String cuDemandado) {
        Optional<DemandadoGeneralMigracion> demandadoDatosGenerales = demandadoGeneralMigracionRepository
                .findBycuDem(cuDemandado);

        if (demandadoDatosGenerales.isPresent()) {
            return demandadoDatosGenerales.get();
        }

        return null;
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

    private String mapTipoPersona(String tipoPersona) {
        return switch (tipoPersona) {
            case "F" -> "fisica";
            case "M" -> "moral";
            default -> "";
        };
    }

    private String mapTipoPartesMigracion(String tipo) {
        return switch (tipo) {
            case "D" -> "Demandado";
            case "A" -> "Actor";
            default -> "";
        };
    }

    private TipoNotificacion mapTipoNotificacion(String tipoNotificacion) {
        if (tipoNotificacion == null) {
            return TipoNotificacion.NINGUNO;
        }

        return switch (tipoNotificacion) {
            case "CO" -> TipoNotificacion.CORREO_ELECTRONICO;
            case "DN" -> TipoNotificacion.DOMICILIO;
            case "DE" -> TipoNotificacion.EMPLAZAMIENTO;
            case "ES", "E" -> TipoNotificacion.ESTRADO;
            case "EX" -> TipoNotificacion.EXHORTO;
            case "ED" -> TipoNotificacion.EDITCTOS;
            default -> TipoNotificacion.NINGUNO;
        };
    }

    private TipoPromocion mapTipoPromocion(String tipoPromocion, String descripcion){
        
        if(descripcion == "PROMOCION ELECTRONICA"){
            return TipoPromocion.CORREO_ELECTRONICO;
        }
        
        if(tipoPromocion == null || tipoPromocion != ""){
            return TipoPromocion.ESCRITO;
        }

        return switch(tipoPromocion) {
            case "0","1","3","E" -> TipoPromocion.ESCRITO;
            case "2" -> TipoPromocion.OFICIO;
            default -> TipoPromocion.ESCRITO;
        };
    }

    private List<Anexo> createAnexosDePromociones(String anexos, Documento documento){
        if(anexos == null || anexos.isBlank() || anexos.contentEquals("Sin Anexos")){
            return List.of();
        }

        List<Anexo> toSave = Pattern.compile("\\s*.\\s*")
            .splitAsStream(anexos)
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .distinct()
            .map(nombre -> new Anexo()
                .setNombre(nombre)
                .setDocumento(documento))
            .toList();
            
        return toSave.isEmpty() ? List.of() : anexoRepository.saveAll(toSave);
    }
}
