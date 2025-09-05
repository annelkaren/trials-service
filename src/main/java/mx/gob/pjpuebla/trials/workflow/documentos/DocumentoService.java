package mx.gob.pjpuebla.trials.workflow.documentos;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.migracion.readers.acuerdos.AcuerdosMigracionSaveRecord;
import mx.gob.pjpuebla.migracion.readers.acuerdos.SentenciaMigracionSaveRecord;
import mx.gob.pjpuebla.migracion.readers.detallesProm.DetallePromSaveRecord;
import mx.gob.pjpuebla.trials.core.conceptos.Concepto;
import mx.gob.pjpuebla.trials.core.conceptos.ConceptoRepository;
import mx.gob.pjpuebla.trials.core.configuraciones.Configuraciones;
import mx.gob.pjpuebla.trials.core.configuraciones.ConfiguracionesRepository;
import mx.gob.pjpuebla.trials.core.eventos.EventoService;
import mx.gob.pjpuebla.trials.core.instituciones.Institucion;
import mx.gob.pjpuebla.trials.core.instituciones.InstitucionRepository;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoService;
import mx.gob.pjpuebla.trials.core.oficialias.Oficialia;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaRepository;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.roles.RoleService;
import mx.gob.pjpuebla.trials.core.rubros.Rubro;
import mx.gob.pjpuebla.trials.core.salas.SalaAudienciaRecord;
import mx.gob.pjpuebla.trials.core.salas.SalaService;
import mx.gob.pjpuebla.trials.core.tipoaudiencia.TipoAudiencia;
import mx.gob.pjpuebla.trials.core.tipoaudiencia.TipoAudienciaRepository;
import mx.gob.pjpuebla.trials.core.tipoaudiencia.TipoAudienciaService;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioDemandasRecord;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;
import mx.gob.pjpuebla.trials.error.ConflictException;
import mx.gob.pjpuebla.trials.error.ConstraintViolationException;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.EmailService;
import mx.gob.pjpuebla.trials.util.Messages;
import mx.gob.pjpuebla.trials.util.enums.*;
import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRecepcionRecord;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.workflow.audiencias.AudienciaRepository;
import mx.gob.pjpuebla.trials.workflow.audiencias.AudienciaService;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.carpetadetalle.CarpetaDetalle;
import mx.gob.pjpuebla.trials.workflow.carpeta.carpetadetalle.CarpetaDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaService;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.ApelacionPersonaRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.ApelacionRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.CarpetaCatalogoRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.CarpetaResponseRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.PiezaRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.amparos.AmparoGetRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.amparos.AmparoRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.amparos.AmparoRecordResponse;
import mx.gob.pjpuebla.trials.workflow.documentos.amparos.AmparoUpdateRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenido;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenidoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalle;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.records.*;
import mx.gob.pjpuebla.trials.workflow.etiquetas.EtiquetaService;
import mx.gob.pjpuebla.trials.workflow.folios.DocumentoFoliosService;
import mx.gob.pjpuebla.trials.workflow.folios.JuzgadoFolios;
import mx.gob.pjpuebla.trials.workflow.movimientos.Movimiento;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoRepository;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoService;
import mx.gob.pjpuebla.trials.workflow.personadetalle.PersonaDetalle;
import mx.gob.pjpuebla.trials.workflow.personadetalle.PersonaDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoItemRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.sello.SelloGenerator;
import mx.gob.pjpuebla.trials.workflow.solicitudesProrrogas.SolicitudesProrrogas;
import mx.gob.pjpuebla.trials.workflow.solicitudesProrrogas.SolicitudesProrrogasService;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Slf4j
@Service
public class DocumentoService {

        @Value("${spring.mail.correoDefensoria}")
        private String correoDefensoria;

        public static final String ACTOR = "Actor";
        public static final String DEMANDADO = "Demandado";
        public static final String IS_INTERNO = "isInterno";
        public static final String VICTIMA = "Victimas";
        public static final String IMPUTADO = "Imputados";
        public static final String MINISTERIO = "Ministerio Publico";
        public static final String TERCERINVOLUCRADO = "Tercero Involucrado";
        public static final String PROMOVENTE = "Promovente";

        private final DocumentoRepository documentoRepository;
        private final JuzgadoService juzgadoService;
        private final TipoJuicioRepository tipoJuicioRepository;
        private final AnexoRepository anexoRepository;
        private final PersonaDocumentoRepository personaDocumentoRepository;
        private final TipoPartesRepository tipoPartesRepository;
        private final CarpetaRepository carpetaRepository;
        private final CarpetaService carpetaService;
        private final TipoAudienciaService tipoAudienciaService;
        private final SalaService salaService;
        private final AudienciaService audienciaService;
        private final MovimientoService movimientoService;
        private final DigitalizacionService digitalizacionService;
        private final MovimientoRepository movimientoRepository;
        private final PersonaService personaService;
        private final PersonaRepository personaRepository;
        private final EtiquetaService etiquetaService;
        private final RoleService roleService;
        private final DocumentoFoliosService documentoFoliosService;
        private final InstitucionRepository institucionRepository;
        private final ConceptoRepository conceptoRepository;
        private final EmailService emailService;
        private final SelloGenerator selloGenerator;
        private final DocumentoDetalleRepository documentoDetalleRepository;
        private final AudienciaRepository audienciaRepository;
        private final CarpetaDetalleRepository carpetaDetalleRepository;
        private final PersonaDetalleRepository personaDetalleRepository;
        private final JuzgadoRepository juzgadoRepository;
        private final TipoAudienciaRepository tipoAudienciaRepository;
        private final DocumentoContenidoRepository documentoContenidoRepository;
        private final EventoService eventosService;
        private final SolicitudesProrrogasService solicitudesProrrogasService;
        private final ConfiguracionesRepository configuracionesRepository;

        private static final String DOC_NOT_FOUND = "Documento no encontrado";
        private static final String DOC_ID = "documentoId: ";
        private static final String TIPO_JUICIO_NOT_FOUND = "Tipo Juicio no encontrado: ";
        private static final String CARPETA_NOT_FOUND = "Carpeta no encontrada";
        private static final String CONCEPTO_NOT_FOUND = "Concepto no encontrado";

        private Persona personaAsignada = null;

        @Transactional(readOnly = true)
        public Page<DocumentoGridRecord> getAll(String key, Pageable pageable, String tipoEntradaFilter) {
                key = (key != null) ? key.toLowerCase() : "";
                Object[] resultado = procesarTipoCarpeta(key);
                TipoCarpeta tipoCarpetaNombre = (TipoCarpeta) resultado[0];
                TipoDocumento tipoDocumentoNombre = (TipoDocumento) resultado[1];
                Integer folioTemp = (Integer) resultado[2];

                Persona currentUser = personaService.getAuditor();
                Integer juzgadoId = getJuzgadoId(currentUser);
                Integer oficialiaId = getOficialiaId(currentUser);

                TipoDocumento tipoEntradaDoc = null;
                TipoCarpeta tipoEntradaCarp = null;

                try {
                        tipoEntradaDoc = TipoDocumento.valueOf(tipoEntradaFilter.toUpperCase());
                } catch (Exception e) {
                        try {
                                tipoEntradaCarp = TipoCarpeta.valueOf(tipoEntradaFilter.toUpperCase());
                        } catch (Exception ignored) {
                        }
                }

                Page<Movimiento> page = movimientoService.getAllBandejaEntrada(
                                pageable,
                                juzgadoId,
                                oficialiaId,
                                key,
                                tipoCarpetaNombre,
                                tipoDocumentoNombre,
                                folioTemp,
                                tipoEntradaDoc,
                                tipoEntradaCarp);

                List<DocumentoGridRecord> list = page.getContent()
                                .stream()
                                .map(movimiento -> {
                                        Carpeta carpeta = movimiento.getCarpeta();
                                        Documento documento = movimiento.getDocumento();

                                        String folio;
                                        String estaEnJuzgado = estadoExpedienteBandejaHistorial(movimiento.getEstado());
                                        String materia;
                                        String tipoEntrada;

                                        if (carpeta == null) {
                                                carpeta = documento.getCarpeta();
                                        }

                                        if (documento == null) {
                                                documento = getDocumentoWhenIsNull(carpeta);
                                        }

                                        TipoDocumento tipoDocumento = documento.getTipoDocumento();
                                        folio = getFolioBandejas(carpeta, documento);
                                        materia = getMateriaExpediente(carpeta);
                                        tipoEntrada = getTipoEntrada(carpeta, documento);

                                        EstadoCarpeta estadoCarpeta = (tipoDocumento != null) ? documento.getEstatus()
                                                        : carpeta.getEstatus();

                                        return new DocumentoGridRecord(
                                                        documento.getId(),
                                                        folio,
                                                        carpeta.getExpediente(),
                                                        materia,
                                                        tipoEntrada,
                                                        documento.getAudit().getFechaAlta(),
                                                        carpeta.getSelloEstatus(),
                                                        estadoCarpeta,
                                                        documento.getRuta() != null,
                                                        documento.getCarpeta().getJuzgado().getNombre(),
                                                        estaEnJuzgado,
                                                        movimiento.getMotivo());
                                }).toList();

                return new PageImpl<>(list, pageable, list.size());
        }

        @Transactional(readOnly = true)
        public Page<DocumentoSalidaResponseRecord> getAllBandejaSalida(String key, Pageable pageable) {
                key = (key != null) ? key.toLowerCase() : "";
                Object[] resultado = procesarTipoCarpeta(key);
                TipoCarpeta tipoCarpetaNombre = (TipoCarpeta) resultado[0];
                TipoDocumento tipoDocumentoNombre = (TipoDocumento) resultado[1];
                Integer folio = (Integer) resultado[2];

                Persona persona = personaService.getAuditor();
                Page<DocumentoSalidaRecord> page = documentoRepository.findByEstatusSalida(
                                key,
                                (persona.getOficialia() != null) ? persona.getOficialia().getId() : null,
                                (persona.getJuzgado() != null) ? persona.getJuzgado().getId() : null,
                                folio,
                                tipoCarpetaNombre,
                                tipoDocumentoNombre,
                                pageable);
                List<DocumentoSalidaResponseRecord> list = page.getContent().stream()
                                .map(item -> new DocumentoSalidaResponseRecord(
                                                item.movid(),
                                                item.id(),
                                                item.folio(),
                                                item.expediente(),
                                                item.juzgadoId(),
                                                item.juzgado(),
                                                item.materia(),
                                                ((item.tipoCarpeta() != null) ? item.tipoCarpeta().name()
                                                                : ((item.tipoDocumento() != null)
                                                                                ? item.tipoDocumento().name()
                                                                                : null)),
                                                item.fechaRegistro(),
                                                item.selloEstatus(),
                                                item.estatus()))
                                .toList();
                return new PageImpl<>(list, pageable, page.getTotalElements());
        }

        public DocumentoRecord updateStatus(Integer id, Integer status) {

                Documento documento = documentoRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException(DOC_NOT_FOUND, DOC_ID + id));

                EstadoCarpeta value = EstadoCarpeta.values()[status];

                TipoDocumento tipoDocumento = documento.getTipoDocumento();
                boolean isDocumento = false;

                if (tipoDocumento == null || tipoDocumento.equals(TipoDocumento.EXHORTO)
                                || tipoDocumento.equals(TipoDocumento.APELACION)) {
                        documento.getCarpeta().setEstatus(value);
                        carpetaRepository.save(documento.getCarpeta());
                } else {
                        documento.setEstatus(value);
                        documentoRepository.save(documento);
                        isDocumento = true;
                }

                boolean isApelacion = Objects.equals(documento.getTipoDocumento(), TipoDocumento.APELACION);

