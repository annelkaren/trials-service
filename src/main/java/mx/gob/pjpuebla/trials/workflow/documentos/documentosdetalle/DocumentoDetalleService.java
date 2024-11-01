package mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import java.nio.file.Path;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.records.DocumentoDetalleRecord;
import java.time.LocalDate;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Transactional
@Slf4j
@RequiredArgsConstructor
@Service
public class DocumentoDetalleService {
    private final DocumentoDetalleRepository documentoDetalleRepository;
    private final DocumentoRepository documentoRepository;
    private final PersonaService personaService;

    @Value("${app.root-folder}")
    private String rootFolder;

    public Integer digitalizacionAcuse(DocumentoDetalleRecord documento) {
        DocumentoDetalle docDetalle = documentoDetalleRepository.findByDocumentoId(documento.documentoId())
                .orElse(null);
        Documento doc = documentoRepository.findById(documento.documentoId()).orElse(null);

        // creamos ruta para guardar el documento
        // TODO: ACOPLARLO CON LA FUNCION DE DIGITALIZACIÓN Y SACAR LA LOGICA DE
        // CREACION DE CARPETA DE AQUI.
        if (doc != null) {
            String year = obtenerYear(doc);
            String juzgado = doc.getCarpeta() == null ? personaService.getAuditor().getJuzgado().getNombre()
                    : doc.getCarpeta().getJuzgado().getNombre();
            juzgado = juzgado.replaceAll(" ", "");

            Path rootPath = Paths.get(rootFolder, "digitalizacion", year, juzgado).resolve("oficios");

            try {
                Files.createDirectories(rootPath);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }

            // Guardar el archivo y manejar posibles excepciones
            try {
                Files.write(rootPath.resolve(UUID.randomUUID() + "_acuse.pdf"), documento.file().getBytes());
            } catch (IOException e) {

                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Error al guardar el archivo en el servidor", e);
            }

            if (docDetalle != null) {
                docDetalle.setRuta(documento.file().getOriginalFilename());
                docDetalle.setEstado(documento.estado());
                docDetalle.setComentario(documento.comentario());
                docDetalle.setFechaEntrega(documento.fechaEntrega());
                documentoDetalleRepository.save(docDetalle);
            }

            return 0;
        } else {
            return 1;
        }

    }

    private String obtenerYear(Documento doc) {
        return doc.getCarpeta() != null
                ? doc.getCarpeta().getExpediente().split("/")[0].trim()
                : String.valueOf(LocalDate.now().getYear());
    }

    public byte[] getAcuse(Integer documentoId) throws IOException {

        Documento doc = documentoRepository.findById(documentoId).orElse(null);
        DocumentoDetalle docDetalle = documentoDetalleRepository.findByDocumentoId(documentoId).orElse(null);

        if(doc != null && docDetalle != null){
            String year = obtenerYear(doc);
            String juzgado = doc.getCarpeta() == null ? personaService.getAuditor().getJuzgado().getNombre()
                    : doc.getCarpeta().getJuzgado().getNombre();
            juzgado = juzgado.replaceAll(" ", "");
    
            Path rootPath = Paths.get(rootFolder, "digitalizacion", year, juzgado).resolve("oficios")
                    .resolve(docDetalle.getRuta());
    
            if (Files.exists(rootPath)) {
                return Files.readAllBytes(rootPath); // Retorna el archivo como un arreglo de bytes
            } else {
                throw new IOException("El archivo " + doc.getRuta() + " no existe en el directorio");
            }
        }
       
        return new byte[1];


    }
}
