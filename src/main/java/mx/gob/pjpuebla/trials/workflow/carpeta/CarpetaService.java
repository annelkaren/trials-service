package mx.gob.pjpuebla.trials.workflow.carpeta;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.migracion.readers.entradas.EntradasMigracion;
import mx.gob.pjpuebla.migracion.readers.entradas.EntradasMigracionRepository;
import mx.gob.pjpuebla.trials.core.conceptos.Concepto;
import mx.gob.pjpuebla.trials.core.conceptos.ConceptoRepository;
import mx.gob.pjpuebla.trials.core.etapaprocesal.EtapaProcesal;
import mx.gob.pjpuebla.trials.core.etapaprocesal.EtapaProcesalRepository;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoService;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaRepository;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.procedimientos.Procedimiento;
import mx.gob.pjpuebla.trials.core.rubros.Rubro;
import mx.gob.pjpuebla.trials.core.tipopieza.TipoPieza;
import mx.gob.pjpuebla.trials.core.tipopieza.TipoPiezaRepository;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.core.rubros.RubroRecord;
import mx.gob.pjpuebla.trials.core.rubros.RubroRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.error.ConflictException;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.Utils;
import mx.gob.pjpuebla.trials.util.enums.*;
import mx.gob.pjpuebla.trials.util.enums.carpeta.*;
import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoBandejaRecepcionRecord;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.workflow.audiencias.Audiencia;
import mx.gob.pjpuebla.trials.workflow.audiencias.AudienciaRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.carpetadetalle.CarpetaDetalle;
import mx.gob.pjpuebla.trials.workflow.carpeta.carpetadetalle.CarpetaDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.carpetaetapas.CarpetaEtapas;
import mx.gob.pjpuebla.trials.workflow.carpeta.carpetaetapas.CarpetaEtapasRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.*;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalle;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoDetalleCarpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoDetalleCarpetaResponse;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoRecepcionMovimientosRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.migracion.Migraciones;
import mx.gob.pjpuebla.trials.workflow.migracion.MigracionesRepository;
import mx.gob.pjpuebla.trials.workflow.movimientos.Movimiento;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoRepository;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoService;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Transactional
@RequiredArgsConstructor
@Service
@Slf4j
public class CarpetaService {

        private final CarpetaRepository carpetaRepository;
        private final PersonaDocumentoRepository personaDocumentoRepository;
        private final DocumentoRepository documentoRepository;
        private final AnexoRepository anexoRepository;
        private final PersonaService personaService;
        private final MovimientoService movimientoService;
        private final TipoPiezaRepository tipoPiezaRepository;
        private final CarpetaDetalleRepository carpetaDetalleRepository;
        private final CarpetaEtapasRepository carpetaEtapasRepository;
        private final TipoJuicioRepository tipoJuicioRepository;
        private final EtapaProcesalRepository etapaProcesalRepository;
        private final RubroRepository rubroRepository;
        private final DocumentoDetalleRepository documentoDetalleRepository;
        private final MovimientoRepository movimientoRepository;
        private final JuzgadoRepository juzgadoRepository;
        private final ConceptoRepository conceptoRepository;
        private final PersonaRepository personaRepository;
        private final EntradasMigracionRepository entradasMigracionRepository;
        private final JuzgadoService juzgadoService;
        private final MigracionesRepository migracionesRepository;
        private final AudienciaRepository audienciaRepository;

        private static final String ACTOR_LABEL = "Actor";
        private static final String DEMANDADO_LABEL = "Demandado";
        private static final String VICTIMA_LABEL = "Victimas";
        private static final String IMPUTADO_LABEL = "Imputados";
        private static final String DOC_NOT_FOUND = "Documento no encontrado";
        private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";

        private static String normalizeKey(String s) {
                return (s == null) ? null : s.trim().toLowerCase();
        }

        private String norm(String s) {
                return (s == null) ? null : s.trim().toLowerCase();
        }

        public CarpetaResponseRecord getCarpetaResponseByNumExpYearJuzgado(String expediente, Integer juzgadoId) {
                // Usar una variable auxiliar para la modificación de juzgadoId
                final Integer finalJuzgadoId = obtenerJuzgadoIdFinal(juzgadoId);

                Juzgado juzgado = this.juzgadoRepository.findById(finalJuzgadoId)
                                .orElseThrow(() -> new NotFoundException("Juzgado no encontrado",
                                                expediente + " - " + finalJuzgadoId));
                Carpeta carpeta;
                if (juzgado.getMateria().getNombre().toLowerCase().contains("penal")
                                || juzgado.getMateria().getNombre().toLowerCase().contains("justicia")) {
                        carpeta = carpetaRepository
                                        .findByExpedienteAndJuzgadoIdPenal(expediente, juzgado.getNomenclatura(),
                                                        finalJuzgadoId)
                                        .orElseThrow(() -> new NotFoundException("Carpeta no encontrada",
                                                        expediente + " - " + finalJuzgadoId));
                } else {
                        carpeta = carpetaRepository.findByExpedienteAndJuzgadoId(expediente, finalJuzgadoId)
                                        .orElseThrow(() -> new NotFoundException("Carpeta no encontrada",
                                                        expediente + " - " + finalJuzgadoId));
                }
                // validaciones nuevas para penal:
                String nombreMateria = carpeta.getJuzgado().getMateria().getNombre();
                boolean isMateriaPenalOrJusticiaPA = nombreMateria.equals("Penal")
                                || nombreMateria.equals("Justicia para adolescentes");

                String actor = getNombrePersonaByIdAndParte(carpeta.getId(), ACTOR_LABEL);
                String demandado = getNombrePersonaByIdAndParte(carpeta.getId(), DEMANDADO_LABEL);
                String tipoJuicio = carpeta.getTipoJuicio().getNombre();

                List<String> victimas = null;
                List<String> imputados = null;
                Estado estadoJuzgado = carpeta.getJuzgado().getEstado();

                if (isMateriaPenalOrJusticiaPA) {
                        victimas = personaDocumentoRepository
                                        .findPersonaAndTipoParteByCarpetaIdPenal(carpeta.getId(), VICTIMA_LABEL,
                                                        List.of(Rol.PRINCIPAL))
                                        .stream()
                                        .map(persona -> persona.pseudonimo() != null ? persona.pseudonimo()
                                                        : persona.nombre() + " " + persona.apellidoPaterno() + " "
                                                                        + persona.apellidoPaterno())
                                        .toList();

                        imputados = personaDocumentoRepository
                                        .findPersonaAndTipoParteByCarpetaIdPenal(carpeta.getId(), IMPUTADO_LABEL,
                                                        List.of(Rol.PRINCIPAL))
                                        .stream()
                                        .map(persona -> persona.pseudonimo() != null ? persona.pseudonimo()
                                                        : persona.nombre() + " " + persona.apellidoPaterno() + " "
                                                                        + persona.apellidoPaterno())
                                        .toList();
                }
                return new CarpetaResponseRecord(carpeta.getId(), actor, demandado, tipoJuicio, victimas, imputados,
                                estadoJuzgado, carpeta.getEstatus().getEtiqueta());
        }

