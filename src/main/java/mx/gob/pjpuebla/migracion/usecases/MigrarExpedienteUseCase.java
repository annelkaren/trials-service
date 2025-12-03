package mx.gob.pjpuebla.migracion.usecases;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.migracion.readers.entradas.EntradasMigracion;
// Readers (legacy)
import mx.gob.pjpuebla.migracion.readers.entradas.EntradasMigracionReader;
import mx.gob.pjpuebla.migracion.readers.entradas.EntradasMigracionRepository;
import mx.gob.pjpuebla.migracion.readers.entradasUsuarios.EntradasUsuarioMigracionReader;
import mx.gob.pjpuebla.migracion.readers.exhortoCapital.ExhortoCapitalMigracionSaveRecord;
import mx.gob.pjpuebla.migracion.readers.exhortoCapital.ExhortosCapitalMigracion;
import mx.gob.pjpuebla.migracion.readers.exhortoCapital.ExhortosCapitalMigracionReader;
import mx.gob.pjpuebla.migracion.readers.exhortoCapital.ExhortosCapitalMigracionRepository;
import mx.gob.pjpuebla.migracion.readers.exhortoForaneo.ExhortoForaneoMigracion;
import mx.gob.pjpuebla.migracion.readers.exhortoForaneo.ExhortoForaneoMigracionReader;
import mx.gob.pjpuebla.migracion.readers.exhortoForaneo.ExhortoForaneoMigracionRepository;
import mx.gob.pjpuebla.migracion.readers.exhortoForaneo.ExhortoForaneoMigracionSaveRecord;
import mx.gob.pjpuebla.migracion.readers.juzgados.JuzgadosMigracionReader;
import mx.gob.pjpuebla.migracion.readers.ubicaciones.UbicacionesReader;
import mx.gob.pjpuebla.migracion.readers.usuario.UsuarioMigracion;
import mx.gob.pjpuebla.migracion.readers.usuario.UsuarioMigracionReader;
import mx.gob.pjpuebla.migracion.readers.juicios.JuiciosMigracionReader;
import mx.gob.pjpuebla.migracion.readers.ocomun.Ocomun;
import mx.gob.pjpuebla.migracion.readers.ocomun.OcomunReader;
import mx.gob.pjpuebla.migracion.readers.oficios.OficiosMigracion;
import mx.gob.pjpuebla.migracion.readers.oficios.OficiosMigracionReader;
import mx.gob.pjpuebla.migracion.readers.oficios.OficiosMigracionRepository;
import mx.gob.pjpuebla.migracion.readers.oficios.OficiosMigracionSaveRecord;
import mx.gob.pjpuebla.migracion.readers.acuerdos.AcuerdosMigracion;
import mx.gob.pjpuebla.migracion.readers.acuerdos.AcuerdosMigracionReader;
import mx.gob.pjpuebla.migracion.readers.acuerdos.AcuerdosMigracionRepository;
import mx.gob.pjpuebla.migracion.readers.acuerdos.AcuerdosMigracionSaveRecord;
import mx.gob.pjpuebla.migracion.readers.acuerdos.SentenciaMigracionSaveRecord;
import mx.gob.pjpuebla.migracion.readers.amparos.AmparoMigracionReader;
import mx.gob.pjpuebla.migracion.readers.amparos.AmparoMigracionRecordSave;
import mx.gob.pjpuebla.migracion.readers.amparos.AmparoMigracionRepository;
import mx.gob.pjpuebla.migracion.readers.amparos.AmparosMigracion;
import mx.gob.pjpuebla.migracion.readers.conceptos.ConceptosMigracionReader;
import mx.gob.pjpuebla.migracion.readers.conceptos.familiar.ConceptosMatFamiliarMigracionReader;
import mx.gob.pjpuebla.migracion.readers.detallesProm.DetallePromSaveRecord;
import mx.gob.pjpuebla.migracion.readers.detallesProm.DetallesProm;
import mx.gob.pjpuebla.migracion.readers.detallesProm.DetallesPromReader;
import mx.gob.pjpuebla.migracion.readers.detallesProm.DetallesPromRepository;
import mx.gob.pjpuebla.migracion.readers.domicilio.DomicilioMigracion;
import mx.gob.pjpuebla.migracion.readers.domicilio.DomicilioMigracionReader;
import mx.gob.pjpuebla.migracion.readers.actores.ActoresMigracion;
import mx.gob.pjpuebla.migracion.readers.actores.ActoresMigracionReader;
import mx.gob.pjpuebla.migracion.readers.actores.ActoresMigracionSaveRecord;
import mx.gob.pjpuebla.migracion.acl.mapper.AmparoMapper;
import mx.gob.pjpuebla.migracion.acl.mapper.EstadoAcuseMapper;
import mx.gob.pjpuebla.migracion.acl.mapper.EstadoOficioMapper;
// ACL
import mx.gob.pjpuebla.migracion.acl.mapper.MateriaMapper;
import mx.gob.pjpuebla.migracion.acl.mapper.NotificacionMapper;
import mx.gob.pjpuebla.migracion.acl.mapper.PartesMapper;
import mx.gob.pjpuebla.migracion.acl.mapper.PromocionMapper;
import mx.gob.pjpuebla.migracion.acl.mapper.ResolucionMapper;
import mx.gob.pjpuebla.migracion.acl.mapper.RubrosMapper;
import mx.gob.pjpuebla.migracion.acl.mapper.SentenciaMapper;
import mx.gob.pjpuebla.migracion.acl.mapper.TipoSistemaMapper;
import mx.gob.pjpuebla.migracion.acl.normalizer.ExpedienteNormalizer;
import mx.gob.pjpuebla.migracion.acl.validate.LegacyValidators;

