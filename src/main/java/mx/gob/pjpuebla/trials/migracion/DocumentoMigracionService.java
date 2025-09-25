package mx.gob.pjpuebla.trials.migracion;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

// ACL mappers:
import mx.gob.pjpuebla.migracion.acl.mapper.RubrosMapper;
import mx.gob.pjpuebla.migracion.acl.mapper.SentenciaMapper;
import mx.gob.pjpuebla.migracion.acl.mapper.AmparoMapper;
import mx.gob.pjpuebla.migracion.acl.mapper.EstadoAcuseMapper;
import mx.gob.pjpuebla.migracion.acl.mapper.EstadoOficioMapper;
import mx.gob.pjpuebla.migracion.acl.mapper.PromocionMapper;
import mx.gob.pjpuebla.migracion.acl.mapper.ResolucionMapper;

// Legacy models:
import mx.gob.pjpuebla.migracion.readers.acuerdos.AcuerdosMigracion;
import mx.gob.pjpuebla.migracion.readers.detallesProm.DetallesProm;
import mx.gob.pjpuebla.migracion.readers.exhortoCapital.ExhortosCapitalMigracion;
import mx.gob.pjpuebla.migracion.readers.exhortoForaneo.ExhortoForaneoMigracion;
import mx.gob.pjpuebla.migracion.readers.ocomun.Ocomun;
import mx.gob.pjpuebla.migracion.readers.oficios.OficiosMigracion;
import mx.gob.pjpuebla.migracion.readers.acuerdos.AcuerdosMigracionSaveRecord;
import mx.gob.pjpuebla.migracion.readers.acuerdos.SentenciaMigracionSaveRecord;
import mx.gob.pjpuebla.migracion.readers.amparos.AmparosMigracion;
import mx.gob.pjpuebla.migracion.readers.detallesProm.DetallePromSaveRecord;

// Core:
import mx.gob.pjpuebla.trials.core.conceptos.Concepto;
import mx.gob.pjpuebla.trials.core.conceptos.ConceptoRepository;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.Migrado;
import mx.gob.pjpuebla.trials.util.enums.TipoPromocion;
import mx.gob.pjpuebla.trials.util.enums.TipoSentencia;
import mx.gob.pjpuebla.trials.util.enums.TipoResolucion;
import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaService;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.PiezaRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoService;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalle;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;
import mx.gob.pjpuebla.trials.core.instituciones.Institucion;
import mx.gob.pjpuebla.trials.core.instituciones.InstitucionService;
import mx.gob.pjpuebla.trials.core.instituciones.records.InstitucionRecord;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;

@Service
@RequiredArgsConstructor
public class DocumentoMigracionService {

    private final DocumentoRepository documentoRepository;
    private final DocumentoDetalleRepository documentoDetalleRepository;
    private final AnexoRepository anexoRepository;
    private final DocumentoService documentoService;
    private final ConceptoRepository conceptoRepository;
    private final CarpetaService carpetaService;
    private final InstitucionService institucionService;

    private final SentenciaMapper sentenciaMapper; // ACL
    private final ResolucionMapper resolucionMapper; // ACL
    private final PromocionMapper promocionMapper; // ACL
    private final EstadoAcuseMapper estadoAcuseMapper; // ACL
    private final EstadoOficioMapper estadoOficioMapper; // ACL
    private final AmparoMapper amparoMapper; // ACL

    /*
     * -----------------------------------------------------------------------------
     * -------------------
     * Acuerdos
     * -----------------------------------------------------------------------------
     * -----------------
     */
    @Transactional
    public List<Documento> createAcuerdosFromLegacy(List<AcuerdosMigracion> acuerdos,
            Carpeta carpeta,
            RubrosMapper rubrosMapper) {
        if (acuerdos == null || acuerdos.isEmpty())
            return List.of();

        List<Documento> documentos = new ArrayList<>();
        for (AcuerdosMigracion a : acuerdos) {
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

            Documento created = documentoService.createAcuerdoMigracion(data);
            documentos.add(created);
        }
        return documentos;
    }

    /*
     * -----------------------------------------------------------------------------
     * -------------------
     * Sentencias
     * -----------------------------------------------------------------------------
     * -----------------
     */
    @Transactional
    public List<Documento> createSentenciasFromLegacy(List<AcuerdosMigracion> sentencias,
            Carpeta carpeta) {
        if (sentencias == null || sentencias.isEmpty())
            return List.of();

        List<Documento> documentos = new ArrayList<>();
        for (AcuerdosMigracion s : sentencias) {
            TipoSentencia tipoSent = sentenciaMapper.mapTipoSentencia(s.getResumen());
            TipoResolucion tipoRes = resolucionMapper.mapTipoResolucionSentencia(s.getSentencia());

            SentenciaMigracionSaveRecord data = new SentenciaMigracionSaveRecord(
                    carpeta,
                    s.getFechaResolucion(),
                    tipoSent,
                    tipoRes,
                    s.getClave().toString(),
                    s.getRuta(),
                    s.getFecha());

            Documento created = documentoService.createSentenciaMigracion(data);
            documentos.add(created);
        }
        return documentos;
    }

