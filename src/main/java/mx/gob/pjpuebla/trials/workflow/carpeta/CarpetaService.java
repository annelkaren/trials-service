package mx.gob.pjpuebla.trials.workflow.carpeta;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.*;
import mx.gob.pjpuebla.trials.util.enums.carpeta.*;
import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoBandejaRecepcionRecord;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.ApelacionRecordResponse;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.BandejaRecepcionRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.CarpetaCatalogoRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.CarpetaResponseRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoRecepcionMovimientosRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoRepository;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoService;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    private final MovimientoRepository movimientoRepository;

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

        Carpeta carpeta = carpetaRepository.findByExpedienteAndJuzgadoId(expediente, finalJuzgadoId)
                .orElseThrow(() -> new NotFoundException("Carpeta no encontrada", expediente + " - " + finalJuzgadoId));
        String actor = getNombrePersonaByIdAndParte(carpeta.getId(), "Actor");
        String demandado = getNombrePersonaByIdAndParte(carpeta.getId(), "Demandado");
        return new CarpetaResponseRecord(carpeta.getId(), actor, demandado);
    }

    protected String getNombrePersonaByIdAndParte(Integer id, String parte) {
        List<Rol> rol = List.of(Rol.PRINCIPAL);
        PersonaDocumentoRecord persona = personaDocumentoRepository.findPersonaAndTipoParteByCarpetaId(id, parte, rol);

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
            throw new NotFoundException("No se encontró la carpeta con el documentoId: " + documentoId, "documentoId");
        }

        // Obtener y devolver los anexos de la bandeja de recepción
        List<AnexoBandejaRecepcionRecord> anexos = carpetaRepository.findAnexosByDocumentoId(documento.getId());
        return new BandejaRecepcionRecord(
                documento.getId(),
                bandeja.folio(),
                bandeja.expediente(),
                bandeja.tipo(),
                bandeja.rutaDigitalizacion(),
                anexos);
    }

    public DocumentoRecord actualizarInformacionAnexos(
            DocumentoRecepcionMovimientosRecord docRecepcionMovimientosRecord,
            Integer documentoId
    ) {
        Documento documento = validacionBandejaRecepcion(documentoId);

        List<String> anexosFaltantes = docRecepcionMovimientosRecord.anexos().stream()
                .filter(anexo -> anexo.estado() == EstadoAnexo.NORECIBIDO)
                .map(AnexoBandejaRecepcionRecord::nombre)
                .toList();

        // actualizamos los anexos.
        for (AnexoBandejaRecepcionRecord anexo : docRecepcionMovimientosRecord.anexos()) {
            Anexo anexoTemp = anexoRepository.findById(anexo.id()).orElseThrow(
                    () -> new NotFoundException("No se encontró el anexo con id: " + anexo.id(), "anexoId"));
            anexoTemp.setEstado(anexo.estado());
            anexoRepository.save(anexoTemp);
        }

        setObservacionesAnexos(documento, anexosFaltantes);

        // actualizamos el estatus en carpeta o documento dependiendo de si es demanda,
        // exhorto o promoción.
        if (documento.getTipoDocumento() == null && documento.getCarpeta() != null && (
                documento.getCarpeta().getTipoCarpeta() == TipoCarpeta.DEMANDA
                || documento.getCarpeta().getTipoCarpeta() == TipoCarpeta.EXHORTO)
        )
            documento.getCarpeta().setEstatus(EstadoCarpeta.ASIGNADO);
        else if (documento.getTipoDocumento() != null && (documento.getTipoDocumento() == TipoDocumento.PROMOCION))
            documento.setEstatus(EstadoCarpeta.ASIGNADO);

        documento = documentoRepository.save(documento);

        //Crear movimiento
        movimientoService.createMovimentoWithObservaciones(
                (documento.getTipoDocumento() == null) ? documento.getCarpeta() : null,
                (documento.getTipoDocumento() == null) ? null : documento,
                EstadoCarpeta.ASIGNADO.name(),
                docRecepcionMovimientosRecord.observaciones(),
                docRecepcionMovimientosRecord.recomendaciones()
        );

        return new DocumentoRecord(documento.getId(), documento.getCarpeta().getFolio(),
                documento.getCarpeta().getTipoCarpeta());
    }

    public Documento validacionBandejaRecepcion(Integer documentoId) {

        // Buscar y validar la existencia de la persona y el documento
        Persona persona = personaService.getAuditor();

        Documento documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new NotFoundException(
                        "No se encontró el documento asociado al documentoId: " + documentoId, "documentoId"));

        // Verificar permisos de acceso al juzgado
        Juzgado juzgado = persona.getJuzgado();
        if (!juzgado.getId().equals(documento.getCarpeta().getJuzgado().getId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "No tiene permiso para visualizar esta información");
        }

        return documento;

    }

    public void setObservacionesAnexos(Documento documento, List<String> anexos) {
        if (anexos.isEmpty()) {
            return;
        }
        String concatenatedAnexos = String.join(", ", anexos);
        String motivo = "Hacen falta los siguientes anexos: " + concatenatedAnexos + ". Por favor validar.";
        movimientoService.createMovimento((documento.getTipoDocumento() == null ? documento.getCarpeta() : null), (documento.getTipoDocumento() == null) ? null : documento, personaService.getAuditor(), motivo);
    }

    public List<CarpetaCatalogoRecord> getCatalogoList(String catalogo) {
        return switch (catalogo) {
            case "catalogoCondicionMigratoria" -> Arrays.stream(CatalogoCondicionMigratoria.values())
                    .map(e -> new CarpetaCatalogoRecord(e.name(), e.getEtiqueta()))
                    .toList();
            case "catalogoDeterminacionJurisdiccional" -> Arrays.stream(CatalogoDeterminacionJurisdiccional.values())
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
            default -> Collections.emptyList();
        };
    }


}