// Core (solo modelos)
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.migracion.CarpetaDetalleMigracionService;
import mx.gob.pjpuebla.trials.migracion.CarpetaMigracionService;
// Facades de migración

import mx.gob.pjpuebla.trials.migracion.ConceptoMigrationService;
import mx.gob.pjpuebla.trials.migracion.DocumentoMigracionService;
import mx.gob.pjpuebla.trials.migracion.JuzgadoMigracionService;
import mx.gob.pjpuebla.trials.migracion.PersonasMigracionService;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.migracion.Migraciones;
import mx.gob.pjpuebla.trials.workflow.migracion.MigracionesRepository;
import mx.gob.pjpuebla.trials.util.Utils;
import mx.gob.pjpuebla.trials.util.enums.EstadoAcuse;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.EstadoMigracion;
import mx.gob.pjpuebla.trials.util.enums.Migrado;
import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;
import mx.gob.pjpuebla.trials.util.enums.TipoPromocion;
import mx.gob.pjpuebla.trials.util.enums.TipoResolucion;
import mx.gob.pjpuebla.trials.util.enums.TipoSentencia;

@Service
@RequiredArgsConstructor
@Slf4j
public class MigrarExpedienteUseCase {

  // Readers legacy...
  private final EntradasMigracionReader entradasReader;
  private final JuzgadosMigracionReader juzgadosReader;
  private final UbicacionesReader ubicacionesReader;
  private final JuiciosMigracionReader juiciosReader;
  private final OcomunReader ocomunReader;
  private final AcuerdosMigracionReader acuerdosReader;
  private final DetallesPromReader detallesReader;
  private final ActoresMigracionReader actoresReader;
  private final OficiosMigracionReader oficiosReader;
  private final ExhortosCapitalMigracionReader exhortosCapitalMigracionReader;
  private final ExhortoForaneoMigracionReader exhortosForaneosMigracionReader;
  private final AmparoMigracionReader amparoMigracionReader;
  private final ConceptosMigracionReader conceptosReader;
  private final ConceptosMatFamiliarMigracionReader conceptosFamReader;
  private final EntradasUsuarioMigracionReader entradasUsuariosReader;
  private final UsuarioMigracionReader usuarioMigracionReader;
  private final DomicilioMigracionReader domicilioMigracionReader;

