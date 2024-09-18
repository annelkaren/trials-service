package mx.gob.pjpuebla.trials.workflow.digitalizacion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.files.DigitalizacionFolderService;

/**
 * Servicio encargado de gestionar la digitalización de documentos.
 */
@Service
@Component
@RequiredArgsConstructor
public class DigitalizacionService {

    /**
     * Ruta raíz donde se almacenan los archivos de digitalización.
     * Este valor se obtiene del archivo de configuración.
     */
    @Value("${app.root-folder}")
    private String rootFolder;

    /**
     * Repositorio para interactuar con la base de datos de documentos.
     */
    private final DocumentoRepository documentoRepository;

    /**
     * Servicio encargado de gestionar las carpetas de digitalización.
     */
    private final DigitalizacionFolderService digitalizacionFolder;

    /**
     * Obtiene el documento digitalizado en forma de arreglo de bytes según el ID proporcionado.
     * 
     * @param documentoId ID del documento que se desea obtener.
     * @return Un arreglo de bytes que representa el contenido del archivo.
     * @throws IOException Si el archivo no se encuentra o si ocurre un error al leerlo.
     * @throws NotFoundException Si no se encuentra el documento en la base de datos.
     */
    public byte[] getDocument(Integer documentoId) throws IOException {
        // Busca el documento en la base de datos o lanza una excepción si no existe
        Documento doc = documentoRepository.findById(documentoId)
            .orElseThrow(() -> new NotFoundException("Archivo no encontrado", "documentoId"));
        
        // Extrae el expediente y año a partir del formato "expediente/año"
        String[] expedienteArray = doc.getExpediente().split("/");
        String expediente = expedienteArray[0].trim();  // Número del expediente
        String year = expedienteArray[1].trim();        // Año del expediente
        String juzgado = doc.getJuzgado().getNombre().trim();  // Nombre del juzgado

        // Creación de la ruta donde se espera encontrar el archivo
        Path rootPath = Paths.get(rootFolder, "digitalizacion", year, juzgado, expediente);
        Path filePath = rootPath.resolve(doc.getRuta());  // Ruta completa del archivo

        // Verifica si el archivo existe y lo retorna como arreglo de bytes
        if (Files.exists(filePath)) {
            return Files.readAllBytes(filePath);  // Retorna el archivo como un arreglo de bytes
        } else {
            throw new IOException("El archivo " + doc.getRuta() + " no existe en el directorio");
        }
    }
}

