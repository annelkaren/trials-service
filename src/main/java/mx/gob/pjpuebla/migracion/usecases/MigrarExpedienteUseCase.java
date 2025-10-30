package mx.gob.pjpuebla.migracion.usecases;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.migracion.readers.entradas.EntradasMigracion;
// Readers (legacy)
import mx.gob.pjpuebla.migracion.readers.entradas.EntradasMigracionReader;
import mx.gob.pjpuebla.migracion.readers.exhortoCapital.ExhortosCapitalMigracionReader;
import mx.gob.pjpuebla.migracion.readers.exhortoForaneo.ExhortoForaneoMigracionReader;
import mx.gob.pjpuebla.migracion.readers.juzgados.JuzgadosMigracionReader;
import mx.gob.pjpuebla.migracion.readers.ubicaciones.UbicacionesReader;
import mx.gob.pjpuebla.migracion.readers.juicios.JuiciosMigracionReader;
import mx.gob.pjpuebla.migracion.readers.ocomun.OcomunReader;
import mx.gob.pjpuebla.migracion.readers.oficios.OficiosMigracionReader;
import mx.gob.pjpuebla.migracion.readers.acuerdos.AcuerdosMigracionReader;
import mx.gob.pjpuebla.migracion.readers.amparos.AmparoMigracionReader;
import mx.gob.pjpuebla.migracion.readers.detallesProm.DetallesPromReader;
import mx.gob.pjpuebla.migracion.readers.actores.ActoresMigracionReader;

// ACL
import mx.gob.pjpuebla.migracion.acl.mapper.MateriaMapper;
import mx.gob.pjpuebla.migracion.acl.mapper.RubrosMapper;
import mx.gob.pjpuebla.migracion.acl.normalizer.ExpedienteNormalizer;
import mx.gob.pjpuebla.migracion.acl.validate.LegacyValidators;

// Core (solo modelos)
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.migracion.CarpetaDetalleMigracionService;
import mx.gob.pjpuebla.trials.migracion.CarpetaMigracionService;
// Facades de migración