  // ACL
  private final MateriaMapper materiaMapper;
  private final RubrosMapper rubrosMapper;
  private final PartesMapper partesMapper;
  private final ExpedienteNormalizer expedienteNormalizer;
  private final TipoSistemaMapper tipoSistemaMapper;
  private final NotificacionMapper notificacionMapper;
  private final LegacyValidators validators;
  private final SentenciaMapper sentenciaMapper;
  private final ResolucionMapper resolucionMapper;
  private final PromocionMapper promocionMapper;
  private final EstadoOficioMapper estadoOficioMapper;
  private final EstadoAcuseMapper estadoAcuseMapper;
  private final AmparoMapper amparoMapper;

  // Facades migración
  private final CarpetaMigracionService carpetaMig;
  private final JuzgadoMigracionService juzgadoMig;
  private final ConceptoMigrationService conceptoMig;
  private final DocumentoMigracionService documentoMig;
  private final PersonasMigracionService personasMig;
  private final CarpetaDetalleMigracionService carpetaDetalleMig;
  private final MigracionesRepository migracionesRepository;
  private final EntradasMigracionRepository entradasMigracionRepository;
  private final AcuerdosMigracionRepository acuerdosMigracionRepository;
  private final DetallesPromRepository detallesPromRepository;
  private final OficiosMigracionRepository oficiosMigracionRepository;
  private final ExhortosCapitalMigracionRepository exhortosCapitalMigracionRepository;
  private final ExhortoForaneoMigracionRepository exhortoForaneoMigracionRepository;
  private final AmparoMigracionRepository amparoMigracionRepository;

  // repositories para actualizar estatus de migración:

  // Orquestador: 1 sola transacción grande (o ajusta a tu estrategia)
  @Transactional
  public void migrarExpedienteCompleto(String expediente, Integer year, String claveJuzgado) {
    var result = migrarExpediente(expediente, year, claveJuzgado);
    // migrarDocumentosExpediente(expediente, year, claveJuzgado,
    // result.migracionId());
  }

