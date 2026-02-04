package mx.gob.pjpuebla.trials.workflow.documentos;

import lombok.Setter;
import mx.gob.pjpuebla.trials.workflow.audiencias.Audiencia;
import mx.gob.pjpuebla.trials.workflow.audiencias.AudienciaService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.migracion.readers.detallesProm.DetallesProm;
import mx.gob.pjpuebla.migracion.readers.detallesProm.DetallesPromRepository;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.EstadoEnvio;
import mx.gob.pjpuebla.trials.util.enums.Migrado;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.util.ftp.FtpDownloader;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalle;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DigitalizacionRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.ResponseGenericRecord;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
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
 * <p>
 * ACUSE (OFICIO ADMINISTRATIVO):
 * /opt/pjp/files/digitalizacion/{year}/{juzgado}/oficiosAdministrativos/{documentId}
 * ACUSE (OFICIO JURISDICCIONAL):
 * /opt/pjp/files/digitalizacion/{year}/{juzgado}/{expediente}/oficiosJurisdiccionales/{documentId}
 * <p>
 * DEMANDA (Debe de tener una carpeta):
 * /opt/pjp/files/digitalizacion/{year}/{juzgado}/{expediente}
 * <p>
 * EXHORTO (Debe de tener una carpeta):
 * /opt/pjp/files/digitalizacion/{year}/{juzgado}/{expediente}/{tipo}
 * </p>
 * SENTENCIA PUBLICA (Debe de tener una carpeta):
 * /opt/pjp/files/digitalizacion/{year}/{juzgado}/{expediente}/{tipo}/{número de
 * sentencia}
 * </p>
 * </p>
 * APELACION (Debe de tener una carpeta):
 * /opt/pjp/files/digitalizacion/{year}/{SALA (JUZGADO)}/{TOCA(expediente)}/
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Setter
public class DigitalizacionService {

    @Value("${app.root-folder}")
    private String rootFolder; // Ruta raíz de la digitalización
    private String basePath; // Ruta base para la digitalización

    private final PersonaService personaService; // Servicio de persona
    private final DocumentoRepository documentoRepository;
    private final CarpetaRepository carpetaRepository;
    private final DocumentoDetalleRepository documentoDetalleRepository;
    private final AudienciaService audienciaService;
    private final FtpDownloader ftpDownloader;
    private final DetallesPromRepository detallesPromRepository;
    private static final long MAX_FILE_SIZE = 50L * 1024L * 1024L; // Tamaño máximo del archivo en bytes (50 MB)
    private static final Set<String> TIPO_ARCHIVOS_PERMITIDOS = Set.of("application/pdf");
    private static final Set<String> TIPO_IMAGENES_PERMITIDAS = Set.of("image/jpg", "image/png", "image/jpeg");
    private static final String EXTENSION_ARCHIVO = ".pdf";

    // Si se usa en mas métodos cambiar variable global por local.
    private Integer audienciaId;

    /**
     * Crea un directorio basado en el tipo de documento y la carpeta asociada.
     *
     * @param documento El documento el cual se quiere crear el directorio.
     * @return La ruta del directorio creado.
     */
    public Path crearDirectorio(Documento documento) {
        this.basePath = this.rootFolder + "/digitalizacion/";
        validateDocumento(documento);

        String year = obtenerYear(documento);
        String juzgado = obtenerJuzgado(documento);
        Carpeta carpeta = documento.getCarpeta();

        // Manejo de tipos de documento
        if (Objects.equals(documento.getTipoDocumento(), TipoDocumento.OFICIO)) {
            return manejarOficio(documento, year, juzgado, juzgado);
        }

        if (Objects.equals(documento.getTipoDocumento(), TipoDocumento.SENTENCIA_PUBLICA)) {
            return crearDirectorios(
                    Paths.get(basePath, year, juzgado, obtenerDatosExpediente(carpeta.getExpediente())[0],
                            TipoDocumento.SENTENCIA_PUBLICA.getEtiqueta(), documento.getId().toString()));
        }

        if (Objects.equals(documento.getTipoDocumento(), TipoDocumento.DOCUMENTO_IDENTIFICACION)) {
            return crearDirectorios(
                    Paths.get(basePath, year, juzgado, obtenerDatosExpediente(carpeta.getExpediente())[0], "Audiencias",
                            this.audienciaId.toString(), "Asistencia"));
        }

        if (Objects.equals(documento.getTipoDocumento(), TipoDocumento.PRUEBA_AUDIENCIA)) {
            // Solo mientras se define la audiencia a la que corresponde
            Audiencia audiencia = audienciaService.obtenerUltimaAudienciaDesahogada();
            String numAudiencia = String.valueOf(audiencia.getId());

            String ruta = construirRutaAudienciaPruebas(juzgado, carpeta.getExpediente(), numAudiencia);
            Path path = Paths.get(basePath, ruta);
            return crearDirectorios(path);
        }

        // Revisar la ruta para los documentos de una pieza
        if (carpeta.getTipoCarpeta() == TipoCarpeta.PIEZA) {
            carpeta = carpeta.getCarpetaPadre();
        }

        return manejarCarpeta(carpeta, year, juzgado);
    }

