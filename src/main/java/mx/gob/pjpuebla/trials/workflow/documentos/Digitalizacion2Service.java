package mx.gob.pjpuebla.trials.workflow.documentos;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DigitalizacionRecord;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

/**
 * Servicio encargado de la digitalización de documentos y la creación de rutas
 * en el sistema de archivos según el tipo de documento y la carpeta asociada.
 *
 * <p>
 * CREACION DE RUTAS SEGUN TIPO DE DOCUMENTO / CARPETA:
 * </p>
 * 
 * <p>
 * OFICIO:
 * - ADMINISTRATIVO (No tiene relacion con una carpeta):
 * /opt/pjp/files/digitalizacion/{year}/{juzgado}/oficiosAdministrativos/{documentId}
 * - JURISDICCIONAL (Debe tener una carpeta):
 * /opt/pjp/files/digitalizacion/{year}/{juzgado}/{expediente}/oficios/{documentId}
 * 
 * ACUSE (OFICIO ADMINISTRATIVO):
 * /opt/pjp/files/digitalizacion/{year}/{juzgado}/oficiosAdministrativos/{documentId}
 * ACUSE (OFICIO JURISDICCIONAL):
 * /opt/pjp/files/digitalizacion/{year}/{juzgado}/{expediente}/oficiosJurisdiccionales/{documentId}
 * 
 * DEMANDA (Debe de tener una carpeta):
 * /opt/pjp/files/digitalizacion/{year}/{juzgado}/{expediente}
 * 
 * EXHORTO (Debe de tener una carpeta):
 * /opt/pjp/files/digitalizacion/{year}/{juzgado}/{expediente}/{tipo}
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class Digitalizacion2Service {

    @Value("${app.root-folder}")
    private String rootFolder; // Ruta raíz de la digitalización
    private String basePath; // Ruta base para la digitalización

    private final PersonaService personaService; // Servicio de persona
    private final DocumentoRepository documentoRepository;
    private static final long MAX_FILE_SIZE = 50L * 1024L * 1024L; // Tamaño máximo del archivo en bytes (50 MB)
    private static final Set<String> TIPO_ARCHIVOS_PERMITIDOS = Set.of("application/pdf");
    private static final String EXTENSION_ARCHIVO = ".pdf";

    /**
     * Método que se ejecuta después de la construcción del bean.
     * Inicializa la ruta base para la digitalización.
     */
    @PostConstruct
    public void init() {
        this.basePath = this.rootFolder + "/digitalizacion/";
    }

    /**
     * Crea un directorio basado en el tipo de documento y la carpeta asociada.
     *
     * @param documentoId El id del documento el cual se quiere crear el directorio.
     * @return La ruta del directorio creado.
     */
    public Path crearDirectorio(Documento documento) {
        validateDocumento(documento);

        String year = obtenerYear(documento);
        String juzgado = obtenerJuzgado(documento);
        Carpeta carpeta = documento.getCarpeta();

        // Manejo de tipos de documento
        if (documento.getTipoDocumento() == TipoDocumento.OFICIO) {
            return manejarOficio(documento, year, juzgado);
        }

        return manejarCarpeta(carpeta, year, juzgado);
    }

    public DigitalizacionRecord guardarArchivo(MultipartFile file, Integer documentoId){
        Documento documento = documentoRepository.findById(documentoId).orElse(null);
        validateNotNull(documento, "No pudo ser obtenido el documento con ID: " + documentoId);
        validarArchivo(file);
        Path rutaArchivo = crearDirectorio(documento);
        String nombreUnicoArchivo = documento.getCarpeta() != null ? generarNombreArchivo(documento.getCarpeta().getTipoCarpeta()) :  generarNombreArchivo(null);

        // Guardar el archivo y manejar posibles excepciones
        try {
            Files.write(rutaArchivo.resolve(nombreUnicoArchivo), file.getBytes());
            log.info("Archivo cargado en el servidor con nombre: " + nombreUnicoArchivo);
        } catch (IOException e) {
            log.error("Error al guardar el archivo: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al guardar el archivo en el servidor", e);
        }

        // Actualiza la carpeta con la ruta del archivo y guarda en la base de datos
        documento.setRuta(nombreUnicoArchivo);
        documentoRepository.save(documento);

        return new DigitalizacionRecord(documento.getId(), rutaArchivo.resolve(nombreUnicoArchivo).toString(), nombreUnicoArchivo);
    }


    public byte[] getDocumento(Integer documentoId) throws IOException {
        Documento documento = documentoRepository.findById(documentoId).orElse(null);
        validateNotNull(documento, "No pudo ser obtenido el documento con ID: " + documentoId);

        Path rutaArchivo = crearDirectorio(documento).resolve(documento.getRuta());

        // Verifica si el archivo existe y lo retorna como arreglo de bytes

        if (Files.exists(rutaArchivo)) {
            return Files.readAllBytes(rutaArchivo); // Retorna el archivo como un arreglo de bytes
        } else {
            throw new IOException("El archivo " + documento.getRuta() + " no existe en el directorio");
        }
    }

    /**
     * Genera un nombre único para el archivo basado en el tipo de documento y un
     * UUID.
     *
     * @param tipoCarpeta El tipo de carpeta para incluir en el nombre del
     *                    archivo.
     * @return Un nombre único generado para el archivo PDF.
     */
    private String generarNombreArchivo(TipoCarpeta tipoCarpeta) {
        if(tipoCarpeta == null){
            return "Acuse" + "_" + UUID.randomUUID() + EXTENSION_ARCHIVO;
        }
        return tipoCarpeta.name() + "_" + UUID.randomUUID() + EXTENSION_ARCHIVO;
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
     * Maneja la creación de directorios para documentos de tipo oficio.
     *
     * @param documento El documento de tipo oficio.
     * @param year      El año relacionado con el documento.
     * @param juzgado   El juzgado relacionado con el documento.
     * @return La ruta del directorio creado para el oficio.
     */
    private Path manejarOficio(Documento documento, String year, String juzgado) {
        String tipoOficio = documento.getData().getTipoOficio();

        if ("Administrativo".equals(tipoOficio)) {
            return crearDirectorios(Paths.get(basePath, year, juzgado, "oficiosAdministrativos"));
        } else if ("Jurisdiccional".equals(tipoOficio)) {
            String expediente = documento.getCarpeta().getExpediente();
            return crearDirectorios(
                    Paths.get(basePath, construirRutaExpediente(year, juzgado, expediente), "oficiosJurisdiccionales"));
        }

        throw new IllegalArgumentException("Tipo de oficio no soportado: " + tipoOficio);
    }

    /**
     * Maneja la creación de directorios para documentos de tipo carpeta.
     *
     * @param carpeta La carpeta asociada al documento.
     * @param year    El año relacionado con el documento.
     * @param juzgado El juzgado relacionado con el documento.
     * @return La ruta del directorio creado para la carpeta.
     */
    private Path manejarCarpeta(Carpeta carpeta, String year, String juzgado) {
        validateNotNull(carpeta, "El documento debe tener una carpeta asignada");
        switch (carpeta.getTipoCarpeta()) {
            case DEMANDA:
            case EXHORTO:
                return crearDirectorios(
                        Paths.get(basePath, construirRutaExpediente(year, juzgado, carpeta.getExpediente())));
            default:
                log.warn("Tipo de carpeta desconocido: {}", carpeta.getTipoCarpeta());
                throw new IllegalArgumentException("Tipo de carpeta no soportado");
        }
    }

    /**
     * Construye la ruta del expediente para el documento.
     *
     * @param year       El año relacionado con el documento.
     * @param juzgado    El juzgado relacionado con el documento.
     * @param expediente El número de expediente.
     * @return La ruta del expediente construida.
     */
    private String construirRutaExpediente(String year, String juzgado, String expediente) {
        return basePath + year + "/" + juzgado + "/" + expediente;
    }

    /**
     * Valida que el documento no sea nulo y que tenga un tipo de documento válido.
     *
     * @param documento El documento a validar.
     */
    private void validateDocumento(Documento documento) {
        validateNotNull(documento, "El documento no puede ser nulo");
        validateNotNull(documento.getTipoDocumento(), "El tipo de documento no puede ser nulo");
    }

    /**
     * Obtiene el nombre del juzgado asociado al documento.
     *
     * @param documento El documento del cual se quiere obtener el juzgado.
     * @return El nombre del juzgado.
     */
    private String obtenerJuzgado(Documento documento) {
        return (documento.getCarpeta() == null ? personaService.getAuditor().getJuzgado().getNombre()
                : documento.getCarpeta().getJuzgado().getNombre()).replaceAll(" ", "");
    }

    /**
     * Obtiene el año relacionado con el documento. Si la carpeta es nula,
     * se obtiene el año actual.
     *
     * @param doc El documento del cual se quiere obtener el año.
     * @return El año relacionado con el documento.
     */
    private String obtenerYear(Documento doc) {
        return doc.getCarpeta() != null
                ? obtenerDatosExpediente(doc.getCarpeta().getExpediente())[1].trim()
                : String.valueOf(LocalDate.now().getYear());
    }

    /**
     * Obtiene los datos del expediente, separando su información por '/'.
     *
     * @param expediente El número de expediente a procesar.
     * @return Un arreglo con los datos del expediente.
     */
    private String[] obtenerDatosExpediente(String expediente) {
        String[] expedienteArray = expediente.split("/");
        if (expedienteArray.length < 2) {
            throw new IllegalArgumentException("El expediente no tiene el formato esperado");
        }
        return expedienteArray;
    }

    /**
     * Crea los directorios especificados en la ruta dada.
     *
     * @param rootPath La ruta donde se desean crear los directorios.
     * @return La ruta del directorio creado.
     */
    private Path crearDirectorios(Path rootPath) {
        try {
            Files.createDirectories(rootPath);
            log.info("Carpeta creada exitosamente en: {}", rootPath);
            return rootPath;
        } catch (IOException e) {
            log.error("Error al crear las carpetas de digitalización: {}", e.getMessage(), e);
            throw new RuntimeException("Error al crear las carpetas de digitalización", e);
        }
    }

    /**
     * Valida que un valor no sea nulo y lanza una excepción si es nulo.
     *
     * @param value   El valor a validar.
     * @param message El mensaje de error si el valor es nulo.
     */
    private void validateNotNull(Object value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
