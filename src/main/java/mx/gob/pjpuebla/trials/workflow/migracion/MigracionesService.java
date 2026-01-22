package mx.gob.pjpuebla.trials.workflow.migracion;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.migracion.usecases.MigrarDocumentosUseCase;
import mx.gob.pjpuebla.trials.core.conceptos.Concepto;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.error.ApiResponse;
import mx.gob.pjpuebla.trials.error.InternalServerError;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.EstadoMigracion;
import mx.gob.pjpuebla.trials.workflow.bandejas.BandejasRepository;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.BandejaMigracionFilter;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.BandejaMigracionResponse;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaService;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoService;
import mx.gob.pjpuebla.trials.workflow.movimientos.Movimiento;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoService;
import mx.gob.pjpuebla.trials.error.NotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class MigracionesService {

        private final MigracionesRepository migracionesRepository;
        private final BandejasRepository bandejasRepository;
        private final PersonaService personaService;
        private final MovimientoService movimientoService;
        private final CarpetaService carpetaService;
        private final DocumentoService documentoService;

        private final MigrarDocumentosUseCase migrarDocumentosUseCase;

        @Transactional(readOnly = true)
        public Page<BandejaMigracionResponse> listar(BandejaMigracionFilter filter, Pageable pageable) {

                Persona persona = personaService.getAuditor();
                List<Juzgado> juzgados = persona.getJuzgado() != null ? List.of(persona.getJuzgado())
                                : persona.getOficialia().getJuzgados();

                Pageable sortedPageable = mapSort(pageable);

                return bandejasRepository.findByJuzgadoIn(
                                juzgados,
                                filter.key(),
                                filter.expediente(),
                                null,
                                filter.estado(),
                                sortedPageable);
        }

        private Pageable mapSort(Pageable pageable) {
                if (pageable.getSort().isUnsorted()) {
                        return pageable;
                }

                List<Sort.Order> orders = pageable.getSort().stream()
                                .map(order -> {
                                        String property = order.getProperty();
                                        switch (property) {
                                                case "expediente":
                                                        return new Sort.Order(
                                                                        order.getDirection(), "m.carpeta.expediente");
                                                case "migradoPor":
                                                        return new Sort.Order(
                                                                        order.getDirection(), "p.nombre");
                                                case "estatus":
                                                case "estadoMigracion":
                                                        return new Sort.Order(
                                                                        order.getDirection(), "m.estatus");
                                                default:
                                                        return order;
                                        }
                                })
                                .toList();

                return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(orders));
        }

        @Transactional
        public ApiResponse<String> turnarExpedienteMigrado(Integer migracionId, Long personaId) {
                // 1) Cargar migración
                Migraciones migracion = migracionesRepository.findById(migracionId)
                                .orElseThrow(() -> new NotFoundException(
                                                "No fue posible encontrar el registro de migración",
                                                migracionId.toString()));

                // 2) Validar estado de la migración

                // 3) Cargar carpeta y dependencias
                Carpeta carpeta = migracion.getCarpeta();
                Concepto concepto = carpeta.getConcepto();
                Persona personaAsignada = personaId == 0 ? personaService.getAuditor()
                                : personaService.findPersonaById(personaId).orElse(null);

                if (personaAsignada == null) {
                        throw new NotFoundException("No existe la persona indicada.", personaId.toString());
                }

                // 4) Idempotencia: si ya está asignada a esa persona, responde OK
                if (carpeta.getEstatus() == EstadoCarpeta.ASIGNADO) {
                        return new ApiResponse<>(true, "El expediente ya estaba turnado.", "SUCCESS_ALREADY_ASSIGNED",
                                        200, "",
                                        LocalDateTime.now());
                }

                // 5) Actualizar carpeta
                carpeta.setEstatus(EstadoCarpeta.ASIGNADO);
                carpeta.setPersona(personaAsignada);
                carpetaService.save(carpeta);

                // 6) verificar si hay piezas relacionadas con esta carpeta:
                List<Carpeta> piezas = carpetaService.findPiezasByCarpeta(carpeta);

                piezas.forEach(pieza -> {
                        pieza.setPersona(personaAsignada);
                });
                carpetaService.saveAll(piezas);

                // 7 verificamos si hay documentos y los asignamos a la persona:
                List<Documento> documentos = documentoService.findDocumentosByCarpetaId(carpeta.getId());
                documentos.forEach(d -> {
                        d.setPersona(personaAsignada);
                });
                documentoService.saveAll(documentos);

                // 8) Crear movimiento
                Integer dias = concepto.getDias();
                String duration = (dias != null ? dias + "d" : null);

                Movimiento movimiento = movimientoService.createMovimentoTurnado(
                                carpeta,
                                null,
                                personaAsignada,
                                null,
                                EstadoCarpeta.ASIGNADO.name(),
                                concepto.getNombre(),
                                null,
                                duration);

                if (movimiento == null) {
                        throw new InternalServerError("No se pudo registrar el movimiento de turnado.");
                }

                // 7) Actualizar estatus de la migración
                migracion.setEstatus(EstadoMigracion.EXPEDIENTE_TURNADO);
                migracion.setPersonaTurnado(personaAsignada);
                migracion.setObservaciones(
                                "Se ha turnado el expediente a "
                                                + personaService.getNamePersona(personaAsignada.getUsuario()));
                migracionesRepository.save(migracion);

                return new ApiResponse<>(true, "El expediente ha sido turnado con éxito.", "SUCCESS", 201, "",
                                LocalDateTime.now());
        }

        @Transactional
        public ApiResponse<String> migrarDocumentosExpediente(Integer migracionId) {
                Migraciones migracion = migracionesRepository.findById(migracionId)
                                .orElseThrow(() -> new NotFoundException(
                                                "No fue posible encontrar el registro de migración",
                                                migracionId.toString()));

                Carpeta carpeta = migracion.getCarpeta();
                String expediente = carpeta.getExpediente().split("/")[0];
                Integer year = Integer.parseInt(carpeta.getExpediente().split("/")[1]);
                String claveJuzgado = migracion.getJuzgado().getClaveJuzgado();

                EstadoMigracion estadoMigracion = migrarDocumentosUseCase.migrarDocumentosExpediente(expediente, year,
                                claveJuzgado, migracionId);

                if (!estadoMigracion.equals(EstadoMigracion.MIGRADO_COMPLETADO)) {
                        throw new InternalServerError("No se pudo migrar el expediente.");
                }

                return new ApiResponse<>(true, "Se ha migrado el expediente completo exitosamente.", "SUCCESS", 200, "",
                                LocalDateTime.now());

        }

}
