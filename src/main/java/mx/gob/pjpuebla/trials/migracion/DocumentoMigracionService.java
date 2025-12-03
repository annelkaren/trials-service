package mx.gob.pjpuebla.trials.migracion;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// ACL mappers:

import mx.gob.pjpuebla.migracion.acl.mapper.AmparoMapper;

// Legacy models:
import mx.gob.pjpuebla.migracion.readers.exhortoCapital.ExhortosCapitalMigracion;
import mx.gob.pjpuebla.migracion.readers.exhortoCapital.ExhortosCapitalMigracionRepository;
import mx.gob.pjpuebla.migracion.readers.exhortoForaneo.ExhortoForaneoMigracion;
import mx.gob.pjpuebla.migracion.readers.exhortoForaneo.ExhortoForaneoMigracionRepository;
import mx.gob.pjpuebla.migracion.readers.oficios.OficiosMigracionSaveRecord;
import mx.gob.pjpuebla.migracion.utils.UtilsMigracion;
import mx.gob.pjpuebla.migracion.readers.acuerdos.AcuerdosMigracionSaveRecord;
import mx.gob.pjpuebla.migracion.readers.acuerdos.SentenciaMigracionSaveRecord;
import mx.gob.pjpuebla.migracion.readers.amparos.AmparoMigracionRepository;
import mx.gob.pjpuebla.migracion.readers.amparos.AmparosMigracion;
import mx.gob.pjpuebla.migracion.readers.detallesProm.DetallePromSaveRecord;