                movimientoService.createMovimento(
                                isDocumento && !isApelacion ? null : documento.getCarpeta(),
                                isDocumento && !isApelacion ? documento : null,
                                personaService.getAuditor(),
                                null,
                                EstadoCarpeta.values()[status].name());
                return new DocumentoRecord(documento.getId(), documento.getCarpeta().getFolio(),
                                documento.getCarpeta().getTipoCarpeta());
        }

        public DocumentoRecord createDemanda(DocumentoSaveRecord documentoRecord) {
                Persona persona = personaService.getAuditor();
                Oficialia oficialia = persona.getOficialia();

                validarOficialia(oficialia); // Lanza una excepción si la oficialia es null

                // Busca el tipoJuicio por id, si no lo encuentra lanza una excepción.
                TipoJuicio tipoJuicio = getTipoJuicioById(documentoRecord.tipoJuicioId());

                // Obtiene los juzgados relacionados con la oficialia.
                List<Juzgado> juzgadosRelacionados = getJuzgadosRelacionados(oficialia.getId(), tipoJuicio);

                // Obtiene conexidad de juzgados
                Juzgado juzgadoConexidad = juzgadoService.getConexidadJuzgado(documentoRecord.actor(),
                                documentoRecord.demandado(), tipoJuicio);

                // Evalua el juzgado que se le asignara a la carpeta dada conexidad o juzgados
                // relacionados.
                Juzgado juzgadoDemanda = getJuzgadoDemanda(juzgadoConexidad, juzgadosRelacionados, tipoJuicio);

                // Llama al metodo crear carpeta para la creacion de una carpeta dinamica.
                Carpeta carpeta = crearCarpeta(tipoJuicio, juzgadoDemanda, getFolio("D"), TipoCarpeta.DEMANDA,
                                generateNumExpediente(juzgadoDemanda, TipoCarpeta.DEMANDA), persona);

                // Llama al metodo crear documento para la creación de un documento dinamico.
                Documento documento = crearDocumento(carpeta, documentoRecord.general(), persona);

                // Llama metodo para crear anexos.
                addAnexos(documentoRecord.anexos(), documento);

                // Logica cuando es demanda de tipo oralidad familiar.
                handleOralidadFamiliarFlow(tipoJuicio, documentoRecord, carpeta, documento);

                // Crea personas parte de una carpeta
                createPersonaDocumento(documentoRecord.actor(), carpeta);
                createPersonaDocumento(documentoRecord.demandado(), carpeta);

                // Crea una carpeta detalle.
                carpetaDetalleRepository.save(new CarpetaDetalle().setCarpeta(carpeta));

                // Crea Movimiento.
                movimientoService.createMovimento(carpeta, null, persona, null, EstadoCarpeta.CAPTURA.name());

                // Actualiza carga de juzgados.
                juzgadoService.actualizarCarga(carpeta.getJuzgado(), carpeta.getTipoCarpeta(), juzgadosRelacionados);

                return new DocumentoRecord(documento.getId(), carpeta.getFolio(),
                                documento.getCarpeta().getTipoCarpeta());
        }

        public DocumentoGenericRecord createDemandaPenal(DocumentoCreateDemandaPenalRecord demanda) {
                Persona persona = personaService.getAuditor();
                Oficialia oficialia = persona.getOficialia();
                validarOficialia(oficialia); // Lanza una excepción si la oficialia es null

                // Busca el tipoJuicio por id, si no lo encuentra lanza una excepción.
                TipoJuicio tipoJuicioPadre = getTipoJuicioById(demanda.tipoJuicioPadre());
                TipoJuicio tipoJuicio = getTipoJuicioById(demanda.tipoJuicio());

                // Obtiene los juzgados relacionados con la oficialia.
                List<Juzgado> juzgadosRelacionados = getJuzgadosRelacionados(oficialia.getId(), tipoJuicio);

                Juzgado juzgadoPorJuicio = juzgadoService.getJuzgado(tipoJuicioPadre, TipoCarpeta.DEMANDA,
                                juzgadosRelacionados);

                String expediente = tipoJuicio.getTipoCausa() != null
                                ? generateNumExpedientePenal(tipoJuicio.getTipoCausa(), juzgadoPorJuicio,
                                                TipoCarpeta.DEMANDA)
                                : null;

                // Definición de carpeta.
                Carpeta carpeta = crearCarpeta(tipoJuicio, juzgadoPorJuicio, getFolio("D"), TipoCarpeta.DEMANDA,
                                expediente, persona);

                // Seteamos todos los datos exclusivos de penal:
                DocumentoData data = new DocumentoData()
                                .setNumOficio(demanda.numOficio())
                                .setNumCarpetaInv(demanda.numCarpetaInv())
                                .setLugarHecho(demanda.lugarHecho())
                                .setFechaHecho(demanda.fechaHecho())
                                .setFechaPresentacion(demanda.fechaPresentacion())
                                .setHoraFormal(demanda.horaFormal())
                                .setHoraMaterial(demanda.horaMaterial())
                                .setLugarDisposicion(demanda.lugarDisposicion())
                                .setTipoSolAudiencia(demanda.tipoSolAudiencia());

                // Creación del documento.
                Documento documento = crearDocumento(carpeta, data, persona);

                // Creación de participantes :promovente, victima, imputado, ministerio y
                // tercero involucrado:
                PersonaDocumentoItemRecord promovente = new PersonaDocumentoItemRecord(
                                demanda.nombreProvente(),
                                demanda.apellidoPaternoProvente(),
                                demanda.apellidoMaternoProvente(),
                                "",
                                "",
                                3,
                                "",
                                "",
                                "",
                                "",
                                "");

                createPersonaDocumento(promovente, carpeta); // Creación del Promovente

                // Creación de los demas participantes.
                final Carpeta carpetaFinal = carpeta; // carpeta final para usar en lamda
                List<List<PersonaDocumentoItemRecord>> participantes = List.of(
                                demanda.victimas(),
                                demanda.imputados(),
                                demanda.ministerio(),
                                demanda.terceroInvolucrado());

                participantes.stream()
                                .flatMap(List::stream)
                                .forEach(participante -> createPersonaDocumento(participante, carpetaFinal));

                // Creación de audiencia:
                crearAudienciaPenal(demanda, carpeta);

                // Creación de anexos:
                addAnexos(demanda.anexos(), documento);

                // Creación del movimiento:
                movimientoService.createMovimento(carpeta, null, persona, null, EstadoCarpeta.CAPTURA.name());
                setAndSaveCarpetaDetalle(documento, carpeta, promovente, tipoJuicio);
                return new DocumentoGenericRecord(documento.getId(), null);
        }

        private void setAndSaveCarpetaDetalle(Documento documento, Carpeta carpeta,
                        PersonaDocumentoItemRecord promovente,
                        TipoJuicio tipoJuicio) {
                CarpetaDetalle detalle = new CarpetaDetalle();
                Optional<TipoJuicio> tipoJuicioOptional = tipoJuicioRepository
                                .findByNombreIgnoreCaseAndTipoJuicioPadreOralIsNotNull(tipoJuicio.getNombre());
                TipoJuicio tipoJuicioAux = null;
                if (tipoJuicioOptional.isPresent()) {
                        tipoJuicioAux = tipoJuicioOptional.get();
                }
                detalle.setTipoJuicio(tipoJuicioAux);
                // setear promovente
                String namePromovente = (promovente.pseudonimo() != null && !promovente.pseudonimo().isEmpty())
                                ? promovente.pseudonimo()
                                : promovente.nombre() + " " + promovente.apellidoPaterno() +
                                                ((promovente.apellidoMaterno() != null)
                                                                ? " " + promovente.apellidoMaterno()
                                                                : "");
                // detalle.setTipoJuicio(documento.getData().getTiposJuicios());
                detalle.setPromovente(namePromovente);
                detalle.setNumeroCarpetaInvestigacion(documento.getData().getNumCarpetaInv());
                detalle.setNumeroOficio(documento.getData().getNumOficio().toString());
                detalle.setLugarHecho(documento.getData().getLugarHecho());
                detalle.setFechaHecho(documento.getData().getFechaHecho());
                detalle.setHoraFormal(documento.getData().getHoraFormal());
                detalle.setHoraMaterial(documento.getData().getHoraMaterial());
                detalle.setLugarDisposicion(documento.getData().getLugarDisposicion());
                detalle.setFechaPresentacionImputado(documento.getData().getFechaPresentacion());
                detalle.setCarpeta(carpeta);
                carpetaDetalleRepository.save(detalle);
        }

        private void crearAudienciaOralidad(DocumentoSaveRecord documentoRecord, Carpeta carpeta,
                        TipoJuicio tpoJuicio) {
                TipoAudiencia tipoAudiencia = tipoAudienciaService.obtenerTipoAudiencia("Audiencia Inicial");
                PersonaDocumentoRecord actor = new PersonaDocumentoRecord(
                                documentoRecord.actor().nombre(),
                                documentoRecord.actor().apellidoPaterno(),
                                documentoRecord.actor().apellidoMaterno(),
                                documentoRecord.actor().pseudonimo(),
                                documentoRecord.actor().tipoPersona(),
                                "",
                                "",
                                "",
                                "",
                                ACTOR,
                                documentoRecord.actor().tipoParte(),
                                carpeta.getId());

                PersonaDocumentoRecord demandado = new PersonaDocumentoRecord(
                                documentoRecord.demandado().nombre(),
                                documentoRecord.demandado().apellidoPaterno(),
                                documentoRecord.demandado().apellidoMaterno(),
                                documentoRecord.demandado().pseudonimo(),
                                documentoRecord.demandado().tipoPersona(),
                                "",
                                "",
                                "",
                                "",
                                DEMANDADO,
                                documentoRecord.demandado().tipoParte(),
                                carpeta.getId());

                SalaAudienciaRecord salaAudienciaConexidad = salaService.asignarSalaConexidad(actor, demandado,
                                tpoJuicio,
                                tipoAudiencia);
                if (salaAudienciaConexidad != null) {
                        audienciaService.create(salaAudienciaConexidad, tipoAudiencia, carpeta);
                } else {

                        SalaAudienciaRecord salaAudiencia = salaService.asignarSala(carpeta.getJuzgado(),
                                        tipoAudiencia);
                        audienciaService.create(salaAudiencia, tipoAudiencia, carpeta);
                }
        }

        private void crearAudienciaPenal(DocumentoCreateDemandaPenalRecord demanda, Carpeta carpeta) {
                TipoAudiencia tipoAudiencia = tipoAudienciaRepository.findById(demanda.tipoAudiencia())
                                .orElseThrow(() -> new NotFoundException("Tipo de audiencia no encontrada",
                                                demanda.tipoAudiencia().toString()));

                TipoJuicio tipoJuicio = getTipoJuicioById(demanda.tipoJuicioPadre()); // Busca el tipoJuicio por id, si
                                                                                      // no lo encuentra lanza una
                                                                                      // excepción.

                if (TipoJuzgadoPenal.ENJUICIAMIENTO.equals(demanda.tipoJuzgado())) {
                        // logica para asignar el juez que selecciono
                        Persona juez = personaRepository.findById((long) demanda.juezId())
                                        .orElseThrow(() -> new NotFoundException("Juez no encontrado", ""));

                        SalaAudienciaRecord salaAudiencia = salaService.asignarSala(juez.getJuzgado(), tipoAudiencia);

                        audienciaService.create(salaAudiencia, tipoAudiencia, carpeta);
                } else {
                        // Logica para asignar la sala de audiencia conforme al tipo de audiencia
                        // seccionado y al primer caso por conexidad, el segundo por carrucel.

                        SalaAudienciaRecord salaAudienciaConexidad = null;

                        for (PersonaDocumentoItemRecord victima : demanda.victimas()) {
                                for (PersonaDocumentoItemRecord imputado : demanda.imputados()) {
                                        PersonaDocumentoRecord victimaTemp = new PersonaDocumentoRecord(
                                                        victima.nombre(),
                                                        victima.apellidoPaterno(),
                                                        victima.apellidoMaterno(),
                                                        victima.pseudonimo(),
                                                        victima.tipoPersona(),
                                                        victima.curp(),
                                                        victima.domicilio(),
                                                        victima.celular(),
                                                        victima.correoElectronico(),
                                                        VICTIMA,
                                                        victima.tipoParte(),
                                                        carpeta.getId());
                                        PersonaDocumentoRecord imputadoTemp = new PersonaDocumentoRecord(
                                                        imputado.nombre(),
                                                        imputado.apellidoPaterno(),
                                                        imputado.apellidoMaterno(),
                                                        imputado.pseudonimo(),
                                                        imputado.tipoPersona(),
                                                        imputado.curp(),
                                                        imputado.domicilio(),
                                                        imputado.celular(),
                                                        imputado.correoElectronico(),
                                                        IMPUTADO,
                                                        imputado.tipoParte(),
                                                        carpeta.getId());

                                        salaAudienciaConexidad = salaService.asignarSalaConexidad(victimaTemp,
                                                        imputadoTemp, tipoJuicio,
                                                        tipoAudiencia);
                                }
                        }

                        if (salaAudienciaConexidad != null) {
                                audienciaService.create(salaAudienciaConexidad, tipoAudiencia, carpeta);
                        } else {
                                // Obtención de sala
                                SalaAudienciaRecord salaAudiencia = salaService.asignarSala(carpeta.getJuzgado(),
                                                tipoAudiencia);
                                audienciaService.create(salaAudiencia, tipoAudiencia, carpeta);
                        }

                }

        }

        private void createPersonaDocumento(PersonaDocumentoItemRecord persona, Carpeta carpeta) {

                String tipoParte = switch (persona.tipoParte()) {
                        case 1 -> ACTOR;
                        case 2 -> DEMANDADO;
                        case 3 -> PROMOVENTE;
                        case 4 -> VICTIMA;
                        case 5 -> IMPUTADO;
                        case 6 -> MINISTERIO;
                        case 7 -> TERCERINVOLUCRADO;
                        default ->
                                throw new IllegalArgumentException("Tipo de parte no valido: " + persona.tipoParte());
                };

                PersonaDocumento entity = new PersonaDocumento();
                entity.setNombre(persona.nombre());
                entity.setApellidoPaterno((persona.apellidoPaterno() == null) ? "" : persona.apellidoPaterno());
                entity.setApellidoMaterno((persona.apellidoMaterno() == null) ? "" : persona.apellidoMaterno());
                entity.setPseudonimo(persona.pseudonimo());
                entity.setTipoPersona(persona.tipoPersona());
                entity.setRol(Rol.PRINCIPAL);
                entity.setTipoPartes(
                                tipoPartesRepository
                                                .findByNombreAndTipoJuicioId(tipoParte, carpeta.getTipoJuicio().getId())
                                                .orElseThrow(() -> new NotFoundException("Tipo parte no encontrada",
                                                                "TipoParteId")));
                entity.setCarpeta(carpeta);

                // campos exlusivos para demanda de tipo familiar
                entity.setCurp(persona.curp());
                entity.setIne(persona.ine());
                entity.setDomicilio(persona.domicilio());
                entity.setCelular(persona.celular());
                entity.setCorreoElectronico(persona.correoElectronico());

                personaDocumentoRepository.save(entity);
        }

        public DocumentoRecord editarAnexos(Integer documentoId, List<String> nuevosAnexos, String motivoEdita,
                        String procedencia) {

                Documento documento = documentoRepository.findById(documentoId)
                                .orElseThrow(() -> new NotFoundException(DOC_NOT_FOUND, DOC_ID + documentoId));
                documento.getCarpeta().setSelloEstatus(SelloEstatus.NO_VALIDO);

                if (documento.getData() != null && procedencia != null
                                && documento.getData().getExhortoProcedencia() != null) {
                        documento.setData(documento.getData().setExhortoProcedencia(procedencia));
                }

                List<Anexo> anexosActuales = anexoRepository.findAllByDocumentoId(documentoId);
                anexosActuales.stream()
                                .filter(anexo -> !nuevosAnexos.contains(anexo.getNombre()))
                                .forEach(anexoRepository::delete);

                for (String anexo : nuevosAnexos) {
                        if (anexosActuales.stream()
                                        .noneMatch(existingAnexo -> existingAnexo.getNombre().equals(anexo))) {
                                Anexo nuevoAnexo = new Anexo();
                                nuevoAnexo.setNombre(anexo);
                                nuevoAnexo.setDocumento(documento);
                                anexoRepository.save(nuevoAnexo);
                        }
                }
                carpetaRepository.save(documento.getCarpeta());
                documentoRepository.save(documento);

                if (documento.getTipoDocumento() != null) {
                        movimientoService.createMovimento(null, documento, documento.getPersona(), motivoEdita,
                                        EstadoCarpeta.EDICION.name());
                } else {
                        movimientoService.createMovimento(documento.getCarpeta(), null, documento.getPersona(),
                                        motivoEdita,
                                        EstadoCarpeta.EDICION.name());
                }
                return new DocumentoRecord(documentoId, documento.getCarpeta().getFolio(),
                                documento.getCarpeta().getTipoCarpeta());
        }

        public DocumentoResponseRecord getDemandaById(Integer id) {

                Documento documento = documentoRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException(DOC_NOT_FOUND, id.toString()));
                List<PersonaDocumentoRecord> personas = personaDocumentoRepository
                                .findPersonasByCarpetaId(documento.getCarpeta().getId(), Rol.PRINCIPAL);
                List<String> anexos = anexoRepository.findNombresAnexosByDocumentoId(id);

                PersonaDocumentoRecord actor = null;
                PersonaDocumentoRecord demandado = null;

                for (PersonaDocumentoRecord persona : personas) {
                        if (ACTOR.equalsIgnoreCase(persona.tipoParte())) {
                                actor = persona;
                        } else if (DEMANDADO.equalsIgnoreCase(persona.tipoParte())) {
                                demandado = persona;
                        }
                }
                return new DocumentoResponseRecord(actor, demandado, anexos, documento.getData(),
                                documento.getCarpeta().getTipoJuicio().getNombre(),
                                documento.getCarpeta().getTipoJuicio().getId());
        }

        /**
         * Devuelve un numero de folio
         *
         * @param tipo E-exhorto, D-demanda, P-promocion, AP-Apelación.
         * @return string
         */
        public String getFolio(String tipo) {

                // Agregamos en la generación del folio el distintivo de la serie:
                Optional<Configuraciones> identificador = configuracionesRepository.findByPropiedad("SERIE");
                String identificadorFolio = identificador.isPresent() ? identificador.get().getValor() : "";

                String valNum = switch (tipo) {
                        case "E" -> // Case para exhorto
                                documentoRepository.getNextValExhorto() + identificadorFolio;
                        case "D" -> // Case para demanda
                                documentoRepository.getNextValDemanda() + identificadorFolio;
                        case "P" -> // Case para promocion
                                documentoRepository.getNextValPromocion() + identificadorFolio;
                        case "ES" -> // Case para exhorto salida
                                documentoRepository.getNextValExhortoSalida() + identificadorFolio;
                        case "AP" -> documentoRepository.getNextValApelacion() + identificadorFolio;
                        default -> throw new IllegalArgumentException("Tipo de documento no válido: " + tipo);
                };
                return valNum.toString();
        }

        public String generateNumExpediente(Juzgado juzgado, TipoCarpeta tipoCarpeta) {
                JuzgadoFolios juzgadoFolios = juzgadoService.getJuzgadoFolios(juzgado, tipoCarpeta);
                juzgadoFolios = juzgadoService.checkYearJuzgadoFolios(juzgadoFolios);
                String letraInicial = switch (tipoCarpeta) {
                        case EXHORTO -> "E";
                        case DESPACHO -> "D";
                        case APELACION_MUNICIPAL -> "T";
                        default -> "";
                };
                String numExpedienteExhorto = letraInicial
                                + StringUtils.leftPad(juzgadoFolios.getValue().toString(), 6, '0')
                                + "/" + juzgadoFolios.getYear();
                juzgadoService.increaseValueJuzgadoFolios(juzgadoFolios);
                return numExpedienteExhorto;
        }

        public Page<DocumentoGridRecord> getAllHistorial(String key, Pageable pageable) {

                // Obtenemos variables para la consulta al repository:
                Persona currentUser = personaService.getAuditor();
                Integer juzgadoId = getJuzgadoId(currentUser);
                Integer oficialiaId = getOficialiaId(currentUser);
                key = (key != null) ? key.toLowerCase() : "";
                Page<Movimiento> page = movimientoRepository.getAllBandejaHistorial(key, juzgadoId, oficialiaId,
                                pageable);

                // Mapeamos resultado de la connsulta
                List<DocumentoGridRecord> listaDocumentoRecords = page.getContent().stream()
                                .map(movimiento -> {

                                        Carpeta carpeta = movimiento.getCarpeta();
                                        Documento documento = movimiento.getDocumento();
                                        String folio;
                                        String estaEnJuzgado = estadoExpedienteBandejaHistorial(movimiento.getEstado());
                                        String materia;
                                        String tipoEntrada;

                                        if (carpeta == null) {
                                                carpeta = documento.getCarpeta();

                                        }

                                        if (documento == null) {
                                                documento = getDocumentoWhenIsNull(carpeta);
                                        }

                                        materia = getMateriaExpediente(carpeta);
                                        tipoEntrada = getTipoEntrada(carpeta, documento);
                                        folio = getFolioBandejas(carpeta, documento);

                                        return new DocumentoGridRecord(
                                                        documento.getId(),
                                                        folio,
                                                        carpeta.getExpediente(),
                                                        materia,
                                                        tipoEntrada,
                                                        movimiento.getFechaAsignacion(),
                                                        null,
                                                        EstadoCarpeta.valueOf(movimiento.getEstado()),
                                                        false,
                                                        "",
                                                        estaEnJuzgado,
                                                        movimiento.getMotivo());
                                }).toList();

                return new PageImpl<>(listaDocumentoRecords, pageable, page.getTotalElements());
        }

        private Documento getDocumentoWhenIsNull(Carpeta carpeta) {
                TipoDocumento tipoDocumento = switch (carpeta.getTipoCarpeta()) {
                        case DEMANDA -> null;
                        case EXHORTO -> TipoDocumento.EXHORTO;
                        case APELACION -> TipoDocumento.APELACION;
                        case PIEZA -> TipoDocumento.PROMOCION; // TODO: VALIDAR ESTE CASO SI ES CORRECTO O COMO
                                                               // TRATARLO.
                        default -> throw new IllegalArgumentException(
                                        "TipoCarpeta no reconocido: " + carpeta.getTipoCarpeta());
                };

                return documentoRepository.findByCarpetaIdAndTipoDocumento(carpeta.getId(), tipoDocumento);
        }

        private String estadoExpedienteBandejaHistorial(String estado) {
                return !(estado.equals("CAPTURA") ||
                                estado.equals("SALIDA") ||
                                estado.equals("DEVUELTO_A_OFICIALIA")) ? "En juzgado" : "";
        }

        private String getTipoEntrada(Carpeta carpeta, Documento documento) {
                return (documento.getTipoDocumento() != null)
                                ? StringUtils.capitalize(documento.getTipoDocumento().name().toLowerCase())
                                : StringUtils.capitalize(carpeta.getTipoCarpeta().name().toLowerCase());
        }

        private String getMateriaExpediente(Carpeta carpeta) {
                return StringUtils.capitalize(carpeta.getJuzgado().getMateria().getNombre().toLowerCase());
        }

        private String getFolioBandejas(Carpeta carpeta, Documento documento) {
                return carpeta != null ? carpeta.getFolio() : documento.getFolio();
        }

        private Integer getJuzgadoId(Persona currentUser) {
                return (currentUser.getJuzgado() != null) ? currentUser.getJuzgado().getId() : null;
        }

        private Integer getOficialiaId(Persona currentUser) {
                return (currentUser.getOficialia() != null) ? currentUser.getOficialia().getId() : null;
        }

        @Transactional
        public DocumentoPromocionResponseRecord createPromocion(DocumentoPromocionRecord documentoPromocionRecord,
                        MultipartFile multipartFile) {
                Carpeta carpeta = carpetaRepository.findById(documentoPromocionRecord.carpetaId())
                                .orElseThrow(() -> new NotFoundException(CARPETA_NOT_FOUND,
                                                String.valueOf(documentoPromocionRecord.carpetaId())));
                Documento documento = new Documento();
                Persona persona = personaService.getAuditor();
                documento.setCarpeta(carpeta);
                documento.setFolio(getFolio("P"));
                DocumentoData documentoData = new DocumentoData();
                documentoData.setTipoPromocion(documentoPromocionRecord.tipoPromocion());
                documento.setEstatus((documentoPromocionRecord.tipoPromocion().equals(TipoPromocion.CORREO_ELECTRONICO))
                                ? EstadoCarpeta.RECEPCION
                                : EstadoCarpeta.ASIGNADO);

                if (persona.getOficialia() != null) {
                        documento.setEstatus(EstadoCarpeta.CAPTURA);
                } else {
                        documento.setConcepto(conceptoRepository.findByNombre("Adjuntar")
                                        .orElseThrow(() -> new NotFoundException(CONCEPTO_NOT_FOUND, "Adjuntar")));
                        documento.setFechaAsignacion(LocalDateTime.now());
                }

                documento.setData(documentoData);
                documento.setPersona(persona);
                documento.setTipoDocumento(TipoDocumento.PROMOCION);

                documento = documentoRepository.save(documento);
                if (persona.getOficialia() == null
                                && !documentoPromocionRecord.tipoPromocion().equals(TipoPromocion.CORREO_ELECTRONICO)) {
                        digitalizacionService.guardarArchivo(multipartFile, documento.getId());
                }
                // promoción desde el portal del litigante
                if (documentoPromocionRecord.tipoPromocion().equals(TipoPromocion.CORREO_ELECTRONICO)) {
                        DocumentoContenido contenido = new DocumentoContenido();
                        contenido.setDocumento(documento);
                        contenido.setTexto(documentoPromocionRecord.contenido());
                        contenido.setTamanioPapel('c');
                        contenido.setOficioPublicado('n');
                        documentoContenidoRepository.save(contenido);
                }
                addAnexos(documentoPromocionRecord.anexos(), documento);
                if (documentoPromocionRecord.tipoPromocion().equals(TipoPromocion.CORREO_ELECTRONICO)) {
                        movimientoService.createMovimentoPromocionElectronica(documento, documento.getPersona(),
                                        EstadoCarpeta.RECEPCION.name(), documento.getConcepto());
                } else {
                        movimientoService.createMovimentoWithConcepto(null, documento, documento.getPersona(), null,
                                        documento.getEstatus().name(), documento.getConcepto());
                }
                return new DocumentoPromocionResponseRecord(documento.getId(), documento.getFolio(),
                                documento.getTipoDocumento());
        }

        @Transactional
        public DocumentoRecord createExhorto(DocumentoExhortoRecord documentoExhortoRecord) {
                Persona auditor = personaService.getAuditor();
                Oficialia oficialia = auditor.getOficialia();

                validarOficialia(oficialia); // Lanza una excepción si la oficialia es null

                List<Juzgado> juzgadosRelacionadosExhorto = juzgadoRepository
                                .findJuzgadoExhortoByOficialiaId(oficialia.getId());
                TipoJuicio tipoJuicio = tipoJuicioRepository.findByNombreIgnoreCase("EXHORTO")
                                .orElseThrow(() -> new NotFoundException(
                                                "Tipo de juicio no encontrado con nombre: Exhorto",
                                                "EXHORTO"));
                Juzgado juzgadoExhorto = juzgadoService.getJuzgado(tipoJuicio, TipoCarpeta.EXHORTO,
                                juzgadosRelacionadosExhorto);

                Carpeta carpeta = new Carpeta()
                                .setEstatus(EstadoCarpeta.CAPTURA)
                                .setFolio(getFolio("E"))
                                .setTipoCarpeta(TipoCarpeta.EXHORTO)
                                .setTipoJuicio(tipoJuicio)
                                .setJuzgado(juzgadoExhorto)
                                .setExpediente(generateNumExpediente(juzgadoExhorto, TipoCarpeta.EXHORTO))
                                .setSelloEstatus(SelloEstatus.VALIDO)
                                .setFechaAsignacion(LocalDateTime.now())
                                .setPersona(auditor);
                carpeta = carpetaRepository.save(carpeta);

                DocumentoData data = new DocumentoData()
                                .setExhortoObservaciones(documentoExhortoRecord.observaciones())
                                .setExhortoProcedencia(documentoExhortoRecord.procedencia());

                Documento documento = new Documento()
                                .setData(data)
                                .setCarpeta(carpeta)
                                .setPersona(auditor)
                                .setTipoDocumento(TipoDocumento.EXHORTO)
                                .setFechaAsignacion(LocalDateTime.now());
                documento = documentoRepository.save(documento);

                addAnexos(documentoExhortoRecord.anexos(), documento);
                juzgadoService.actualizarCarga(carpeta.getJuzgado(), carpeta.getTipoCarpeta(),
                                juzgadosRelacionadosExhorto);

                movimientoService.createMovimento(carpeta, null, auditor, null, EstadoCarpeta.CAPTURA.name());

                carpetaDetalleRepository.save(new CarpetaDetalle().setCarpeta(carpeta));

                return new DocumentoRecord(documento.getId(), carpeta.getFolio(),
                                documento.getCarpeta().getTipoCarpeta());
        }

        private void addAnexos(List<String> anexos, Documento documento) {
                if (anexos != null && !anexos.isEmpty()) {
                        for (String anexo : anexos) {
                                Anexo entity = new Anexo();
                                entity.setNombre(anexo);
                                entity.setDocumento(documento);
                                anexoRepository.save(entity);
                        }
                }
        }

        public DocumentoRecord createApelacion(ApelacionRecord apelacionRecord) {
                Persona auditor = personaService.getAuditor();

                Oficialia oficialia = auditor.getOficialia();
                validarOficialia(oficialia); // Lanza una excepción si la oficialia es null

                Documento documento = new Documento();
                Carpeta carpeta = new Carpeta();

                Carpeta carpetaParent = carpetaRepository.findById(apelacionRecord.carpetaId())
                                .orElseThrow(() -> new NotFoundException(CARPETA_NOT_FOUND,
                                                "carpetaId: " + apelacionRecord.carpetaId()));

                // Busca el tipoJuicio por id, si no lo encuentra lanza una excepción.
                TipoJuicio tipoJuicio = getTipoJuicioById(carpetaParent.getTipoJuicio().getId());

                List<Juzgado> juzgadosRelacionados = juzgadoRepository.findSalaByOficialiaId(oficialia.getId())
                                .stream().filter(j -> j.getTipoJuicios().contains(tipoJuicio)).toList();

                carpeta.setTipoJuicio(tipoJuicio);
                carpeta.setTipoCarpeta(TipoCarpeta.APELACION);
                carpeta.setJuzgado(juzgadoService.getJuzgado(carpeta.getTipoJuicio(), carpeta.getTipoCarpeta(),
                                juzgadosRelacionados));
                carpeta.setFolio(getFolio("AP"));
                carpeta.setExpediente(generateNumExpediente(carpeta.getJuzgado(), TipoCarpeta.APELACION));
                carpeta.setEstatus(EstadoCarpeta.CAPTURA);
                carpeta.setSelloEstatus(SelloEstatus.VALIDO);
                carpeta.setPersona(auditor);
                carpeta.setFechaAsignacion(LocalDateTime.now());
                carpeta = carpetaRepository.save(carpeta);

                documento.setCarpeta(carpeta);
                DocumentoData data = new DocumentoData();
                data.setApelacionOtroActorNombre(apelacionRecord.otroNombreActor());
                data.setApelacionOtroDemandadoNombre(apelacionRecord.otroNombreDemandado());
                data.setApelacionAntecedenteCarpeta(apelacionRecord.carpetaId().toString());

                documento.setData(data);
                documento.setPersona(auditor);
                documento.setFechaAsignacion(LocalDateTime.now());
                documento.setTipoDocumento(TipoDocumento.APELACION);

                // Obtiene folio
                Integer folio = documentoFoliosService.getFolio(TipoDocumento.ACUERDO, auditor.getJuzgado(),
                                auditor.getOficialia());
                documento.setFolio(folio.toString());

                documento = documentoRepository.save(documento);

                for (Anexo anexo : apelacionRecord.anexos()) {
                        Anexo entity = new Anexo();
                        entity.setNombre(anexo.getNombre());
                        entity.setDocumento(documento);
                        anexoRepository.save(entity);
                }

                for (ApelacionPersonaRecord persona : apelacionRecord.apelacionPersonaRecords()) {
                        PersonaDocumento entity = new PersonaDocumento();
                        entity.setNombre(persona.nombre());
                        entity.setApellidoPaterno(persona.apellidoPaterno());
                        entity.setApellidoMaterno(persona.apellidoMaterno());
                        entity.setPseudonimo(persona.pseudonimo());
                        entity.setTipoPersona(persona.tipoPersona());
                        entity.setRol(Rol.SECUNDARIO);
                        entity.setTipoPartes(tipoPartesRepository.findById(persona.tipoPartes())
                                        .orElseThrow(() -> new NotFoundException("Tipo Parte no encontrado",
                                                        "tipoParteId: " + persona.tipoPartes())));
                        entity.setCarpeta(carpeta);
                        personaDocumentoRepository.save(entity);
                }

                juzgadoService.actualizarCarga(carpeta.getJuzgado(), carpeta.getTipoCarpeta(), juzgadosRelacionados);
                movimientoService.createMovimento(carpeta, null, auditor, null, EstadoCarpeta.CAPTURA.name());

                return new DocumentoRecord(documento.getId(), carpeta.getFolio(),
                                documento.getCarpeta().getTipoCarpeta());
        }

        public Page<DocumentoBandejaRecepcionRecord> getAllBandejaRecepcion(String key, Pageable pageable,
                        String tipoEntradaFilter) {

                key = (key != null) ? key.toLowerCase() : "";
                Persona currentUser = personaService.getAuditor();
                Object[] resultado = procesarTipoCarpeta(key);
                TipoCarpeta tipoCarpetaNombre = (TipoCarpeta) resultado[0];
                TipoDocumento tipoDocumentoNombre = (TipoDocumento) resultado[1];
                Integer folioTemp = (Integer) resultado[2];

                // Filtro:
                TipoDocumento tipoEntradaDoc = null;
                TipoCarpeta tipoEntradaCarp = null;
                try {
                        tipoEntradaDoc = TipoDocumento.valueOf(tipoEntradaFilter.toUpperCase());
                } catch (Exception e) {
                        try {
                                tipoEntradaCarp = TipoCarpeta.valueOf(tipoEntradaFilter.toUpperCase());
                        } catch (Exception ignored) {
                        }
                }

                if (roleService.hasRole(currentUser.getUsuario(), "OFICIAL_MAYOR_JUZGADO")) {
                        return renderOficialMayorData(key, pageable, currentUser, tipoCarpetaNombre,
                                        tipoDocumentoNombre, folioTemp, tipoEntradaDoc, tipoEntradaCarp);
                }
                return renderData(key, pageable, currentUser, tipoCarpetaNombre, tipoDocumentoNombre, folioTemp,
                                tipoEntradaDoc, tipoEntradaCarp);
        }

        private Page<DocumentoBandejaRecepcionRecord> renderData(String key, Pageable pageable, Persona currentUser,
                        TipoCarpeta tipoCarpetaNombre, TipoDocumento tipoDocumentoNombre, Integer folioTemp,
                        TipoDocumento tipoEntradaDoc, TipoCarpeta tipoEntradaCarp) {
                Page<Movimiento> page = movimientoService.getBandejaRecepcion(
                                pageable,
                                currentUser.getJuzgado().getId(),
                                EstadoCarpeta.TURNADO,
                                key,
                                EstadoCarpeta.TURNADO.name(),
                                currentUser,
                                tipoCarpetaNombre,
                                tipoDocumentoNombre,
                                folioTemp,
                                tipoEntradaDoc,
                                tipoEntradaCarp);

                List<DocumentoBandejaRecepcionRecord> list = page.getContent().stream()
                                .map(movimiento -> {
                                        Carpeta carpeta = movimiento.getCarpeta();

                                        String tipoEntrada = etiquetaService.renderEtiquetaRecepcion("nuevoNombre",
                                                        carpeta);

                                        String origen = movimiento.getPersona().getNombre() + " "
                                                        + movimiento.getPersona().getApellidoPaterno() + " "
                                                        + ((movimiento.getPersona().getApellidoMaterno() != null)
                                                                        ? movimiento.getPersona().getApellidoMaterno()
                                                                        : "");

                                        return new DocumentoBandejaRecepcionRecord(
                                                        carpeta.getId(),
                                                        null,
                                                        carpeta.getFolio(),
                                                        carpeta.getExpediente(),
                                                        tipoEntrada.replace("Promocion", "Promoción"),
                                                        origen,
                                                        carpeta.getConcepto().getNombre(),
                                                        movimiento.getFechaAsignacion(),
                                                        true,
                                                        carpeta.getPrioridad(),
                                                        carpeta.getHoras(),
                                                        carpeta.getConcepto().getId(),
                                                        null);
                                })
                                .toList();

                return new PageImpl<>(list, pageable, page.getTotalElements());
        }

        private Page<DocumentoBandejaRecepcionRecord> renderOficialMayorData(String key, Pageable pageable,
                        Persona currentUser, TipoCarpeta tipoCarpetaNombre, TipoDocumento tipoDocumentoNombre,
                        Integer folioTemp, TipoDocumento tipoEntradaDoc, TipoCarpeta tipoEntradaCarp) {

                Page<Movimiento> page = movimientoService.getAllBandejaRecepcion(
                                pageable,
                                currentUser.getJuzgado().getId(),
                                List.of(EstadoCarpeta.TURNADO, EstadoCarpeta.RECEPCION),
                                key,
                                List.of(EstadoCarpeta.TURNADO.name(), EstadoCarpeta.RECEPCION.name()),
                                currentUser,
                                tipoCarpetaNombre,
                                tipoDocumentoNombre,
                                folioTemp,
                                tipoEntradaDoc,
                                tipoEntradaCarp);

                List<DocumentoBandejaRecepcionRecord> list = page.getContent().stream()
                                .map(movimiento -> {
                                        Carpeta carpeta = movimiento.getCarpeta() != null ? movimiento.getCarpeta()
                                                        : movimiento.getDocumento().getCarpeta();

                                        Map<String, Object> map = getOrigen(movimiento, currentUser);
                                        Documento documento = getDocumentoForRenderOficialMayor(movimiento, carpeta);

                                        boolean isPromocion = documento != null && documento.getTipoDocumento() != null
                                                        && documento.getTipoDocumento().equals(TipoDocumento.PROMOCION);

                                        String folio;
                                        String tipoEntrada;
                                        String concepto;
                                        String expediente;
                                        Integer carpetaId;
                                        Integer documentoId = documento != null ? documento.getId() : null;
                                        Integer conceptoId;
                                        String tipoPromocion = "";

                                        // Si documento no es null, se obtienen los valores correspondientes
                                        if (documento != null && isPromocion) {
                                                folio = documento.getFolio();
                                                tipoEntrada = documento.getTipoDocumento().name();
                                                concepto = documento.getConcepto().getNombre();
                                                conceptoId = documento.getConcepto().getId();
                                                expediente = documento.getCarpeta().getExpediente();
                                                carpetaId = documento.getCarpeta().getId();
                                                tipoPromocion = documento.getData().getTipoPromocion().name();
                                        } else {
                                                // Si documento es null, se toman los valores de carpeta

                                                folio = carpeta.getFolio();
                                                tipoEntrada = carpeta.getTipoCarpeta().getEtiqueta();
                                                concepto = carpeta.getConcepto().getNombre();
                                                conceptoId = carpeta.getConcepto().getId();
                                                expediente = carpeta.getExpediente();
                                                carpetaId = carpeta.getId();
                                        }

                                        return new DocumentoBandejaRecepcionRecord(
                                                        carpetaId,
                                                        documentoId,
                                                        folio,
                                                        expediente,
                                                        StringUtils.capitalize(tipoEntrada.toLowerCase()),
                                                        map.get("name").toString(),
                                                        concepto,
                                                        movimiento.getFechaAsignacion(),
                                                        (Boolean) map.get(IS_INTERNO),
                                                        null,
                                                        null,
                                                        conceptoId,
                                                        tipoPromocion);

                                })
                                .toList();

                return new PageImpl<>(list, pageable, page.getTotalElements());
        }

        protected Documento getDocumentoForRenderOficialMayor(Movimiento movimiento, Carpeta carpeta) {

                if (movimiento.getDocumento() != null) {
                        return movimiento.getDocumento();
                }

                if (carpeta.getTipoCarpeta().equals(TipoCarpeta.DEMANDA)) {
                        return documentoRepository.findByCarpetaIdAndTipoDocumentoIsNull(carpeta.getId());
                }
                try {
                        TipoDocumento tipoDocumento = TipoDocumento.valueOf(carpeta.getTipoCarpeta().name());
                        return documentoRepository.findByCarpetaIdAndTipoDocumento(carpeta.getId(), tipoDocumento);

                } catch (Exception e) {
                        log.error("Error: ", e);
                        return new Documento().setId(0);
                }

        }

        protected Map<String, Object> getOrigen(Movimiento movimiento, Persona persona) {
                Integer documentoId = movimiento.getDocumento() != null ? movimiento.getDocumento().getId() : null;
                Integer carpetaId = movimiento.getCarpeta() != null ? movimiento.getCarpeta().getId() : null;

                Map<String, Object> origen = movimientoService.getOrigen(documentoId, carpetaId);
                String centroTrabajo = ((String) origen.get("centroTrabajo"));
                String nombrePersona = (String) origen.get("nombrePersona");

                boolean esInterno = false;
                if (persona.getJuzgado() != null && centroTrabajo.equalsIgnoreCase(persona.getJuzgado().getNombre())) {

                        esInterno = true;
                } else if (persona.getOficialia() != null
                                && centroTrabajo.equalsIgnoreCase(persona.getOficialia().getNombre())) {
                        System.out.println("PERDSONA OFICIALIA: " + persona.getOficialia().getNombre());
                        esInterno = true;
                }

                Map<String, Object> resultado = new HashMap<>();
                resultado.put(IS_INTERNO, esInterno);
                resultado.put("name", nombrePersona);

                return resultado;
        }

        public Page<DocumentoAsignadoResponseRecord> getAllAsignado(String key, Long personaId, Pageable pageable,
                        String tipoEntradaFilter) {
                key = (key != null) ? key.toLowerCase() : "";

                // Buscamos a la persona si es que la manda en el parametro
                if (personaId != null) {
                        personaAsignada = personaRepository.findById(personaId)
                                        .orElseThrow(() -> new NotFoundException("persona no encontrada",
                                                        "persona id" + personaId));
                }

                Persona persona = personaAsignada != null ? personaAsignada : personaService.getAuditor();
                Juzgado juzgado = persona.getJuzgado();
                Oficialia oficialia = persona.getOficialia();
                boolean esOficialMayor = roleService.hasRole(persona.getUsuario(), "OFICIAL_MAYOR_JUZGADO");

                Object[] resultado = procesarTipoCarpeta(key);
                TipoCarpeta tipoCarpetaNombre = (TipoCarpeta) resultado[0];
                TipoDocumento tipoDocumentoNombre = (TipoDocumento) resultado[1];
                Integer folioTemp = (Integer) resultado[2];

                TipoDocumento tipoEntradaDoc = null;
                TipoCarpeta tipoEntradaCarp = null;

                try {
                        tipoEntradaDoc = TipoDocumento.valueOf(tipoEntradaFilter.toUpperCase());
                } catch (Exception e) {
                        try {
                                tipoEntradaCarp = TipoCarpeta.valueOf(tipoEntradaFilter.toUpperCase());
                        } catch (Exception ignored) {
                        }
                }

                Page<Movimiento> page = documentoRepository.findByPersonaAsignada(key, persona.getJuzgado().getId(),
                                persona, esOficialMayor, pageable, tipoCarpetaNombre, tipoDocumentoNombre, folioTemp,
                                tipoEntradaDoc, tipoEntradaCarp);

                List<DocumentoAsignadoResponseRecord> list = page.getContent()
                                .stream()
                                .map(mov -> {
                                        Documento documento = mov.getDocumento();

                                        boolean isPromocion = (documento != null && documento.getTipoDocumento() != null
                                                        && Objects.equals(documento.getTipoDocumento(),
                                                                        TipoDocumento.PROMOCION));

                                        Carpeta carpeta = mov.getCarpeta() != null ? mov.getCarpeta()
                                                        : documento != null ? documento.getCarpeta() : null;

                                        LocalDateTime fechaTurnado = mov.getFechaAsignacion();

                                        LocalDateTime fechaTermino = (isPromocion)
                                                        ? mov.getFechaAsignacion()
                                                                        .plusDays(documento != null
                                                                                        ? documento.getConcepto()
                                                                                                        .getDias()
                                                                                        : 0)

                                                        : (carpeta != null && carpeta.getConcepto() != null)
                                                                        ? mov.getFechaAsignacion().plusDays(
                                                                                        carpeta.getConcepto().getDias())
                                                                        : null;

                                        Boolean esDiaInhabil = eventosService.esDiaInHabil(
                                                        fechaTermino != null ? fechaTermino.toLocalDate() : null,
                                                        juzgado, oficialia);

                                        if (esDiaInhabil) {
                                                fechaTermino = eventosService
                                                                .siguienteDiaHabil(
                                                                                fechaTermino != null ? fechaTermino
                                                                                                .toLocalDate() : null,
                                                                                juzgado,
                                                                                oficialia)
                                                                .atStartOfDay();
                                        }

                                        SolicitudesProrrogas solicitudProrroga = solicitudesProrrogasService
                                                        .getLastProrrogas(mov.getId());

                                        String motivoProrroga = solicitudProrroga != null
                                                        ? solicitudProrroga.getMotivoProrroga()
                                                        : null;

                                        EstadoProrroga estadoProrroga = solicitudProrroga != null
                                                        ? solicitudProrroga.getEstado()
                                                        : null;

                                        boolean turnadoVencido = fechaTermino != null &&
                                                        fechaTermino.isBefore(LocalDateTime.now()) &&
                                                        (mov.getConcepto() != null
                                                                        && !mov.getConcepto().equals("RESGUARDO"));

                                        boolean prorrogaActiva = solicitudProrroga != null
                                                        && solicitudProrroga.getEstado()
                                                                        .equals(EstadoProrroga.AUTORIZADA)
                                                        && !LocalDateTime.now().isAfter(solicitudProrroga
                                                                        .getFechaAutorizada().atStartOfDay());

                                        String observaciones = (isPromocion) ? mov.getObservaciones()
                                                        : getObservaciones(carpeta,
                                                                        mov.getObservaciones());
                                        String textoNotificacion = getTextoNotificacion(
                                                        Objects.equals(observaciones, "URGENTE"),
                                                        turnadoVencido,
                                                        prorrogaActiva,
                                                        solicitudProrroga != null && solicitudProrroga.getEstado()
                                                                        .equals(EstadoProrroga.AUTORIZADA));

                                        String colorNotificacion = getColorCorrespondenciaAsignados(
                                                        Objects.equals(observaciones, "URGENTE"),
                                                        turnadoVencido,
                                                        prorrogaActiva,
                                                        solicitudProrroga != null && solicitudProrroga.getEstado()
                                                                        .equals(EstadoProrroga.AUTORIZADA));

                                        return new DocumentoAsignadoResponseRecord(
                                                        mov.getId(),
                                                        ((isPromocion) && documento != null) ? documento.getId() : null,
                                                        carpeta != null ? carpeta.getId() : null,
                                                        carpeta != null ? carpeta.getExpediente() : null,
                                                        (isPromocion) && documento != null ? documento.getFolio()
                                                                        : carpeta != null ? carpeta.getFolio() : "",
                                                        StringUtils.capitalize(
                                                                        (isPromocion && documento != null)
                                                                                        ? documento.getTipoDocumento()
                                                                                                        .name()
                                                                                                        .toLowerCase()
                                                                                        : carpeta != null ? carpeta
                                                                                                        .getTipoCarpeta()
                                                                                                        .name()
                                                                                                        .toLowerCase()
                                                                                                        : ""),
                                                        (isPromocion && documento != null)
                                                                        ? documento.getConcepto().getNombre()
                                                                        : (carpeta != null && carpeta
                                                                                        .getConcepto() != null)
                                                                                                        ? carpeta.getConcepto()
                                                                                                                        .getNombre()
                                                                                                        : "-",
                                                        fechaTurnado,
                                                        fechaTermino,
                                                        StringUtils.capitalize(
                                                                        (isPromocion && documento != null)
                                                                                        ? documento.getEstatus().name()
                                                                                                        .toLowerCase()
                                                                                        : carpeta != null ? carpeta
                                                                                                        .getEstatus()
                                                                                                        .name()
                                                                                                        .toLowerCase()
                                                                                                        : ""),
                                                        observaciones,
                                                        turnadoVencido,
                                                        motivoProrroga,
                                                        estadoProrroga,
                                                        textoNotificacion,
                                                        colorNotificacion,
                                                        (isPromocion && documento != null)
                                                                        ? documento.getData().getTipoPromocion().name()
                                                                        : "");
                                })
                                .toList();

                personaAsignada = null;
                return new PageImpl<>(list, pageable, page.getTotalElements());
        }

        private LocalDateTime getFechaTermino(Movimiento movimiento, Documento documento, Carpeta carpeta) {
                Concepto concepto = (documento != null) ? documento.getConcepto() : carpeta.getConcepto();
                int aumentoDeDias = (concepto != null) ? concepto.getDias() : 0;

                return movimiento.getFechaAsignacion().plusDays(aumentoDeDias);
        }

        private String getObservaciones(Carpeta carpeta, String observaciones) {
                if (carpeta == null) {
                        return observaciones;
                }

                if (carpeta.getPrioridad() != null && carpeta.getPrioridad().equals(Prioridad.URGENTE)) {
                        return StringUtils.capitalize(Prioridad.URGENTE.name().toLowerCase());
                }

                Integer promociones = documentoRepository.countByCarpetaIdAndTipoDocumentoAndEstatus(
                                carpeta.getId(), TipoDocumento.PROMOCION, EstadoCarpeta.INTEGRADO);

                if (promociones > 0) {
                        return promociones + " promociones nuevas";
                }
                return observaciones;
        }

        private String getColorCorrespondenciaAsignados(boolean isUrgente, boolean turnadoVencido,
                        boolean prorrogaActiva, boolean prorrogaAutorizada) {
                String color = "green";

                if (isUrgente || (turnadoVencido && !prorrogaAutorizada) || (prorrogaAutorizada && !prorrogaActiva)) {
                        color = "red";
                }

                if (prorrogaAutorizada && prorrogaActiva) {
                        color = "orange";
                }

                return color;
        }

        private String getTextoNotificacion(
                        boolean esUrgente,
                        boolean turnadoVencido,
                        boolean prorrogaActiva,
                        boolean tieneProrrogaAutorizada) {
                if (esUrgente && turnadoVencido) {
                        return "Este documento urgente ya venció el plazo de atención";
                }

                if (turnadoVencido && !tieneProrrogaAutorizada) {
                        return "Este documento ha vencido sin prórroga autorizada";
                }

                if (prorrogaActiva) {
                        return "Este documento tiene una prórroga activa";
                }

                if (tieneProrrogaAutorizada && turnadoVencido) {
                        return "Este documento tuvo una prórroga autorizada que ya venció";
                }

                if (esUrgente) {
                        return "Este documento urgente debe atenderse prioritariamente";
                }

                return "Normal";
        }

        public List<DocumentoAsignadoResponseRecord> getAllAsignado(Persona persona, String uuid) {
                personaAsignada = persona;
                String key = Objects.toString(uuid, "");

                return getAllAsignado(key, null, Pageable.unpaged(), null).getContent();
        }

        protected String sendToBandejaRecepcion(List<Integer> idList, Integer personaCarrito) {
                UUID uuid = UUID.randomUUID();

                List<Movimiento> movimientoList = movimientoRepository.findAllById(idList);
                Persona persona = personaRepository.findById(Long.valueOf(personaCarrito))
                                .orElseThrow(() -> new NotFoundException("Persona no encontrada",
                                                "PersonaId: " + personaCarrito));
                Persona personaAuditor = personaService.getAuditor();
                String nombrePersona = String.format("%s %s %s", persona.getNombre(), persona.getApellidoPaterno(),
                                Objects.toString(persona.getApellidoMaterno(), ""));

                for (Movimiento mov : movimientoList) {
                        Movimiento movimiento = new Movimiento()
                                        .setFechaAsignacion(LocalDateTime.now())
                                        .setEstado(EstadoCarpeta.RECEPCION.name())
                                        .setPersona(personaAuditor)
                                        .setUuid(uuid)
                                        .setCargo(personaAuditor.getRolPrincipal())
                                        .setObservaciones(nombrePersona);

                        if (mov.getDocumento() != null) {
                                Documento documento = mov.getDocumento();
                                documento.setFechaAsignacion(LocalDateTime.now())
                                                .setPersona(persona)
                                                .setEstatus(EstadoCarpeta.RECEPCION);
                                documento.setConcepto(
                                                getConceptoByTipoCarpetaDocumento(documento.getTipoDocumento(), null));
                                documento = documentoRepository.save(documento);
                                movimiento.setConcepto(documento.getConcepto().getNombre());
                                movimiento.setDuracion(documento.getConcepto().getDias().toString() + "d");
                                movimiento.setDocumento(documento);
                                movimiento.setOficialia(persona.getOficialia());
                                // movimiento.setJuzgado(documento.getCarpeta().getJuzgado());
                        } else {
                                Carpeta carpeta = mov.getCarpeta();
                                carpeta.setFechaAsignacion(LocalDateTime.now())
                                                .setPersona(persona)
                                                .setConcepto(getConceptoByTipoCarpetaDocumento(null,
                                                                carpeta.getTipoCarpeta()))
                                                .setEstatus(EstadoCarpeta.RECEPCION);

                                carpeta = carpetaRepository.save(carpeta);
                                movimiento.setConcepto(carpeta.getConcepto().getNombre());
                                movimiento.setDuracion(carpeta.getConcepto().getDias().toString() + "d");
                                movimiento.setCarpeta(carpeta);
                                // movimiento.setJuzgado(carpeta.getJuzgado());
                                movimiento.setOficialia(persona.getOficialia());
                        }
                        this.movimientoRepository.save(movimiento);
                }
                return uuid.toString();
        }

        private Concepto getConceptoByTipoCarpetaDocumento(TipoDocumento tipoDocumento, TipoCarpeta tipoCarpeta) {
                String conceptoDistri = "Distribución";
                String conceptoAdjun = "Adjuntar";

                Concepto concepto = new Concepto();
                if (tipoDocumento == TipoDocumento.PROMOCION) {
                        concepto = conceptoRepository.findByNombre(conceptoAdjun)
                                        .orElseThrow(() -> new NotFoundException(CONCEPTO_NOT_FOUND, conceptoAdjun));
                } else if ((tipoCarpeta == TipoCarpeta.DEMANDA || tipoCarpeta == TipoCarpeta.EXHORTO
                                || tipoCarpeta == TipoCarpeta.APELACION) || tipoCarpeta == TipoCarpeta.PIEZA) {
                        concepto = conceptoRepository.findByNombre(conceptoDistri)
                                        .orElseThrow(() -> new NotFoundException(CONCEPTO_NOT_FOUND, conceptoDistri));
                }
                return concepto;
        }

        public Object[] getQR(String folioDocumentoQR) {
                String[] parte = folioDocumentoQR.split("\\.");

                if (parte.length != 2) {
                        throw new IllegalArgumentException("El código QR tiene un formato inválido.");
                }

                String prefix = parte[0].trim();
                int folio = Integer.parseInt(parte[1].trim());
                return new Object[] { prefix, folio };
        }

        public Object[] procesarTipoCarpeta(String key) {
                String tipoCarpeta = null;
                Integer folio = null;

                if (key.matches("[a-zA-Z].\\d+")) {
                        Object[] qrValues = getQR(key);
                        tipoCarpeta = (String) qrValues[0];
                        folio = (Integer) qrValues[1];
                }

                TipoCarpeta tipoCarpetaNombre = null;
                TipoDocumento tipoDocumentoNombre = null;

                if (tipoCarpeta != null) {
                        switch (tipoCarpeta.toUpperCase()) {
                                case "E":
                                        tipoCarpetaNombre = TipoCarpeta.EXHORTO;
                                        break;
                                case "D":
                                        tipoCarpetaNombre = TipoCarpeta.DEMANDA;
                                        break;
                                case "A":
                                        tipoCarpetaNombre = TipoCarpeta.APELACION;
                                        break;
                                case "P":
                                        tipoDocumentoNombre = TipoDocumento.PROMOCION;
                                        break;
                                case "PZ":
                                        tipoCarpetaNombre = TipoCarpeta.PIEZA;
                                        break;
                                default:
                                        throw new IllegalArgumentException("El tipo de carpeta es desconocido");
                        }
                }

                return new Object[] { tipoCarpetaNombre, tipoDocumentoNombre, folio };
        }

        public IndicadoresRecord getIndicadores() {
                int totalPendientes;
                Integer totalRecibidosHoy = 0;
                Integer totalRecibidosAyer = 0;
                Integer totalOldies = 0;

                Page<DocumentoBandejaRecepcionRecord> page = getAllBandejaRecepcion("", Pageable.unpaged(), "Todas");

                totalPendientes = page.getSize();

                for (DocumentoBandejaRecepcionRecord item : page.getContent()) {
                        LocalDate fechaAsignacion = item.fechaHoraEnvio().toLocalDate();

                        if (fechaAsignacion.equals(LocalDate.now())) {
                                totalRecibidosHoy++;
                        } else if (fechaAsignacion.equals(LocalDate.now().minusDays(1))) {
                                totalRecibidosAyer++;
                        } else {
                                totalOldies++;
                        }
                }

                return new IndicadoresRecord(totalPendientes, totalRecibidosHoy, totalRecibidosAyer, totalOldies);
        }

        public List<CarpetaCatalogoRecord> getTipoEntradas(String bandeja) {

                return switch (bandeja) {
                        case "ASIGNADOS" -> {
                                Page<DocumentoAsignadoResponseRecord> page = getAllAsignado(null, null,
                                                Pageable.unpaged(), "Todas");
                                yield page.getContent().stream()
                                                .map(item -> new CarpetaCatalogoRecord(item.tipoEntrada(),
                                                                item.tipoEntrada()))
                                                .distinct()
                                                .toList();
                        }
                        case "ENTRADA" -> {
                                Page<DocumentoGridRecord> page = getAll(null, Pageable.unpaged(), "Todas");
                                yield page.getContent().stream()
                                                .map(item -> new CarpetaCatalogoRecord(item.tipoEntrada(),
                                                                item.tipoEntrada()))
                                                .distinct()
                                                .toList();
                        }
                        case "RECEPCION" -> {
                                Page<DocumentoBandejaRecepcionRecord> page;
                                page = getAllBandejaRecepcion(null, Pageable.unpaged(), "Todas");
                                yield page.getContent().stream()
                                                .map(item -> new CarpetaCatalogoRecord(item.tipoEntrada(),
                                                                item.tipoEntrada()))
                                                .distinct()
                                                .toList();
                        }
                        default -> Collections.emptyList();
                };
        }

        public Integer createOficio(Integer institucionId, LocalDate fechaEmision, String asunto, Integer carpetaId) {

                // Obtenemos folio
                Persona persona = personaService.getAuditor();
                Integer folio = documentoFoliosService.getFolio(TipoDocumento.OFICIO, persona.getJuzgado(),
                                persona.getOficialia());

                // Obtenemos la institución y seteamos información para la Data del documento
                Institucion institucion = institucionRepository.findById(institucionId)
                                .orElseThrow(
                                                () -> new NotFoundException("Institución no encontrada",
                                                                "institucionId: " + institucionId));
                DocumentoData docData = new DocumentoData()
                                .setTipoOficio(carpetaId == null ? "Administrativo" : "Jurisdiccional");

                Optional<Carpeta> carpeta = carpetaId == null ? Optional.empty()
                                : carpetaRepository.findById(carpetaId);
                // Creamos y guardamos el documento con la información obtenida.
                Documento doc = new Documento()
                                .setCarpeta(carpeta.orElse(null))
                                .setFolio(String.valueOf(folio))
                                .setTipoDocumento(TipoDocumento.OFICIO)
                                .setData(docData)
                                .setInstitucion(institucion)
                                .setEstatus(EstadoCarpeta.CREADO);
                doc = documentoRepository.save(doc);

                DocumentoDetalle documentoDetalle = new DocumentoDetalle()
                                .setAsunto(asunto)
                                .setFechaEmision(fechaEmision)
                                .setDocumento(doc)
                                .setEstado(EstadoAcuse.CREADO);
                documentoDetalleRepository.save(documentoDetalle);
                return folio;
        }

        public DocumentoRecepcionRecord getDataDocumentoRecepcion(Integer id) {

                Documento doc = documentoRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException(DOC_NOT_FOUND, DOC_ID + id));

                List<AnexoRecepcionRecord> anexosActuales = anexoRepository.findAnexosByDocumentoId(id);
                addAnexoExtra(anexosActuales, doc);
                String origen = (String) movimientoService.getOrigen(
                                (doc.getTipoDocumento() != null) ? doc.getId() : null,
                                (doc.getTipoDocumento() != null) ? null : doc.getCarpeta().getId())
                                .get("centroTrabajo");

                return new DocumentoRecepcionRecord(
                                (doc.getTipoDocumento() != null
                                                && !Objects.equals(doc.getTipoDocumento(), TipoDocumento.APELACION))
                                                                ? doc.getFolio()
                                                                : doc.getCarpeta().getFolio(),
                                doc.getCarpeta().getExpediente(),
                                StringUtils.capitalize((doc.getTipoDocumento() != null)
                                                ? doc.getTipoDocumento().name().toLowerCase()
                                                : doc.getCarpeta().getTipoCarpeta().name().toLowerCase()),
                                doc.getRuta(),
                                origen,
                                anexosActuales);
        }

        private void addAnexoExtra(List<AnexoRecepcionRecord> anexos, Documento documento) {
                if (documento.getCarpeta().getTipoJuicio().getMateria().getNombre().equals("LABORAL")) {
                        Optional<AnexoRecepcionRecord> anexo = anexos.stream()
                                        .filter(it -> it.nombre().equalsIgnoreCase("Constancia de no conciliación"))
                                        .findFirst();
                        if (anexo.isEmpty()) {
                                Anexo entity = new Anexo();
                                entity.setNombre("Constancia de no conciliación");
                                entity.setDocumento(documento);
                                entity = anexoRepository.save(entity);
                                anexos.add(new AnexoRecepcionRecord(entity.getId(), entity.getEstado(),
                                                entity.getNombre()));
                        }
                }
        }

        public Page<OficioResponseRecord> getAllOficios(String key, Pageable pageable) {
                key = (key != null) ? key.toLowerCase() : "";

                Page<OficioResponseRecord> page = documentoRepository.findAllByTipoDocumento(key, TipoDocumento.OFICIO,
                                pageable);
                List<OficioResponseRecord> list = page.getContent().stream()
                                .map(item -> new OficioResponseRecord(
                                                item.docId(),
                                                item.folio(),
                                                item.dependencia(),
                                                (item.asunto() != null && item.asunto().length() > 30)
                                                                ? item.asunto().substring(0, 30) + "..."
                                                                : item.asunto(),
                                                item.estatus(),
                                                item.estatus() != null ? item.estatus().getEtiqueta() : null,
                                                item.fechaEmision(),
                                                item.fechaEntrega(),
                                                item.bandAcuse(),
                                                item.bandDigitalizado(),
                                                item.tamanioPapel(),
                                                item.expediente()))
                                .toList();

                return new PageImpl<>(list, pageable, page.getTotalElements());
        }

        public String cancelOficio(Integer idDocumento) {
                Persona personaAuditor = personaService.getAuditor();

                Documento doc = documentoRepository.findById(idDocumento)
                                .orElseThrow(() -> new NotFoundException(DOC_NOT_FOUND, DOC_ID + idDocumento));

                documentoRepository.actualizarEstatus(idDocumento, EstadoCarpeta.CANCELADO);

                Movimiento mov = movimientoService.createMovimento(
                                null,
                                doc,
                                personaAuditor,
                                null,
                                EstadoCarpeta.CANCELADO.name());

                return String.valueOf(mov.getId());
        }

        public MovimientoPersonalJuzgadoRecord movimientoPersonalJuzgado(PersonalJuzgadoRecord personalJuzgadoRecord) {
                Persona persona = personaService.getAuditor();
                Concepto concepto = conceptoRepository.findById(personalJuzgadoRecord.idConcepto())
                                .orElseThrow(() -> new NotFoundException(CONCEPTO_NOT_FOUND,
                                                "conceptoId" + personalJuzgadoRecord.idConcepto()));
                Carpeta carpeta = carpetaRepository.findById(personalJuzgadoRecord.idDocumentoRecepcion())
                                .orElseThrow(() -> new NotFoundException(CARPETA_NOT_FOUND,
                                                "carpetaId" + personalJuzgadoRecord.idDocumentoRecepcion()));

                carpeta.setConcepto(concepto);
                carpeta.setEstatus(EstadoCarpeta.ASIGNADO);
                carpeta.setPersona(persona);
                carpetaRepository.save(carpeta);

                String duration = (carpeta.getHoras() != null && carpeta.getHoras() > 0) ? carpeta.getHoras() + "h"
                                : concepto.getDias().toString() + "d";

                Movimiento movimiento = movimientoService.createMovimentoTurnado(carpeta, null, persona, null,
                                EstadoCarpeta.ASIGNADO.name(), concepto.getNombre(), null, duration);

                return new MovimientoPersonalJuzgadoRecord(
                                personalJuzgadoRecord.idDocumentoRecepcion(),
                                movimiento.getFechaAsignacion(),
                                persona.getNombre(),
                                movimiento.getMotivo(),
                                persona.getJuzgado().getNombre());
        }

        public List<MovimientoPersonalJuzgadoRecord> movimientoPersonalJuzgadoList(
                        List<PersonalJuzgadoRecord> personalJuzgadoRecords) {

                Persona persona = personaService.getAuditor();
                List<MovimientoPersonalJuzgadoRecord> movimientoPersonal = new ArrayList<>();

                for (PersonalJuzgadoRecord p : personalJuzgadoRecords) {
                        Concepto concepto = conceptoRepository.findById(p.idConcepto())
                                        .orElseThrow(() -> new NotFoundException(CONCEPTO_NOT_FOUND,
                                                        "conceptoId" + p.idConcepto()));
                        Movimiento movimiento;

                        if (p.tipoEntrada().toUpperCase().equals("PROMOCION")) {

                                Documento documento = documentoRepository.findById(p.idDocumentoRecepcion())
                                                .orElseThrow(() -> new NotFoundException("Documento no encontrado",
                                                                "documento ID" + p.idDocumentoRecepcion()));

                                documento.setConcepto(concepto);
                                documento.setPersona(persona);
                                documento.setEstatus(EstadoCarpeta.ASIGNADO);
                                documentoRepository.save(documento);

                                movimiento = movimientoService.createMovimentoTurnado(null, documento, persona, null,
                                                EstadoCarpeta.ASIGNADO.name(), concepto.getNombre(), null,
                                                concepto.getDias().toString() + "d");

                        } else {
                                Carpeta carpeta = carpetaRepository.findById(p.idCarpetaRecepcion())
                                                .orElseThrow(() -> new NotFoundException(CARPETA_NOT_FOUND,
                                                                "carpetaId" + p.idCarpetaRecepcion()));

                                String duration = (carpeta.getHoras() != null && carpeta.getHoras() > 0)
                                                ? carpeta.getHoras() + "h"
                                                : concepto.getDias().toString() + "d";

                                carpeta.setConcepto(concepto);
                                carpeta.setEstatus(EstadoCarpeta.ASIGNADO);
                                carpeta.setPersona(persona);
                                carpetaRepository.save(carpeta);

                                movimiento = movimientoService.createMovimentoTurnado(carpeta, null, persona, null,
                                                EstadoCarpeta.ASIGNADO.name(), concepto.getNombre(), null, duration);
                        }

                        movimientoPersonal.add(new MovimientoPersonalJuzgadoRecord(p.idDocumentoRecepcion(),
                                        movimiento.getFechaAsignacion(),
                                        persona.getNombre(), movimiento.getMotivo(),
                                        persona.getJuzgado().getNombre()));
                }

                return movimientoPersonal;
        }

        public void sendEmailFamiliar(DocumentoSaveRecord documentoRecord, Documento documento, Carpeta carpeta,
                        TipoJuicio tpoJuicio) {
                Map<String, Object> sendEmail = new HashMap<>();

                String tiposJuicios = documento.getData().getTiposJuicios().stream()
                                .map(TipoJuicioDemandasRecord::nombre)
                                .collect(Collectors.joining(", "));
                sendEmail.put("juicio", tiposJuicios);

                String salaAudiencia = audienciaRepository.getSalaNombreByCarpetaId(carpeta.getId());
                sendEmail.put("sala", salaAudiencia);

                sendEmail.put("carpetaDigital", selloGenerator.updateExpedientePorTipoJuicio(documento));
                sendEmail.put("tipoJuicio", tpoJuicio.getNombre());

                String apellidoMaternoActor = documentoRecord.actor().apellidoMaterno();
                sendEmail.put("actor", String.join(" ",
                                documentoRecord.actor().nombre(),
                                documentoRecord.actor().apellidoPaterno(),
                                (apellidoMaternoActor != null && !apellidoMaternoActor.isEmpty() ? apellidoMaternoActor
                                                : "").trim()));
                sendEmail.put("telefono", documentoRecord.actor().celular());
                sendEmail.put("correo", documentoRecord.actor().correoElectronico());

                String apellidoMaternoDemandado = documentoRecord.demandado().apellidoMaterno();
                sendEmail.put("demandado", String.join(" ",
                                documentoRecord.demandado().nombre(),
                                documentoRecord.demandado().apellidoPaterno(),
                                (apellidoMaternoDemandado != null && !apellidoMaternoDemandado.isEmpty()
                                                ? apellidoMaternoDemandado
                                                : "").trim()));

                StringBuilder anexosHtml = new StringBuilder("<ul>");

                if (documentoRecord.anexos() == null || documentoRecord.anexos().isEmpty()) {
                        anexosHtml.append("<li>Sin anexo</li>");
                } else {

                        for (String anexo : documentoRecord.anexos()) {
                                anexosHtml.append("<li>").append(anexo).append("</li>");
                        }
                }
                anexosHtml.append("</ul>");
                sendEmail.put("anexos", anexosHtml.toString());

                emailService.sendMail(
                                List.of(correoDefensoria),
                                Collections.emptyList(),
                                Collections.emptyList(),
                                "Recepción de Asignación de Juicio",
                                "EmailDemandaFamiliar.ftl",
                                sendEmail);
        }

        public List<MovimientoPersonalJuzgadoRecord> turnadoPersonalJuzgado(List<AsignadoTurnadoRecord> records) {
                List<MovimientoPersonalJuzgadoRecord> resultados = new ArrayList<>();

                for (AsignadoTurnadoRecord item : records) {
                        Persona personalJuzgado = personaRepository.findById(item.idPersonalJuzgado().longValue())
                                        .orElseThrow(() -> new NotFoundException("Personal no encontrado",
                                                        "personalJuzgadoId" + item.idPersonalJuzgado()));

                        Concepto concepto = conceptoRepository.findById(item.idConcepto())
                                        .orElseThrow(() -> new NotFoundException(CONCEPTO_NOT_FOUND,
                                                        "conceptoId" + item.idConcepto()));

                        Carpeta carpeta = carpetaRepository.findById(item.idDocumentoAsignado())
                                        .orElseThrow(
                                                        () -> new NotFoundException(CARPETA_NOT_FOUND,
                                                                        "carpetaId" + item.idDocumentoAsignado()));

                        carpeta.setConcepto(concepto);
                        carpeta.setPrioridad(item.prioridad());

                        // Conversiones de dias a horas o dias. de momento se comentan ya que se
                        // menciono que se manejaria en dias no en horas.
                        /*
                         * float toDays = (float) item.horas() / 24;
                         * if (toDays != (float) concepto.getDias()) {
                         * carpeta.setHoras(item.horas());
                         * } else {
                         * carpeta.setHoras(null);
                         * }
                         */

                        // ajuste en la obtención de dias ya que en si ya se le pasan
                        // los dias no hay necesidad de dividir entre 24
                        float toDays = (float) (item.dias() != null ? item.dias() : 0);
                        if (toDays != (float) concepto.getDias()) {
                                carpeta.setHoras(item.dias());
                        } else {
                                carpeta.setHoras(null);
                        }

                        carpeta.setEstatus(EstadoCarpeta.TURNADO);

                        carpetaRepository.save(carpeta);

                        Persona persona = personaService.getAuditor();

                        String duracion = (carpeta.getHoras() != null && carpeta.getHoras() > 0)
                                        ? carpeta.getHoras() + "h"
                                        : concepto.getDias().toString() + "d";

                        Movimiento movimiento = movimientoService.createMovimentoTurnado(carpeta, null, persona, null,
                                        EstadoCarpeta.TURNADO.name(),
                                        StringUtils.capitalize(concepto.getNombre().toLowerCase()),
                                        personalJuzgado, duracion);

                        MovimientoPersonalJuzgadoRecord resultado = new MovimientoPersonalJuzgadoRecord(
                                        carpeta.getId(),
                                        movimiento.getFechaAsignacion(),
                                        persona.getNombre(),
                                        movimiento.getMotivo(),
                                        persona.getJuzgado().getNombre());
                        resultados.add(resultado);
                }
                return resultados;
        }

        public IndicadoresRecord getIndicadoresAsignados() {
                int totalAsignados;
                Integer terminoRebasado = 0;
                Integer termino24horas = 0;
                Integer termino3dias = 0;

                Page<DocumentoAsignadoResponseRecord> asignados = getAllAsignado("", null, Pageable.unpaged(), null);

                totalAsignados = asignados.getSize();

                for (DocumentoAsignadoResponseRecord asignado : asignados) {
                        if (asignado.fechaTermino() != null) {
                                if (LocalDateTime.now().isAfter(asignado.fechaTermino())) {
                                        terminoRebasado++;
                                } else if (asignado.fechaTermino().isAfter(LocalDateTime.now())
                                                && asignado.fechaTermino().isBefore(LocalDateTime.now().plusDays(1))) {
                                        termino24horas++;
                                } else {
                                        termino3dias++;
                                }
                        }
                }

                return new IndicadoresRecord(totalAsignados, terminoRebasado, termino24horas, termino3dias);
        }

        public void deleteAsignado(Integer id) {
                try {
                        Optional<PersonaDocumento> personaDocumento = personaDocumentoRepository.findById(id);

                        Persona persona = personaService.getAuditor();
                        if (personaDocumento.isPresent()) {
                                movimientoService.createMovimento(
                                                personaDocumento.get().getCarpeta(),
                                                null,
                                                persona,
                                                String.join(" ", "ELIMINADO DE PARTICIPANTE",
                                                                personaDocumento.get().getNombre()),
                                                null);
                                Optional<PersonaDetalle> personaDetalle = personaDetalleRepository
                                                .findByPersonaDocumentoId(personaDocumento.get().getId());
                                personaDetalle.ifPresent(
                                                detalle -> personaDetalleRepository.deleteById(detalle.getId()));
                                personaDocumentoRepository.deleteById(id);
                                personaDocumentoRepository.flush();
                        } else {
                                throw new EntityNotFoundException("No se encontró la persona documento con ID: " + id);
                        }
                } catch (DataIntegrityViolationException ex) {
                        throw new ConstraintViolationException(Messages.CONSTRAINT_ERROR, "actualizar a asignado" + id);
                }
        }

        public AmparoRecordResponse createAmparo(AmparoRecord amparoRecord) {
                Persona persona = personaService.getAuditor();
                DocumentoData data = new DocumentoData();

                Carpeta carpeta = carpetaRepository.findById(amparoRecord.carpetaId())
                                .orElseThrow(() -> new NotFoundException("La Carpeta no existe", "Carpeta"));

                Integer folio = documentoFoliosService.getFolio(TipoDocumento.AMPARO, persona.getJuzgado(), null);

                data.setAmparoFechaPresentacion(amparoRecord.fechaPresentacion());
                data.setAmparoImpugnacion(amparoRecord.impugnacion());
                data.setAmparoQuejoso(amparoRecord.quejoso());
                data.setAmparoTribunalId(amparoRecord.tribunalId());
                data.setAmparoSalaId(amparoRecord.salaId());
                data.setAmparoSentido(amparoRecord.sentidoAmparo());
                data.setAmparoSentidoImpugnacion(amparoRecord.sentidoImpugnacion());
                data.setAmparoTipo(amparoRecord.tipoAmparo());
                data.setAmparoFechaTermino(amparoRecord.fechaTermino());

                Documento amparo = new Documento()
                                .setCarpeta(carpeta)
                                .setData(data)
                                .setFolio(String.valueOf(folio))
                                .setEstatus(EstadoCarpeta.ASIGNADO)
                                .setFechaAsignacion(LocalDateTime.now())
                                .setTipoDocumento(TipoDocumento.AMPARO)
                                .setPersona(persona)
                                .setConcepto(conceptoRepository.findByNombre("Distribución").orElseThrow());

                documentoRepository.save(amparo);

                Carpeta pieza = carpetaService.createPieza(carpeta.getId(),
                                new PiezaRecord(null, amparoRecord.tipoAmparo(),
                                                Collections.singletonList(amparo.getId())));

                return new AmparoRecordResponse(pieza.getId(), amparo.getId(), pieza.getExpediente(),
                                amparo.getFechaAsignacion());
        }

        public DocumentoRecord createDemandaAntigua(DocumentoAntiguoSaveRecord documentoRecord,
                        MultipartFile multipartFile) {
                Persona persona = personaService.getAuditor();
                Carpeta carpeta = new Carpeta();
                Documento documento = new Documento();

                if (persona != null && persona.getJuzgado() != null && persona.getJuzgado().getTipoJuicios() != null) {
                        TipoJuicio tipoJuicioTradicional = persona.getJuzgado().getTipoJuicios().stream()
                                        .filter(tipoJuicio -> tipoJuicio != null && tipoJuicio.getTipoSistema() != null
                                                        && "Tradicional".equals(
                                                                        tipoJuicio.getTipoSistema().getNombre()))
                                        .findFirst()
                                        .orElseThrow(() -> new NotFoundException(
                                                        "Tipo Juicio 'Tradicional' no encontrado para la persona logueada",
                                                        String.valueOf(persona.getId())));

                        if (tipoJuicioTradicional == null) {
                                throw new NotFoundException(TIPO_JUICIO_NOT_FOUND, "");
                        }
                        carpeta.setTipoJuicio(tipoJuicioTradicional);
                } else {
                        throw new NotFoundException("No se encontraron juzgado", "");
                }

                carpeta.setJuzgado(persona.getJuzgado());
                carpeta.setFolio(getFolio("D"));
                carpeta.setTipoCarpeta(TipoCarpeta.DEMANDA);
                carpeta.setExpediente(String.format("%06d", Integer.parseInt(documentoRecord.numero())) + "/"
                                + documentoRecord.anio());
                carpeta.setEstatus(EstadoCarpeta.ASIGNADO);
                carpeta.setSelloEstatus(SelloEstatus.VALIDO);
                carpeta.setFechaAsignacion(LocalDateTime.now());
                carpeta.setPersona(persona);
                Concepto concepto = conceptoRepository.findByNombre("Distribución")
                                .orElseThrow(() -> new NotFoundException(CONCEPTO_NOT_FOUND, "Distribución"));
                carpeta.setConcepto(concepto);
                carpeta = carpetaRepository.save(carpeta);

                movimientoService.createMovimento(carpeta, null, persona, null, EstadoCarpeta.ASIGNADO.name());
                documento.setCarpeta(carpeta);
                documento.setFechaAsignacion(null);
                documento.setPersona(null);
                documento = documentoRepository.save(documento);
                createPersonaDocumento(documentoRecord.actor(), carpeta);
                createPersonaDocumento(documentoRecord.demandado(), carpeta);
                addAnexos(documentoRecord.anexos(), documento);
                carpetaDetalleRepository.save(new CarpetaDetalle().setCarpeta(carpeta));

                digitalizacionService.guardarArchivo(multipartFile, documento.getId());

                return new DocumentoRecord(documento.getId(), carpeta.getFolio(),
                                documento.getCarpeta().getTipoCarpeta());
        }

        public ExhortoResponseRecord getExhortoById(Integer id) {
                Documento documento = documentoRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException(DOC_NOT_FOUND, id.toString()));
                List<String> anexos = anexoRepository.findNombresAnexosByDocumentoId(id);
                return new ExhortoResponseRecord(anexos, documento.getData().getExhortoObservaciones(),
                                documento.getData().getExhortoProcedencia(),
                                documento.getCarpeta().getTipoJuicio().getNombre());
        }

        public DocPromocionInfoRecord getInfoPromocion(Integer docId) {
                Documento doc = documentoRepository.findById(docId)
                                .orElseThrow(() -> new NotFoundException(DOC_NOT_FOUND, DOC_ID + docId));

                String[] expediente = doc.getCarpeta().getExpediente().split("/");

                CarpetaResponseRecord carpetaResponseRecord = carpetaService.getCarpetaResponseByNumExpYearJuzgado(
                                doc.getCarpeta().getExpediente(), doc.getCarpeta().getJuzgado().getId());

                List<String> anexos = anexoRepository.findNombresAnexosByDocumentoId(docId);

                return new DocPromocionInfoRecord(
                                expediente[0],
                                Integer.parseInt(expediente[1]),
                                doc.getCarpeta().getJuzgado().getNombre(),
                                carpetaResponseRecord.actor(),
                                carpetaResponseRecord.demandado(),
                                doc.getData().getTipoPromocion().name(),
                                anexos);
        }

        @Transactional
        public DocumentoPromocionResponseRecord createExhortoSalida(DocumentoExhortoSalidaRecord docExhortoSalidaRecord,
                        MultipartFile multipartFile) {
                Carpeta carpeta = carpetaRepository.findById(docExhortoSalidaRecord.carpetaId())
                                .orElseThrow(() -> new NotFoundException("La Carpeta no existe", "Carpeta"));
                Persona auditor = personaService.getAuditor();
                DocumentoData data = new DocumentoData();
                data.setTramite(docExhortoSalidaRecord.tramite())
                                .setDestino(docExhortoSalidaRecord.destino())
                                .setExhortoObservaciones(docExhortoSalidaRecord.observaciones())
                                .setFechaEntrega(docExhortoSalidaRecord.fechaEntrega())
                                .setFechaDevolucion(docExhortoSalidaRecord.fechaDevolucion());
                Documento documento = new Documento();
                documento.setData(data)
                                .setCarpeta(carpeta)
                                .setPersona(auditor)
                                .setFolio(getFolio("ES"))
                                .setTipoDocumento(TipoDocumento.EXHORTO_SALIDA);
                documento = documentoRepository.save(documento);
                if (multipartFile != null) {
                        digitalizacionService.guardarArchivo(multipartFile, documento.getId());
                }
                movimientoService.createMovimento(null, documento, auditor, null, EstadoCarpeta.CREADO.name());
                return new DocumentoPromocionResponseRecord(documento.getId(), documento.getFolio(),
                                documento.getTipoDocumento());
        }

        public DocumentoPromocionResponseRecord adjuntarPromocion(Integer documentoId) {
                Documento documento = documentoRepository.findById(documentoId)
                                .orElseThrow(() -> new NotFoundException("La promoción no existe", "documentoId"));

                if (documento.getEstatus() == EstadoCarpeta.ASIGNADO) {
                        documento.setEstatus(EstadoCarpeta.INTEGRADO);
                        documentoRepository.save(documento);

                        return new DocumentoPromocionResponseRecord(documento.getId(), documento.getFolio(),
                                        documento.getTipoDocumento());
                }

                throw new ConflictException("No se puede integrar la promoción");
        }

        @Transactional
        public void saveSentenciaPublica(Integer idDocumento, MultipartFile multipartFile) {
                Persona auditor = personaService.getAuditor();

                Documento docSentencia = documentoRepository.findById(idDocumento)
                                .orElseThrow(() -> new NotFoundException(DOC_NOT_FOUND, idDocumento.toString()));

                Documento docSentenciaPublica = new Documento();
                docSentenciaPublica
                                .setCarpeta(docSentencia.getCarpeta())
                                .setPersona(auditor)
                                .setTipoDocumento(TipoDocumento.SENTENCIA_PUBLICA)
                                .setAcuerdoRespuesta(docSentencia);
                docSentenciaPublica = documentoRepository.save(docSentenciaPublica);

                digitalizacionService.guardarArchivo(multipartFile, docSentenciaPublica.getId());
        }

        public AmparoGetRecord getAmparoById(Integer id) {
                Documento documento = documentoRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Documento no encontrado con id: " + id));

                Carpeta carpeta = carpetaRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Documento no encontrado con id: " + id));

                Integer carpetaId = carpeta.getId();
                DocumentoData data = documento.getData();

                return new AmparoGetRecord(
                                carpetaId,
                                data.getAmparoTipo(),
                                data.getAmparoFechaPresentacion(),
                                data.getAmparoFechaTermino(),
                                data.getAmparoImpugnacion(),
                                data.getAmparoSentido(),
                                data.getAmparoSentidoImpugnacion(),
                                data.getAmparoQuejoso(),
                                data.getAmparoTribunalId(),
                                data.getAmparoSalaId());
        }

        public void updateAmparoData(Integer id, AmparoUpdateRecord amparoUpdateRecord) {
                Documento documento = documentoRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Documento not found with id: " + id));

                DocumentoData data = documento.getData();

                data.setAmparoFechaPresentacion(amparoUpdateRecord.fechaPresentacion());
                data.setAmparoFechaTermino(amparoUpdateRecord.fechaTermino());
                data.setAmparoImpugnacion(amparoUpdateRecord.impugnacion());
                data.setAmparoSentido(amparoUpdateRecord.sentidoAmparo());
                data.setAmparoSentidoImpugnacion(amparoUpdateRecord.sentidoImpugnacion());
                data.setAmparoQuejoso(amparoUpdateRecord.quejoso());
                data.setAmparoTribunalId(amparoUpdateRecord.tribunalId());
                data.setAmparoSalaId(amparoUpdateRecord.salaId());
                data.setAmparoTipo(amparoUpdateRecord.tipoAmparo());

                documentoRepository.save(documento);
        }

        /**
         * Genera el número de expediente penal basado en el tipo de causa, juzgado y
         * tipo de carpeta.
         *
         * @param tipo        El tipo de causa (CONTROL_JUDICIAL_PREVIO, JUICIO_ORAL,
         *                    etc.).
         * @param juzgado     El juzgado asociado al expediente.
         * @param tipoCarpeta El tipo de carpeta (puede afectar los folios del juzgado).
         * @return El número de expediente generado en formato específico o {@code null}
         *         si el tipo no es válido.
         */
        public String generateNumExpedientePenal(TipoCausa tipo, Juzgado juzgado, TipoCarpeta tipoCarpeta) {
                // Obtiene y valida los folios del juzgado para el año actual
                JuzgadoFolios juzgadoFolios = juzgadoService.checkYearJuzgadoFolios(
                                juzgadoService.getJuzgadoFolios(juzgado, tipoCarpeta));

                // Construye la parte base del número de expediente
                String baseNumExpediente = StringUtils.leftPad(juzgadoFolios.getValue().toString(), 6, '0')
                                + "/" + juzgadoFolios.getYear()
                                + "/" + tipo.getIniciales();

                // Obtiene el sufijo específico según el tipo de causa
                String suffix = getNumExpedienteSuffix(tipo, juzgado);

                // Si el sufijo es válido, incrementa el folio y retorna el número completo
                if (suffix != null) {
                        juzgadoService.increaseValueJuzgadoFolios(juzgadoFolios);
                        if (tipo.getIniciales().equals("EXT")) {
                                return tipo.getIniciales() + '/'
                                                + StringUtils.leftPad(juzgadoFolios.getValue().toString(), 6, '0')
                                                + "/" + juzgadoFolios.getYear() + suffix;
                        }
                        return baseNumExpediente + suffix;
                }
                return null; // Retorna null si no hay lógica para el tipo de causa
        }

        /**
         * Obtiene el sufijo del número de expediente según el tipo de causa y el
         * juzgado.
         *
         * @param tipo    El tipo de causa (CONTROL_JUDICIAL_PREVIO, JUICIO_ORAL, etc.).
         * @param juzgado El juzgado asociado al expediente.
         * @return El sufijo correspondiente al tipo de causa o {@code null} si no se
         *         encuentra definido.
         */
        private String getNumExpedienteSuffix(TipoCausa tipo, Juzgado juzgado) {
                switch (tipo) {
                        case CARPETAS_JUDICIALES:
                        case CONTROL_JUDICIAL_PREVIO:
                        case CONTROL_ACTOS_INVESTIGACION:
                        case EJECUCION:
                                // Los sufijos estándar que usan la nomenclatura del juzgado
                                return "/" + juzgado.getNomenclatura().toUpperCase();
                        case EXHORTO:
                                // Sufijo exclusivo para EXHORTO
                                return "/" + juzgado.getNomenclatura().toUpperCase();
                        case JUICIO_ORAL:
                                // Sufijo que incluye la región del distrito del juzgado
                                return "/" + juzgado.getSede().getDistrito().getRegion().toUpperCase();
                        default:
                                // Retorna null para tipos de causa no definidos
                                return null;
                }
        }

        @Transactional(readOnly = true)
        public Page<DocumentoBandejaDevueltos> getBandejaDevueltosOCP(String key, Pageable pageable) {
                key = (key != null) ? key.toLowerCase() : "";
                Persona persona = personaService.getAuditor();
                List<Juzgado> juzgados = persona.getOficialia().getJuzgados();

                Page<Movimiento> page = movimientoRepository.getBandejaDevueltos(
                                pageable,
                                juzgados,
                                EstadoCarpeta.DEVUELTO_A_OFICIALIA,
                                key,
                                EstadoCarpeta.DEVUELTO_A_OFICIALIA.name());

                List<DocumentoBandejaDevueltos> list = page.getContent()
                                .stream()
                                .map(movimiento -> {
                                        Carpeta carpeta = movimiento.getCarpeta();
                                        Documento documento = movimiento.getDocumento();
                                        String tipoEntrada = etiquetaService.renderEtiquetaRecepcion("nuevoNombre",
                                                        carpeta);
                                        Persona p = movimiento.getPersona();
                                        String nombre = p.getNombre() + " " + p.getApellidoPaterno() + " "
                                                        + ((p.getApellidoMaterno() != null) ? p.getApellidoMaterno()
                                                                        : "");
                                        return new DocumentoBandejaDevueltos(
                                                        carpeta.getId(),
                                                        documento != null ? documento.getId() : null,
                                                        carpeta.getFolio(),
                                                        carpeta.getExpediente(),
                                                        tipoEntrada,
                                                        nombre,
                                                        carpeta.getConcepto().getNombre(),
                                                        movimiento.getMotivo(),
                                                        movimiento.getFechaAsignacion(),
                                                        true,
                                                        carpeta.getPrioridad(),
                                                        carpeta.getHoras());
                                }).toList();

                return new PageImpl<>(list, pageable, page.getTotalElements());

        }

        public void devolverABandejas(DevolucionBandejasRecord devolucion) {
                Persona persona = personaService.getAuditor();
                Carpeta carpeta = carpetaRepository.findById(devolucion.carpetaId()).orElse(null);
                Documento documento = devolucion.documentoId() != null
                                ? documentoRepository.findById(devolucion.documentoId()).orElse(null)
                                : null;

                movimientoService.createMovimento(carpeta, documento, persona, devolucion.motivoDevolucion(),
                                devolucion.estado());

        }

        private void validarOficialia(Oficialia oficialia) {
                if (oficialia == null) {
                        throw new NotFoundException("La persona no está relacionada con ninguna oficialía",
                                        "persona.getOficialia()");
                }
        }

        private TipoJuicio getTipoJuicioById(Integer tipoJuicioId) {

                return tipoJuicioRepository.findById(tipoJuicioId)
                                .orElseThrow(() -> new NotFoundException(TIPO_JUICIO_NOT_FOUND,
                                                tipoJuicioId.toString()));
        }

        private List<Juzgado> getJuzgadosRelacionados(Integer oficialiaId, TipoJuicio tipoJuicio) {
                return juzgadoRepository.findJuzgadoByOficialiaIdAndTipoJuicio(oficialiaId, tipoJuicio.getId());
        }

        private Juzgado getJuzgadoDemanda(Juzgado juzgadoConexidad, List<Juzgado> juzgadosRelacionados,
                        TipoJuicio tipoJuicio) {

                if (juzgadoConexidad != null) {
                        if (!juzgadosRelacionados.contains(juzgadoConexidad)) {
                                throw new NotFoundException("El juzgado asignado no está relacionado con la oficialía",
                                                "juzgadoConexidad");
                        }
                        return juzgadoConexidad;
                } else {
                        Juzgado juzgadoDemanda = juzgadoService.getJuzgado(tipoJuicio, TipoCarpeta.DEMANDA,
                                        juzgadosRelacionados);
                        if (!juzgadosRelacionados.contains(juzgadoDemanda)) {
                                throw new NotFoundException("El juzgado asignado no está relacionado con la oficialía",
                                                "juzgadoPorJuicio");
                        }
                        return juzgadoDemanda;
                }
        }

        private void handleOralidadFamiliarFlow(TipoJuicio tipoJuicio, DocumentoSaveRecord documentoRecord,
                        Carpeta carpeta, Documento documento) {
                if ("FAMILIAR".contains(tipoJuicio.getMateria().getNombre().toUpperCase())
                                && tipoJuicio.getNombre().toUpperCase().contains("ORAL")) {
                        crearAudienciaOralidad(documentoRecord, carpeta, tipoJuicio);
                        if (documentoRecord.general().getTieneAbogado() == 0) {
                                sendEmailFamiliar(documentoRecord, documento, carpeta, tipoJuicio);
                        }
                }
        }

        private Carpeta crearCarpeta(TipoJuicio tipoJuicio, Juzgado juzgado, String folio, TipoCarpeta tipoCarpeta,
                        String expediente, Persona personaLogueada) {

                Carpeta carpeta = new Carpeta()
                                .setTipoJuicio(tipoJuicio)
                                .setJuzgado(juzgado)
                                .setFolio(folio)
                                .setTipoCarpeta(tipoCarpeta)
                                .setExpediente(expediente)
                                .setEstatus(EstadoCarpeta.CAPTURA)
                                .setSelloEstatus(SelloEstatus.VALIDO)
                                .setFechaAsignacion(LocalDateTime.now())
                                .setPersona(personaLogueada)
                                .setCu(getCu(juzgado, expediente));

                return carpetaRepository.save(carpeta);
        }

        public String getCu(Juzgado juzgado, String expediente) {
                String clave = (juzgado != null) ? Objects.toString(juzgado.getClaveJuzgado(), "") : "";
                String exp = (expediente != null) ? expediente.replace("/", "") : "";
                return clave + exp;
        }

        private Documento crearDocumento(Carpeta carpeta, DocumentoData documentoData, Persona persona) {
                Documento documento = new Documento()
                                .setCarpeta(carpeta)
                                .setData(documentoData)
                                .setFechaAsignacion(LocalDateTime.now())
                                .setPersona(persona);

                return documentoRepository.save(documento);
        }

        public Documento createPromocionMigracion(DetallePromSaveRecord promocion) {

                Persona persona = personaService.getAuditor();
                Concepto concepto = conceptoRepository.findByNombre("Adjuntar")
                                .orElseThrow(() -> new NotFoundException(CONCEPTO_NOT_FOUND, "Adjuntar"));

                DocumentoData docData = new DocumentoData()
                                .setTipoPromocion(promocion.tipoPromocion());

                Documento promocionNew = new Documento()
                                .setCarpeta(promocion.carpeta())
                                .setFolio(promocion.folio())
                                .setEstatus(EstadoCarpeta.INTEGRADO)
                                .setConcepto(concepto)
                                .setData(docData)
                                .setPersona(persona)
                                .setTipoDocumento(TipoDocumento.PROMOCION)
                                .setRuta(promocion.ruta())
                                .setMigrado(Migrado.SI);
                
                //Buscamos si esta promoción esta relacionada con un acuerdo
                Optional<Documento> acuerdo = documentoRepository.findByTipoDocumentoAndFolio(TipoDocumento.ACUERDO, promocion.acuerdo());

                if(acuerdo.isPresent()){
                        Documento documentoAcuerdo = acuerdo.get();
                        promocionNew.setAcuerdoRespuesta(documentoAcuerdo);
                }

                // promoción electronica:
                if (promocion.tipoPromocion().equals(TipoPromocion.CORREO_ELECTRONICO)) {

                }
                return documentoRepository.save(promocionNew);
        }

        @Transactional
        public Documento createAcuerdoMigracion(AcuerdosMigracionSaveRecord acuerdo) {
                // Creamos información de los rubros en documentoData:
                DocumentoData docData = new DocumentoData()
                                .setRubros(acuerdo.rubros());

                // Creamos información del documento.
                Documento documento = new Documento()
                                .setCarpeta(acuerdo.carpeta())
                                .setTipoDocumento(TipoDocumento.ACUERDO)
                                .setEstatus(EstadoCarpeta.PUBLICADO)
                                .setData(docData)
                                .setFolio(acuerdo.folio())
                                .setRuta(acuerdo.ruta());

                documento = documentoRepository.save(documento);

                DocumentoDetalle documentoDetalle = new DocumentoDetalle()
                                .setTipoAcuerdo(acuerdo.tipoAcuerdo())
                                .setFechaResolucion(acuerdo.fechaResolucion())
                                .setEtapaProcesal("")
                                .setResumen("")
                                .setFechaPublicacion(acuerdo.fechaPublicacion())
                                .setDocumento(documento);

                documentoDetalleRepository.save(documentoDetalle);

                return documento;

        }

        @Transactional
        public Documento createSentenciaMigracion(SentenciaMigracionSaveRecord sentencia) {
                // Creamos documento:
                Documento documentoSentencia = new Documento()
                                .setCarpeta(sentencia.carpeta())
                                .setTipoDocumento(TipoDocumento.SENTENCIA)
                                .setEstatus(EstadoCarpeta.PUBLICADO)
                                .setFolio(sentencia.folio())
                                .setRuta(sentencia.ruta());

                documentoSentencia = documentoRepository.save(documentoSentencia);

                DocumentoDetalle documentoDetalle = new DocumentoDetalle()
                                .setFechaResolucion(sentencia.fechaResolucion())
                                .setEtapaProcesal("")
                                .setTipoSentencia(sentencia.tipoSentencia())
                                .setTipoResolucion(sentencia.tipoResolucion())
                                .setExtractoSentencia("")
                                .setDocumento(documentoSentencia)
                                .setFechaPublicacion(sentencia.fechaPublicacion());

                documentoDetalleRepository.save(documentoDetalle);

                return documentoSentencia;
        }
}