  @Transactional
  public MigracionExpedienteResult migrarExpediente(String exp, Integer year, String claveJuzgado) {
    String expediente = Utils.normalizarExpediente(exp);

    // 1) Fetch legacy (todo local, sin estado global)
    Optional<EntradasMigracion> entradaOptional = entradasReader.buscarEntradasPorFiltros(expediente, year,
        claveJuzgado);

    if (entradaOptional.isEmpty()) {
      throw new IllegalArgumentException(
          "No se encontraron entradas para: " + expediente + "/" + year + "/" + claveJuzgado);
    }

    EntradasMigracion entrada = entradaOptional.get();

    // Validación si entrada (expediente) ya ha sido migrado.
    if (entrada.getEstadoMigracion().equals(EstadoMigracion.EXPEDIENTE_MIGRADO)) {
      Carpeta existingCarpeta = carpetaMig.findByExpYearAndClaveJuzgado(exp, year, claveJuzgado);
      Integer existingMigracionId = migracionesRepository.findByCarpetaId(existingCarpeta.getId())
          .map(Migraciones::getId)
          .orElse(null);
      return new MigracionExpedienteResult(existingCarpeta, existingMigracionId);
    }

    var juzLegacy = juzgadosReader.requireByCodigo(claveJuzgado);

    var ocomun = ocomunReader.findByOcomun(entrada.getCu()).orElse(null);
    var juicioLg = juiciosReader.buscarJuicio(entrada.getJuicio());
    validators.requireNonEmpty(claveJuzgado, "claveJuzgado");

    // 2) Normalizar / requerir juzgado
    String expCompleto = expedienteNormalizer.normalizeExpediente(exp + "/" + year);
    Juzgado juzgado = juzgadoMig.requireJuzgadoActual(claveJuzgado);
    carpetaMig.assertExpedienteDisponible(expCompleto, juzgado);

    // 3) Último movimiento / mapeos
    var ubicUlt = ubicacionesReader.buscarUltimoMovimiento(entrada.getCu(), juzLegacy.getTablaUbicacion());
    String materiaNombre = materiaMapper.mapMateria(juicioLg.getMateria());

    var tipoSistema = tipoSistemaMapper.mapTipoSistema(juzLegacy);
    var tipoJuicio = conceptoMig.findOrCreateTipoJuicioForMigration(materiaNombre, juicioLg.getDescripcion(),
        tipoSistema);

    String estadoUltMov = (ubicUlt != null && ubicUlt.estado() != null && !ubicUlt.estado().isBlank())
        ? ubicUlt.estado()
        : "Archivo";

    var diasConcepto = getDias(tipoJuicio, estadoUltMov);
    var concepto = conceptoMig.findOrCreateByUltimoMovimiento(tipoJuicio, estadoUltMov, diasConcepto);

    // 4) Crear carpeta y documentos base
    var carpeta = carpetaMig.crearCarpeta(getExpedienteCompleto(entrada), getFolio(ocomun), entrada.getCu(), juzgado,
        tipoJuicio, concepto);

    var ruta = (ocomun != null) ? ocomun.getRutaDigitalizacion() : null;
    var docInicial = documentoMig.createDemandaInicial(carpeta, concepto, ruta);
    documentoMig.createAnexos(ocomun != null ? ocomun.getAnexos() : null, docInicial);

    // 5) Detalle + personas
    carpetaDetalleMig.createCarpetaDetalle(carpeta);
    var actores = actoresReader.buscarPorClave(entrada.getCu());
    List<ActoresMigracionSaveRecord> personasLegacy = getActoresExpediente(actores);
    personasMig.crearPersonaLegacy(personasLegacy, tipoJuicio, carpeta);

    // 6) Registro de migración
    String recibio = (ubicUlt != null) ? ubicUlt.recibio() : null;
    String puesto = (ubicUlt != null) ? ubicUlt.puestoRecibioTBLPuesto() : null;

    var migracion = createMigraciones(
        EstadoMigracion.EXPEDIENTE_MIGRADO,
        "Se ha migrado el expediente principal",
        recibio,
        puesto,
        juzgado,
        carpeta);

    // Marcar entradas (expediente PHP) como migrado:
    entrada.setEstadoMigracion(EstadoMigracion.EXPEDIENTE_MIGRADO);
    entradasMigracionRepository.save(entrada);

    return new MigracionExpedienteResult(carpeta, migracion.getId());
  }

  @Transactional
  public EstadoMigracion migrarDocumentosExpediente(
      String exp, Integer year, String claveJuzgado, Integer migracionId) {

    String expediente = Utils.normalizarExpediente(exp);

    Optional<EntradasMigracion> entradaOptional = entradasReader.buscarEntradasPorFiltros(expediente, year,
        claveJuzgado);
    if (entradaOptional.isEmpty()) {
      throw new IllegalArgumentException(
          "No se encontraron entradas para: " + expediente + "/" + year + "/" + claveJuzgado);
    }
    EntradasMigracion entrada = entradaOptional.get();
    var juzLegacy = juzgadosReader.requireByCodigo(claveJuzgado);
    var carpeta = carpetaMig.findByExpYearAndClaveJuzgado(exp, year, claveJuzgado);

    // Lecturas legacy
    var acuerdos = acuerdosReader.buscarAcuerdosPorCu(entrada.getCu());
    var sentencias = acuerdosReader.buscarSentenciasPorCu(entrada.getCu());
    var promos = detallesReader.buscarPorCu(entrada.getCu());
    var oficios = oficiosReader.buscarPorCu(entrada.getCu());
    var piezasLegacy = ubicacionesReader.buscarPiezasByCu(entrada.getCu(), juzLegacy.getTablaUbicacion());
    var exhortosCapital = exhortosCapitalMigracionReader.buscarPorExpAmoJuzgado(expediente, year, claveJuzgado);
    var exhortosForaneos = exhortosForaneosMigracionReader.buscarPorExpAmoJuzgado(expediente, year, claveJuzgado);
    var amparos = amparoMigracionReader.buscarPorCu(entrada.getCu());

    // Documentos (idealmente idempotentes)
    crearAcuerdosFromLegacy(acuerdos, carpeta);
    crearSentenciaFromLegacy(sentencias, carpeta);
    crearPromocionesFromLegacy(promos, carpeta);
    crearOficiosFromLegacy(oficios, carpeta);
    crearExhortoSalida(exhortosCapital, carpeta);
    crearExhortoEntrada(exhortosForaneos, carpeta);

    // Piezas
    List<Carpeta> piezas = carpetaMig.createPiezas(carpeta, piezasLegacy, getTipoPieza(entrada.getCu()));
    createDocumentosPieza(piezas);
    // Amparos
    crearAmparosFromLegacy(amparos, carpeta);

    updateMigraciones(
        migracionId,
        EstadoMigracion.MIGRADO_COMPLETADO,
        "Se ha migrado el expediente principal junto con sus documentos");

    return EstadoMigracion.MIGRADO_COMPLETADO;
  }