import mx.gob.pjpuebla.trials.migracion.ConceptoMigrationService;
import mx.gob.pjpuebla.trials.migracion.DocumentoMigracionService;
import mx.gob.pjpuebla.trials.migracion.PersonasMigracionService;
import mx.gob.pjpuebla.trials.workflow.migracion.MigracionesService;
import mx.gob.pjpuebla.trials.util.Utils;
import mx.gob.pjpuebla.trials.util.enums.EstadoMigracion;

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

  // ACL
  private final MateriaMapper materiaMapper;
  private final RubrosMapper rubrosMapper;
  private final ExpedienteNormalizer expedienteNormalizer;
  private final LegacyValidators validators;

  // Facades migración
  private final CarpetaMigracionService carpetaMig;
  private final ConceptoMigrationService conceptoMig;
  private final DocumentoMigracionService documentoMig;
  private final PersonasMigracionService personasMig;
  private final MigracionesService migracionesService;
  private final CarpetaDetalleMigracionService carpetaDetalleMig;

  
  // Orquestador: 1 sola transacción grande (o ajusta a tu estrategia)
  @Transactional
  public void migrarExpedienteCompleto(String expediente, Integer year, String claveJuzgado) {
    var result = migrarExpediente(expediente, year, claveJuzgado);
    migrarDocumentosExpediente(expediente, year, claveJuzgado, result.migracionId());
  }

  @Transactional
  public MigracionExpedienteResult migrarExpediente(String exp, Integer year, String claveJuzgado) {
    String expediente = Utils.normalizarExpediente(exp);

    // 1) Fetch legacy (todo local, sin estado global)
    Optional<EntradasMigracion> entradaOptional = entradasReader.buscarEntradasPorFiltros(expediente, year, claveJuzgado);
    
    if(entradaOptional.isEmpty()) {
      throw new IllegalArgumentException("No se encontraron entradas para: " + expediente + "/" + year + "/" + claveJuzgado);
    }
    EntradasMigracion entrada = entradaOptional.get();
    
    
    var juzLegacy = juzgadosReader.requireByCodigo(claveJuzgado);

    var ocomun   = ocomunReader.findByOcomun(entrada.getCu()).orElse(null); // puede ser null
    if(ocomun != null) {
        log.info("La ruta de digitalizacion de OCOUMUN ES: " + ocomun.getRutaDigitalizacion());
    }
    var juicioLg = juiciosReader.buscarJuicio(entrada.getJuicio());
    validators.requireNonEmpty(claveJuzgado, "claveJuzgado");

    // 2) Normalizar / requerir juzgado
    String expCompleto = expedienteNormalizer.normalizeExpediente(expediente + "/" + year);
    Juzgado juzgado = carpetaMig.requireJuzgadoActual(claveJuzgado);
    carpetaMig.assertExpedienteDisponible(expCompleto, juzgado);

    // 3) Último movimiento / mapeos
    var ubicUlt = ubicacionesReader.buscarUltimoMovimiento(entrada.getCu(), juzLegacy.getTablaUbicacion());
    String materiaNombre = materiaMapper.mapMateria(juicioLg.getMateria());
    var tipoJuicio = conceptoMig.findOrCreateTipoJuicioForMigration(materiaNombre, juicioLg.getDescripcion(), juzLegacy);

    String estadoUltMov = (ubicUlt != null && ubicUlt.estado() != null && !ubicUlt.estado().isBlank())
        ? ubicUlt.estado() : "Archivo";
    var concepto = conceptoMig.findOrCreateByUltimoMovimiento(tipoJuicio, estadoUltMov);

    // 4) Crear carpeta y documentos base
    var carpeta = carpetaMig.createFromLegacy(entrada, ocomun, juzgado, tipoJuicio, concepto);
    var docInicial = documentoMig.createDemandaInicial(ocomun, carpeta, concepto);
    documentoMig.createAnexos(ocomun != null ? ocomun.getAnexos() : null, docInicial);

    // 5) Detalle + personas
    carpetaDetalleMig.createCarpetaDetalle(carpeta);
    var actores = actoresReader.buscarPorClave(entrada.getCu());
    personasMig.createFromLegacy(actores, tipoJuicio, carpeta);

    // 6) Registro de migración
    String recibio = (ubicUlt != null) ? ubicUlt.recibio() : null;
    String puesto  = (ubicUlt != null) ? ubicUlt.puestoRecibioTBLPuesto() : null;

    var migracion = migracionesService.createMigraciones(
        EstadoMigracion.EXPEDIENTE_MIGRADO,
        "Se ha migrado el expediente principal",
        recibio,
        puesto,
        juzgado,
        carpeta
    );

    return new MigracionExpedienteResult(carpeta, migracion.getId());
  }

  @Transactional
  public EstadoMigracion migrarDocumentosExpediente(
      String exp, Integer year, String claveJuzgado, Integer migracionId) {
    
    String expediente = Utils.normalizarExpediente(exp);

    // Relee lo necesario para soportar ejecución "por partes"
    Optional<EntradasMigracion>entradaOptional   = entradasReader.buscarEntradasPorFiltros(expediente, year, claveJuzgado);
    if(entradaOptional.isEmpty()) {
      throw new IllegalArgumentException("No se encontraron entradas para: " + expediente + "/" + year + "/" + claveJuzgado);
    }
    EntradasMigracion entrada = entradaOptional.get();
    var juzLegacy = juzgadosReader.requireByCodigo(claveJuzgado);
    var carpeta   = carpetaMig.findByExpYearAndClaveJuzgado(expediente, year, claveJuzgado);

    // Lecturas legacy
    var acuerdos         = acuerdosReader.buscarAcuerdosPorCu(entrada.getCu());
    var sentencias       = acuerdosReader.buscarSentenciasPorCu(entrada.getCu());
    var promos           = detallesReader.buscarPorCu(entrada.getCu());
    var oficios          = oficiosReader.buscarPorCu(entrada.getCu());
    var piezasLegacy     = ubicacionesReader.buscarPiezasByCu(entrada.getCu(), juzLegacy.getTablaUbicacion());
    var exhortosCapital  = exhortosCapitalMigracionReader.buscarPorExpAmoJuzgado(expediente, year, claveJuzgado);
    var exhortosForaneos = exhortosForaneosMigracionReader.buscarPorExpAmoJuzgado(expediente, year, claveJuzgado);
    var amparos          = amparoMigracionReader.buscarPorCu(entrada.getCu());

    // Documentos (idealmente idempotentes)
    documentoMig.createAcuerdosFromLegacy(acuerdos, carpeta, rubrosMapper);
    documentoMig.createSentenciasFromLegacy(sentencias, carpeta);
    documentoMig.createPromocionesFromLegacy(promos, carpeta);
    documentoMig.createOficiosFromLegacy(oficios, carpeta);
    documentoMig.createExhortoSalidaFromLegacy(exhortosCapital, carpeta);
    documentoMig.createExhortoEntradaFromLegacy(exhortosForaneos, carpeta);

    // Piezas
    carpetaMig.createPiezaConDocumentos(carpeta, piezasLegacy);

    // Amparos
    documentoMig.createAmparos(carpeta, amparos);

    migracionesService.updateMigraciones(
        migracionId,
        EstadoMigracion.MIGRADO_COMPLETADO,
        "Se ha migrado el expediente principal junto con sus documentos"
    );

    return EstadoMigracion.MIGRADO_COMPLETADO;
  }

}