// Core:
import mx.gob.pjpuebla.trials.core.conceptos.Concepto;
import mx.gob.pjpuebla.trials.core.conceptos.ConceptoRepository;
import mx.gob.pjpuebla.trials.util.enums.EstadoAcuse;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.Migrado;
import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaService;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.PiezaRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalle;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;
import mx.gob.pjpuebla.trials.core.instituciones.InstitucionService;
import mx.gob.pjpuebla.trials.core.instituciones.records.InstitucionRecord;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.util.enums.TipoResolucion;
import mx.gob.pjpuebla.trials.util.enums.TipoSentencia;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentoMigracionService {

    private final DocumentoRepository documentoRepository;
    private final DocumentoDetalleRepository documentoDetalleRepository;
    private final AnexoRepository anexoRepository;
    private final ConceptoRepository conceptoRepository;
    private final CarpetaService carpetaService;
    private final InstitucionService institucionService;

    private final AmparoMapper amparoMapper; // ACL

    private final ExhortosCapitalMigracionRepository exhortosCapitalMigracionRepository;
    private final ExhortoForaneoMigracionRepository exhortoForaneoMigracionRepository;
    private final AmparoMigracionRepository amparoMigracionRepository;
    private final NotificacionMigracionService notificacionMigracionService;
    private final PersonaService personaService;

    private static final String CONCEPTO_NOT_FOUND = "Concepto no encontrado";

    /*
     * -----------------------------------------------------------------------------
     * -------------------
     * Acuerdos
     * -----------------------------------------------------------------------------
     * -----------------
     */
    @Transactional
    public List<Documento> createAcuerdosFromLegacy(List<AcuerdosMigracionSaveRecord> acuerdos) {

        if (acuerdos == null || acuerdos.isEmpty())
            return List.of();

        List<Documento> documentos = new ArrayList<>();

        for (AcuerdosMigracionSaveRecord acuerdo : acuerdos) {
            // Creación de acuerdo:
            // Creamos información de los rubros en documentoData:
            DocumentoData docData = new DocumentoData().setRubros(acuerdo.rubros());

            // Creamos información del documento.
            Documento documento = createDocumento(
                    acuerdo.carpeta(),
                    TipoDocumento.ACUERDO,
                    EstadoCarpeta.PUBLICADO,
                    docData,
                    acuerdo.folio(),
                    acuerdo.ruta(),
                    null,
                    null,
                    null,
                    null);

            createDocumentoDetalle(
                    acuerdo.tipoAcuerdo(),
                    acuerdo.fechaResolucion(),
                    "",
                    "",
                    acuerdo.fechaPublicacion(),
                    documento,
                    null,
                    null,
                    "",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);

            documentos.add(documento);
            // Obtener notificaciones de acuerdos y crearlas:
            notificacionMigracionService.crearNotificacionLegacy(documento, Integer.parseInt((acuerdo.folio())));
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
    public List<Documento> createSentenciasFromLegacy(List<SentenciaMigracionSaveRecord> sentencias) {
        if (sentencias == null || sentencias.isEmpty())
            return List.of();

        List<Documento> documentos = new ArrayList<>();
        for (SentenciaMigracionSaveRecord sentencia : sentencias) {

            Documento documentoSentencia = createDocumento(
                    sentencia.carpeta(),
                    TipoDocumento.SENTENCIA,
                    EstadoCarpeta.PUBLICADO,
                    null,
                    sentencia.folio(),
                    sentencia.ruta(),
                    null,
                    null,
                    null,
                    null);

            createDocumentoDetalle(
                    null,
                    sentencia.fechaResolucion(),
                    "",
                    "",
                    sentencia.fechaPublicacion(),
                    documentoSentencia,
                    sentencia.tipoSentencia(),
                    sentencia.tipoResolucion(),
                    "",
                    null, null, null, null, null, null);

            documentos.add(documentoSentencia);
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
    public List<Documento> createPromocionesFromLegacy(List<DetallePromSaveRecord> promociones) {
        if (promociones == null || promociones.isEmpty())
            return List.of();

        List<Documento> documentos = new ArrayList<>();

        for (DetallePromSaveRecord promocion : promociones) {

            // inicia persistencia:
            Persona persona = personaService.getAuditor();
            Concepto concepto = conceptoRepository.findByNombre("Adjuntar")
                    .orElseThrow(() -> new NotFoundException(CONCEPTO_NOT_FOUND, "Adjuntar"));

            DocumentoData docData = new DocumentoData().setTipoPromocion(promocion.tipoPromocion());

            Documento promocionNew = createDocumento(
                    promocion.carpeta(),
                    TipoDocumento.PROMOCION,
                    EstadoCarpeta.INTEGRADO,
                    docData,
                    promocion.folio(),
                    promocion.ruta(),
                    concepto,
                    persona,
                    null,
                    null);

            // Buscamos si esta promoción esta relacionada con un acuerdo
            Optional<Documento> acuerdo = documentoRepository.findByTipoDocumentoAndFolio(TipoDocumento.ACUERDO,
                    promocion.acuerdo());

            if (acuerdo.isPresent()) {
                Documento documentoAcuerdo = acuerdo.get();
                promocionNew.setAcuerdoRespuesta(documentoAcuerdo);
            }

            // FIn persistencia
            createAnexosDePromocion(promocion.anexos(), promocionNew);
            documentos.add(promocionNew);
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
    public Documento createDemandaInicial(Carpeta carpeta, Concepto concepto, String rutaDigitalizacion) {
        DocumentoData data = new DocumentoData();

        return createDocumento(
                carpeta,
                null,
                EstadoCarpeta.MIGRADO,
                data, 
                "", 
                rutaDigitalizacion,
                concepto,
                null,
                null,
                null); 
    }

    @Transactional
    public List<Anexo> createAnexos(String anexos, Documento documento) {
        if (documento == null)
            throw new IllegalArgumentException("Documento obligatorio.");
        if (anexos == null || anexos.isBlank())
            return List.of();

        String anexosNormalizados = UtilsMigracion.normalizeSpaces(anexos);

        List<Anexo> toSave = Pattern.compile("\\s*,\\s*")
                .splitAsStream(anexosNormalizados)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .map(nombre -> new Anexo()
                        .setNombre(nombre)
                        .setDocumento(documento))
                .toList();

        return toSave.isEmpty() ? List.of() : anexoRepository.saveAll(toSave);
    }

    @Transactional
    public List<Anexo> createAnexosDePromocion(String anexos, Documento documento) {
        if (anexos == null || anexos.isBlank() || "Sin Anexos".equalsIgnoreCase(anexos)) {
            return List.of();
        }

        List<Anexo> anexosToSave = Pattern.compile("\\s*,\\s*")
                .splitAsStream(anexos)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .map(nombre -> new Anexo().setNombre(nombre).setDocumento(documento))
                .toList();

        return anexosToSave.isEmpty() ? List.of() : anexoRepository.saveAll(anexosToSave);
    }

    /*
     * -----------------------------------------------------------------------------
     * -------------------
     * OFICIOS
     * -----------------------------------------------------------------------------
     * -----------------
     */

    @Transactional
    public List<Documento> createOficiosFromLegacy(List<OficiosMigracionSaveRecord> oficiosMigracion) {
        List<Documento> oficios = new ArrayList<>();

        oficiosMigracion.forEach(oficio -> {

            DocumentoData oficioData = new DocumentoData()
                    .setTipoOficio("Jurisdiccional")
                    .setOficioRealizadoPor(oficio.nombre());

            Documento documento = createDocumento(
                oficio.carpeta(),
                TipoDocumento.OFICIO,
                oficio.estadoCarpeta(), 
                oficioData, 
                oficio.oficio().toString(), 
                oficio.ruta(),
                null,
                null,
                oficio.folio(),
                oficio.dependencia());
            
            createDocumentoDetalle(
                null, 
                null,
                null, 
                null,
                null, 
                documento, 
                null, 
                null, 
                null,
                oficio.asunto(),
                oficio.fechaEmision(),
                oficio.fechaEntrega(),
                oficio.ruta(),
                oficio.motivo(),
                oficio.estadoAcuse()
                
            );
                   
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
            exhorto.setMigrado(Migrado.SI);
        });

        exhortosCapitalMigracionRepository.saveAll(exhortosCapital);

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
            exhorto.setMigrado(Migrado.SI);
            exhortos.add(documento);
        });

        exhortoForaneoMigracionRepository.saveAll(exhortosForaneo);

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

            amparo.setMigrado(Migrado.SI);

            PiezaRecord piezaRecord = new PiezaRecord(null, amparoMapper.mapTipoAmparo(amparo.getTipo()), null,
                    Collections.singletonList(amparoDoc.getId()));

            carpetaService.createPieza(carpeta.getId(), piezaRecord);
        });

        amparoMigracionRepository.saveAll(amparos);

        return documentos;

    }


    public Integer findTribunalDistrito(String nombre) {
        return institucionService.findByTipoInstitucion("Tribunal Federal")
                .stream()
                .filter(inst -> inst.nombre().equalsIgnoreCase(nombre))
                .findFirst()
                .map(InstitucionRecord::id)
                .orElse(null);
    }


    //Metodos para creación de documento y detalels:
    public Documento createDocumento(Carpeta carpeta, TipoDocumento tipoDocumento, EstadoCarpeta estadoCarpeta,
            DocumentoData data, String folio, String ruta, Concepto concepto, Persona persona, Integer idHistorico, String institucionHistorica) {
        Documento documento = new Documento()
                .setCarpeta(carpeta)
                .setTipoDocumento(tipoDocumento)
                .setEstatus(estadoCarpeta)
                .setData(data)
                .setFolio(folio)
                .setRuta(ruta)
                .setConcepto(concepto)
                .setPersona(persona)
                .setMigrado(Migrado.SI)
                .setIdHistorico(idHistorico)
                .setInstitucionHistorica(institucionHistorica);

        return documentoRepository.save(documento);
    }

    public DocumentoDetalle createDocumentoDetalle(String tipoAcuerdo, LocalDate fechaResolucion, String etapaProcesal,
            String resumen, LocalDate fechaPublicacion, Documento documento, TipoSentencia tipoSentencia,
            TipoResolucion tipoResolucion, String extractoSentencia, String asunto, LocalDate fechaEmision, LocalDate fechaEntrega, String rutaAcuse, String motivo, EstadoAcuse estadoAcuse) {
        DocumentoDetalle documentoDetalle = new DocumentoDetalle()
                .setTipoAcuerdo(tipoAcuerdo)
                .setFechaResolucion(fechaResolucion)
                .setEtapaProcesal(etapaProcesal)
                .setResumen(resumen)
                .setFechaPublicacion(fechaPublicacion)
                .setDocumento(documento)
                .setTipoSentencia(tipoSentencia)
                .setTipoResolucion(tipoResolucion)
                .setExtractoSentencia(extractoSentencia)
                .setAsunto(asunto)
                .setFechaEmision(fechaEmision)
                .setFechaEntrega(fechaEntrega)
                .setRuta(rutaAcuse)
                .setComentario(motivo)
                .setEstado(estadoAcuse);

        return documentoDetalleRepository.save(documentoDetalle);
    }

}