  // Metodos para setear el regisgro en la tabla de migracioens:

  private Migraciones createMigraciones(EstadoMigracion estadoMigracion, String observaciones,
      String asignacionAnterior, String puestoAsignacionAnterior, Juzgado juzgado, Carpeta carpeta) {

    Migraciones migracion = new Migraciones()
        .setVersion(0)
        .setEstatus(estadoMigracion)
        .setObservaciones(observaciones)
        .setAsignacionAnterior(asignacionAnterior)
        .setPuestoAsignacionAnterior(puestoAsignacionAnterior)
        .setJuzgado(juzgado)
        .setCarpeta(carpeta);

    return migracionesRepository.save(migracion);
  }

  private Migraciones updateMigraciones(Integer migracionId, EstadoMigracion estadoMigracion, String observaciones) {
    Migraciones migracion = migracionesRepository.findById(migracionId)
        .orElseThrow(() -> new NotFoundException("No fue posible encontrar el registro de migración",
            migracionId.toString()));

    migracion.setEstatus(estadoMigracion);
    migracion.setObservaciones(observaciones);
    return migracionesRepository.save(migracion);
  }

  private String getFolio(Ocomun ocomun) {
    return ocomun != null ? ocomun.getFolio().toString() : UUID.randomUUID().toString();
  }

  private String getExpedienteCompleto(EntradasMigracion entrada) {
    return entrada.getExpediente() + "/" + entrada.getAmo();
  }

