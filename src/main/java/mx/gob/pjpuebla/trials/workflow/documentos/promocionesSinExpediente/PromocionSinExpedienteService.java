package mx.gob.pjpuebla.trials.workflow.documentos.promocionesSinExpediente;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.migracion.readers.entradas.EntradasMigracion;
import mx.gob.pjpuebla.migracion.readers.entradas.EntradasMigracionReader;
import mx.gob.pjpuebla.migracion.usecases.MigracionExpedienteResult;
import mx.gob.pjpuebla.migracion.usecases.MigrarExpedienteUseCase;
import mx.gob.pjpuebla.migracion.utils.UtilsMigracion;
import mx.gob.pjpuebla.trials.core.conceptos.Concepto;
import mx.gob.pjpuebla.trials.core.conceptos.ConceptoService;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoService;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.error.ApiResponse;
import mx.gob.pjpuebla.trials.error.ApiResponseFactory;
import mx.gob.pjpuebla.trials.error.ConflictException;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.Utils;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.Migrado;
import mx.gob.pjpuebla.trials.util.enums.PromocionSinExpedienteEnum;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.util.enums.TipoPromocion;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaService;
import mx.gob.pjpuebla.trials.workflow.carpeta.carpetadetalle.CarpetaDetalle;
import mx.gob.pjpuebla.trials.workflow.carpeta.carpetadetalle.CarpetaDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoService;
import mx.gob.pjpuebla.trials.workflow.documentos.promocionesSinExpediente.records.PromocionSinExpedienteFiltrosRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.promocionesSinExpediente.records.PromocionSinExpedientePageRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.promocionesSinExpediente.records.PromocionSinExpedienteRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.promocionesSinExpediente.records.PromocionSinExpedienteSaveRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoPromocionResponseRecord;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoService;

@RequiredArgsConstructor
@Service
@Slf4j
public class PromocionSinExpedienteService {

        private final DocumentoService documentoService;
        private final JuzgadoService juzgadoService;
        private final PromocionSinExpedienteRepository promocionSinExpedienteRepository;
        private final EntradasMigracionReader entradasMigracionReader;
        private final MigrarExpedienteUseCase migrarExpedienteUseCase;
        private final MovimientoService movimientoService;
        private final PersonaService personaService;
        private final CarpetaService carpetaService;
        private final ConceptoService conceptoService;
        private final CarpetaDetalleRepository carpetaDetalleRepository;
        private static final String CONCEPTO_NOT_FOUND = "Concepto no encontrado";

        @Transactional
        public Page<PromocionSinExpedientePageRecord> getAll(
                        PromocionSinExpedienteFiltrosRecord filtros,
                        Pageable pageable) {

                Persona persona = personaService.getAuditor();
                List<Juzgado> juzgados = persona.getJuzgado() != null ? List.of(persona.getJuzgado())
                                : persona.getOficialia().getJuzgados();

                return promocionSinExpedienteRepository.getAll(pageable, juzgados);
        }

        @Transactional
        public ApiResponse<PromocionSinExpedienteSaveRecord> save(PromocionSinExpedienteRecord promocion) {

                Juzgado juzgado = juzgadoService.findJuzgadoById(promocion.juzgadoId());
                PromocionSinExpediente promocionSinExp = new PromocionSinExpediente()
                                .setFolio(documentoService.getFolio("P"))
                                .setExpediente(promocion.expediente() + "/" + promocion.year())
                                .setJuzgado(juzgado)
                                .setTipoJuicio(null)
                                .setAnexos(String.join(", ", promocion.anexos()))
                                .setEstado(PromocionSinExpedienteEnum.REGISTRADO)
                                .setTipoRegistro(null)
                                .setTipoPromocion(promocion.tipoPromocion())
                                .setPrioridad(promocion.prioridad());

                promocionSinExp = promocionSinExpedienteRepository.save(promocionSinExp);
                PromocionSinExpedienteSaveRecord saveRecord = new PromocionSinExpedienteSaveRecord(
                                promocionSinExp.getId(),
                                "La promoción ha sido registrada exitosamente");
                return ApiResponseFactory.success("La promoción ha sido registrada exitosamente", saveRecord);
        }