        public CarpetaResponsePromSinExpediente getCarpetaPromocionSinExpediente(String expediente, Integer juzgadoId,
                        Integer isApelacion) {

                // Usar una variable auxiliar para la modificación de juzgadoId
                final Integer finalJuzgadoId = obtenerJuzgadoIdFinal(juzgadoId);

                // Busca Juzgado en sistema SECJ JAVA, de no encontrarlo manda exception:
                Juzgado juzgado = juzgadoService.requiredJuzgadoById(finalJuzgadoId);
                String nombreMateriaJuz = juzgado.getMateria().getNombre().toLowerCase();
                Optional<Carpeta> carpetaOptional;

                if (nombreMateriaJuz.contains("penal") || nombreMateriaJuz.contains("justicia")) {
                        carpetaOptional = carpetaRepository.findByExpedienteAndJuzgadoIdPenal(expediente,
                                        juzgado.getNomenclatura(), finalJuzgadoId);
                } else {
                        log.info("Buscando expediente: " + Utils.normalizarExpediente(expediente) + " con juzgado id: "
                                        + finalJuzgadoId);

                        carpetaOptional = carpetaRepository.findByExpedienteNormalizadoAndJuzgadoId(
                                        Utils.normalizarExpediente(expediente), finalJuzgadoId);
                }

                if (carpetaOptional.isPresent()) {
                        CarpetaResponseRecord carpetaResponse = getDataCarpeta(carpetaOptional.get());
                        return new CarpetaResponsePromSinExpediente(
                                        carpetaResponse.idCarpeta(),
                                        carpetaResponse.actor(),
                                        carpetaResponse.demandado(),
                                        carpetaResponse.tipoJuicio(),
                                        carpetaResponse.victimas(),
                                        carpetaResponse.imputados(),
                                        carpetaResponse.estadoJuzgado(),
                                        carpetaResponse.estadoCarpeta(),
                                        Integer.valueOf(200),
                                        "carpeta encontrada");

                } else if (carpetaOptional.isEmpty() && isApelacion == 1) {
                        return new CarpetaResponsePromSinExpediente(404,
                                        "El expediente no existe en el sistema");
                } else {
                        // Busca en SECJ PHP:
                        String expedientePart = Utils.normalizarExpediente(expediente.split("/")[0]);
                        Integer year = Integer.parseInt(expediente.split("/")[1]);

                        log.info("Expediente: {}, Year: {}, Juzgado: {}", expedientePart, year,
                                        juzgado.getClaveJuzgado());

                        Optional<EntradasMigracion> entrada = entradasMigracionRepository
                                        .findTopByExpedienteNormalizado(
                                                        expedientePart, year, juzgado.getClaveJuzgado(), "A");

                        if (entrada.isPresent()) {
                                // El expediente existe en SECJ PHP
                                return new CarpetaResponsePromSinExpediente(200,
                                                "¿El expediente no ha sido importado, desea recepciónar la promoción sin expediente?");
                        } else {
                                // El expediente no existe en SECJ PHP
                                return new CarpetaResponsePromSinExpediente(404,
                                                "El expediente no existe, desea aun así recepcionar la promoción?");
                        }
                }
        }

        private CarpetaResponseRecord getDataCarpeta(Carpeta carpeta) {
                // validaciones nuevas para penal:
                String nombreMateria = carpeta.getJuzgado().getMateria().getNombre();
                boolean isMateriaPenalOrJusticiaPA = nombreMateria.equals("Penal")
                                || nombreMateria.equals("Justicia para adolescentes");

                String actor = getNombrePersonaByIdAndParte(carpeta.getId(), ACTOR_LABEL);
                String demandado = getNombrePersonaByIdAndParte(carpeta.getId(), DEMANDADO_LABEL);
                String tipoJuicio = carpeta.getTipoJuicio().getNombre();

                List<String> victimas = null;
                List<String> imputados = null;
                Estado estadoJuzgado = carpeta.getJuzgado().getEstado();

                if (isMateriaPenalOrJusticiaPA) {
                        victimas = personaDocumentoRepository
                                        .findPersonaAndTipoParteByCarpetaIdPenal(carpeta.getId(), VICTIMA_LABEL,
                                                        List.of(Rol.PRINCIPAL))
                                        .stream()
                                        .map(persona -> persona.pseudonimo() != null ? persona.pseudonimo()
                                                        : persona.nombre() + " " + persona.apellidoPaterno()
                                                                        + " "
                                                                        + persona.apellidoPaterno())
                                        .toList();

                        imputados = personaDocumentoRepository
                                        .findPersonaAndTipoParteByCarpetaIdPenal(carpeta.getId(),
                                                        IMPUTADO_LABEL,
                                                        List.of(Rol.PRINCIPAL))
                                        .stream()
                                        .map(persona -> persona.pseudonimo() != null ? persona.pseudonimo()
                                                        : persona.nombre() + " " + persona.apellidoPaterno()
                                                                        + " "
                                                                        + persona.apellidoPaterno())
                                        .toList();
                }

                return new CarpetaResponseRecord(carpeta.getId(), actor, demandado, tipoJuicio, victimas,
                                imputados,
                                estadoJuzgado, carpeta.getEstatus().getEtiqueta());
        }

        private Integer obtenerJuzgadoIdFinal(Integer juzgadoId) {
                if (juzgadoId == null) {
                        Persona auditor = personaService.getAuditor();
                        if (auditor == null || auditor.getJuzgado() == null) {
                                throw new IllegalArgumentException("No se puede determinar el juzgado.");
                        }
                        return auditor.getJuzgado().getId();
                } else {
                        return juzgadoId;
                }
        }

        protected String getNombrePersonaByIdAndParte(Integer id, String parte) {
                List<Rol> rol = List.of(Rol.PRINCIPAL);
                List<PersonaDocumentoRecord> personas = personaDocumentoRepository
                                .findPersonaAndTipoParteByCarpetaIdLibroGobierno(id,
                                                parte, rol);

                if (personas == null || personas.isEmpty()) {
                        return "";
                }

                List<String> nombresCompletos = getNombreCompletos(personas);
                return String.join(", ", nombresCompletos);
        }

        private List<String> getNombreCompletos(List<PersonaDocumentoRecord> personas) {
                List<String> nombresCompletos = new ArrayList<>();

                for (PersonaDocumentoRecord p : personas) {
                        String nombreCompleto = buildNombreCompleto(p);
                        if (!nombreCompleto.isBlank()) {
                                nombresCompletos.add(nombreCompleto);
                        }
                }

                return nombresCompletos;
        }

        private String buildNombreCompleto(PersonaDocumentoRecord p) {
                List<String> partesNombre = new ArrayList<>();
                if (p.nombre() != null && !p.nombre().isBlank()) {
                        partesNombre.add(p.nombre());
                }
                if (p.apellidoPaterno() != null && !p.apellidoPaterno().isBlank()) {
                        partesNombre.add(p.apellidoPaterno());
                }
                if (p.apellidoMaterno() != null && !p.apellidoMaterno().isBlank()) {
                        partesNombre.add(p.apellidoMaterno());
                }
                return String.join(" ", partesNombre);
        }

        @Transactional(readOnly = true)
        public List<ApelacionRecordResponse> getPersonasDocumentoByCarpetaId(Integer carpetaId) {
                return personaDocumentoRepository.findPersonaDocumentoByCarpetaId(carpetaId);
        }