  public String getTipoPieza(String cu) {
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

  private void createDocumentosPieza(List<Carpeta> piezas) {
    piezas.forEach(pieza -> {
      // obtenemos los acuerdos, sentencias, promociones de la pieza:
      var acuerdos = acuerdosReader.buscarAcuerdosPorCu(pieza.getCu());
      var sentencias = acuerdosReader.buscarSentenciasPorCu(pieza.getCu());
      var promos = detallesReader.buscarPorCu(pieza.getCu());

      crearAcuerdosFromLegacy(acuerdos, pieza);
      crearSentenciaFromLegacy(sentencias, pieza);
      crearPromocionesFromLegacy(promos, pieza);

    });
  }

  private int getDias(TipoJuicio tipoJuicio, String estado) {
    String materia = tipoJuicio.getMateria() != null ? tipoJuicio.getMateria().getNombre() : "";
    String sistema = tipoJuicio.getTipoSistema() != null ? tipoJuicio.getTipoSistema().getNombre() : "";

    boolean esFamiliarOral = "FAMILIAR".equalsIgnoreCase(materia)
        && "ORAL".equalsIgnoreCase(sistema);

    if (esFamiliarOral) {
      return conceptosFamReader.findByClave(estado)
          .map(x -> parseDias(x.getDias()))
          .orElse(0);
    }
    return conceptosReader.findByClave(estado)
        .map(x -> parseDias(x.getDias()))
        .orElse(0);
  }

  private int parseDias(String s) {
    if (s == null)
      return 0;
    try {
      return Integer.parseInt(s.trim());
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  private List<ActoresMigracionSaveRecord> getActoresExpediente(List<ActoresMigracion> actores) {
    List<ActoresMigracionSaveRecord> actoresRecord = new ArrayList<>();

    actores.forEach(a -> {
      String tipoPartes = partesMapper.mapTipoPartesMigracion(a.getTipo());
      TipoNotificacion tipoNotificacion = notificacionMapper.mapTipoNotificacion(a.getTipoNotificacion());
      String correoNotificacion = entradasUsuariosReader
          .findByClaveActorAndEstatus(a.getClaveAct())
          .map(entradaUsuario -> {
            int idUsuario = entradaUsuario.getIdusuario();
            UsuarioMigracion usuario = usuarioMigracionReader
                .findUsuarioMigracionByIdUsuarioAnEstado(idUsuario);

            return (usuario != null) ? usuario.getCorreo() : "";
          }).orElse("");

      DomicilioMigracion domicilioMigracion = domicilioMigracionReader.findByCuActorAndEstado(a.getClaveAct());

      String nombrePersona = a.getNombre().trim().length() < 3 ? a.getNombre().trim() + " N/E"
          : a.getNombre().trim();

      ActoresMigracionSaveRecord actorMigracionSave = new ActoresMigracionSaveRecord(
          tipoPartes,
          tipoNotificacion,
          correoNotificacion,
          domicilioMigracion,
          nombrePersona,
          a.getTipoPersona());

      actoresRecord.add(actorMigracionSave);

    });

    return actoresRecord;

  }

  public void crearSentenciaFromLegacy(List<AcuerdosMigracion> sentencias, Carpeta carpeta) {
    List<SentenciaMigracionSaveRecord> sentenciasRecord = new ArrayList<>();
    sentencias.forEach(sentencia -> {
      TipoSentencia tipoSent = sentenciaMapper.mapTipoSentencia(sentencia.getResumen());
      TipoResolucion tipoRes = resolucionMapper.mapTipoResolucionSentencia(sentencia.getSentencia());

      sentenciasRecord.add(new SentenciaMigracionSaveRecord(
          carpeta,
          sentencia.getFechaResolucion(),
          tipoSent,
          tipoRes,
          sentencia.getClave().toString(),
          sentencia.getRuta(),
          sentencia.getFecha()));
      sentencia.setMigrado(Migrado.SI);
    });

    documentoMig.createSentenciasFromLegacy(sentenciasRecord);
    acuerdosMigracionRepository.saveAll(sentencias);

  }

  public void crearAcuerdosFromLegacy(List<AcuerdosMigracion> acuerdos, Carpeta carpeta) {
    List<AcuerdosMigracionSaveRecord> acuerdosRecord = new ArrayList<>();

    acuerdos.forEach(a -> {
      List<String> rubros = rubrosMapper.mapRubros(a.getResumen());
      String rubroPrincipal = rubros.isEmpty() ? "" : rubros.get(0);

      AcuerdosMigracionSaveRecord data = new AcuerdosMigracionSaveRecord(
          carpeta,
          rubroPrincipal,
          a.getFechaResolucion(),
          rubros,
          a.getClave().toString(),
          a.getRuta(),
          a.getFecha());

      ;
      acuerdosRecord.add(data);
      a.setMigrado(Migrado.SI);
    });

    documentoMig.createAcuerdosFromLegacy(acuerdosRecord);
    acuerdosMigracionRepository.saveAll(acuerdos);
  }

  public void crearPromocionesFromLegacy(List<DetallesProm> promociones, Carpeta carpeta) {
    List<DetallePromSaveRecord> promocionesRecord = new ArrayList<>();

    promociones.forEach(promo -> {
      TipoPromocion tipoPromocion = promocionMapper.mapTipoPromocion(promo.getTipo(), promo.getDescrip());

      promocionesRecord.add(new DetallePromSaveRecord(
          carpeta,
          tipoPromocion,
          promo.getId().toString(),
          promo.getArchivo(),
          promo.getAcuerdo(),
          promo.getAnexos()));

      promo.setMigrado(Migrado.SI);

    });

    documentoMig.createPromocionesFromLegacy(promocionesRecord);
    detallesPromRepository.saveAll(promociones);
  }

  public void crearOficiosFromLegacy(List<OficiosMigracion> oficios, Carpeta carpeta) {
    List<OficiosMigracionSaveRecord> oficiosRecord = new ArrayList<>();

    oficios.forEach(oficio -> {
      EstadoCarpeta estadoCarpeta = estadoOficioMapper.estadoOficioMapper(oficio.getMotivo(), oficio.getRutaOfiAcuse(),
          oficio.getEstatusOfi());
      EstadoAcuse estadoAcuse = estadoAcuseMapper.mapEstadoAcuse(oficio.getMotivo(), oficio.getRutaOfiAcuse());

      oficiosRecord.add(new OficiosMigracionSaveRecord(
          carpeta,
          estadoCarpeta,
          oficio.getOficio(),
          oficio.getRutaOfi(),
          oficio.getId(),
          oficio.getDependencia(),
          oficio.getAsunto(),
          oficio.getFecha(),
          oficio.getFechaEntrega(),
          oficio.getRuta(),
          oficio.getMotivo(),
          estadoAcuse,
          oficio.getNombre()));

      oficio.setMigrado(Migrado.SI);
    });

    documentoMig.createOficiosFromLegacy(oficiosRecord);
    oficiosMigracionRepository.saveAll(oficios);
  }

  public void crearExhortoSalida(List<ExhortosCapitalMigracion> exhortos, Carpeta carpeta) {
    List<ExhortoCapitalMigracionSaveRecord> exhortosRecord = new ArrayList<>();

    exhortos.forEach(exhorto -> {
      exhortosRecord.add(new ExhortoCapitalMigracionSaveRecord(
          exhorto.getTramite(),
          exhorto.getDestino(),
          exhorto.getObse(),
          exhorto.getFechaEn(),
          exhorto.getFechaDe(),
          carpeta,
          exhorto.getExhorto()));

      exhorto.setMigrado(Migrado.SI);
    });

    documentoMig.createExhortoSalidaFromLegacy(exhortosRecord);
    exhortosCapitalMigracionRepository.saveAll(exhortos);

  }

  public void crearExhortoEntrada(List<ExhortoForaneoMigracion> exhorto, Carpeta carpeta) {

    List<ExhortoForaneoMigracionSaveRecord> exhortosRecord = new ArrayList<>();

    exhorto.forEach(exhortoForaneo -> {
      exhortosRecord.add(new ExhortoForaneoMigracionSaveRecord(
          exhortoForaneo.getTramite(),
          exhortoForaneo.getObse(),
          carpeta,
          exhortoForaneo.getExhorto()));

      exhortoForaneo.setMigrado(Migrado.SI);
    });

    documentoMig.createExhortoEntradaFromLegacy(exhortosRecord);
    exhortoForaneoMigracionRepository.saveAll(exhorto);
  }

  public void crearAmparosFromLegacy(List<AmparosMigracion> amparos, Carpeta carpeta) {
    List<AmparoMigracionRecordSave> amparosRecord = new ArrayList<>();

    amparos.forEach(amparo -> {
      Integer amparoImpugnacion = amparoMapper.mapImpugnacion(amparo.getRevision());
      String amparoSentido = amparoMapper.mapSentido(amparo.getConcede());
      String sentidoImpugnacion = amparoMapper.mapSentidoImpugnacion(amparo.getImpugna());
      String tipoAmparo = amparoMapper.mapTipoAmparo(amparo.getTipo());

      amparosRecord.add(new AmparoMigracionRecordSave(
          carpeta,
          amparo.getFecha(),
          amparoImpugnacion,
          amparo.getQuejoso(),
          null,
          null,
          amparoSentido,
          sentidoImpugnacion,
          tipoAmparo,
          amparo.getFechaConclusion(),
          amparo.getClave().toString()));

      amparo.setMigrado(Migrado.SI);
    });

    documentoMig.createAmparosFromLegacy(amparosRecord);
    amparoMigracionRepository.saveAll(amparos);
  }

}