    /*
     * -----------------------------------------------------------------------------
     * -------------------
     * Promociones
     * -----------------------------------------------------------------------------
     * -----------------
     */
    @Transactional
    public List<Documento> createPromocionesFromLegacy(List<DetallesProm> promociones, Carpeta carpeta) {
        if (promociones == null || promociones.isEmpty())
            return List.of();

        List<Documento> documentos = new ArrayList<>();

        for (DetallesProm p : promociones) {

            TipoPromocion tipoPromocion = promocionMapper.mapTipoPromocion(p.getTipo(), p.getDescrip());

            DetallePromSaveRecord save = new DetallePromSaveRecord(
                    carpeta,
                    tipoPromocion,
                    p.getId().toString(),
                    p.getArchivo(),
                    p.getAcuerdo());
            Documento doc = documentoService.createPromocionMigracion(save);
            createAnexosDePromocion(p.getAnexos(), doc);
            documentos.add(doc);
        }
        return documentos;
    }

    /*
     * -----------------------------------------------------------------------------
     * -------------------
     * Demanda inicial y Anexos
     * -----------------------------------------------------------------------------
     * -----------------
     */
    @Transactional
    public Documento createDemandaInicial(Ocomun ocomun,
            Carpeta carpeta,
            Concepto concepto) {
        DocumentoData data = new DocumentoData();
        return createDocumento(null, carpeta, data,
                ocomun != null ? ocomun.getRutaDigitalizacion() : "",
                null, concepto, null, null);
    }

    @Transactional
    public Documento createDocumento(TipoDocumento tipoDocumento,
            Carpeta carpeta,
            DocumentoData data,
            String ruta,
            String folio,
            Concepto concepto,
            Institucion institucion,
            Documento documentoRelacionado) {
        Documento doc = new Documento()
                .setVersion(0)
                .setTipoDocumento(tipoDocumento)
                .setData(data)
                .setRuta(ruta)
                .setCarpeta(carpeta)
                .setPersona(null)
                .setFechaAsignacion(java.time.LocalDateTime.now())
                .setEstatus(EstadoCarpeta.MIGRADO)
                .setFolio(folio)
                .setConcepto(concepto)
                .setInstitucion(institucion)
                .setAcuerdoRespuesta(documentoRelacionado)
                .setMigrado(Migrado.SI);

        return documentoRepository.save(doc);
    }

    @Transactional
    public List<Anexo> createAnexos(String anexos, Documento documento) {
        if (documento == null)
            throw new IllegalArgumentException("Documento obligatorio.");
        if (anexos == null || anexos.isBlank())
            return List.of();

        List<Anexo> toSave = Pattern.compile("\\s*,\\s*")
                .splitAsStream(anexos)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .map(nombre -> new Anexo().setNombre(nombre).setDocumento(documento))
                .toList();

        return toSave.isEmpty() ? List.of() : anexoRepository.saveAll(toSave);
    }

    @Transactional
    public List<Anexo> createAnexosDePromocion(String anexos, Documento documento) {
        if (anexos == null || anexos.isBlank() || "Sin Anexos".equalsIgnoreCase(anexos)) {
            return List.of();
        }
        List<Anexo> toSave = Pattern.compile("\\s*,\\s*")
                .splitAsStream(anexos)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .map(nombre -> new Anexo().setNombre(nombre).setDocumento(documento))
                .toList();

        return toSave.isEmpty() ? List.of() : anexoRepository.saveAll(toSave);
    }

    /*
     * -----------------------------------------------------------------------------
     * -------------------
     * OFICIOS
     * -----------------------------------------------------------------------------
     * -----------------
     */

    @Transactional
    public List<Documento> createOficiosFromLegacy(List<OficiosMigracion> oficiosMigracion, Carpeta carpeta) {
        List<Documento> oficios = new ArrayList<>();

        oficiosMigracion.forEach(oficioMigracion -> {

            DocumentoData oficioData = new DocumentoData()
                    .setTipoOficio("Jurisdiccional")
                    .setOficioRealizadoPor(oficioMigracion.getNombre()); // Por recomendación se guarda nombre de la
                                                                         // persona quien elaboro el ofico en form data.

            Documento documento = new Documento()
                    .setCarpeta(carpeta)
                    .setFolio(oficioMigracion.getOficio().toString())
                    .setTipoDocumento(TipoDocumento.OFICIO)
                    .setIdHistorico(oficioMigracion.getId())
                    .setData(oficioData)
                    .setInstitucionHistorica(oficioMigracion.getDependencia()) // nombre de la dependencia historica.
                    .setMigrado(Migrado.SI)
                    .setRuta(oficioMigracion.getRutaOfi()) // ruta del oficio.
                    .setEstatus(estadoOficioMapper.estadoOficioMapper(oficioMigracion.getMotivo(),
                            oficioMigracion.getRutaOfiAcuse(), oficioMigracion.getEstatusOfi()));

            documento = documentoRepository.save(documento);

            DocumentoDetalle documentoDetalle = new DocumentoDetalle()
                    .setAsunto(oficioMigracion.getAsunto())
                    .setFechaEmision(oficioMigracion.getFecha())
                    .setFechaEntrega(oficioMigracion.getFechaEntrega())
                    .setDocumento(documento)
                    .setRuta(oficioMigracion.getRutaOfiAcuse()) // ruta del acuse.
                    .setComentario(oficioMigracion.getMotivo())
                    .setEstado(estadoAcuseMapper.mapEstadoAcuse(oficioMigracion.getMotivo(),
                            oficioMigracion.getRutaOfiAcuse()));

            documentoDetalleRepository.save(documentoDetalle);

            oficios.add(documento);
        });

        return oficios;
    }