    public DigitalizacionRecord guardarArchivo(MultipartFile file, Integer documentoId) {
        this.basePath = this.rootFolder + "/digitalizacion/";
        Documento documento = documentoRepository.findById(documentoId).orElse(null);
        validateNotNull(documento, "No pudo ser obtenido el documento con ID: " + documentoId);
        validarArchivo(file);
        Path rutaArchivo = crearDirectorio(documento);
        boolean isOficio = Objects.equals(documento.getTipoDocumento(), TipoDocumento.OFICIO);
        String nombreUnicoArchivo = "";

        if (isOficio) {

            DocumentoDetalle documentoDetalle = documentoDetalleRepository.findByDocumentoId(documento.getId())
                    .orElse(null);

            if (documentoDetalle != null
                    && Objects.equals(documentoDetalle.getEstadoEnvio(), EstadoEnvio.RECIBIDO_DESTINO)) {
                nombreUnicoArchivo = generarNombreArchivo("OFICIO_OCP");
            }
        }

        if (nombreUnicoArchivo.isEmpty()) {
            nombreUnicoArchivo = documento.getTipoDocumento() == null
                    ? generarNombreArchivo(documento.getCarpeta().getTipoCarpeta().name())
                    : generarNombreArchivo(documento.getTipoDocumento().name());

        }

        // Guardar el archivo y manejar posibles excepciones
        try {
            Files.write(rutaArchivo.resolve(nombreUnicoArchivo), file.getBytes());
            log.info("Archivo cargado en el servidor con nombre: {}", nombreUnicoArchivo);
        } catch (IOException e) {
            log.error("Error al guardar el archivo: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al guardar el archivo en el servidor", e);
        }

        // Actualiza la carpeta con la ruta del archivo y guarda en la base de datos
        documento.setRuta(nombreUnicoArchivo);

        // ACTUALIZAMOS ESTATUS DE LA CARPETA O DOCUMENTO SI SE REQUIERE (ESTO EN CASO
        // DE DEVOLUCIÓN DEL JUZGADO)
        if (documento.getTipoDocumento() != null && (documento.getEstatus() == EstadoCarpeta.DEVUELTO_A_OFICIALIA
                || documento.getEstatus() == EstadoCarpeta.EDICION)) {
            documento.setEstatus(EstadoCarpeta.CAPTURA);
        } else {
            if (documento.getData().getTipoOficio().equals("Administrativo")) {
                documentoRepository.save(documento);

                return new DigitalizacionRecord(documento.getId(), rutaArchivo.resolve(nombreUnicoArchivo).toString(),
                        nombreUnicoArchivo);
            }

            if (documento.getCarpeta().getEstatus() == EstadoCarpeta.DEVUELTO_A_OFICIALIA
                    || documento.getCarpeta().getEstatus() == EstadoCarpeta.EDICION) {
                documento.getCarpeta().setEstatus(EstadoCarpeta.CAPTURA);
                carpetaRepository.save(documento.getCarpeta());
            }
        }

        documentoRepository.save(documento);

        return new DigitalizacionRecord(documento.getId(), rutaArchivo.resolve(nombreUnicoArchivo).toString(),
                nombreUnicoArchivo);
    }