        public BandejaRecepcionRecord getBandejaRecepcionByDocumentoId(Integer documentoId) {

                Documento documento = validacionBandejaRecepcion(documentoId);

                // Buscar la bandeja de recepción por id del documento
                BandejaRecepcionRecord bandeja = carpetaRepository.findByDocumentoId(documento.getId());
                if (bandeja == null) {
                        throw new NotFoundException("No se encontró la carpeta con el documentoId: " + documentoId,
                                        "documentoId");
                }

                // Obtener y r los anexos de la bandeja de recepción
                List<AnexoBandejaRecepcionRecord> anexos = carpetaRepository.findAnexosByDocumentoId(documento.getId());
                return new BandejaRecepcionRecord(
                                documento.getId(),
                                bandeja.folio(),
                                bandeja.expediente(),
                                bandeja.tipo(),
                                bandeja.rutaDigitalizacion(),
                                anexos);
        }

        public List<DocumentoRecord> actualizarInformacionAnexos(
                        List<DocumentoRecepcionMovimientosRecord> docRecepcionMovimientosRecord) {

                Persona persona = personaService.getAuditor();
                List<DocumentoRecord> documentosResponse = new ArrayList<>();

                for (DocumentoRecepcionMovimientosRecord recepcion : docRecepcionMovimientosRecord) {
                        Documento documento = validacionBandejaRecepcion(recepcion.documentoId());

                        // Validar anexos faltantes
                        List<String> anexosFaltantes = Optional.ofNullable(recepcion.anexos())
                                        .orElse(Collections.emptyList())
                                        .stream()
                                        .filter(anexo -> (anexo.estado() == EstadoAnexo.NORECIBIDO
                                                        || anexo.estado() == null))
                                        .map(AnexoBandejaRecepcionRecord::nombre)
                                        .toList();

                        // Actualizar los anexos
                        if (recepcion.anexos() != null) {
                                for (AnexoBandejaRecepcionRecord anexo : recepcion.anexos()) {
                                        Anexo anexoTemp = anexoRepository.findById(anexo.id())
                                                        .orElseThrow(() -> new NotFoundException(
                                                                        "No se encontró el anexo con id: " + anexo.id(),
                                                                        "anexoId"));
                                        anexoTemp.setEstado(anexo.estado());
                                        anexoRepository.save(anexoTemp);
                                }
                        }

                        // Actualizar el estatus en carpeta o documento
                        if (documento.getTipoDocumento() == null
                                        || documento.getCarpeta().getTipoCarpeta().equals(TipoCarpeta.APELACION)) {
                                documento.getCarpeta().setEstatus(EstadoCarpeta.ASIGNADO);
                                documento.getCarpeta().setPersona(persona);
                                carpetaRepository.save(documento.getCarpeta());
                        } else {
                                documento.setEstatus(EstadoCarpeta.ASIGNADO);
                                documento.setPersona(persona);
                        }

                        // Guardar el documento después de todas las actualizaciones
                        documento = documentoRepository.save(documento);

                        // Crear movimiento
                        movimientoService.createMovimentoWithObservaciones(
                                        (documento.getTipoDocumento() == null || Objects
                                                        .equals(documento.getTipoDocumento(), TipoDocumento.APELACION))
                                                                        ? documento.getCarpeta()
                                                                        : null,
                                        (documento.getTipoDocumento() == null || Objects
                                                        .equals(documento.getTipoDocumento(), TipoDocumento.APELACION))
                                                                        ? null
                                                                        : documento,
                                        EstadoCarpeta.ASIGNADO.name(),
                                        recepcion.observaciones(),
                                        recepcion.recomendaciones(),
                                        setObservacionesAnexos(anexosFaltantes),
                                        documento.getCarpeta().getConcepto().getNombre(),
                                        documento.getCarpeta().getConcepto().getDias().toString() + "d");

                        // Agregar el documento procesado a la respuesta
                        documentosResponse.add(new DocumentoRecord(
                                        documento.getId(),
                                        documento.getCarpeta().getFolio(),
                                        documento.getCarpeta().getTipoCarpeta()));
                }

                return documentosResponse;
        }

        public Documento validacionBandejaRecepcion(Integer documentoId) {

                // Buscar y validar la existencia de la persona y el documento
                Persona persona = personaService.getAuditor();

                Documento documento = documentoRepository.findById(documentoId)
                                .orElseThrow(() -> new NotFoundException(
                                                "No se encontró el documento asociado al documentoId: " + documentoId,
                                                "documentoId"));

                // Verificar permisos de acceso al juzgado
                Juzgado juzgado = persona.getJuzgado();
                if (!juzgado.getId().equals(documento.getCarpeta().getJuzgado().getId())) {
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                                        "No tiene permiso para visualizar esta información");
                }

