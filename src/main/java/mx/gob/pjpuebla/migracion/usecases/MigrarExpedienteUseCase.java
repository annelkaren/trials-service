package mx.gob.pjpuebla.migracion.usecases;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.migracion.readers.entradas.EntradasMigracion;
// Readers (legacy)
import mx.gob.pjpuebla.migracion.readers.entradas.EntradasMigracionReader;
import mx.gob.pjpuebla.migracion.readers.entradas.EntradasMigracionRepository;
import mx.gob.pjpuebla.migracion.readers.juzgados.JuzgadosMigracionReader;
import mx.gob.pjpuebla.migracion.readers.juzgados.JuzgadosMigracionRegistroRecord;
import mx.gob.pjpuebla.migracion.readers.movimientos.MovimientosMigracionRecord;
import mx.gob.pjpuebla.migracion.readers.ubicaciones.UbicacionesReader;
import mx.gob.pjpuebla.migracion.readers.juicios.JuicioResponseRecord;
import mx.gob.pjpuebla.migracion.readers.juicios.JuiciosMigracionReader;
import mx.gob.pjpuebla.migracion.readers.ocomun.OcomunReader;
import mx.gob.pjpuebla.migracion.readers.ocomun.OcomunResponseRecord;
import mx.gob.pjpuebla.migracion.readers.actores.ActoresMigracion;
import mx.gob.pjpuebla.migracion.readers.actores.ActoresMigracionReader;
import mx.gob.pjpuebla.migracion.readers.actores.ActoresMigracionSaveRecord;
import mx.gob.pjpuebla.migracion.acl.mapper.TipoPiezaMapper;
// ACL
import mx.gob.pjpuebla.migracion.acl.mapper.TipoSistemaMapper;
import mx.gob.pjpuebla.trials.core.conceptos.Concepto;

// Core (solo modelos)
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;

import mx.gob.pjpuebla.trials.migracion.CarpetaDetalleMigracionService;
import mx.gob.pjpuebla.trials.migracion.CarpetaMigracionService;
// Facades de migración

import mx.gob.pjpuebla.trials.migracion.ConceptoMigrationService;
import mx.gob.pjpuebla.trials.migracion.DocumentoMigracionService;
import mx.gob.pjpuebla.trials.migracion.JuzgadoMigracionService;
import mx.gob.pjpuebla.trials.migracion.PersonasMigracionService;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.migracion.Migraciones;
import mx.gob.pjpuebla.trials.workflow.migracion.MigracionesRepository;
import mx.gob.pjpuebla.trials.util.enums.EstadoMigracion;

@Service
@RequiredArgsConstructor
@Slf4j
public class MigrarExpedienteUseCase {

  // READERS legacy
  private final EntradasMigracionReader entradasReader;
  private final JuzgadosMigracionReader juzgadosReader;
  private final UbicacionesReader ubicacionesReader;
  private final OcomunReader ocomunReader;
  private final JuiciosMigracionReader juiciosReader;
  private final ActoresMigracionReader actoresReader;

  // MAPPERS / ACL
  private final TipoSistemaMapper tipoSistemaMapper;


  // SERVICIOS de migración (lado trials.migracion / core)
  private final JuzgadoMigracionService juzgadoMig;
  private final ConceptoMigrationService conceptoMig;
  private final CarpetaMigracionService carpetaMig;
  private final DocumentoMigracionService documentoMig;
  private final PersonasMigracionService personasMig;
  private final CarpetaDetalleMigracionService carpetaDetalleMig;
  private final MigracionesRepository migracionesRepository;
  private final EntradasMigracionRepository entradasMigracionRepository;

  private final MigrarDocumentosUseCase migrarDocumentosUseCase;