    /*
     * -----------------------------------------------------------------------------
     * -------------------
     * EXHORTO CAPITAL - SALIDA
     * -----------------------------------------------------------------------------
     * -----------------
     */
    @Transactional
    public List<Documento> createExhortoSalidaFromLegacy(List<ExhortosCapitalMigracion> exhortosCapital,
            Carpeta carpeta) {
        List<Documento> exhortos = new ArrayList<>();

        exhortosCapital.forEach(exhorto -> {
            DocumentoData data = new DocumentoData()
                    .setTramite(exhorto.getTramite())
                    .setDestino(exhorto.getDestino())
                    .setExhortoObservaciones(exhorto.getObse())
                    .setFechaEntrega(exhorto.getFechaEn())
                    .setFechaDevolucion(exhorto.getFechaDe());

            Documento documento = new Documento()
                    .setData(data)
                    .setCarpeta(carpeta)
                    .setFolio(exhorto.getExhorto())
                    .setTipoDocumento(TipoDocumento.EXHORTO_SALIDA);

            documento = documentoRepository.save(documento);

            exhortos.add(documento);
        });

        return exhortos;

    }

    /*
     * -----------------------------------------------------------------------------
     * -------------------
     * EXHORTO FORANEO - ENTRADA
     * -----------------------------------------------------------------------------
     * -----------------
     */
    @Transactional
    public List<Documento> createExhortoEntradaFromLegacy(List<ExhortoForaneoMigracion> exhortosForaneo,
            Carpeta carpeta) {
        List<Documento> exhortos = new ArrayList<>();

        exhortosForaneo.forEach(exhorto -> {
            DocumentoData data = new DocumentoData()
                    .setTramite(exhorto.getTramite())
                    // .setDestino(exhorto.getDestino())
                    .setExhortoObservaciones(exhorto.getObse());
            // .setFechaEntrega(exhorto.getFechaEn())
            // .setFechaDevolucion(exhorto.getFechaDe());

            Documento documento = new Documento()
                    .setData(data)
                    .setCarpeta(carpeta)
                    .setFolio(exhorto.getExhorto())
                    .setTipoDocumento(TipoDocumento.EXHORTO_SALIDA);

            documento = documentoRepository.save(documento);

            exhortos.add(documento);
        });

        return exhortos;

    }

    /*
     * 
     * AMPAROS
     */

    public List<Documento> createAmparos(Carpeta carpeta, List<AmparosMigracion> amparos) {

        if (amparos == null || amparos.isEmpty()) {
            return List.of();
        }

        List<Documento> documentos = new ArrayList<>();
        amparos.forEach(amparo -> {


            DocumentoData data = new DocumentoData()
                    .setAmparoFechaPresentacion(amparo.getFecha())
                    .setAmparoImpugnacion(amparoMapper.mapImpugnacion(amparo.getRevision()))
                    .setAmparoQuejoso(amparo.getQuejoso1())
                    .setAmparoTribunalId(null) // se setea si es AD institución de tipo tribunal feneral
                    .setAmparoSalaId(null) // se setea si es AI segunda instancia - sala
                    .setAmparoSentido(amparoMapper.mapSentido(amparo.getConcede()))
                    .setAmparoSentidoImpugnacion(amparoMapper.mapSentidoImpugnacion(amparo.getImpugna()))
                    .setAmparoTipo(amparoMapper.mapTipoAmparo(amparo.getTipo()))
                    .setAmparoFechaTermino(amparo.getFechaConclusion());

            Documento amparoDoc = new Documento()
                    .setCarpeta(carpeta)
                    .setData(data)
                    .setFolio(amparo.getClave().toString())
                    .setEstatus(EstadoCarpeta.MIGRADO)
                    .setTipoDocumento(TipoDocumento.AMPARO)
                    .setConcepto(conceptoRepository.findByNombre("Distribución").orElseThrow());
            documentos.add(amparoDoc);

            documentoRepository.save(amparoDoc);
            PiezaRecord piezaRecord = new PiezaRecord(null, amparoMapper.mapTipoAmparo(amparo.getTipo()),
                    Collections.singletonList(amparoDoc.getId()));

            carpetaService.createPieza(carpeta.getId(), piezaRecord);
        });

        return documentos;

    }

    private Integer findTribunalDistrito(String nombre){
        return institucionService.findByTipoInstitucion("Tribunal Federal")
            .stream()
            .filter(inst -> inst.nombre().equalsIgnoreCase(nombre))
            .findFirst()
            .map(InstitucionRecord::id)
            .orElse(null);
    }

}