                return documento;

        }

        public String setObservacionesAnexos(List<String> anexos) {
                if (anexos.isEmpty()) {
                        return null;
                }
                String concatenatedAnexos = String.join(", ", anexos);
                return "Hacen falta los siguientes anexos: " + concatenatedAnexos + ". Por favor validar.";
        }

        public List<CarpetaCatalogoRecord> getCatalogoList(String catalogo) {
                return switch (catalogo) {
                        case "catalogoCondicionMigratoria" -> Arrays.stream(CatalogoCondicionMigratoria.values())
                                        .map(e -> new CarpetaCatalogoRecord(e.name(), e.getEtiqueta()))
                                        .toList();
                        case "catalogoDeterminacionJurisdiccional" ->
                                Arrays.stream(CatalogoDeterminacionJurisdiccional.values())
                                                .map(e -> new CarpetaCatalogoRecord(e.name(), e.getEtiqueta()))
                                                .toList();
                        case "catalogoTiposDomicilio" -> Arrays.stream(CatalogoTiposDomicilio.values())
                                        .map(e -> new CarpetaCatalogoRecord(e.name(), e.getEtiqueta()))
                                        .toList();
                        case "catalogoGrupoVulnerable" -> Arrays.stream(CatalogoGrupoVulnerable.values())
                                        .map(e -> new CarpetaCatalogoRecord(e.name(), e.getEtiqueta()))
                                        .toList();
                        case "catalogoProfesionOficio" -> Arrays.stream(CatalogoProfesionOficio.values())
                                        .map(e -> new CarpetaCatalogoRecord(e.name(), e.getEtiqueta()))
                                        .toList();
                        case "catalogoFrecuenciaIngreso" -> Arrays.stream(CatalogoFrecuenciaIngreso.values())
                                        .map(e -> new CarpetaCatalogoRecord(e.name(), e.getEtiqueta()))
                                        .toList();
                        case "catalogoTipoDefensor" -> Arrays.stream(CatalogoTipoDefensor.values())
                                        .map(e -> new CarpetaCatalogoRecord(e.name(), e.getEtiqueta()))
                                        .toList();
                        case "catalogoIngresoMensualNeto" -> Arrays.stream(CatalogoIngresoMensualNeto.values())
                                        .map(e -> new CarpetaCatalogoRecord(e.name(), e.getEtiqueta()))
                                        .toList();
                        case "catalogoEstadoCivil" -> Arrays.stream(CatalogoEstadoCivil.values())
                                        .map(e -> new CarpetaCatalogoRecord(e.name(), e.getEtiqueta()))
                                        .toList();
                        case "catalogoDiscapacidades" -> Arrays.stream(CatalogoDiscapacidades.values())
                                        .map(e -> new CarpetaCatalogoRecord(e.name(), e.getEtiqueta()))
                                        .toList();
                        case "catalogoSentidoAmparo" -> Arrays.stream(CatalogoSentidoAmparo.values())
                                        .map(e -> new CarpetaCatalogoRecord(e.name(), e.getEtiqueta()))
                                        .toList();
                        case "catalogoImpugnacionAmparo" -> Arrays.stream(CatalogoImpugnacionAmparo.values())
                                        .map(e -> new CarpetaCatalogoRecord(e.name(), e.getEtiqueta()))
                                        .toList();
                        case "catalogoTipoPiezas" -> this.tipoPiezaRepository.findAll().stream()
                                        .map(e -> new CarpetaCatalogoRecord(e.getClave(), e.getTipo()))
                                        .toList();
                        case "tipoEntradas" -> Stream.concat(
                                        Arrays.stream(TipoCarpeta.values())
                                                        .map(e -> new CarpetaCatalogoRecord(e.name(), e.getEtiqueta())),
                                        Stream.of(new CarpetaCatalogoRecord("PROMOCION", "Promoción"))).toList();
                        case "posicionTrabajo" -> Arrays.stream(CatalogoPosicionTrabajo.values())
                                        .map(e -> new CarpetaCatalogoRecord(String.valueOf(e.getId()), e.getNombre()))
                                        .toList();
                        default -> Collections.emptyList();
                };
        }

        public InfoExpedienteRecord getInfoExpediente(Integer docId) {
                Carpeta carpeta = carpetaRepository.findById(docId)
                                .orElseThrow(() -> new NotFoundException(DOC_NOT_FOUND, docId.toString()));

                // Obtiene nombre de rubros
                List<RubroRecord> rubros = carpeta.getRubros().stream()
                                .map(rubro -> new RubroRecord(rubro.getId(), rubro.getNombre()))
                                .sorted(Comparator.comparing(RubroRecord::name))
                                .toList();

                // Obtiene nombre de procedimientos dados los rubros
                String tipoProcedimiento = carpeta.getRubros().stream()
                                .map(Rubro::getProcedimiento)
                                .filter(Objects::nonNull)
                                .map(Procedimiento::getNombre)
                                .distinct()
                                .sorted()
                                .collect(Collectors.joining(", "));

                // Obtiene los participantes del expediente
                List<PersonaDataRecord> apelacionRecordResponseList = personaDocumentoRepository
                                .findPersonaDocumentoDataByCarpetaId(carpeta.getId());

                DateTimeFormatter pattern = DateTimeFormatter.ofPattern(DATE_FORMAT);

                EtapaProcesalRecord etapaProcesalRecord = null;

                Optional<CarpetaEtapas> optionalCarpetaEtapas = carpetaEtapasRepository
                                .findByCarpetaId(carpeta.getId());
                if (optionalCarpetaEtapas.isPresent()) {
                        CarpetaEtapas carpetaEtapas = optionalCarpetaEtapas.get();
                        etapaProcesalRecord = new EtapaProcesalRecord(carpetaEtapas.getEtapaProcesal().getId(),
                                        carpetaEtapas.getEtapaProcesal().getNombre());
                }

                // obtenemos juzgado y nombre del juez :

                String nombreJuez = getJuezExpediente(carpeta);

                return new InfoExpedienteRecord(
                                carpeta.getExpediente(),
                                carpeta.getTipoJuicio().getNombre(), // TODO mapear de forma correcta expediente tipo
                                                                     // PENAL
                                carpeta.getTipoJuicio().getId(), // TODO mapear de forma correcta expediente tipo PENAL
                                nombreJuez,
                                carpeta.getAudit().getFechaAlta().format(pattern),
                                "Asunto de penal desde Backend", // TODO añadir asunto para expediente tipo PENAL
                                tipoProcedimiento,
                                rubros,
                                etapaProcesalRecord,
                                getParticipantes(apelacionRecordResponseList),
                                null, // TODO añadir razón de devolución
                                carpeta.getJuzgado().getMateria().getNombre(),
                                carpeta.getJuzgado().getMateria().getId(),
                                carpeta.getTipoJuicio().getTipoSistema() != null
                                                ? carpeta.getTipoJuicio().getTipoSistema().getNombre()
                                                : null,
                                carpeta.getJuzgado().getNombre(),
                                (carpeta.getTipoPieza() != null) ? carpeta.getTipoPieza().getTipo() : null);
        }

        private String getJuezExpediente(Carpeta carpeta) {
                TipoSistema tipoSistema = carpeta.getTipoJuicio().getTipoSistema();
                Materia materia = carpeta.getJuzgado().getMateria();

                if (!tipoSistema.getNombre().equals("Oral") && !materia.getNombre().equals("FAMILIAR")) {
                        Juzgado juzgado = carpeta.getJuzgado();
                        Persona juez = personaRepository.findByJuzgadoAndRolPrincipal(juzgado, "Juez").orElse(null);
                        return juez != null
                                        ? juez.getNombre() + " " + juez.getApellidoPaterno() + " "
                                                        + (juez.getApellidoMaterno() != null ? juez.getApellidoMaterno()
                                                                        : "")
                                        : "";
                }

                if (tipoSistema.getNombre().equals("Oral") && materia.getNombre().equals("FAMILIAR")) {
                        Optional<Audiencia> audiencia = audienciaRepository.findFirstByCarpetaOrderByIdDesc(carpeta);
                        Persona juez = audiencia.map(a -> a.getSala().getJuez()).orElse(null);

                        return audiencia.isPresent() && juez != null
                                        ? juez.getNombre() + " "
                                                        + juez.getApellidoPaterno() + " "
                                                        + (juez.getApellidoMaterno() != null ? juez.getApellidoMaterno()
                                                                        : "")
                                        : "";
                }

                return "";

        }

        public static List<ParticipantesRecord> getParticipantes(List<PersonaDataRecord> participantes) {
                Map<String, List<ParticipanteDataRecord>> agrupadoPorTipo = new HashMap<>();
                for (PersonaDataRecord participante : participantes) {
                        String nombreCompleto = Stream
                                        .of(participante.nombre(), participante.apellidoPaterno(),
                                                        participante.apellidoMaterno())
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.joining(" "));

                        if (nombreCompleto.isBlank()) {
                                nombreCompleto = participante.pseudonimo();
                        }

                        if (!nombreCompleto.isEmpty()) {
                                ParticipanteDataRecord persona = new ParticipanteDataRecord(participante.id(),
                                                nombreCompleto,
                                                participante.rol());
                                agrupadoPorTipo.computeIfAbsent(participante.tipoPartesNombre(), k -> new ArrayList<>())
                                                .add(persona);
                        }
                }
                return agrupadoPorTipo.entrySet().stream()
                                .map(entry -> new ParticipantesRecord(entry.getKey(), entry.getValue()))
                                .toList();
        }

        public Carpeta createPieza(Integer carpetaId, PiezaRecord piezaRecord) {
                Persona persona = personaService.getAuditor();

                // Obtenemos el concepto que tiene la promoción para colocarselo a la pieza:
                Concepto conceptoPromocion = piezaRecord.documentos().stream()
                                .map(documentoRepository::findById)
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .filter(doc -> TipoDocumento.PROMOCION.equals(doc.getTipoDocumento()))
                                .map(Documento::getConcepto)
                                .findFirst()
                                .orElse(conceptoRepository.findByNombre("Nueva creación")
                                                .orElseThrow(() -> new IllegalStateException(
                                                                "El concepto 'Nueva creación' no se encontró en la base de datos")));

                Carpeta carpetaPadre = carpetaRepository.findById(carpetaId)
                                .orElseThrow(() -> new NotFoundException("La Carpeta no existe", "carpetaId"));
                TipoPieza tipoPieza = tipoPiezaRepository
                                .findByIdOrClave(piezaRecord.tipoPiezaId(), piezaRecord.clavePieza())
                                .stream().findFirst()
                                .orElseThrow(() -> new NotFoundException("El Tipo de Pieza no existe", "tipoPieza"));

                if (piezaRecord.documentos().isEmpty()) {
                        throw new NotFoundException("No se puede crear una pieza vacía", "documentos");
                }

                Carpeta pieza = new Carpeta();

                String numeroPieza = carpetaPadre.getExpediente() + "/"
                                + consecutivoPieza(carpetaId, tipoPieza.getClave());

                pieza.setFolio(documentoRepository.getNextValPieza().toString());
                pieza.setExpediente(numeroPieza);
                pieza.setCarpetaPadre(carpetaPadre);
                pieza.setFechaAsignacion(LocalDateTime.now());
                pieza.setTipoCarpeta(TipoCarpeta.PIEZA);
                pieza.setPersona(persona);
                pieza.setSelloEstatus(SelloEstatus.VALIDO);
                pieza.setEstatus(EstadoCarpeta.ASIGNADO);
                pieza.setJuzgado(carpetaPadre.getJuzgado());
                pieza.setTipoJuicio(carpetaPadre.getTipoJuicio());
                pieza.setTipoPieza(tipoPieza);
                pieza.setConcepto(conceptoPromocion);
                pieza.setAudit(new Audit());

                pieza = carpetaRepository.save(pieza);

                asignarPieza(pieza, piezaRecord.documentos());
                movimientoService.createMovimento(pieza, null, persona, "", EstadoCarpeta.ASIGNADO.name());
                return pieza;

        }

        public Carpeta createPiezaMigracion(Integer carpetaId, PiezaRecord piezaRecord) {
                Persona persona = personaService.getAuditor();
                // Obtenemos el registro de la promoción a la cual se quiere adjuntar 'crear
                // pieza':
                Documento promocion = documentoRepository.findById(piezaRecord.promocionId())
                                .orElseThrow(() -> new NotFoundException("La promoción no existe", "promocionId"));

                Optional<Migraciones> migracionesOpt = migracionesRepository.findByCarpetaId(carpetaId);

                if (promocion.getMigrado().equals(Migrado.SI) && migracionesOpt.isPresent()) {
                        Migraciones migraciones = migracionesOpt.get();
                        if (migraciones.getEstatus().equals(EstadoMigracion.EXPEDIENTE_MIGRADO)) {
                                throw new ConflictException(
                                                "Error al crear pieza, es necesario migrar el expediente completo.");
                        }

                        if (migraciones.getEstatus().equals(EstadoMigracion.MIGRADO_COMPLETADO)
                                        && migraciones.getCarpeta().getPersona() != persona) {
                                throw new ConflictException(
                                                "Error al crear pieza, es necesario que usted tenga asignado el expediente completo.");
                        }
                }

                // Obtenemos el concepto que tiene la promoción para colocarselo a la pieza:
                Concepto conceptoPromocion = piezaRecord.documentos().stream()
                                .map(documentoRepository::findById)
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .filter(doc -> TipoDocumento.PROMOCION.equals(doc.getTipoDocumento()))
                                .map(Documento::getConcepto)
                                .findFirst()
                                .orElse(
                                                conceptoRepository.findByNombre("Nueva creación")
                                                                .orElseThrow(() -> new IllegalStateException(
                                                                                "El concepto 'Nueva creación' no se encontró en la base de datos")));

                Carpeta carpetaPadre = carpetaRepository.findById(carpetaId)
                                .orElseThrow(() -> new NotFoundException("La Carpeta no existe", "carpetaId"));
                TipoPieza tipoPieza = tipoPiezaRepository
                                .findByIdOrClave(piezaRecord.tipoPiezaId(), piezaRecord.clavePieza())
                                .stream().findFirst()
                                .orElseThrow(() -> new NotFoundException("El Tipo de Pieza no existe", "tipoPieza"));

                if (piezaRecord.documentos().isEmpty()) {
                        throw new NotFoundException("No se puede crear una pieza vacía", "documentos");
                }

                Carpeta pieza = new Carpeta();

                String numeroPieza = carpetaPadre.getExpediente() + "/"
                                + consecutivoPieza(carpetaId, tipoPieza.getClave());

                pieza.setFolio(documentoRepository.getNextValPieza().toString());
                pieza.setExpediente(numeroPieza);
                pieza.setCarpetaPadre(carpetaPadre);
                pieza.setFechaAsignacion(LocalDateTime.now());
                pieza.setTipoCarpeta(TipoCarpeta.PIEZA);
                pieza.setPersona(persona);
                pieza.setSelloEstatus(SelloEstatus.VALIDO);
                pieza.setEstatus(EstadoCarpeta.ASIGNADO);
                pieza.setJuzgado(carpetaPadre.getJuzgado());
                pieza.setTipoJuicio(carpetaPadre.getTipoJuicio());
                pieza.setTipoPieza(tipoPieza);
                pieza.setConcepto(conceptoPromocion);
                pieza.setAudit(new Audit());

                pieza = carpetaRepository.save(pieza);

                asignarPieza(pieza, piezaRecord.documentos());
                movimientoService.createMovimento(pieza, null, persona, "", EstadoCarpeta.ASIGNADO.name());
                return pieza;

        }

        public String consecutivoPieza(Integer carpetaId, String clavePieza) {
                if (!carpetaRepository.existsById(carpetaId))
                        throw new NotFoundException("La Carpeta con Id " + carpetaId + " no existe", "carpetaId");
                if (!tipoPiezaRepository.existsByClave(clavePieza))
                        throw new NotFoundException("El tipo de pieza " + clavePieza + " no existe", "clavePieza");

                return clavePieza
                                + StringUtils.leftPad(
                                                carpetaRepository.getNumeroPieza(carpetaId, clavePieza).toString(), 2,
                                                '0');
        }

        public void asignarPieza(Carpeta pieza, List<Integer> documentos) {
                Persona persona = personaService.getAuditor();

                for (Integer documentoId : documentos) {
                        Documento documento = documentoRepository.findById(documentoId).orElseThrow();

                        if (documento.getData() == null) {
                                documento.setData(new DocumentoData());
                        }

                        documento.setCarpeta(pieza);
                        documento.setData(
                                        documento.getData().setPieza(pieza.getExpediente())
                                                        .setEstadoPieza(EstadoCarpeta.ASIGNADO));
                        documentoRepository.save(documento);
                        movimientoService.createMovimento(null, documento, persona, "Asignar a pieza",
                                        EstadoCarpeta.ASIGNADO.name());
                }

                Documento documento = documentoRepository.findById(documentos.stream().findFirst().orElseThrow())
                                .orElseThrow();

                documento.setEstatus(EstadoCarpeta.ASIGNADO);
                documentoRepository.save(documento);
        }

        public InfoExpedienteDetalleRecord getInfoExpedienteDetalle(Integer docId) {
                DateTimeFormatter pattern = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                Carpeta carpeta = carpetaRepository.findById(docId)
                                .orElseThrow(() -> new NotFoundException(DOC_NOT_FOUND, docId.toString()));

                CarpetaDetalle carpetaDetalle = carpetaDetalleRepository.findByCarpetaId(carpeta.getId());

                if (carpetaDetalle == null) {
                        throw new NotFoundException("No hay registro del detalle de la carpeta", "docId");
                }

                Persona persona = carpeta.getPersona();
                String nombre = persona.getNombre() + " " +
                                (persona.getApellidoPaterno() != null
                                                ? carpeta.getPersona().getApellidoPaterno() + " "
                                                : "")
                                +
                                (persona.getApellidoMaterno() != null
                                                ? carpeta.getPersona().getApellidoMaterno()
                                                : "")
                                + ", ";

                String rol = (persona.getOcupacion() != null ? persona.getOcupacion() + ", " : "");

                String ubicacion = persona.getJuzgado() != null ? persona.getJuzgado().getNombre()
                                : persona.getOficialia().getNombre();

                return new InfoExpedienteDetalleRecord(
                                carpeta.getDeterminacionJurisdiccional() != null
                                                ? carpeta.getDeterminacionJurisdiccional().name()
                                                : null,
                                carpetaDetalle.getFechaAdmision() != null
                                                ? carpetaDetalle.getFechaAdmision().format(pattern)
                                                : null,
                                carpetaDetalle.getFechaDesechado() != null
                                                ? carpetaDetalle.getFechaDesechado().format(pattern)
                                                : null,
                                nombre + rol + ubicacion,
                                carpetaDetalle.getAsunto(),
                                null,
                                carpetaDetalle.getObservaciones(),
                                carpeta.getSentencia() != null ? carpeta.getSentencia().name() : null,
                                carpetaDetalle.getPromovente(),
                                carpetaDetalle.getNumeroCarpetaInvestigacion(),
                                carpetaDetalle.getNumeroOficio(),
                                carpetaDetalle.getLugarHecho(),
                                carpetaDetalle.getFechaHecho(),
                                carpetaDetalle.getCantidadPrincipal(),
                                carpetaDetalle.getMoneda(),
                                carpetaDetalle.getNumeroHijos(),
                                carpetaDetalle.getNumeroHijosMenoresEdad(),
                                carpetaDetalle.getActaMatrimonio(),
                                carpetaDetalle.getLugarRegistroMatrimonio(),
                                carpetaDetalle.getEntidad(),
                                carpetaDetalle.getMunicipio(),
                                carpetaDetalle.getLocalidad(),
                                carpetaDetalle.getFechaRegistro() != null
                                                ? carpetaDetalle.getFechaRegistro().format(pattern)
                                                : null,
                                carpetaDetalle.getHoraFormal(),
                                carpetaDetalle.getHoraMaterial(),
                                carpetaDetalle.getLugarDisposicion(),
                                carpetaDetalle.getPresentacionImputado() != null
                                                ? carpetaDetalle.getPresentacionImputado().name()
                                                : null,
                                carpetaDetalle.getSolicitudAudiencia() != null
                                                ? carpetaDetalle.getSolicitudAudiencia().name()
                                                : null,
                                carpetaDetalle.getFechaPresentacionImputado() != null
                                                ? carpetaDetalle.getFechaPresentacionImputado().format(pattern)
                                                : null,
                                carpetaDetalle.getTipoJuicio() != null ? carpetaDetalle.getTipoJuicio().getId() : null,
                                carpetaDetalle.getTipoJuicio() != null ? carpetaDetalle.getTipoJuicio().getNombre()
                                                : null,
                                carpetaDetalle.getCujus(),
                                carpetaDetalle.getFechaEjecutoria());
        }

        public void saveExpedienteDetalle(
                        SaveExpedienteDetalleRecord detalle,
                        Integer docId) {
                DateTimeFormatter pattern = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                Carpeta carpeta = carpetaRepository.findById(docId)
                                .orElseThrow(() -> new NotFoundException(DOC_NOT_FOUND, docId.toString()));
                CarpetaDetalle carpetaDetalle = carpetaDetalleRepository.findByCarpetaId(docId);

                // TODO editar fase para PENAL
                // TODO editar juez en caso penal

                carpeta.setDeterminacionJurisdiccional(detalle.determinacion() != null ? detalle.determinacion()
                                : CatalogoDeterminacionJurisdiccional.PRESENTACION);

                // edita tipo juicio
                // Si tipoJuicioHijoId está presente, se busca y asigna el tipo de juicio, sino
                // no se hace nada
                if (detalle.tipoJuicioHijoId() != null) {
                        TipoJuicio tipoJuicioHijo = tipoJuicioRepository.findById(detalle.tipoJuicioHijoId())
                                        .orElseThrow(
                                                        () -> new NotFoundException("TipoJuicio no encontrado",
                                                                        detalle.tipoJuicioHijoId().toString()));
                        carpetaDetalle.setTipoJuicio(tipoJuicioHijo);

                }
                // edita rubros
                Set<Rubro> rubros = detalle.rubros().stream()
                                .map(rubroRecord -> rubroRepository.findById(rubroRecord.id())
                                                .orElseThrow(
                                                                () -> new IllegalArgumentException(
                                                                                "Rubro no encontrado con id: "
                                                                                                + rubroRecord.id())))
                                .collect(Collectors.toSet());

                carpeta.setRubros(rubros);

                // edita etapa procesal
                // Si tipoJuicioHijoId está presente, se busca y asigna el tipo de juicio, sino
                // no se hace nada
                if (detalle.etapaProcesal() != null && detalle.etapaProcesal().id() != null) {
                        EtapaProcesal etapaProcesalDetalle = etapaProcesalRepository
                                        .findById(detalle.etapaProcesal().id())
                                        .orElseThrow(() -> new NotFoundException("Etapa Procesal no encontrada",
                                                        detalle.etapaProcesal().id().toString()));
                        Optional<CarpetaEtapas> optionalCarpetaEtapas = carpetaEtapasRepository
                                        .findByCarpetaId(carpeta.getId());
                        if (optionalCarpetaEtapas.isPresent()) {
                                CarpetaEtapas carpetaEtapas = optionalCarpetaEtapas.get();
                                // Si el registro Etapa Procesal mas reciente no coincide con el obtenido de
                                // detalle registra la nueva etapa procesal
                                if (!carpetaEtapas.getEtapaProcesal().getId().equals(etapaProcesalDetalle.getId())) {
                                        carpetaEtapasRepository.save(new CarpetaEtapas()
                                                        .setCarpeta(carpeta)
                                                        .setFechaRegistro(LocalDateTime.now())
                                                        .setEtapaProcesal(etapaProcesalDetalle));
                                }
                        } else {
                                carpetaEtapasRepository.save(new CarpetaEtapas()
                                                .setCarpeta(carpeta)
                                                .setFechaRegistro(LocalDateTime.now())
                                                .setEtapaProcesal(etapaProcesalDetalle));
                        }
                }

                // TODO falta actualizar domicilios para Familiar Oralidad

                carpetaDetalle
                                .setAsunto(detalle.asunto())
                                .setObservaciones(detalle.observaciones())
                                .setPromovente(detalle.promovente())
                                .setNumeroCarpetaInvestigacion(detalle.numeroCarpetaInvestigacion())
                                .setNumeroCarpetaInvestigacion(detalle.numeroCarpetaInvestigacion())
                                .setNumeroOficio(detalle.numeroOficio())
                                .setLugarHecho(detalle.lugarHecho())
                                .setFechaHecho(detalle.fechaHecho())
                                .setCantidadPrincipal(detalle.cantidadPrincipal())
                                .setMoneda(detalle.moneda())
                                .setNumeroHijos(detalle.numeroHijos())
                                .setNumeroHijosMenoresEdad(detalle.numeroHijosMenoresEdad())
                                .setActaMatrimonio(detalle.actaMatrimonio())
                                .setLugarRegistroMatrimonio(detalle.lugarRegistroMatrimonio())
                                .setEntidad(detalle.entidad())
                                .setMunicipio(detalle.municipio())
                                .setLocalidad(detalle.localidad())
                                .setFechaRegistro(
                                                detalle.fechaRegistro() != null
                                                                ? (LocalDate.parse(detalle.fechaRegistro(),
                                                                                pattern))
                                                                : null)
                                .setHoraFormal(detalle.horaFormal())
                                .setHoraMaterial(detalle.horaMaterial())
                                .setLugarDisposicion(detalle.lugarDisposicion())
                                .setPresentacionImputado(detalle.presentacionImputado())
                                .setSolicitudAudiencia(detalle.solicitudAudiencia())
                                .setFechaPresentacionImputado(detalle.fechaPresentacionImputado() != null
                                                ? (LocalDate.parse(detalle.fechaPresentacionImputado(), pattern))
                                                : null)
                                .setCujus(detalle.cujus())
                                .setFechaEjecutoria(detalle.fechaEjecutoria());

                carpetaDetalleRepository.save(carpetaDetalle);
                carpetaRepository.save(carpeta);
        }

        public List<PiezaRecordResponse> getPiezas(Integer documentoId) {
                return this.carpetaRepository.findPiezasByDocumentoId(documentoId);
        }

        public PiezaRecordResponse adjuntarPiezaDocumentos(Integer piezaId, PiezaRecord piezaRecord) {
                Carpeta pieza = carpetaRepository.findById(piezaId)
                                .orElseThrow(() -> new NotFoundException("La pieza no existe", "piezaId"));

                asignarPieza(pieza, piezaRecord.documentos());

                return new PiezaRecordResponse(pieza.getId(), pieza.getExpediente(), pieza.getTipoPieza().getTipo(),
                                pieza.getEstatus());
        }

        public List<DocumentoDetalleCarpetaResponse> getAllPiezasCarpeta(String key, Integer carpetaId) {
                List<DocumentoDetalleCarpeta> list = carpetaRepository.findPiezasByCarpetaPadreId(key, carpetaId);
                Persona persona = personaService.getAuditor();

                return list.stream()
                                .map(
                                                e -> new DocumentoDetalleCarpetaResponse(
                                                                e.id(),
                                                                "PIEZA DE " + e.tipoPieza().getTipo(),
                                                                e.folio(),
                                                                e.fechaRegistro(),
                                                                e.ruta(),
                                                                personaService.findById(e.personaOrigenId()).permisos()
                                                                                .get(0).name(),
                                                                e.tipoCarpeta().name(),
                                                                Objects.equals(e.personaOrigenId(), persona.getId()),
                                                                e.estadoCarpeta().name(),
                                                                ""))
                                .toList();
        }

        public List<DocumentoDetalleCarpetaResponse> getAllDocumentosCarpeta(String key, Integer carpetaId) {
                List<DocumentoDetalleCarpeta> list = documentoRepository.findDocumentosByCarpeta(carpetaId);

                return list.stream()
                                .map(
                                                e -> new DocumentoDetalleCarpetaResponse(
                                                                e.id(),
                                                                e.tipoDocumento() != null
                                                                                ? e.tipoDocumento().getEtiqueta()
                                                                                : "DEMANDA",
                                                                e.folio(),
                                                                e.fechaRegistro(),
                                                                e.ruta(),
                                                                (e.personaOrigenId() != null)
                                                                                ? personaService.findById(
                                                                                                e.personaOrigenId())
                                                                                                .permisos().get(0)
                                                                                                .name()
                                                                                : "",
                                                                e.tipoCarpeta().name(),
                                                                Boolean.FALSE,
                                                                e.estadoCarpeta() != null ? e.estadoCarpeta().name()
                                                                                : "",
                                                                ""))
                                .filter(d -> key == null || d.tipo().toUpperCase().contains(key)).toList();
        }

        public Page<DocumentoDetalleCarpetaResponse> getAllDocumentosPiezas(String key, Integer carpetaId,
                        Pageable pageable) {

                List<DocumentoDetalleCarpetaResponse> documentos = this.getAllDocumentosCarpeta(key, carpetaId);

                if (key != null && key.equals("TODAS PIEZAS")) {
                        key = "";
                }
                List<DocumentoDetalleCarpetaResponse> piezas = this.getAllPiezasCarpeta(key, carpetaId);

                List<DocumentoDetalleCarpetaResponse> lista = Stream.concat(documentos.stream(), piezas.stream())
                                .toList();

                return new PageImpl<>(lista, pageable, lista.size());
        }

        public PiezaRecordResponse acoplarPieza(Integer piezaId, String estadoPiezaReq) {
                Carpeta pieza = carpetaRepository.findById(piezaId)
                                .orElseThrow(() -> new NotFoundException("La pieza no existe", "piezaId"));
                EstadoCarpeta estadoPieza = EstadoCarpeta.valueOf(estadoPiezaReq);
                Persona persona = personaService.getAuditor();

                if (pieza.getTipoCarpeta() != TipoCarpeta.PIEZA) {
                        throw new ConflictException("No es una pieza");
                }

                if (pieza.getEstatus() == EstadoCarpeta.CANCELADO || pieza.getEstatus() == EstadoCarpeta.INTEGRADO) {
                        throw new ConflictException("No se puede actualizar el estado de la Pieza");
                }

                if (estadoPieza == EstadoCarpeta.CANCELADO
                                && validaCancelacionPieza(pieza.getId(),
                                                pieza.getAudit().getFechaAlta()) == Boolean.FALSE) {
                        throw new ConflictException(
                                        "No es posible la cancelación, fue turnada o tiene documentos publicados");
                }

                List<Documento> documentos = documentoRepository.findByCarpetaId(piezaId);

                for (Documento doc : documentos) {
                        doc.setData(doc.getData().setEstadoPieza(estadoPieza));

                        documentoRepository.save(doc);
                }

                pieza.setEstatus(estadoPieza);
                movimientoService.createMovimento(pieza, null, persona, null, estadoPieza.name());

                pieza = carpetaRepository.save(pieza);

                return new PiezaRecordResponse(pieza.getId(), pieza.getExpediente(), pieza.getTipoPieza().getTipo(),
                                pieza.getEstatus());
        }

        public Page<LibroGobiernoRecord> libroDeGobierno(
                        Pageable pageable, String key, String expediente,
                        LocalDate fechaFrom, LocalDate fechaTo, String descripcion,
                        String actorFilter, String demandadoFilter, String cujus) {

                key = normalizeKey(key);
                expediente = norm(expediente);
                String tipoJuicio = norm(descripcion);
                actorFilter = norm(actorFilter);
                demandadoFilter = norm(demandadoFilter);
                cujus = norm(cujus);

                // operamos fechas:
                LocalDateTime from = (fechaFrom != null) ? fechaFrom.atStartOfDay() : null;

                LocalDateTime to = null;
                if (fechaFrom != null && fechaTo == null) {
                        to = fechaFrom.plusDays(1).atStartOfDay(); // EXCLUSIVO ✅
                }

                Persona persona = personaService.getAuditor();

                List<Juzgado> juzgados = persona.getJuzgado() != null
                                ? List.of(persona.getJuzgado())
                                : persona.getOficialia().getJuzgados();

                juzgados = juzgados.stream()
                                .filter(j -> j.getEstado().equals(Estado.ACTIVE))
                                .toList();

                Pageable mappedPageable = mapSortLibroGobierno(pageable);

                return carpetaRepository.findLibroGobierno(
                                juzgados,
                                key,
                                expediente,
                                from,
                                to,
                                tipoJuicio,
                                actorFilter,
                                demandadoFilter,
                                cujus,
                                persona.getId().longValue(),
                                EstadoCarpeta.ASIGNADO,
                                mappedPageable);
        }

        private Pageable mapSortLibroGobierno(Pageable pageable) {
                if (pageable == null || pageable.isUnpaged())
                        return Pageable.unpaged();

                Sort incoming = pageable.getSort();
                Sort mapped = Sort.unsorted();

                for (Sort.Order o : incoming) {
                        String p = o.getProperty();
                        boolean asc = o.isAscending();

                        Sort s = switch (p) {
                                case "numExpediente", "expediente" -> JpaSort.unsafe("c.expediente");
                                case "fechaHora" -> JpaSort.unsafe("c.audit.fechaAlta");
                                case "tipoJuicio" -> JpaSort.unsafe("tj.nombre");
                                case "cujus" -> JpaSort.unsafe("COALESCE(cd.cujus,'')");
                                case "asignado" -> JpaSort.unsafe(
                                                "CASE WHEN c.persona.id = :userId AND c.estatus = :estadoAsignado THEN 1 ELSE 0 END");
                                default -> null;
                        };

                        if (s != null)
                                mapped = mapped.and(asc ? s.ascending() : s.descending());
                }

                // default
                if (mapped.isUnsorted()) {
                        mapped = JpaSort.unsafe("c.audit.fechaAlta").descending()
                                        .and(JpaSort.unsafe("c.id").descending());
                } else {
                        mapped = mapped.and(JpaSort.unsafe("c.id").descending());
                }

                return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), mapped);
        }

        public SentenciaPublicaResponseRecord getCarpetaByExpedienteAndSentencia(String expediente) {
                Persona auditor = personaService.getAuditor();
                if (auditor == null || auditor.getJuzgado() == null) {
                        throw new IllegalArgumentException("No se puede determinar el juzgado.");
                }

                Documento documento = documentoRepository
                                .findByExpedienteAndTipoDocumento(expediente, TipoDocumento.SENTENCIA,
                                                auditor.getJuzgado().getId())
                                .orElseThrow(() -> new NotFoundException("Carpeta no encontrada o le falta sentencia",
                                                expediente));

                DocumentoDetalle documentoDetalle = documentoDetalleRepository.findByDocumentoId(documento.getId())
                                .orElseThrow(
                                                () -> new NotFoundException("Detalle documento no encontrado",
                                                                documento.getId().toString()));

                String actor = getNombrePersonaByIdAndParte(documento.getCarpeta().getId(), ACTOR_LABEL);
                String demandado = getNombrePersonaByIdAndParte(documento.getCarpeta().getId(), DEMANDADO_LABEL);
                return new SentenciaPublicaResponseRecord(
                                documento.getCarpeta().getId(),
                                documento.getId(),
                                actor,
                                demandado,
                                documento.getCarpeta().getJuzgado().getMateria().getNombre(),
                                documento.getCarpeta().getJuzgado().getNombre(),
                                documentoDetalle.getTipoSentencia().name(),
                                documentoDetalle.getTipoResolucion().name(),
                                documentoDetalle.getFechaResolucion());
        }

        public Boolean validaCancelacionPieza(Integer piezaId, LocalDateTime fechaRegistro) {
                // Buscar movimientos
                Integer movimientos = movimientoRepository.countByCarpetaId(piezaId);
                Integer numDocumentos = documentoRepository.countByCarpetaIdAndTipoDocumentoAndAuditFechaAltaAfter(
                                piezaId,
                                TipoDocumento.ACUERDO, fechaRegistro);

                if (movimientos > 1 || numDocumentos > 0) {
                        return Boolean.FALSE;
                }

                return Boolean.TRUE;

        }

        @Transactional
        public void actualizarEstado(List<Integer> ids) {
                List<Carpeta> carpetas = carpetaRepository.findAllById(ids);
                carpetas.forEach(carpeta -> carpeta.setEstatus(EstadoCarpeta.ARCHIVO_JUDICIAL));
                carpetaRepository.saveAll(carpetas);
        }

        public CarpetaResponseRecord getCarpetaByExpedienteAndEstado(String expediente,
                        EstadoCarpeta estado) {

                Persona auditor = personaService.getAuditor();
                if (auditor == null || auditor.getJuzgado() == null) {
                        throw new IllegalArgumentException("No se puede determinar el juzgado.");
                }
                Integer juzgadoId = auditor.getJuzgado().getId();

                Carpeta carpeta = carpetaRepository
                                .findByExpedienteAndJuzgadoIdAndEstatus(expediente, juzgadoId, estado)
                                .orElseThrow(() -> new NotFoundException("Carpeta no encontrada",
                                                expediente + " - " + juzgadoId));

                String actor = getNombrePersonaByIdAndParte(carpeta.getId(), ACTOR_LABEL);
                String demandado = getNombrePersonaByIdAndParte(carpeta.getId(), DEMANDADO_LABEL);
                String tipoJuicio = carpeta.getTipoJuicio().getNombre();
                return new CarpetaResponseRecord(carpeta.getId(), actor, demandado, tipoJuicio, null, null,
                                carpeta.getJuzgado().getEstado(), carpeta.getEstatus().getEtiqueta());
        }

        public void devolverArchivoJudicial(List<Integer> ids) {
                for (Integer id : ids) {
                        Movimiento ultimoMovimiento = movimientoRepository
                                        .findTopByCarpetaIdOrderByFechaAsignacionDesc(id);

                        if (ultimoMovimiento != null) {
                                EstadoCarpeta estado = EstadoCarpeta.valueOf(ultimoMovimiento.getEstado());

                                Optional<Carpeta> carpetaOptional = carpetaRepository.findById(id);
                                if (carpetaOptional.isPresent()) {
                                        Carpeta carpeta = carpetaOptional.get();
                                        carpeta.setEstatus(estado);
                                        carpetaRepository.save(carpeta);
                                } else {
                                        throw new RuntimeException("Carpeta no encontrada con ID: " + id);
                                }
                        } else {
                                throw new RuntimeException("No se encontró movimiento para la carpeta con ID: " + id);
                        }
                }
        }

        public Optional<Carpeta> getExpediente(String expediente, Juzgado juzgado) {
                return carpetaRepository.findByExpedienteAndJuzgado(expediente, juzgado);
        }

        public Carpeta save(Carpeta carpeta) {
                return carpetaRepository.save(carpeta);
        }

        public void saveAll(List<Carpeta> carpetas) {
                carpetaRepository.saveAll(carpetas);
        }

        public List<Carpeta> findPiezasByCarpeta(Carpeta carpeta) {
                return carpetaRepository.findByCarpetaPadre(carpeta);
        }

        public Carpeta findByExpedienteAndJuzgado(String expediente, Juzgado juzgado) {
                return carpetaRepository.findByExpedienteAndJuzgado(expediente, juzgado).orElse(null);

        }
}