        public PromocionSinExpediente findById(Integer id) {
                return promocionSinExpedienteRepository.findById(id).orElse(null);
        }

        @Transactional
        public DocumentoPromocionResponseRecord asociarExpediente(Integer idPromocion) {
                Persona persona = personaService.getAuditor();

                // Recuperamos registro de promocion sin expediente y verificamos el

                PromocionSinExpediente promocion = promocionSinExpedienteRepository.findById(idPromocion)
                                .orElseThrow(() -> new NotFoundException(
                                                "No fue posible encontrar el registro de promoción sin expediente",
                                                idPromocion.toString()));

                if (promocion.getEstado().equals(PromocionSinExpedienteEnum.PROMOCION_REGISTRADA)) {
                        throw new IllegalStateException("La promoción ya cuenta con un expediente asociado.");
                }

                // Verificamos si NO ya se ha migrado o se encuentra en el SECGJ Java
                Carpeta carpetaExistente = carpetaService.findByExpedienteAndJuzgado(promocion.getExpediente(),
                                promocion.getJuzgado());

                if (carpetaExistente != null) {
                        // Si ya se ha migrado la carpeta anteriormente, solo registramos la promoción

                        promocion.setEstado(PromocionSinExpedienteEnum.PROMOCION_REGISTRADA);
                        promocion.setTipoJuicio(carpetaExistente.getTipoJuicio());
                        promocion.setTipoRegistro("Carpeta existente");
                        promocion.setCarpeta(carpetaExistente);
                        Migrado migrado = carpetaExistente.getMigrado();
                        return registraPromocion(promocion.getTipoPromocion(), carpetaExistente, promocion.getFolio(),
                                        persona, parseAnexos(promocion.getAnexos()), promocion, migrado);

                }

                // Paso 3 buscamos el expediente en la base de datos del SECGJ PHP:
                String expediente = promocion.getExpediente().split("/")[0];
                Integer year = Integer.parseInt(promocion.getExpediente().split("/")[1]);
                Juzgado juzgado = promocion.getJuzgado();

                Optional<EntradasMigracion> entrada = entradasMigracionReader.buscarEntradasPorFiltrosProm(
                                expediente, year, juzgado.getClaveJuzgado());

                if (entrada.isPresent()) {

                        try {
                                log.info("Expediente encontrado en el SECGJ PHP, se procederá a migrar el expediente.");
                                // SI se encuentra el expediente en el SECGJ PHP, lo migramos

                                MigracionExpedienteResult expedienteMigrado = migrarExpedienteUseCase
                                                .migrarExpedienteCompleto(
                                                                promocion.getExpediente().split("/")[0],
                                                                year,
                                                                juzgado.getClaveJuzgado());

                                // Actualizamos estatus de la promoción sin expediente
                                promocion.setEstado(PromocionSinExpedienteEnum.PROMOCION_REGISTRADA);
                                promocion.setTipoJuicio(expedienteMigrado.carpeta().getTipoJuicio());
                                promocion.setTipoRegistro("Migración");

                                // Ahora una vez que el expediente principal esta agregado creamos el registro
                                // de la promoción asociada al expediente principal:
                                List<String> anexos = parseAnexos(promocion.getAnexos());

                                return registraPromocion(promocion.getTipoPromocion(), expedienteMigrado.carpeta(),
                                                promocion.getFolio(), persona, anexos, promocion, Migrado.SI);
                        } catch (Exception e) {
                                log.error("Error al migrar el expediente", e);
                                throw new RuntimeException("Error al migrar el expediente", e);
                        }

                } else {
                        log.info("No se encontró el expediente en el SECGJ PHP, se procederá a crear expediente antiguo.");
                        // Creamos registro de carpeta:
                        TipoJuicio tipoJuicioTradicional = promocion.getJuzgado().getTipoJuicios()
                                        .stream()
                                        .filter(tipoJuicio -> tipoJuicio != null && tipoJuicio.getTipoSistema() != null
                                                        && "Tradicional".equals(
                                                                        tipoJuicio.getTipoSistema().getNombre()))
                                        .findFirst()
                                        .orElseThrow(() -> new NotFoundException(
                                                        "Tipo Juicio 'Tradicional' no encontrado para la persona logueada",
                                                        String.valueOf(persona.getId())));

                        Persona oficialMayor = personaService.getOficialMayor(juzgado);
                        if (oficialMayor == null) {
                                throw new ConflictException(
                                                "No existe un oficial mayor en el juzgado, imposible crear la promoción.");
                        }

                        Carpeta carpeta = new Carpeta()
                                        .setJuzgado(juzgado)
                                        .setTipoJuicio(tipoJuicioTradicional)
                                        .setExpediente(promocion.getExpediente())
                                        .setFolio(promocion.getFolio())
                                        .setTipoCarpeta(TipoCarpeta.DEMANDA)
                                        .setEstatus(EstadoCarpeta.ASIGNADO)
                                        .setFechaAsignacion(LocalDateTime.now())
                                        .setSelloEstatus(SelloEstatus.VALIDO)
                                        .setPersona(oficialMayor);

                        Concepto concepto = conceptoService.findByNombre("Distribución")
                                        .orElseThrow(() -> new NotFoundException(CONCEPTO_NOT_FOUND, "Distribución"));
                        carpeta.setConcepto(concepto);
                        carpeta = carpetaService.save(carpeta);
                        carpetaDetalleRepository.save(new CarpetaDetalle().setCarpeta(carpeta));

                        // Actualizamos estatus de la promoción sin expediente
                        promocion.setEstado(PromocionSinExpedienteEnum.PROMOCION_REGISTRADA);
                        promocion.setTipoJuicio(tipoJuicioTradicional);
                        promocion.setTipoRegistro("Expediente antiguo");

                        // Ahora una vez que el expediente principal esta agregado creamos el registro
                        // de la promoción asociada al expediente principal:
                        List<String> anexos = parseAnexos(promocion.getAnexos());

                        return registraPromocion(promocion.getTipoPromocion(), carpeta, promocion.getFolio(), persona,
                                        anexos, promocion, Migrado.NO);
                }
        }

