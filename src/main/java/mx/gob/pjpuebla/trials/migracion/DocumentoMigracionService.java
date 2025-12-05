package mx.gob.pjpuebla.trials.migracion;

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


import mx.gob.pjpuebla.migracion.readers.exhortoCapital.ExhortoCapitalMigracionSaveRecord;
// Legacy models:
import mx.gob.pjpuebla.migracion.readers.exhortoForaneo.ExhortoForaneoMigracionSaveRecord;
import mx.gob.pjpuebla.migracion.readers.oficios.OficiosMigracionSaveRecord;
import mx.gob.pjpuebla.migracion.utils.UtilsMigracion;
import mx.gob.pjpuebla.migracion.readers.acuerdos.AcuerdosMigracionSaveRecord;
import mx.gob.pjpuebla.migracion.readers.acuerdos.SentenciaMigracionSaveRecord;
import mx.gob.pjpuebla.migracion.readers.amparos.AmparoMigracionRecordSave;
import mx.gob.pjpuebla.migracion.readers.detallesProm.DetallePromSaveRecord;

// Core:
import mx.gob.pjpuebla.trials.core.conceptos.Concepto;
import mx.gob.pjpuebla.trials.core.conceptos.ConceptoRepository;
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
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentoMigracionService {

    private final DocumentoRepository documentoRepository;
    private final DocumentoDetalleRepository documentoDetalleRepository;
    private final AnexoRepository anexoRepository;
    private final ConceptoRepository conceptoRepository;
    private final CarpetaService carpetaService;
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
            Documento documento = new Documento()
                    .setCarpeta(acuerdo.carpeta())
                    .setTipoDocumento(TipoDocumento.ACUERDO)
                    .setEstatus(EstadoCarpeta.PUBLICADO)
                    .setData(docData)
                    .setFolio(acuerdo.folio())
                    .setRuta(acuerdo.ruta())
                    .setMigrado(Migrado.SI);

            documento = documentoRepository.save(documento);

            DocumentoDetalle documentoDetalle = new DocumentoDetalle()
                    .setTipoAcuerdo(acuerdo.tipoAcuerdo())
                    .setFechaResolucion(acuerdo.fechaResolucion())
                    .setFechaPublicacion(acuerdo.fechaPublicacion())
                    .setDocumento(documento);
            documentoDetalleRepository.save(documentoDetalle);

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

            Documento documentoSentencia = new Documento()
                    .setCarpeta(sentencia.carpeta())
                    .setTipoDocumento(TipoDocumento.SENTENCIA)
                    .setEstatus(EstadoCarpeta.PUBLICADO)
                    .setFolio(sentencia.folio())
                    .setRuta(sentencia.ruta())
                    .setMigrado(Migrado.SI);
            documentoSentencia = documentoRepository.save(documentoSentencia);

            DocumentoDetalle documentoDetalle = new DocumentoDetalle()
                    .setFechaResolucion(sentencia.fechaResolucion())
                    .setFechaPublicacion(sentencia.fechaPublicacion())
                    .setDocumento(documentoSentencia)
                    .setTipoSentencia(sentencia.tipoSentencia())
                    .setTipoResolucion(sentencia.tipoResolucion());
            documentoDetalleRepository.save(documentoDetalle);

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

            Documento promocionNew = new Documento()
                    .setCarpeta(promocion.carpeta())
                    .setTipoDocumento(TipoDocumento.PROMOCION)
                    .setEstatus(EstadoCarpeta.INTEGRADO)
                    .setData(docData)
                    .setFolio(promocion.folio())
                    .setRuta(promocion.ruta())
                    .setConcepto(concepto)
                    .setPersona(persona)
                    .setMigrado(Migrado.SI);

            promocionNew = documentoRepository.save(promocionNew);

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
        Documento demandaInicial = new Documento()
                .setCarpeta(carpeta)
                .setTipoDocumento(null)
                .setEstatus(EstadoCarpeta.MIGRADO)
                .setData(data)
                .setFolio("")
                .setRuta(rutaDigitalizacion)
                .setConcepto(concepto);
        demandaInicial = documentoRepository.save(demandaInicial);

        return demandaInicial;
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

            Documento documento = new Documento()
                    .setCarpeta(oficio.carpeta())
                    .setTipoDocumento(TipoDocumento.OFICIO)
                    .setEstatus(oficio.estadoCarpeta())
                    .setData(oficioData)
                    .setFolio(oficio.oficio().toString())
                    .setRuta(oficio.ruta())
                    .setIdHistorico(oficio.folio())
                    .setInstitucionHistorica(oficio.dependencia())
                    .setMigrado(Migrado.SI);

            documento = documentoRepository.save(documento);

            DocumentoDetalle detalle = new DocumentoDetalle()
                    .setDocumento(documento)
                    .setAsunto(oficio.asunto())
                    .setFechaEmision(oficio.fechaEmision())
                    .setFechaEntrega(oficio.fechaEntrega())
                    .setRuta(oficio.ruta())
                    .setComentario(oficio.motivo())
                    .setEstado(oficio.estadoAcuse());

            documentoDetalleRepository.save(detalle);

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
    public List<Documento> createExhortoSalidaFromLegacy(List<ExhortoCapitalMigracionSaveRecord> exhortosCapital) {
        List<Documento> exhortos = new ArrayList<>();

        exhortosCapital.forEach(exhorto -> {
            DocumentoData data = new DocumentoData()
                    .setTramite(exhorto.tramite())
                    .setDestino(exhorto.destino())
                    .setExhortoObservaciones(exhorto.observaciones())
                    .setFechaEntrega(exhorto.fechaEntrega())
                    .setFechaDevolucion(exhorto.fechaDevolucion());

            Documento documento = new Documento()
                    .setData(data)
                    .setCarpeta(exhorto.carpeta())
                    .setFolio(exhorto.folio())
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
    public List<Documento> createExhortoEntradaFromLegacy(List<ExhortoForaneoMigracionSaveRecord> exhortosForaneo) {
        List<Documento> exhortos = new ArrayList<>();

        exhortosForaneo.forEach(exhorto -> {
            DocumentoData data = new DocumentoData()
                    .setTramite(exhorto.tramite())
                    // .setDestino(exhorto.getDestino())
                    .setExhortoObservaciones(exhorto.observaciones());
            // .setFechaEntrega(exhorto.getFechaEn())
            // .setFechaDevolucion(exhorto.getFechaDe());

            Documento documento = new Documento()
                    .setData(data)
                    .setCarpeta(exhorto.carpeta())
                    .setFolio(exhorto.folio())
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

    public List<Documento> createAmparosFromLegacy(List<AmparoMigracionRecordSave> amparos) {

        if (amparos == null || amparos.isEmpty()) {
            return List.of();
        }

        List<Documento> documentos = new ArrayList<>();
        amparos.forEach(amparo -> {

            DocumentoData data = new DocumentoData()
                    .setAmparoFechaPresentacion(amparo.fechaPresenteacion())
                    .setAmparoImpugnacion(amparo.amparoImpugnacion())
                    .setAmparoQuejoso(amparo.quejoso())
                    .setAmparoTribunalId(null) // se setea si es AD institución de tipo tribunal feneral
                    .setAmparoSalaId(null) // se setea si es AI segunda instancia - sala
                    .setAmparoSentido(amparo.sentido())
                    .setAmparoSentidoImpugnacion(amparo.sentidoImpugnacion())
                    .setAmparoTipo(amparo.tipo())
                    .setAmparoFechaTermino(amparo.fechaTermino());

            Documento amparoDoc = new Documento()
                    .setCarpeta(amparo.carpeta())
                    .setData(data)
                    .setFolio(amparo.folio())
                    .setEstatus(EstadoCarpeta.MIGRADO)
                    .setTipoDocumento(TipoDocumento.AMPARO)
                    .setConcepto(conceptoRepository.findByNombre("Distribución").orElseThrow());
            documentos.add(amparoDoc);

            documentoRepository.save(amparoDoc);

            PiezaRecord piezaRecord = new PiezaRecord(null, amparo.tipo(), null,
                    Collections.singletonList(amparoDoc.getId()));

            carpetaService.createPieza(amparo.carpeta().getId(), piezaRecord); 
        });

        return documentos;

    }
}