  // 🔹 Solo migra el expediente principal (sin documentos)
  @Transactional
  public MigracionExpedienteResult migrarExpedientePrincipal(String exp, Integer year, String claveJuzgado) {

    // 1) Entrada y validación de estado
    EntradasMigracion entrada = entradasReader.buscarEntradasPorFiltros(exp, year, claveJuzgado);
    MigracionExpedienteResult migracionExpedienteResult = expedienteFueMigrado(entrada, exp, year, claveJuzgado);
    if (migracionExpedienteResult != null) {
      return migracionExpedienteResult;
    }

    // 2) Obtenemos datos legacy juzgado, ultimo movimiento, oficialia comun, Juicio, actores
    JuzgadosMigracionRegistroRecord juzgadoLegacy = juzgadosReader.findByCodigo(claveJuzgado);
    MovimientosMigracionRecord ultimoMovimientoLegacy = ubicacionesReader.buscarUltimoMovimiento(entrada.getCu(),juzgadoLegacy.tablaUbicacion());
    OcomunResponseRecord ocomunLegacy = ocomunReader.findByOcomun(entrada.getCu()).orElse(null);
    JuicioResponseRecord juicioLegacy = juiciosReader.buscarJuicio(entrada.getJuicio());
    List<ActoresMigracion> actores = actoresReader.buscarPorClave(entrada.getCu());

    String expedienteCompleto =  entrada.getExpediente() + "/" + entrada.getAmo();
    String folioEntrada = ocomunLegacy != null ? ocomunLegacy.folio().toString() : UUID.randomUUID().toString();
    List<ActoresMigracionSaveRecord> personasLegacy = actoresReader.getActoresExpediente(actores);

    // 3) Obtiene datos de legacy y trasnformarlos a datos de Java:
    TipoSistema tipoSistema = tipoSistemaMapper.mapTipoSistema(
        juzgadoLegacy.materiaCodigo(), juzgadoLegacy.juzgadoCodigo());

    Juzgado juzgado = juzgadoMig.requireJuzgadoActual(claveJuzgado);

    TipoJuicio tipoJuicio = conceptoMig.findOrCreateTipoJuicioForMigration(
        juicioLegacy.materia(), juicioLegacy.descripcion(), tipoSistema);

    Concepto concepto = conceptoMig.findOrCreateByUltimoMovimiento(
        tipoJuicio, ultimoMovimientoLegacy);

    String ruta = ocomunLegacy != null ? ocomunLegacy.rutaDigitalizacion() : "";
    String recibio = ultimoMovimientoLegacy.recibio();
    String puesto = ultimoMovimientoLegacy.puestoRecibioTBLPuesto();
    String anexos = ocomunLegacy != null ? ocomunLegacy.anexos() : "";

    // 4) Creación de Expediente y demanda principal, anexos, carpeta detalle y personas en el documento:
    Carpeta carpeta = carpetaMig.crearCarpeta(
        expedienteCompleto,
        folioEntrada,
        entrada.getCu(),
        juzgado,
        tipoJuicio,
        concepto);

    Documento demandaInicial = documentoMig.createDemandaInicial(carpeta, concepto, ruta);
    documentoMig.createAnexos(anexos, demandaInicial);
    carpetaDetalleMig.createCarpetaDetalle(carpeta);
    personasMig.crearPersonaLegacy(personasLegacy, tipoJuicio, carpeta);

     // 5) Actualiza tabla de migraciones con el registro que se acaba de migrar:
    Migraciones migracion = createMigraciones(
        EstadoMigracion.EXPEDIENTE_MIGRADO,
        "Se ha migrado el expediente principal",
        recibio,
        puesto,
        juzgado,
        carpeta);

    entrada.setEstadoMigracion(EstadoMigracion.EXPEDIENTE_MIGRADO);
    entradasMigracionRepository.save(entrada);

    return new MigracionExpedienteResult(carpeta, migracion.getId());
  }

  // 🔹 Use case “completo”: expediente + documentos
  @Transactional
  public MigracionExpedienteResult migrarExpedienteCompleto(String exp, Integer year, String claveJuzgado) {
    log.info("Iniciando la migración del expediente " +  exp + "/" + year);
    // 1) Migrar expediente principal
    MigracionExpedienteResult principal = migrarExpedientePrincipal(exp, year, claveJuzgado);

    // 2) Migrar documentos asociados
    migrarDocumentosUseCase.migrarDocumentosExpediente(
        exp,
        year,
        claveJuzgado,
        principal.migracionId());

    return principal;
  }

  // METODOS YA CONSTRUIDOS:

  @Transactional
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


  private MigracionExpedienteResult expedienteFueMigrado(EntradasMigracion entrada,
      String exp,
      Integer year,
      String claveJuzgado) {
    if (!EstadoMigracion.EXPEDIENTE_MIGRADO.equals(entrada.getEstadoMigracion())) {
      return null;
    }

    Carpeta existingCarpeta = carpetaMig.findByExpYearAndClaveJuzgado(exp, year, claveJuzgado);
    if(existingCarpeta == null){
      return null;
    }
    
    Integer existingMigracionId = migracionesRepository.findByCarpetaId(existingCarpeta.getId())
        .map(Migraciones::getId)
        .orElse(null);

    return new MigracionExpedienteResult(existingCarpeta, existingMigracionId);
  }

}