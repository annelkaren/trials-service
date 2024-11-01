package mx.gob.pjpuebla.trials.workflow.files;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class DigitalizacionFolderService {

    @Value("${app.root-folder}")
    private String rootFolder;
    
    private static final String DIGITALIZACION_FOLDER = "digitalizacion";
    private static final String ENTRADA_FOLDER = "entrada";
    private static final String OFICIOS_FOLDER = "oficios";
    private static final String OFICIO_ADMINISTRATIVO = "administrativo";
    private static final String OFICIO_JURISDICCIONAL = "jurisdiccional";
    
    private final PersonaService personaService;

    public String createFolderDigitalizacion(Documento doc) {
        validacionDigitalizacion(doc);

        String year = obtenerYear(doc);
        String juzgado = doc.getCarpeta() == null ? personaService.getAuditor().getJuzgado().getNombre() : doc.getCarpeta().getJuzgado().getNombre();
        juzgado = juzgado.replaceAll(" ", "");
        Path rootPath = (TipoDocumento.OFICIO.equals(doc.getTipoDocumento()))
                ? construirRutaOficio(doc, year, juzgado)
                : construirRutaConCarpeta(doc, year, juzgado);

        return crearDirectorios(rootPath);
    }

    protected void validacionDigitalizacion(Documento doc) {
        if (doc == null ) {
            throw new IllegalArgumentException("Documento o datos del documento no válidos");
        }

        if (!TipoDocumento.OFICIO.equals(doc.getTipoDocumento())) {
            Carpeta carpeta = doc.getCarpeta();
            if (carpeta == null || carpeta.getExpediente() == null) {
                throw new IllegalArgumentException("Propiedades no válidas en la carpeta del documento");
            }
        }
    }

    private String obtenerYear(Documento doc) {
        return doc.getCarpeta() != null
                ? obtenerDatosExpediente(doc.getCarpeta().getExpediente())[1].trim()
                : String.valueOf(LocalDate.now().getYear());
    }

    private Path obtenerBasePath(String year, String juzgado) {
        return Paths.get(rootFolder, DIGITALIZACION_FOLDER, year, juzgado);
    }

    protected String[] obtenerDatosExpediente(String expediente) {
        String[] expedienteArray = expediente.split("/");
        if (expedienteArray.length < 2) {
            throw new IllegalArgumentException("El expediente no tiene el formato esperado");
        }
        return expedienteArray;
    }

    private String obtenerNombreCarpeta(Carpeta carpeta) {
        String expediente = obtenerDatosExpediente(carpeta.getExpediente())[0].trim();
        return TipoCarpeta.EXHORTO.equals(carpeta.getTipoCarpeta())
                ? expediente
                : String.format("%06d", Integer.parseInt(expediente));
    }

    private Path construirRutaConCarpeta(Documento doc, String year, String juzgado) {
        Carpeta carpeta = doc.getCarpeta();
        String expediente = obtenerDatosExpediente(carpeta.getExpediente())[0].trim();
        String nombreCarpeta = obtenerNombreCarpeta(carpeta);
        Path basePath = obtenerBasePath(year, juzgado);

        switch (carpeta.getTipoCarpeta()) {
            case DEMANDA:
                return basePath.resolve(nombreCarpeta);
            case EXHORTO:
                return basePath.resolve(ENTRADA_FOLDER).resolve(nombreCarpeta);
            default:
                log.warn("Tipo de carpeta desconocido: {}", carpeta.getTipoCarpeta());
                throw new IllegalArgumentException("Tipo de carpeta no soportado");
        }
    }

    private Path construirRutaOficio(Documento doc, String year, String juzgado) {
        Path basePath = obtenerBasePath(year, juzgado);
        String nombreOficio = doc.getData().getTipoOficio();
        // Definición de ruta:  
      
        if (OFICIO_ADMINISTRATIVO.equalsIgnoreCase(nombreOficio)) {
            return basePath.resolve(OFICIOS_FOLDER).resolve(String.valueOf(doc.getId()));
        } else if (OFICIO_JURISDICCIONAL.equalsIgnoreCase(nombreOficio)) {
            return basePath.resolve(doc.getCarpeta().getExpediente()).resolve(OFICIOS_FOLDER).resolve(String.valueOf(doc.getId()));
        } else {
            log.warn("Tipo de oficio desconocido: {}", nombreOficio);
            throw new IllegalArgumentException("Tipo de oficio no soportado");
        }
    }

    private String crearDirectorios(Path rootPath) {
        try {
            Files.createDirectories(rootPath);
            log.info("Carpeta creada exitosamente en: {}", rootPath);
            return rootPath.toString();
        } catch (IOException e) {
            log.error("Error al crear las carpetas de digitalización: {}", e.getMessage(), e);
            throw new RuntimeException("Error al crear las carpetas de digitalización", e);
        }
    }
}
