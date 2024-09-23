package mx.gob.pjpuebla.trials.workflow.documentos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.files.DigitalizacionFolderService;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;

/**
 * Servicio para la gestión de documentos, incluyendo la validación y el
 * almacenamiento de archivos PDF.
 */
@Service
@RequiredArgsConstructor
public class DigitalizacionService {

    /**
     * Ruta raíz donde se almacenan los archivos de digitalización.
     * Este valor se obtiene del archivo de configuración.
     */
    @Value("${app.root-folder}")
    private String rootFolder;

    private final DigitalizacionFolderService digitalizacionFolderService;
    private final DocumentoRepository documentoRepository;
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // Tamaño máximo del archivo en bytes (50 MB)
    private static final Set<String> TIPO_ARCHIVOS_PERMITIDOS = Set.of("application/pdf");
    private static final String EXTENSION_ARCHIVO = ".pdf";

    /**
     * Procesa y almacena un archivo PDF en el servidor, asociado a un documento
     * existente.
     * Valida que el archivo y el documento cumplan con los requisitos, crea una
     * ruta de almacenamiento, y actualiza la entidad del documento con la ruta
     * del archivo. Devuelve un record con los detalles del archivo procesado.
     * 
     * @param file        El archivo PDF a procesar.
     * @param documentoId El ID del documento asociado al archivo.
     * @return Un record que contiene el ID del documento, la ruta del archivo
     *         almacenado y el nombre único del archivo.
     * @throws IOException Si ocurre un error al guardar el archivo en el sistema de
     *                     archivos.
     */
    public DigitalizacionRecord procesarArchivo(MultipartFile file, Integer documentoId) {
        Documento doc = validarDocumento(file, documentoId);

        // Crea la ruta donde se almacenará el archivo
        String rutaArchivo = digitalizacionFolderService.createFolderDigitalizacion(doc);

        String nombreUnicoArchivo = generarNombreArchivo(doc.getTipoDocumento());
        Path path = Paths.get(rutaArchivo);

        // Crear directorios si no existen y guardar el archivo
        
        try {
            Files.createDirectories(path);
        } catch (IOException e) {

            e.printStackTrace();
        }

        // Guardar el archivo y manejar posibles excepciones
        try {
            Files.write(path.resolve(nombreUnicoArchivo), file.getBytes());
        } catch (IOException e) {

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al guardar el archivo en el servidor", e);
        }

        // Actualiza el documento con la ruta del archivo y guarda en la base de datos
        doc.setRuta(nombreUnicoArchivo);
        documentoRepository.save(doc);

        // Devuelve los detalles del documento en un record
        return new DigitalizacionRecord(doc.getId(), path.resolve(nombreUnicoArchivo).toString(), nombreUnicoArchivo);
    }

    /**
     * Obtiene el documento digitalizado en forma de arreglo de bytes según el ID
     * proporcionado.
     * 
     * @param documentoId ID del documento que se desea obtener.
     * @return Un arreglo de bytes que representa el contenido del archivo.
     * @throws IOException       Si el archivo no se encuentra o si ocurre un error
     *                           al leerlo.
     * @throws NotFoundException Si no se encuentra el documento en la base de
     *                           datos.
     */
    public byte[] getDocumento(Integer documentoId) throws IOException {
        // Busca el documento en la base de datos o lanza una excepción si no existe
        Documento doc = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new NotFoundException("Archivo no encontrado", "documentoId"));

        // Extrae el expediente y año a partir del formato "expediente/año"
        String[] expedienteArray = doc.getExpediente().split("/");
        String expediente = expedienteArray[0].trim(); // Número del expediente
        String year = expedienteArray[1].trim(); // Año del expediente
        String juzgado = doc.getJuzgado().getNombre().trim(); // Nombre del juzgado

        // Creación de la ruta donde se espera encontrar el archivo
        Path rootPath = Paths.get(rootFolder, "digitalizacion", year, juzgado, expediente);
       
        Path filePath = rootPath.resolve(doc.getRuta()); // Ruta completa del archivo

        // Verifica si el archivo existe y lo retorna como arreglo de bytes
      
        if (Files.exists(filePath)) {
            return Files.readAllBytes(filePath); // Retorna el archivo como un arreglo de bytes
        } else {
            throw new IOException("El archivo " + doc.getRuta() + " no existe en el directorio");
        }
    }

    /**
     * Valida el archivo PDF y verifica que el documento asociado exista en la base
     * de datos.
     * Además, asegura que el archivo cumpla con los criterios (no esté vacío, sea
     * un PDF, y esté dentro del tamaño permitido).
     * 
     * @param file        El archivo a validar.
     * @param documentoId El ID del documento asociado.
     * @return El documento validado.
     * @throws ResponseStatusException Si no se cumplen las validaciones del archivo
     *                                 o el documento no existe.
     */
    private Documento validarDocumento(MultipartFile file, Integer documentoId) {
        // Verifica que el documento exista
        Documento doc = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El registro del documento no existe"));

        // Valida las propiedades del archivo
        validarArchivo(file);

        // Verifica que el tipo de documento no sea nulo
        if (doc.getTipoDocumento() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tipo de documento es obligatorio.");
        }

        return doc;
    }

    /**
     * Valida las propiedades del archivo: que no esté vacío, que sea un PDF, y que
     * no exceda el tamaño máximo permitido.
     * 
     * @param file El archivo a validar.
     * @throws ResponseStatusException Si el archivo no cumple con las condiciones.
     */
    private void validarArchivo(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo no puede estar vacío.");
        }

        if (file.getContentType() == null || !TIPO_ARCHIVOS_PERMITIDOS.contains(file.getContentType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo debe ser un PDF.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo no puede superar los 50 MB.");
        }
    }

    /**
     * Genera un nombre único para el archivo basado en el tipo de documento y un
     * UUID.
     * 
     * @param tipoDocumento El tipo de documento para incluir en el nombre del
     *                      archivo.
     * @return Un nombre único generado para el archivo PDF.
     */
    private String generarNombreArchivo(TipoDocumento tipoDocumento) {
        return tipoDocumento.name() + "_" + UUID.randomUUID() + EXTENSION_ARCHIVO;
    }
}