    public byte[] getDocumento(Integer documentoId) throws IOException {
        this.basePath = this.rootFolder + "/digitalizacion/";
        Documento documento = documentoRepository.findById(documentoId).orElse(null);
        validateNotNull(documento, "No pudo ser obtenido el documento con ID: " + documentoId);

        if (documento.getMigrado().equals(Migrado.SI)) {
            return getDocumentoMigrado(documento);
        }

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
     * @param tipo El tipo de documento o carpeta para incluir en el nombre del
     *             archivo.
     * @return Un nombre único generado para el archivo PDF.
     */
    private String generarNombreArchivo(String tipo) {
        return tipo + "_" + UUID.randomUUID() + EXTENSION_ARCHIVO;
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
     * Valida las propiedades del archivo: que no esté vacío, que sea una imagen, y
     * que
     * no exceda el tamaño máximo permitido.
     *
     * @param file El archivo a validar.
     * @throws ResponseStatusException Si el archivo no cumple con las condiciones.
     */
    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo no puede estar vacío.");
        }

        if (file.getContentType() == null || !TIPO_IMAGENES_PERMITIDAS.contains(file.getContentType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo debe ser una imagen.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La imagen no puede superar los 50 MB.");
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
    private Path manejarOficio(Documento documento, String year, String juzgado, String oficialia) {
        String tipoOficio = documento.getData().getTipoOficio();

        if ("Administrativo".equals(tipoOficio)) {
            return crearDirectorios(Paths.get(basePath, year, oficialia, "oficiosAdministrativos"));
        } else if ("Jurisdiccional".equals(tipoOficio)) {
            String expediente = obtenerDatosExpediente(documento.getCarpeta().getExpediente())[0];
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
        String expediente = construirRutaExpediente(year, juzgado, obtenerDatosExpediente(carpeta.getExpediente())[0]);

        switch (carpeta.getTipoCarpeta()) {
            case DEMANDA,
                    APELACION:
                return crearDirectorios(Paths.get(basePath, expediente));
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
        return year + "/" + juzgado + "/" + expediente;
    }

    private String construirRutaAudienciaPruebas(String juzgado, String expediente, String numAudiencia) {
        return "/" + juzgado + "/" + expediente + "/" + "audiencias" + "/" + numAudiencia + "/" + "pruebas";
    }

    /**
     * Valida que el documento no sea nulo y que tenga un tipo de documento válido.
     *
     * @param documento El documento a validar.
     */
    private void validateDocumento(Documento documento) {
        validateNotNull(documento, "El documento no puede ser nulo");
        if (documento.getCarpeta() == null) {
            validateNotNull(documento.getTipoDocumento(), "El tipo de documento no puede ser nulo");
        }

    }

    /**
     * Obtiene el nombre del juzgado asociado al documento.
     *
     * @param documento El documento del cual se quiere obtener el juzgado.
     * @return El nombre del juzgado.
     */
    private String obtenerJuzgado(Documento documento) {
        Persona persona = personaService.getAuditor();
        String nombreCentroTrabajo = "";

        if (persona.getJuzgado() == null && persona.getOficialia() != null) {
            nombreCentroTrabajo = persona.getOficialia().getNombre();
        }

        return (documento.getCarpeta() == null ? nombreCentroTrabajo
                : documento.getCarpeta().getJuzgado().getNombre()).replace(" ", "");
    }

    /**
     * Obtiene el año relacionado con el documento. Si la carpeta es nula,
     * se obtiene el año actual.
     *
     * @param doc El documento del cual se quiere obtener el año.
     * @return El año relacionado con el documento.
     */
    private String obtenerYear(Documento doc) {
        return doc.getCarpeta() != null && !doc.getCarpeta().getTipoCarpeta().equals(TipoCarpeta.EXHORTO)
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

    /**
     * Guarda una fotografía relacionada a una sede
     *
     * @param photo  imagen de la sede
     * @param sedeId identificador interno de la sede
     * @return Nuevo objeto con ruta y nombre de la imagen guardada
     */
    public DigitalizacionRecord savePhoto(MultipartFile photo, Integer sedeId) {
        this.basePath = this.rootFolder + "/digitalizacion/sedes/";
        validateImage(photo);
        Path rutaArchivo = crearDirectorios(Paths.get(basePath, sedeId.toString()));
        String photoName = UUID.randomUUID() + "." + FilenameUtils.getExtension(photo.getOriginalFilename());
        // Guardar el archivo y manejar posibles excepciones
        try {
            Files.write(rutaArchivo.resolve(photoName), photo.getBytes());
            log.info("Fotografía cargada en el servidor con nombre: {}", photoName);
        } catch (IOException e) {
            log.error("Error al guardar la imagen: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al guardar la imagen en el servidor", e);
        }
        // Eliminar archivos anteriores, solo puede existir una fotografía
        File[] allContents = Paths.get(basePath, sedeId.toString()).toFile().listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                if (!file.getName().equals(photoName)) {
                    if (!file.delete()) {
                        log.warn("No se pudo eliminar el archivo anterior: {}", file.getAbsolutePath());
                    }
                }
            }

        }
        return new DigitalizacionRecord(sedeId, rutaArchivo.resolve(photoName).toString(),
                photoName);
    }

    /**
     * Obtiene un String en base64 de la fotografía relacionada a la sede
     *
     * @param sedeId - identificador interno de la sede
     * @param name   - nombre de la imagen
     * @return base64 de la imagen, si no existe se retorna vacio
     */
    public String getPhoto(Integer sedeId, String name) {
        this.basePath = this.rootFolder + "/digitalizacion/sedes/";
        Path rutaArchivo = Paths.get(basePath, sedeId.toString(), name);
        if (Files.exists(rutaArchivo)) {
            try {
                return "data:image/png;base64," + Base64.encodeBase64String(Files.readAllBytes(rutaArchivo)); // Retorna
                                                                                                              // el
                                                                                                              // archivo
                                                                                                              // como
                                                                                                              // Base64
            } catch (IOException e) {
                log.error("Error al obtener la fotografía de la sede: {}", e.getMessage(), e);
                return "";
            }
        }
        return "";
    }

    /**
     * Elimina la fotografía de la sede, la eliminamos unicamente cuando la sede es
     * eliminada
     *
     * @param sedeId - identificador interno de la sede
     */
    public void deletePhoto(Integer sedeId) {
        this.basePath = this.rootFolder + "/digitalizacion/sedes/";
        Path path = Paths.get(basePath, sedeId.toString());
        deleteContent(path);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("Error al eliminar la fotografía de la sede: {}", e.getMessage(), e);
        }
    }

    /**
     * Método auxiliar que verifica si un directorio tiene elementos, si tiene los
     * elimina
     *
     * @param path - ruta de la carpeta
     */
    private void deleteContent(Path path) {
        File[] allContents = path.toFile().listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                if (!file.delete()) {
                    log.warn("No se pudo eliminar el archivo: {}", file.getAbsolutePath());
                }
            }
        }
    }

    public byte[] getDocumentoMigrado(Documento documento) {

        String ruta = documento.getRuta();
        if (ruta.contentEquals("172.16.6.11")) {
            throw new NotFoundException("El archivo no existe en el servidor", ruta);
        }

        return ftpDownloader.downloadFromFullUrl("ftp://" + ruta);
    }

    public byte[] getDocumentoMigrado(String ruta) {

        if (ruta.contentEquals("172.16.6.11")) {
            throw new NotFoundException("El archivo no existe en el servidor", ruta);
        }

        return ftpDownloader.downloadFromFullUrl("ftp://" + ruta);
    }

    public byte[] getPromocionMigrada(Integer promocionId) {
        Optional<DetallesProm> detallesProm = detallesPromRepository.findById(promocionId);
        if (detallesProm.isEmpty()) {
            throw new NotFoundException("El archivo no existe en el servidor", promocionId.toString());
        }
        String ruta = detallesProm.get().getArchivo();

        if (ruta.contentEquals("172.16.6.11")) {
            throw new NotFoundException("El archivo no existe en el servidor", ruta);
        }

        return ftpDownloader.downloadFromFullUrl("ftp://" + ruta);
    }

    public ResponseGenericRecord autorizarRedigitalizacion(Integer documentoId) {
        Documento documento = documentoRepository.findById(documentoId).orElseThrow(
                () -> new NotFoundException("No se pudo encontrar el documento con ID: ", documentoId.toString()));

        documento.setRuta(null);
        documentoRepository.save(documento);
        return new ResponseGenericRecord("Autorización registrada exitosamente", "OK");
    }

}
