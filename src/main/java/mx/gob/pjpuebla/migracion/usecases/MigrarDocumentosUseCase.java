package mx.gob.pjpuebla.migracion.usecases;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.migracion.acl.mapper.AmparoMapper;
import mx.gob.pjpuebla.migracion.acl.mapper.EstadoAcuseMapper;
import mx.gob.pjpuebla.migracion.acl.mapper.EstadoOficioMapper;
import mx.gob.pjpuebla.migracion.acl.mapper.PromocionMapper;
import mx.gob.pjpuebla.migracion.acl.mapper.ResolucionMapper;
import mx.gob.pjpuebla.migracion.acl.mapper.RubrosMapper;
import mx.gob.pjpuebla.migracion.acl.mapper.SentenciaMapper;
import mx.gob.pjpuebla.migracion.acl.mapper.TipoPiezaMapper;
import mx.gob.pjpuebla.migracion.readers.acuerdos.AcuerdosMigracion;
import mx.gob.pjpuebla.migracion.readers.acuerdos.AcuerdosMigracionReader;
import mx.gob.pjpuebla.migracion.readers.acuerdos.AcuerdosMigracionRepository;
import mx.gob.pjpuebla.migracion.readers.acuerdos.AcuerdosMigracionSaveRecord;
import mx.gob.pjpuebla.migracion.readers.acuerdos.SentenciaMigracionSaveRecord;
import mx.gob.pjpuebla.migracion.readers.amparos.AmparoMigracionReader;
import mx.gob.pjpuebla.migracion.readers.amparos.AmparoMigracionRecordSave;
import mx.gob.pjpuebla.migracion.readers.amparos.AmparoMigracionRepository;
import mx.gob.pjpuebla.migracion.readers.amparos.AmparosMigracion;
import mx.gob.pjpuebla.migracion.readers.detallesProm.DetallePromSaveRecord;
import mx.gob.pjpuebla.migracion.readers.detallesProm.DetallesProm;
import mx.gob.pjpuebla.migracion.readers.detallesProm.DetallesPromReader;
import mx.gob.pjpuebla.migracion.readers.detallesProm.DetallesPromRepository;
import mx.gob.pjpuebla.migracion.readers.entradas.EntradasMigracion;
import mx.gob.pjpuebla.migracion.readers.entradas.EntradasMigracionReader;
import mx.gob.pjpuebla.migracion.readers.exhortoCapital.ExhortoCapitalMigracionSaveRecord;
import mx.gob.pjpuebla.migracion.readers.exhortoCapital.ExhortosCapitalMigracion;
import mx.gob.pjpuebla.migracion.readers.exhortoCapital.ExhortosCapitalMigracionReader;
import mx.gob.pjpuebla.migracion.readers.exhortoCapital.ExhortosCapitalMigracionRepository;
import mx.gob.pjpuebla.migracion.readers.exhortoForaneo.ExhortoForaneoMigracion;
import mx.gob.pjpuebla.migracion.readers.exhortoForaneo.ExhortoForaneoMigracionReader;
import mx.gob.pjpuebla.migracion.readers.exhortoForaneo.ExhortoForaneoMigracionRepository;
import mx.gob.pjpuebla.migracion.readers.exhortoForaneo.ExhortoForaneoMigracionSaveRecord;
import mx.gob.pjpuebla.migracion.readers.juzgados.JuzgadosMigracionReader;
import mx.gob.pjpuebla.migracion.readers.juzgados.JuzgadosMigracionRegistroRecord;
import mx.gob.pjpuebla.migracion.readers.movimientos.MovimientosMigracionRecord;
import mx.gob.pjpuebla.migracion.readers.oficios.OficiosMigracion;
import mx.gob.pjpuebla.migracion.readers.oficios.OficiosMigracionReader;
import mx.gob.pjpuebla.migracion.readers.oficios.OficiosMigracionRepository;
import mx.gob.pjpuebla.migracion.readers.oficios.OficiosMigracionSaveRecord;
import mx.gob.pjpuebla.migracion.readers.ubicaciones.UbicacionesReader;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.migracion.CarpetaMigracionService;
import mx.gob.pjpuebla.trials.migracion.DocumentoMigracionService;
import mx.gob.pjpuebla.trials.util.enums.EstadoAcuse;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.EstadoMigracion;
import mx.gob.pjpuebla.trials.util.enums.Migrado;
import mx.gob.pjpuebla.trials.util.enums.TipoPromocion;
import mx.gob.pjpuebla.trials.util.enums.TipoResolucion;
import mx.gob.pjpuebla.trials.util.enums.TipoSentencia;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.migracion.Migraciones;
import mx.gob.pjpuebla.trials.workflow.migracion.MigracionesRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class MigrarDocumentosUseCase {

    // READERS legacy
    private final EntradasMigracionReader entradasReader;
    private final JuzgadosMigracionReader juzgadosReader;
    private final AcuerdosMigracionReader acuerdosReader;
    private final DetallesPromReader detallesPromReader;
    private final OficiosMigracionReader oficiosReader;
    private final UbicacionesReader ubicacionesReader;
    private final ExhortosCapitalMigracionReader exhortosCapitalReader;
    private final ExhortoForaneoMigracionReader exhortosForaneosReader;
    private final AmparoMigracionReader amparosReader;

    // REPOS legacy
    private final AcuerdosMigracionRepository acuerdosMigracionRepository;
    private final DetallesPromRepository detallesPromRepository;
    private final OficiosMigracionRepository oficiosMigracionRepository;
    private final ExhortosCapitalMigracionRepository exhortosCapitalMigracionRepository;
    private final ExhortoForaneoMigracionRepository exhortosForaneosMigracionRepository;
    private final AmparoMigracionRepository amparoMigracionRepository;

    // ACL/MAPPERS
    private final RubrosMapper rubrosMapper;
    private final ResolucionMapper resolucionMapper;
    private final PromocionMapper promocionMapper;
    private final SentenciaMapper sentenciaMapper;
    private final AmparoMapper amparoMapper;
    private final EstadoAcuseMapper estadoAcuseMapper;
    private final EstadoOficioMapper estadoOficioMapper;

    // SERVICIOS lado trials.migracion
    private final DocumentoMigracionService documentoMig;
    private final CarpetaMigracionService carpetaMig;
    private final MigracionesRepository migracionesRepository;

    // Record auxiliar para no repedir logica:
    public record ContextoExpedienteMigracion(
            String exp,
            Integer year,
            String claveJuzgado,
            EntradasMigracion entrada,
            JuzgadosMigracionRegistroRecord juzgadoLegacy,
            Carpeta carpetaPrincipal) {
    }

    private ContextoExpedienteMigracion cargarContexto(String exp, Integer year, String claveJuzgado) {
        EntradasMigracion entrada = entradasReader.buscarEntradasPorFiltros(exp, year, claveJuzgado);
        JuzgadosMigracionRegistroRecord juzgadoLegacy = juzgadosReader.findByCodigo(claveJuzgado);
        Carpeta carpetaPrincipal = carpetaMig.findByExpYearAndClaveJuzgado(exp, year, claveJuzgado);

        return new ContextoExpedienteMigracion(
                exp,
                year,
                claveJuzgado,
                entrada,
                juzgadoLegacy,
                carpetaPrincipal);
    }

    @Transactional
    public EstadoMigracion migrarDocumentosExpediente(
            String exp,
            Integer year,
            String claveJuzgado,
            Integer migracionId) {

        // 1) Reutilizamos la entrada y juzgado legacy
        EntradasMigracion entrada = entradasReader.buscarEntradasPorFiltros(exp, year, claveJuzgado);
        JuzgadosMigracionRegistroRecord juzgadoLegacy = juzgadosReader.findByCodigo(claveJuzgado);

        // 2) Recopilamos todo lo legacy necesario
        List<AcuerdosMigracion> acuerdos = acuerdosReader.buscarAcuerdosPorCu(entrada.getCu());
        List<AcuerdosMigracion> sentencias = acuerdosReader.buscarSentenciasPorCu(entrada.getCu());
        List<DetallesProm> promociones = detallesPromReader.buscarPorCu(entrada.getCu());
        List<OficiosMigracion> oficios = oficiosReader.buscarPorCu(entrada.getCu());
        List<MovimientosMigracionRecord> piezasLegacy = ubicacionesReader.buscarPiezasByCu(entrada.getCu(),
                juzgadoLegacy.tablaUbicacion());
        List<ExhortosCapitalMigracion> exhortosCapital = exhortosCapitalReader.buscarPorExpAmoJuzgado(exp, year,
                claveJuzgado);
        List<ExhortoForaneoMigracion> exhortosForaneos = exhortosForaneosReader.buscarPorExpAmoJuzgado(exp, year,
                claveJuzgado);
        List<AmparosMigracion> amparos = amparosReader.buscarPorCu(entrada.getCu());

        // 3) Obtener carpeta principal del sistema nuevo (ya creada por
        // MigrarExpedienteUseCase)
        Carpeta carpetaPrincipal = carpetaMig.findByExpYearAndClaveJuzgado(exp, year, claveJuzgado);

        // 4) Migrar cada tipo de documento con métodos específicos
        migrarAcuerdos(acuerdos, carpetaPrincipal);
        migrarSentencias(sentencias, carpetaPrincipal);
        migrarPromociones(promociones, carpetaPrincipal);
        migrarOficios(oficios, carpetaPrincipal);
        migrarExhortosSalida(exhortosCapital, carpetaPrincipal);
        migrarExhortosEntrada(exhortosForaneos, carpetaPrincipal);
        migrarAmparos(amparos, carpetaPrincipal);
        
        piezasLegacy.forEach(pieza ->    log.info(pieza.cu()) );

        List<Carpeta> piezas = carpetaMig.createPiezas(carpetaPrincipal, piezasLegacy);
        migrarPiezas(piezas);

        // 5) Actualizar estado en tabla Migraciones si quieres más granularidad
        updateMigraciones(migracionId, EstadoMigracion.MIGRADO_COMPLETADO,
                "Se han migrado los documentos exitosamente.");

        return EstadoMigracion.MIGRADO_COMPLETADO;
    }

    // Metodos migrar acuerdos:
    @Transactional
    public void migrarAcuerdosExpediente(String exp, Integer year, String claveJuzgado) {
        ContextoExpedienteMigracion ctx = cargarContexto(exp, year, claveJuzgado);

        List<AcuerdosMigracion> acuerdos = acuerdosReader.buscarAcuerdosPorCu(ctx.entrada().getCu());

        migrarAcuerdos(acuerdos, ctx.carpetaPrincipal());
    }

    @Transactional
    private void migrarAcuerdos(List<AcuerdosMigracion> acuerdos, Carpeta carpeta) {
        List<AcuerdosMigracionSaveRecord> acuerdosRecord = acuerdos
                .stream()
                .map(a -> {
                    List<String> rubros = rubrosMapper.mapRubros(a.getResumen());
                    String rubroPrincipal = rubros.isEmpty() ? "" : rubros.get(0);
                    a.setMigrado(Migrado.SI);

                    return new AcuerdosMigracionSaveRecord(
                            carpeta,
                            rubroPrincipal,
                            a.getFechaResolucion(),
                            rubros,
                            a.getClave().toString(),
                            a.getRuta(),
                            a.getFecha());
                })
                .toList();

        documentoMig.createAcuerdosFromLegacy(acuerdosRecord);
        acuerdosMigracionRepository.saveAll(acuerdos);

    }

    // Metodos migrar sentencias
    @Transactional
    public void migrarSentenciasExpediente(String exp, Integer year, String claveJuzgado) {
        ContextoExpedienteMigracion ctx = cargarContexto(exp, year, claveJuzgado);

        List<AcuerdosMigracion> sentencias = acuerdosReader.buscarSentenciasPorCu(ctx.entrada().getCu());

        migrarSentencias(sentencias, ctx.carpetaPrincipal());
    }

    @Transactional
    private void migrarSentencias(List<AcuerdosMigracion> sentencias, Carpeta carpeta) {
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

    // Metodos migrar promociones
    @Transactional
    public void migrarPromocionesExpediente(String exp, Integer year, String claveJuzgado) {
        ContextoExpedienteMigracion ctx = cargarContexto(exp, year, claveJuzgado);

        List<DetallesProm> promociones = detallesPromReader.buscarPorCu(ctx.entrada.getCu());

        migrarPromociones(promociones, ctx.carpetaPrincipal());
    }

    @Transactional
    private void migrarPromociones(List<DetallesProm> promociones, Carpeta carpeta) {
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

    // Metodos migrar oficios
    @Transactional
    public void migrarOficiosExpediente(String exp, Integer year, String claveJuzgado) {
        ContextoExpedienteMigracion ctx = cargarContexto(exp, year, claveJuzgado);
        List<OficiosMigracion> oficios = oficiosReader.buscarPorCu(ctx.entrada.getCu());
        migrarOficios(oficios, ctx.carpetaPrincipal());
    }

    @Transactional
    private void migrarOficios(List<OficiosMigracion> oficios, Carpeta carpeta) {
        List<OficiosMigracionSaveRecord> oficiosRecord = new ArrayList<>();

        oficios.forEach(oficio -> {
            EstadoCarpeta estadoCarpeta = estadoOficioMapper.estadoOficioMapper(oficio.getMotivo(),
                    oficio.getRutaOfiAcuse(),
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

    // Metodos migrar exhortos salida
    @Transactional
    public void migrarExhortosSalidaExpediente(String exp, Integer year, String claveJuzgado) {
        ContextoExpedienteMigracion ctx = cargarContexto(exp, year, claveJuzgado);
        List<ExhortosCapitalMigracion> exhortos = exhortosCapitalReader.buscarPorExpAmoJuzgado(exp, year, claveJuzgado);
        migrarExhortosSalida(exhortos, ctx.carpetaPrincipal());
    }

    @Transactional
    private void migrarExhortosSalida(List<ExhortosCapitalMigracion> exhortos, Carpeta carpeta) {
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

    // Metodos migrar exhortos entrada
    @Transactional
    public void migrarExhortosEntradaExpediente(String exp, Integer year, String claveJuzgado) {
        ContextoExpedienteMigracion ctx = cargarContexto(exp, year, claveJuzgado);
        List<ExhortoForaneoMigracion> exhorto = exhortosForaneosReader.buscarPorExpAmoJuzgado(exp, year, claveJuzgado);
        migrarExhortosEntrada(exhorto, ctx.carpetaPrincipal());
    }

    @Transactional
    private void migrarExhortosEntrada(List<ExhortoForaneoMigracion> exhorto, Carpeta carpeta) {

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
        exhortosForaneosMigracionRepository.saveAll(exhorto);
    }

    // Metodos migrar amparos
    @Transactional
    public void migrarAmparosExpediente(String exp, Integer year, String claveJuzgado) {
        ContextoExpedienteMigracion ctx = cargarContexto(exp, year, claveJuzgado);
        List<AmparosMigracion> amparos = amparosReader.buscarPorCu(ctx.entrada.getCu());
        migrarAmparos(amparos, ctx.carpetaPrincipal());
    }

    @Transactional
    private void migrarAmparos(List<AmparosMigracion> amparos, Carpeta carpeta) {
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

    @Transactional
    private Migraciones updateMigraciones(Integer migracionId, EstadoMigracion estadoMigracion, String observaciones) {
        Migraciones migracion = migracionesRepository.findById(migracionId)
                .orElseThrow(() -> new NotFoundException("No fue posible encontrar el registro de migración",
                        migracionId.toString()));

        migracion.setEstatus(estadoMigracion);
        migracion.setObservaciones(observaciones);
        return migracionesRepository.save(migracion);
    }

    @Transactional
    private void migrarPiezas(List<Carpeta> piezas) {
        piezas.forEach(pieza -> {
            // obtenemos los acuerdos, sentencias, promociones de la pieza:
            List<AcuerdosMigracion> acuerdos = acuerdosReader.buscarAcuerdosPorCu(pieza.getCu());
            List<AcuerdosMigracion> sentencias = acuerdosReader.buscarSentenciasPorCu(pieza.getCu());
            List<DetallesProm> promos = detallesPromReader.buscarPorCu(pieza.getCu());

            migrarAcuerdos(acuerdos, pieza);
            migrarSentencias(sentencias, pieza);
            migrarPromociones(promos, pieza);

        });
    }

}