        private DocumentoPromocionResponseRecord registraPromocion(TipoPromocion tipoPromocion, Carpeta carpeta,
                        String folio, Persona persona, List<String> anexos, PromocionSinExpediente promocion,
                        Migrado migrado) {
                DocumentoData docData = new DocumentoData().setTipoPromocion(tipoPromocion);

                Documento documento = new Documento()
                                .setCarpeta(carpeta)
                                .setTipoDocumento(TipoDocumento.PROMOCION)
                                .setData(docData)
                                .setFolio(folio)
                                .setEstatus(EstadoCarpeta.CAPTURA)
                                .setPersona(persona)
                                .setMigrado(migrado);

                documento = documentoService.save(documento);

                // Completamos los datos de la promoción:
                promocion.setCarpeta(carpeta);
                promocion.setDocumento(documento);
                promocionSinExpedienteRepository.save(promocion);

                // Agregamos los anexos
                if (anexos != null && !anexos.isEmpty()) {
                        documentoService.addAnexos(anexos, documento);
                }

                // Agregamos movimiento del documento para que aparezca en la bandeja de captura
                movimientoService.createMovimentoWithConcepto(null, documento, documento.getPersona(), null,
                                documento.getEstatus().name(), documento.getConcepto());

                return new DocumentoPromocionResponseRecord(documento.getId(), documento.getFolio(),
                                documento.getTipoDocumento());
        }

        private List<String> parseAnexos(String anexos) {
                if (anexos == null || anexos.trim().isEmpty()) {
                        return java.util.Collections.emptyList();
                }
                String anexosNormalizados = UtilsMigracion.normalizeSpaces(anexos);
                return Pattern.compile("\\s*,\\s*")
                                .splitAsStream(anexosNormalizados)
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .distinct()
                                .toList();
        }
}
