package mx.gob.pjpuebla.trials.workflow.audienciaspruebas;

import mx.gob.pjpuebla.trials.util.enums.DesistimientoAdmision;
import mx.gob.pjpuebla.trials.workflow.audiencias.AudienciaRepository;
import mx.gob.pjpuebla.trials.workflow.audienciaspruebas.record.DetallesPruebasRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.materiapericial.MateriaPericial;
import mx.gob.pjpuebla.trials.core.materiapericial.MateriaPericialRepository;
import mx.gob.pjpuebla.trials.core.tipoprueba.TipoPruebas;
import mx.gob.pjpuebla.trials.core.tipoprueba.TipoPruebasRepository;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.audiencias.Audiencia;
import mx.gob.pjpuebla.trials.workflow.audiencias.AudienciaService;
import mx.gob.pjpuebla.trials.workflow.audienciaspruebas.record.AudienciaPruebaRequestRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.DigitalizacionService;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DigitalizacionRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RequiredArgsConstructor
@Service
public class AudienciaPruebasService {
    
    @Autowired
    private final TipoPruebasRepository tipoPruebaRepository;
    private final MateriaPericialRepository materiaPericialRepository;
    private final AudienciaPruebasRepository audienciaPruebaRepository;
    private final DigitalizacionService digitalizacionService;
    private final DocumentoRepository documentoRepository;
    private final AudienciaService audienciaService;

    @Value("${app.root-folder}")
    private String rootFolder; // Ruta raíz de la digitalización
    private String basePath; // Ruta base para la digitalización


    @Transactional
    public AudienciaPruebas createAudienciaPrueba(AudienciaPruebaRequestRecord audienciaPruebaRequest, MultipartFile file) {
        // Asignación de Audiencia temporal hasta que se defina a que audiencia corresponde
        Audiencia audiencia = audienciaService.obtenerUltimaAudienciaDesahogada();

        TipoPruebas tipoPrueba = tipoPruebaRepository.findById(audienciaPruebaRequest.tipoPruebaId())
                .orElseThrow(() -> new EntityNotFoundException("TipoPrueba no encontrado para ID: " + audienciaPruebaRequest.tipoPruebaId()));

        MateriaPericial materiaPericial = null;
        if (audienciaPruebaRequest.materiaPericialId() != null) {
            materiaPericial = materiaPericialRepository.findById(audienciaPruebaRequest.materiaPericialId())
                    .orElseThrow(() -> new EntityNotFoundException("MateriaPericial no encontrada para ID: " + audienciaPruebaRequest.materiaPericialId()));
        }

        AudienciaPruebas audienciaPrueba = new AudienciaPruebas()
                .setAudiencia(audiencia)
                .setTipoPrueba(tipoPrueba)
                .setMateriaPericial(materiaPericial)
                .setNombreDeclarante(audienciaPruebaRequest.nombreDeclarante())
                .setDescripcionInstrumento(audienciaPruebaRequest.descripcionInstrumento())
                .setAbsolvente(audienciaPruebaRequest.absolvente())
                .setDescripcionDocumento(audienciaPruebaRequest.descripcionDocumento())
                .setObjeto(audienciaPruebaRequest.objeto())
                .setUrlDocumento(audienciaPruebaRequest.urlDocumento());

        if (file != null && !file.isEmpty()) {
            Documento documento = new Documento();
            documento.setCarpeta(audiencia.getCarpeta());
            documento.setTipoDocumento(TipoDocumento.PRUEBA_AUDIENCIA);
            documentoRepository.save(documento);

            DigitalizacionRecord digitalizacionRecord = digitalizacionService.guardarArchivo(file, documento.getId());
            audienciaPrueba.setUrlDocumento(digitalizacionRecord.rutaArchivo());
        }

        audienciaPrueba = audienciaPruebaRepository.save(audienciaPrueba);
        return audienciaPrueba;
    }

    public  Page<DetallesPruebasRecord> getAudienciaPruebasByAudiencia(Integer idAudiencia, Pageable pageable){
        return  audienciaPruebaRepository.getAllByAudiencia(idAudiencia,pageable);
    }

    public byte[] getAudienciaPurebasDocumento(Long audienciaId) throws IOException {
        this.basePath = this.rootFolder + "/digitalizacion/";

        AudienciaPruebas audienciaPruebas = audienciaPruebaRepository.findById(audienciaId).orElse(null);
        assert audienciaPruebas != null;
        Path rutaArchivo =   Paths.get(audienciaPruebas.getUrlDocumento());

        if (Files.exists(rutaArchivo)) {
            return Files.readAllBytes(rutaArchivo);
        } else {
            throw new IOException("El archivo relacionado con la audiencia " + audienciaPruebas.getId() + " no existe en el directorio");
        }
    }
    public void pachDesistimientoAdmision(String desAdm, Integer id) {
        AudienciaPruebas audienciaPruebas = audienciaPruebaRepository.findById(id.longValue()).orElseThrow(() -> new RuntimeException("Audiencia no encontrada"));
        DesistimientoAdmision desistimientoAdmision = DesistimientoAdmision.valueOf(desAdm.split("=")[0]);

        audienciaPruebas.setDesistimientoAdmision(desistimientoAdmision);
        audienciaPruebaRepository.save(audienciaPruebas);
    }

}
