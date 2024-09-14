package mx.gob.pjpuebla.trials.core.digitalizacion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.files.DigitalizacionFolderService;
import mx.gob.pjpuebla.trials.util.TipoDocumento;


/**
 * Servicio para la gestión de documentos, incluyendo la validación y el almacenamiento de archivos PDF.
 */
@Service
public class DigitalizacionService {

    @Value("${app.root-folder}")
    private String rootFolder;
    private  DigitalizacionFolderService digitalizacionFolderService;
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // Tamaño máximo del archivo en bytes (50 MB)

    /**
     * Procesa y almacena un archivo PDF en el servidor, asociado a un documento.
     * 
     * @param file El archivo PDF a procesar.
     * @param doc El documento asociado al archivo.
     * @throws IOException Si ocurre un error al guardar el archivo.
     */
    public void procesarArchivo(MultipartFile file, Documento doc) throws IOException {
        
        validarDocumentoYArchivo(file, doc);

        String rutaFile = digitalizacionFolderService.createFolderDigitalizacion(doc);
        String uniqueFileName = generarNombreArchivo(doc.getTipoDocumento());
        Path path = Paths.get(rootFolder, rutaFile);

        // Guarda el archivo en el servidor
        Files.write(path.resolve(uniqueFileName), file.getBytes());

        // Actualiza el documento con la ruta del archivo
        doc.setRuta(path.resolve(uniqueFileName).toString());
    }

    /**
     * Valida el archivo y el documento antes de procesarlos.
     * 
     * @param file El archivo PDF a validar.
     * @param doc El documento asociado al archivo.
     * @throws ResponseStatusException Si el archivo o el documento no cumplen con las restricciones.
     */
    private void validarDocumentoYArchivo(MultipartFile file, Documento doc) {
        // Verifica si el archivo está vacío
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo no puede estar vacío.");
        }

        // Verifica si el archivo es un PDF
        if (!file.getContentType().equals("application/pdf")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo debe ser un PDF.");
        }

        // Verifica si el tamaño del archivo es menor o igual a 50 MB
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo no puede superar los 5 MB.");
        }

        // Verifica si el tipo de documento es nulo
        if (doc.getTipoDocumento() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tipo de documento es obligatorio.");
        }
    }

    /**
     * Genera un nombre único para el archivo basado en el tipo de documento y un UUID.
     * 
     * @param tipoDocumento El tipo de documento para incluir en el nombre del archivo.
     * @return El nombre único generado para el archivo.
     */
    private String generarNombreArchivo(TipoDocumento tipoDocumento) {
        return tipoDocumento.name() + "_" + UUID.randomUUID() + ".pdf";
    }
}
