package mx.gob.pjpuebla.trials.workflow.carpeta;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.conceptos.Concepto;
import mx.gob.pjpuebla.trials.core.conceptos.ConceptoRepository;
import mx.gob.pjpuebla.trials.core.etapaprocesal.EtapaProcesal;
import mx.gob.pjpuebla.trials.core.etapaprocesal.EtapaProcesalRepository;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.oficialias.OficialiaRepository;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.procedimientos.Procedimiento;
import mx.gob.pjpuebla.trials.core.rubros.Rubro;
import mx.gob.pjpuebla.trials.core.tipopieza.TipoPieza;
import mx.gob.pjpuebla.trials.core.tipopieza.TipoPiezaRepository;
import mx.gob.pjpuebla.trials.core.rubros.RubroRecord;
import mx.gob.pjpuebla.trials.core.rubros.RubroRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.error.ConflictException;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.enums.*;
import mx.gob.pjpuebla.trials.util.enums.carpeta.*;
import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoBandejaRecepcionRecord;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
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
import mx.gob.pjpuebla.trials.workflow.movimientos.Movimiento;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoRepository;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoService;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
        private final OficialiaRepository oficialiaRepository;
        private final ConceptoRepository conceptoRepository;

        private static final String ACTOR_LABEL = "Actor";
        private static final String DEMANDADO_LABEL = "Demandado";
        private static final String VICTIMA_LABEL = "Victimas";
        private static final String IMPUTADO_LABEL = "Imputados";
        private static final String DOC_NOT_FOUND = "Documento no encontrado";
        private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";

        public CarpetaResponseRecord getCarpetaResponseByNumExpYearJuzgado(String expediente, Integer juzgadoId) {
                // Usar una variable auxiliar para la modificación de juzgadoId
                final Integer finalJuzgadoId;

                // Validar juzgadoId
                if (juzgadoId == null) {
                        Persona auditor = personaService.getAuditor();
                        if (auditor == null || auditor.getJuzgado() == null) {
                                throw new IllegalArgumentException("No se puede determinar el juzgado.");
                        }
                        finalJuzgadoId = auditor.getJuzgado().getId();
                } else {
                        finalJuzgadoId = juzgadoId;
                }

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

        protected String getNombrePersonaByIdAndParte(Integer id, String parte) {
                List<Rol> rol = List.of(Rol.PRINCIPAL);
                PersonaDocumentoRecord persona = personaDocumentoRepository.findPersonaAndTipoParteByCarpetaId(id,
                                parte, rol);

                if (persona == null) {
                        return "";
                }

                String nombre = persona.nombre() != null ? persona.nombre() : "";
                String apellidoPaterno = persona.apellidoPaterno() != null ? persona.apellidoPaterno() : "";
                String apellidoMaterno = persona.apellidoMaterno() != null ? persona.apellidoMaterno() : "";

                return String.format("%s %s %s", nombre, apellidoPaterno, apellidoMaterno).trim();
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

                // Obtiene el nombre del juez TODO.obtener nombre del juez

                DateTimeFormatter pattern = DateTimeFormatter.ofPattern(DATE_FORMAT);

                EtapaProcesalRecord etapaProcesalRecord = null;

                Optional<CarpetaEtapas> optionalCarpetaEtapas = carpetaEtapasRepository
                                .findByCarpetaId(carpeta.getId());
                if (optionalCarpetaEtapas.isPresent()) {
                        CarpetaEtapas carpetaEtapas = optionalCarpetaEtapas.get();
                        etapaProcesalRecord = new EtapaProcesalRecord(carpetaEtapas.getEtapaProcesal().getId(),
                                        carpetaEtapas.getEtapaProcesal().getNombre());
                }

                return new InfoExpedienteRecord(
                                carpeta.getExpediente(),
                                carpeta.getTipoJuicio().getNombre(), // TODO mapear de forma correcta expediente tipo
                                                                     // PENAL
                                carpeta.getTipoJuicio().getId(), // TODO mapear de forma correcta expediente tipo PENAL
                                "", // extraAudienciaSelloRecord.nombreJuez(),
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
                                             .orElseThrow(() -> new IllegalStateException("El concepto 'Nueva creación' no se encontró en la base de datos"))
                                     );

                Carpeta carpetaPadre = carpetaRepository.findById(carpetaId)
                                .orElseThrow(() -> new NotFoundException("La Carpeta no existe", "carpetaId"));
                TipoPieza tipoPieza = tipoPiezaRepository
                                .findByIdOrClave(piezaRecord.tipoPiezaId(), piezaRecord.clavePieza())
                                .stream().findFirst()
                                .orElseThrow(() -> new NotFoundException("El Tipo de Pieza no existe", "tipoPieza"));

                Persona persona = personaService.getAuditor();

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

                String nombre = carpeta.getPersona().getNombre() + " " +
                                (carpeta.getPersona().getApellidoPaterno() != null
                                                ? carpeta.getPersona().getApellidoPaterno() + " "
                                                : "")
                                +
                                (carpeta.getPersona().getApellidoMaterno() != null
                                                ? carpeta.getPersona().getApellidoMaterno()
                                                : "")
                                + ", ";

                String rol = (carpeta.getPersona().getOcupacion() != null ? carpeta.getPersona().getOcupacion() + ", "
                                : "");

                String ubicacion = carpeta.getPersona().getJuzgado() != null
                                ? carpeta.getPersona().getJuzgado().getNombre()
                                : carpeta.getPersona().getOficialia().getNombre();

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
                                carpetaDetalle.getCujus());
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
                                                                ? (LocalDateTime.parse(detalle.fechaRegistro(),
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
                                .setCujus(detalle.cujus());

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
                        doc.setCarpeta(pieza.getCarpetaPadre());
                        doc.setData(doc.getData().setEstadoPieza(estadoPieza));

                        documentoRepository.save(doc);
                }

                pieza.setEstatus(estadoPieza);
                movimientoService.createMovimento(pieza, null, persona, null, estadoPieza.name());

                pieza = carpetaRepository.save(pieza);

                return new PiezaRecordResponse(pieza.getId(), pieza.getExpediente(), pieza.getTipoPieza().getTipo(),
                                pieza.getEstatus());
        }

        public Page<LibroGobiernoRecord> libroDeGobierno(String key, Pageable pageable) {
                key = (key != null) ? key.toLowerCase() : "";
                Persona persona = personaService.getAuditor();

                Page<Carpeta> carpetas;

                if (persona.getJuzgado() != null) {
                        List<Juzgado> juzgado = Collections.singletonList(persona.getJuzgado());
                        carpetas = carpetaRepository.findByJuzgado(juzgado, key, pageable);
                } else {
                        Integer oficialiaId = persona.getOficialia().getId();
                        List<Estado> estados = List.of(Estado.ACTIVE);
                        List<Juzgado> juzgados = juzgadoRepository.findByOficialiaIdAndEstadoIn(oficialiaId, estados);

                        carpetas = carpetaRepository.findByJuzgado(juzgados, key, pageable);
                }

                return carpetas.map(carpeta -> {
                        String actor = getNombrePersonaByIdAndParte(carpeta.getId(), ACTOR_LABEL);
                        String demandado = getNombrePersonaByIdAndParte(carpeta.getId(), DEMANDADO_LABEL);
                        CarpetaDetalle carpetaDetalle = carpetaDetalleRepository.findByCarpetaId(carpeta.getId());

                        return new LibroGobiernoRecord(
                                        carpeta.getId(),
                                        carpeta.getExpediente(),
                                        (carpeta.getAudit() != null && carpeta.getAudit().getFechaAlta() != null)
                                                        ? carpeta.getAudit().getFechaAlta()
                                                        : null,
                                        carpeta.getTipoJuicio() != null ? carpeta.getTipoJuicio().getNombre()
                                                        : "Sin Tipo de Juicio",
                                        actor,
                                        demandado,
                                        carpeta.getPersona().equals(persona)
                                                        && carpeta.getEstatus() == EstadoCarpeta.ASIGNADO,
                                        carpetaDetalle != null ? Objects.toString(carpetaDetalle.getCujus(), "") : "");
                });
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

}
