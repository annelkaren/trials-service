package mx.gob.pjpuebla.trials.workflow.etiquetas;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EtiquetaService {

    private final EtiquetaRepository etiquetaRepository;

    public List<EtiquetaRecordItem> getAllByTipoJuicioId(Integer id) {
        List<Etiqueta> originalList = this.etiquetaRepository.findByTipoJuicioId(id);
        return originalList.stream()
                .map(tje -> new EtiquetaRecordItem(tje.getNombre(), tje.getValue()))
                .toList();
    }

    public String renderEtiquetaRecepcion(String nombre, Documento documento) {
        if (documento.getTipoDocumento() != null) {
            return documento.getTipoDocumento().name();
        }
        if (documento.getCarpeta().getTipoCarpeta().equals(TipoCarpeta.EXHORTO) || documento.getCarpeta().getTipoCarpeta().equals(TipoCarpeta.APELACION)) {
            return documento.getCarpeta().getTipoCarpeta().name();
        } else {
            return this.etiquetaRepository.findByTipoJuicioIdAndNombre(
                100, nombre).getValue().toUpperCase();
        }
    }

    public String renderEtiquetaRecepcion(String nombre, Carpeta carpeta) {
        if (carpeta.getTipoCarpeta().equals(TipoCarpeta.EXHORTO) || carpeta.getTipoCarpeta().equals(TipoCarpeta.APELACION)) {
            return carpeta.getTipoCarpeta().name();
        } else {
            return this.etiquetaRepository.findByTipoJuicioIdAndNombre(
                    100, nombre).getValue().toUpperCase();
        }
    }
}
