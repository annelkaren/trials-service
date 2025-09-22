package mx.gob.pjpuebla.migracion.readers.entradas;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.migracion.readers.actores.ActoresMigracion;
import mx.gob.pjpuebla.migracion.readers.actores.ActoresMigracionReader;
import mx.gob.pjpuebla.migracion.readers.acuerdos.AcuerdosMigracion;
import mx.gob.pjpuebla.migracion.readers.acuerdos.AcuerdosMigracionSaveRecord;
import mx.gob.pjpuebla.migracion.readers.acuerdos.AcuerdosMigracionReader;
import mx.gob.pjpuebla.migracion.readers.acuerdos.SentenciaMigracionSaveRecord;
import mx.gob.pjpuebla.migracion.readers.amparos.AmparoMigracionReader;
import mx.gob.pjpuebla.migracion.readers.conceptos.ConceptosMigracion;
import mx.gob.pjpuebla.migracion.readers.conceptos.ConceptosMigracionReader;
import mx.gob.pjpuebla.migracion.readers.conceptos.familiar.ConceptosMatFamiliarMigracion;
import mx.gob.pjpuebla.migracion.readers.conceptos.familiar.ConceptosMatFamiliarMigracionReader;
import mx.gob.pjpuebla.migracion.readers.detallesProm.DetallePromSaveRecord;
import mx.gob.pjpuebla.migracion.readers.detallesProm.DetallesProm;
import mx.gob.pjpuebla.migracion.readers.detallesProm.DetallesPromReader;
import mx.gob.pjpuebla.migracion.readers.juicios.JuiciosMigracion;
import mx.gob.pjpuebla.migracion.readers.juicios.JuiciosMigracionReader;
import mx.gob.pjpuebla.migracion.readers.juzgados.JuzgadosMigracion;
import mx.gob.pjpuebla.migracion.readers.juzgados.JuzgadosMigracionReader;
import mx.gob.pjpuebla.migracion.readers.movimientos.MovimientosMigracionRecord;
import mx.gob.pjpuebla.migracion.readers.ocomun.Ocomun;
import mx.gob.pjpuebla.migracion.readers.ocomun.OcomunReader;
import mx.gob.pjpuebla.migracion.readers.ubicaciones.UbicacionesReader;
import mx.gob.pjpuebla.migracion.utils.UtilsMigracion;
import mx.gob.pjpuebla.trials.core.conceptos.Concepto;
import mx.gob.pjpuebla.trials.core.conceptos.ConceptoRepository;
import mx.gob.pjpuebla.trials.core.conceptos.ConceptoService;
import mx.gob.pjpuebla.trials.core.instituciones.Institucion;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaService;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
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
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoService;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;
import mx.gob.pjpuebla.trials.workflow.migracion.MigracionesService;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntradasMigracionReader {

    private final AcuerdosMigracionReader acuerdosMigracionService;
    private final EntradasMigracionRepository entradasMigracionRepository;
    private final JuzgadosMigracionReader juzgadosMigracionService;
    private final JuiciosMigracionReader juiciosMigracionService;
    private final AmparoMigracionReader amparoMigracionService;
    private final ActoresMigracionReader actoresMigracionService;
    private final DetallesPromReader detallesPromService;
  
    private final OcomunReader ocomunService;
    private final ConceptosMigracionReader conceptosMigracionService;
    private final ConceptosMatFamiliarMigracionReader conceptosMatFamiliarMigracionService;
    private final UtilsMigracion utilsMigracion;
    private final UbicacionesReader ubicacionesService;

    // service de sistema actual:
    private final CarpetaRepository carpetaRepository;
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
        EntradasMigracion entrada = requireByExpedienteAmoJuzgado(expediente, amo, juzgadoCodigo);

        // Buscar juzgado:
        JuzgadosMigracion juzgado = juzgadosMigracionService.requireByCodigo(juzgadoCodigo);

        // Definir tabla ubicacion:
        String tablaUbi = juzgado != null ? juzgado.getTablaUbicacion() : null;

        // Se obtienen las ubicaciones dependiendo del CU y de la tabla dinámica según
        // juzgado
        MovimientosMigracionRecord ubicaciones = ubicacionesService.buscarUltimoMovimiento(entrada.getCu(), tablaUbi);

        // Se obtiene el juicio asociado al campo `juicio` de la entrada
        JuiciosMigracion juicio = juiciosMigracionService.buscarJuicio(entrada.getJuicio());


        // Se obtienen amparos:
        // List<AmparosMigracion> amparos =
        // amparoMigracionService.buscarPorCu(entrada.getCu());


        // Se obtienen los actores:
        List<ActoresMigracion> actores = actoresMigracionService.buscarPorClave(entrada.getCu());

        // Se obtienen los detalles de la promocion si es que existen
        List<DetallesProm> detallesProm = detallesPromService.buscarPorCu(entrada.getCu());

        // Se ensambla el registro final
        return new EntradasMigracionRecord(entrada, juzgado, ubicaciones, juicio, actores, detallesProm);
    }

    /**
     * Consulta la tabla `entradas` por filtros principales.
     *
     * @param expediente    Número de expediente
     * @param amo           Año
     * @param juzgadoCodigo Código del juzgado
     * @return Lista de entidades `EntradasMigracion`
     */
    public EntradasMigracion requireByExpedienteAmoJuzgado(String expediente, Integer amo, String juzgadoCodigo) {
        Optional<EntradasMigracion> entradasOptional = entradasMigracionRepository
                .findTopByExpedienteAndAmoAndJuzgadoAndStatusOrderByIdDesc(expediente, amo, juzgadoCodigo, "A");

        if (entradasOptional.isPresent()) {
            return entradasOptional.get();
        }

        throw new NotFoundException("No se encontro el expediente", expediente);
    }


    @Transactional
    public ResponseEntity<String> migrarExpediente(String expediente, Integer year, String claveJuzgado) {

        // Paso 1: validar que exista tanto el juzgado como la oficialia en el sistema
        JuzgadosMigracion juzgadoMigracion = juzgadosMigracionService.requireByCodigo(claveJuzgado);
        Juzgado juzgado = juzgadosMigracionService.getJuzgadoFromSistema(claveJuzgado);

        // Paso 2: traemos información del expediente desde mysql :
        EntradasMigracion entrada = requireByExpedienteAmoJuzgado(expediente, year, claveJuzgado);

        // Paso 3: Buscamos si ya existe el expediente en el sistema por el juzgado.
        assertExpedienteDisponible(expediente + "/" + year, juzgado);
      

        // PASO 4: traer información de ocomun para ir llenando mi expediente:
        Optional<Ocomun> oficiliaComunPhpOptional = ocomunService.findByOcomun(entrada.getCu()); 
        Ocomun oficiliaComunPhp = oficiliaComunPhpOptional.isPresent() ? oficiliaComunPhpOptional.get() : null;

        // Paso 5: Se obtiene el juicio asociado al campo `juicio` de la entrada
        JuiciosMigracion juicioPhp = juiciosMigracionService.buscarJuicio(entrada.getJuicio());

        // Paso 6: se busca la materia de la entrada para vincularla con el tipoJuicio:
        String materiaString = utilsMigracion.mapMateria(juicioPhp.getMateria());
        Materia materia = materiaService.findByNombre(materiaString);

        // Paso 7 : se busca si existe el tipo de juicio en el sistema actual si no lo crea desactivado
        TipoJuicio tipoJuicio = tipoJuicioService.findByNombre(juicioPhp.getDescripcion());
        if (tipoJuicio == null) {
            tipoJuicio = juiciosMigracionService.crearTipoJuicio(materia, juicioPhp.getDescripcion());
        }

        // paso 8: se busca el concepto del ultimo turnado si no se encuentra lo crea
        MovimientosMigracionRecord ultimoMovimientoPhp = ubicacionesService.buscarUltimoMovimiento(entrada.getCu(), juzgadoMigracion.getTablaUbicacion());
        String ultimoMovimientoText = ultimoMovimientoPhp.estado() != null ? ultimoMovimientoPhp.estado() : "Archivo";

        Integer diasConcepto = getDiasConceptoMigracion(tipoJuicio, ultimoMovimientoText);

        Optional<Concepto> conceptoOptional = conceptoService.findByNombreAndTipoJuicio(ultimoMovimientoText, tipoJuicio);
        Concepto concepto = conceptoOptional.isPresent() ? conceptoOptional.get() : null;
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
        crearAnexosMigracion(oficiliaComunPhp.getAnexos(), documento);

        // paso 12: buscar a las personas involucradas en la entrada: actores,
        // demandados o terceros involucrados:
        List<ActoresMigracion> personas = actoresMigracionService.buscarPorClave(entrada.getCu());

        // paso 13: crear registro de actores, demandados y terceros involucrados:
        crearPersonaDocumento(personas, tipoJuicio, carpeta);

        // Migración de documentos:

        // paso 14 crear acuerdos y sentencias:

        List<AcuerdosMigracion> acuerdos = acuerdosMigracionService.buscarAcuerdosPorCu(entrada.getCu());
        List<AcuerdosMigracion> sentencias = acuerdosMigracionService.buscarSentenciasPorCu(entrada.getCu());
        List<DetallesProm> promociones = detallesPromService.buscarPorCu(entrada.getCu());
        
        createAcuerdoMigracion(acuerdos, carpeta);
        createSentenciaMigracion(sentencias, carpeta);
        createPromocionesMigracion(promociones, carpeta);

        // Paso 15: se crea el registro de migración
        
        String observacionesMigracion = "Se ha migrado el expediente principal";
        migracionesService.createMigraciones(EstadoMigracion.MIGRADO_COMPLETADO, observacionesMigracion,
                    ultimoMovimientoPhp.recibio(), ultimoMovimientoPhp.puestoRecibioTBLPuesto(), juzgado, carpeta);
        

        return ResponseEntity.ok("Expediente migrado correctamente.");
    }


    /**
     * Busca una {@link Carpeta} por su número de expediente y juzgado.
     * <p>
     * No lanza excepciones: si no existe, devuelve {@link Optional#empty()}.
     * </p>
     *
     * @param expediente Número de expediente normalizado (por ejemplo, "123/2025"). No nulo ni vacío.
     * @param juzgado    Juzgado propietario del expediente. No nulo.
     * @return {@link Optional} con la carpeta si existe; vacío en caso contrario.
     * @throws IllegalArgumentException si los parámetros son inválidos.
     */
    @Transactional(readOnly = true)
    private Optional<Carpeta> findCarpeta(String expediente, Juzgado juzgado) {
        String exp = utilsMigracion.normalizeExpediente(expediente);
        utilsMigracion.requireNonNullJuzgado(juzgado);
        return carpetaRepository.findByExpedienteAndJuzgado(exp, juzgado);
    }

       /**
     * Obtiene una {@link Carpeta} por expediente y juzgado, fallando si no existe.
     * <p>
     * Útil en flujos donde la carpeta es obligatoria y se desea fallar temprano.
     * </p>
     *
     * @param expediente Número de expediente normalizado (por ejemplo, "123/2025"). No nulo ni vacío.
     * @param juzgado    Juzgado propietario del expediente. No nulo.
     * @return La carpeta encontrada (nunca {@code null}).
     * @throws IllegalArgumentException si los parámetros son inválidos.
     * @throws NotFoundException        si no existe una carpeta con ese expediente en ese juzgado.
     */
    @Transactional(readOnly = true)
    private Carpeta requireCarpeta(String expediente, Juzgado juzgado) {
        return findCarpeta(expediente, juzgado)
                .orElseThrow(() -> new NotFoundException(
                        "Carpeta (expediente) no encontrada para el juzgado indicado.",
                        expediente
                ));
    }

        /**
     * Verifica que NO exista una {@link Carpeta} con el expediente y juzgado dados.
     * <p>
     * Útil antes de crear/insertar: si ya existe, lanza {@link ConstraintViolationException}.
     * </p>
     *
     * @param expediente Número de expediente normalizado (por ejemplo, "123/2025"). No nulo ni vacío.
     * @param juzgado    Juzgado propietario del expediente. No nulo.
     * @throws IllegalArgumentException       si los parámetros son inválidos.
     * @throws ConstraintViolationException   si ya existe una carpeta con ese expediente en ese juzgado.
     */
    @Transactional(readOnly = true)
    public void assertExpedienteDisponible(String expediente, Juzgado juzgado) {
        String exp = utilsMigracion.normalizeExpediente(expediente);
        utilsMigracion.requireNonNullJuzgado(juzgado);

        // Si tienes existsByExpedienteAndJuzgado en el repo, úsalo para eficiencia.
        Optional<Carpeta> existente = carpetaRepository.findByExpedienteAndJuzgado(exp, juzgado);
        if (existente.isPresent()) {
            Carpeta c = existente.get();
            throw new ConstraintViolationException(
                    "El expediente ya se encuentra en el sistema",
                    c.getId() != null ? c.getId().toString() : exp
            );
        }
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

            TipoPartes tipoPartes = findOrCreateTipoPartes(tipoJuicio, utilsMigracion.mapTipoPartesMigracion(persona.getTipo()));

            // campos que se llenan unicamente para tipo de juicio familiar y tipo de
            // sistema oralidad:
            String ine = "";
            String curp = "";
            String celular = "";
            String correoElectronico = "";
            String domicilio = "";

            // Configurar notificaciones:
            TipoNotificacion tipoNotificacion = utilsMigracion.mapTipoNotificacion(persona.getTipoNotificacion());
            String correoElectronicoNotificacion = null;

            PersonaDocumento personaObj = createPersonaDocumento(
                    persona.getNombre(),
                    utilsMigracion.mapTipoPersona(persona.getTipoPersona()),
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

    private List<Documento> createAcuerdoMigracion(List<AcuerdosMigracion> acuerdos, Carpeta carpeta) {
        List<Documento> documentosAcuerdos = new ArrayList<>();

        acuerdos.forEach(acuerdo -> {
            List<String> rubros = utilsMigracion.mapRubros(acuerdo.getResumen());
            String rubroPrincipal = !rubros.isEmpty() ? rubros.get(0) : "";

            AcuerdosMigracionSaveRecord data = new AcuerdosMigracionSaveRecord(
                    carpeta,
                    rubroPrincipal,
                    acuerdo.getFechaResolucion(),
                    rubros,
                    acuerdo.getClave().toString(),
                    acuerdo.getRuta(),
                    acuerdo.getFecha());

            Documento acuerdoCreado = documentoService.createAcuerdoMigracion(data);
            documentosAcuerdos.add(acuerdoCreado);

        });

        return documentosAcuerdos;
    }

    private List<Documento> createSentenciaMigracion(List<AcuerdosMigracion> sentencias, Carpeta carpeta){
        List<Documento> documentosSentencias = new ArrayList<>();

        sentencias.forEach(sentencia -> {

            SentenciaMigracionSaveRecord data = new SentenciaMigracionSaveRecord(
                carpeta,
                sentencia.getFechaResolucion(),
                utilsMigracion.mapTipoSentencia(sentencia.getResumen()),
                utilsMigracion.mapTipoResolucionSentencia(sentencia.getSentencia()),
                sentencia.getClave().toString(),
                sentencia.getRuta(),
                sentencia.getFecha()
            );

            Documento sentenciaCreada = documentoService.createSentenciaMigracion(data);
            documentosSentencias.add(sentenciaCreada);
        });

        return documentosSentencias;
    }

    private List<Documento> createPromocionesMigracion(List<DetallesProm> detallesProm, Carpeta carpeta) {
        List<Documento> promocionesCreadas = new ArrayList<>();

        detallesProm.forEach(promocion -> {
            // pasamos el tipo de promoción 1 escrito 2 oficio
            // Pasamos la descripción si es promocion electronica o una promocion .
            TipoPromocion tipoPromocion = utilsMigracion.mapTipoPromocion(promocion.getTipo(), promocion.getDescrip());

            // crear promoción:
            DetallePromSaveRecord detallePromocion = new DetallePromSaveRecord
            (carpeta, 
            tipoPromocion,
            promocion.getId().toString(),
            promocion.getArchivo(),
            promocion.getAcuerdo());

            Documento promocionCreada = documentoService.createPromocionMigracion(detallePromocion);
            // Crear anexos:
            createAnexosDePromociones(promocion.getAnexos(), promocionCreada);

            promocionesCreadas.add(promocionCreada);
        });

        return promocionesCreadas;
    }

    private Integer getDiasConceptoMigracion(TipoJuicio tipoJuicio, String estado) {

        if (tipoJuicio.getMateria().getNombre().equalsIgnoreCase("FAMILIAR") && tipoJuicio.getTipoSistema().getNombre().equalsIgnoreCase("Oral")) {
            ConceptosMatFamiliarMigracion concepto = conceptosMatFamiliarMigracionService
                    .findConceptoMatFamiliarByClave(estado);
            return concepto != null ? Integer.parseInt(concepto.getDias().trim()) : 0;
        } else {
            ConceptosMigracion concepto = conceptosMigracionService.findConceptoByClave(estado);
            return concepto != null ? Integer.parseInt(concepto.getDias().trim()) : 0;
        }
    }

    private List<Anexo> createAnexosDePromociones(String anexos, Documento documento) {
        if (anexos == null || anexos.isBlank() || anexos.contentEquals("Sin Anexos")) {
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
