package mx.gob.pjpuebla.trials.workflow.carpeta;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.*;
import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoBandejaRecepcionRecord;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.ApelacionRecordResponse;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.BandejaRecepcionRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.CarpetaResponseRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoService;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

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

    public CarpetaResponseRecord getCarpetaResponseByNumExpYearJuzgado(String expediente, Integer juzgadoId) {
        Carpeta carpeta = carpetaRepository.findByExpedienteAndJuzgadoId(expediente, juzgadoId)
                .orElseThrow(() -> new NotFoundException("Carpeta no encontrada", expediente + " - " + juzgadoId));
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

    public DocumentoRecord actualizarInformacionAnexos(List<AnexoBandejaRecepcionRecord> anexos,
            Integer documentoId) {
                Documento documento = validacionBandejaRecepcion(documentoId);

                List<String> anexosFaltantes = anexos.stream()
                    .filter(anexo -> anexo.estado() == EstadoAnexo.NORECIBIDO)
                    .map(AnexoBandejaRecepcionRecord::nombre)
                    .collect(Collectors.toList());

                //actualizamos los anexos.
                for (AnexoBandejaRecepcionRecord anexo : anexos) {
                    Anexo anexoTemp = anexoRepository.findById(anexo.id()).orElseThrow(() -> new NotFoundException("No se encontró el anexo con id: " + anexo.id(), "anexoId"));
                    anexoTemp.setEstado(anexo.estado());
                    anexoRepository.save(anexoTemp);
                }

                setObservacionesAnexos(documento, anexosFaltantes);

                //actualizamos el estatus en carpeta o documento dependiendo de si es demanda, exhorto o promoción.
                if(documento.getTipoDocumento() == TipoDocumento.PROMOCION){    
                    documento.setEstatus(EstadoCarpeta.ASIGNADO);
                }

                if(documento.getCarpeta() != null && (documento.getCarpeta().getTipoCarpeta() == TipoCarpeta.DEMANDA || documento.getCarpeta().getTipoCarpeta() == TipoCarpeta.EXHORTO) ){
                    documento.getCarpeta().setEstatus(EstadoCarpeta.ASIGNADO);
                }

                documentoRepository.save(documento);

        return new DocumentoRecord(documento.getId(), documento.getCarpeta().getFolio(), documento.getCarpeta().getTipoCarpeta());
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

        movimientoService.createMovimento(documento.getCarpeta(), documento, personaService.getAuditor(), motivo);
    }


}
