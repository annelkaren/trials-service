package mx.gob.pjpuebla.migracion.usecases;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
import mx.gob.pjpuebla.migracion.acl.mapper.PromocionMapper;
import mx.gob.pjpuebla.migracion.acl.normalizer.ExpedienteNormalizer;
import mx.gob.pjpuebla.migracion.acl.validate.LegacyValidators;

// Core (solo modelos)
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.conceptos.Concepto;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.migracion.CarpetaDetalleMigracionService;
import mx.gob.pjpuebla.trials.migracion.CarpetaMigracionService;
// Facades de migración

import mx.gob.pjpuebla.trials.migracion.ConceptoMigrationService;
import mx.gob.pjpuebla.trials.migracion.DocumentoMigracionService;
import mx.gob.pjpuebla.trials.migracion.PersonasMigracionService;
import mx.gob.pjpuebla.trials.workflow.migracion.MigracionesService;
import mx.gob.pjpuebla.trials.util.enums.EstadoMigracion;

@Service
@RequiredArgsConstructor
@Slf4j
public class MigrarExpedienteUseCase {

    // Readers legacy
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
    private final PromocionMapper promocionMapper;
    private final ExpedienteNormalizer expedienteNormalizer;
    private final LegacyValidators validators;

    // Facades de migración
    private final CarpetaMigracionService carpetaMig;
    private final ConceptoMigrationService conceptoMig;
    private final DocumentoMigracionService documentoMig;
    private final PersonasMigracionService personasMig;
    private final MigracionesService migracionesService;
    private final CarpetaDetalleMigracionService carpetaDetaleMig;


    @Transactional
    public void migrarExpediente(String expediente, Integer year, String claveJuzgado) {
        // 1) Fetch legacy
        var entrada = entradasReader.requireByExpedienteAmoJuzgado(expediente, year, claveJuzgado);
        var juzLegacy = juzgadosReader.requireByCodigo(claveJuzgado);
        var ocomun = ocomunReader.findByOcomun(entrada.getCu()).orElse(null); // puede ser null en el caso de expedientes relacionados con juzgados foraneos
        var juicioLg = juiciosReader.buscarJuicio(entrada.getJuicio());
        var ubicUlt = ubicacionesReader.buscarUltimoMovimiento(entrada.getCu(), juzLegacy.getTablaUbicacion());
        var acuerdos = acuerdosReader.buscarAcuerdosPorCu(entrada.getCu());
        var sentencias = acuerdosReader.buscarSentenciasPorCu(entrada.getCu());
        var promos = detallesReader.buscarPorCu(entrada.getCu());
        var actores = actoresReader.buscarPorClave(entrada.getCu());
        var oficios = oficiosReader.buscarPorCu(entrada.getCu());
        var piezasLegacy = ubicacionesReader.buscarPiezasByCu(entrada.getCu(), juzLegacy.getTablaUbicacion());
        var exhortosCapital = exhortosCapitalMigracionReader.buscarPorExpAmoJuzgado(expediente, year, claveJuzgado);
        var exhortosForaneos = exhortosForaneosMigracionReader.buscarPorExpAmoJuzgado(expediente, year, claveJuzgado);
        var amparos = amparoMigracionReader.buscarPorCu(entrada.getCu());

        // 2) Normalizar/validar
        String expCompleto = expedienteNormalizer.normalizeExpediente(expediente + "/" + year);
        validators.requireNonEmpty(claveJuzgado, "claveJuzgado");

        // 3) Requerir juzgado actual y asegurar idempotencia
        Juzgado juzgado = carpetaMig.requireJuzgadoActual(claveJuzgado);
        carpetaMig.assertExpedienteDisponible(expCompleto, juzgado);

        // 4) Mapeos de valores necesarios en sistema actual provenientes del sistema legacy
        String materiaNombre = materiaMapper.mapMateria(juicioLg.getMateria());
        TipoJuicio tipoJuicio = conceptoMig.findOrCreateTipoJuicioForMigration(materiaNombre, juicioLg.getDescripcion(), juzLegacy);
        String estadoUltMov = (ubicUlt != null && ubicUlt.estado() != null && !ubicUlt.estado().isBlank())
                ? ubicUlt.estado()
                : "Archivo";
        Concepto concepto = conceptoMig.findOrCreateByUltimoMovimiento(tipoJuicio, estadoUltMov);

        // 5) Crear carpeta y documentos
        Carpeta carpeta = carpetaMig.createFromLegacy(entrada, ocomun, juzgado, tipoJuicio, concepto);

        Documento docInicial = documentoMig.createDemandaInicial(ocomun, carpeta, concepto);
        documentoMig.createAnexos(ocomun != null ? ocomun.getAnexos() : null, docInicial);

        // 6) Creamos el detalle de la carpeta: 
        carpetaDetaleMig.createCarpetaDetalle(carpeta);
        
        // 7) Personas
        personasMig.createFromLegacy(actores, tipoJuicio, carpeta);

        // 8) Documentos: acuerdos, sentencias, promociones, oficios, exhortos capital - foraneo
        documentoMig.createAcuerdosFromLegacy(acuerdos, carpeta, rubrosMapper);
        documentoMig.createSentenciasFromLegacy(sentencias, carpeta);
        documentoMig.createPromocionesFromLegacy(promos, carpeta);
        documentoMig.createOficiosFromLegacy(oficios, carpeta);
        documentoMig.createExhortoSalidaFromLegacy(exhortosCapital, carpeta);
        documentoMig.createExhortoEntradaFromLegacy(exhortosForaneos, carpeta);

        // 9) Piezas: Migración de piezas y sus documentos.
        carpetaMig.createPiezaConDocumentos(carpeta, piezasLegacy);

        // 10) Amparos
        documentoMig.createAmparos(carpeta, amparos);
        

        // 10) Registro de migración
        String recibio = (ubicUlt != null) ? ubicUlt.recibio() : null;
        String puesto = (ubicUlt != null) ? ubicUlt.puestoRecibioTBLPuesto() : null;
        migracionesService.createMigraciones(
                EstadoMigracion.MIGRADO_COMPLETADO,
                "Se ha migrado el expediente principal",
                recibio,
                puesto,
                juzgado,
                carpeta);
    }
}